package PIU.utilities;

import PIU.exceptions.LineOutOfOrderException;
import PIU.models.Device;
import PIU.models.SubSystem;
import PIU.models.Vendor;
import PIU.parsers.LineParser;
import PIU.parsers.models.VendorModel;

import java.util.PriorityQueue;

public class VendorUpdater {
    private enum COMMANDS {
        CONTINUE,
        INSERT_VENDOR, INSERT_DEVICE, INSERT_SUBSYSTEM, INSERT_DEVICES, INSERT_SUBSYSTEMS,
        POLL_VENDOR, POLL_DEVICE, POLL_SUBSYSTEM
    };

    private static boolean isInsertCommand(COMMANDS cmd){
        return cmd == COMMANDS.INSERT_VENDOR ||
                cmd == COMMANDS.INSERT_DEVICE ||
                cmd == COMMANDS.INSERT_SUBSYSTEM ||
                cmd == COMMANDS.INSERT_DEVICES ||
                cmd == COMMANDS.INSERT_SUBSYSTEMS;
    }

    /* Assuming vendors are equal */
    private static COMMANDS getCommand(VendorModel line, SubSystem to){
        COMMANDS ret = COMMANDS.CONTINUE;
        if(line.getSub().getvId() > to.getSubVendor()){
            ret = COMMANDS.INSERT_SUBSYSTEM;
        }else if(line.getSub().getvId().equals(to.getSubVendor())){
            if(line.getSub().getdId() > to.getSubDevice()){ ret = COMMANDS.INSERT_SUBSYSTEM; }
            else if(line.getSub().getdId().equals(to.getSubDevice())){ ret = COMMANDS.POLL_SUBSYSTEM; }
        }
        return ret;
    }

    /* Assuming vendors are equal */
    private static COMMANDS getCommand(VendorModel line, Device to, LineParser.LineType currentLineType, LineParser.LineType nextLineType){
        COMMANDS ret = COMMANDS.CONTINUE;
        if(line.getDev().getId() > to.getDevice()){
            ret = COMMANDS.INSERT_DEVICE;
        }else if(line.getDev().getId().equals(to.getDevice())) {
            if (to.size() == 0) ret = COMMANDS.POLL_DEVICE;
            else if (currentLineType == LineParser.LineType.DEVICE_LINE) {
                if (nextLineType == LineParser.LineType.DEVICE_LINE) ret = COMMANDS.INSERT_SUBSYSTEM;
                else if (nextLineType == LineParser.LineType.SUBSYSTEM_LINE) ret = COMMANDS.CONTINUE;
                else if (nextLineType == LineParser.LineType.VENDOR_LINE) ret = COMMANDS.INSERT_SUBSYSTEM;
            } else {
                ret = getCommand(line, to.getSubSystems().peek());
            }
        }


        return ret;
    }


    /* Handle nulls for command later */
    private static COMMANDS getCommand(VendorModel line, Vendor to, LineParser.LineType currLineType, LineParser.LineType nextLineType)
            throws LineOutOfOrderException
    {

        COMMANDS ret = COMMANDS.CONTINUE;
        if(line.getVen().getId() > to.getVendor()) {
            ret = COMMANDS.INSERT_VENDOR;
        }else if(line.getVen().getId().equals(to.getVendor())){
            /* Case 1 - check to see if there is nothing to write */
            if(to.size() == 0) ret = COMMANDS.POLL_VENDOR;
            else if(currLineType == LineParser.LineType.VENDOR_LINE){
                if(nextLineType == LineParser.LineType.VENDOR_LINE) ret = COMMANDS.INSERT_DEVICES; /* insert Device means poll vendor */
                else if(nextLineType == LineParser.LineType.DEVICE_LINE) ret = COMMANDS.CONTINUE;
            }else{
                ret = getCommand(line, to.getDevices().peek(), currLineType, nextLineType);
            }
        }
        return ret;
    }
    private static void runCommand(PriorityQueue<Vendor> pv, LineCountWriter writer, COMMANDS command) throws Exception{
        Vendor tempV;
        Device tempD;
        SubSystem tempS;
        switch (command){
            case INSERT_VENDOR:
                tempV = pv.poll();
                writer.write(tempV.toString() + "\n");
                Logger.getInstance().println("new output at line " + writer.getLineNumber() + ":\n" +  tempV);
                break;
            case INSERT_DEVICE:
                tempV = pv.peek();
                tempD = tempV.getDevices().poll();
                writer.write(tempD.toString() + "\n");
                Logger.getInstance().println("new output at line " + writer.getLineNumber() + ":\n" + tempD);
                if(tempV.size() == 0) pv.poll();
                break;
            case INSERT_SUBSYSTEM:
                tempV = pv.peek();
                tempD = tempV.getDevices().peek();
                tempS = tempD.getSubSystems().poll();
                writer.write(tempS + "\n");
                Logger.getInstance().println("new output at line " + writer.getLineNumber() + ": \n" + tempS);
                if(tempD.size() == 0) tempV.getDevices().poll();
                if(tempV.size() == 0) pv.poll();
                break;
            case INSERT_DEVICES:
                tempV = pv.poll();
                writer.write(tempV.devicesToString() + "\n");
                Logger.getInstance().println("new output at line :D" + writer.getLineNumber() + ": \n" + tempV.devicesToString());
                break;
            case INSERT_SUBSYSTEMS:
                tempV = pv.peek();
                tempD = tempV.getDevices().poll();
                writer.write(tempD.subSystemsToString() + "\n");
                Logger.getInstance().println("new output at line " + writer.getLineNumber() + ": \n" + tempD.subSystemsToString());
                if(tempV.size() == 0) pv.poll();
                break;
            case POLL_VENDOR:
                pv.poll();
                break;
            case POLL_DEVICE:
                tempV = pv.peek();
                tempV.getDevices().poll();
                if(tempV.size() == 0) pv.poll();
                break;
            case POLL_SUBSYSTEM:
                tempV = pv.peek();
                tempD = tempV.getDevices().peek();
                tempD.getSubSystems().poll();
                if(tempD.size() == 0) tempV.getDevices().poll();
                if(tempV.size() == 0) pv.poll();
                break;


        }


    }


    /* Assumed to have at least one vendor in the queue*/
    public static void update(PriorityQueue<Vendor> pv, LineCountWriter writer,
                              LineParser.LineType currLine, LineParser.LineType nextLine) throws Exception{

        VendorModel vendorModel = LineParser.getInstance().getVendorModel();
        Vendor currVendor = pv.peek();
        COMMANDS command = getCommand(vendorModel, currVendor, currLine, nextLine);

        while (currVendor != null  && isInsertCommand(command)) {
            runCommand(pv, writer, command);
            currVendor = pv.peek();
            command = getCommand(vendorModel, currVendor, currLine, nextLine);
        }
        if(!isInsertCommand(command))runCommand(pv, writer, command);

    }
}
