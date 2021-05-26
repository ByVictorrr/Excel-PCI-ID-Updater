package utilities;

import exceptions.LineOutOfOrderException;
import models.Class;
import models.Device;
import models.SubSystem;
import models.Vendor;
import parsers.LineParser;
import parsers.models.VendorModel;

import java.io.BufferedReader;
import java.util.PriorityQueue;


public class Updater {

    private static final int CONTINUE =  -1;
    private static final int EQUAL =  0; // EQUAL mean continue unless overiding enabled
    private static final int WRITE =  1;
    enum UPDATER_COMMANDS {
        CONTINUE,
        INSERT_VENDOR, INSERT_DEVICE, INSERT_SUBSYSTEM, INSERT_DEVICES, INSERT_SUBSYSTEMS,
        POLL_VENDOR, POLL_DEVICE, POLL_SUBSYSTEM
    };
    private static boolean isInsertCommand(UPDATER_COMMANDS cmd){
        return cmd == UPDATER_COMMANDS.INSERT_VENDOR ||
                cmd == UPDATER_COMMANDS.INSERT_DEVICE ||
                cmd == UPDATER_COMMANDS.INSERT_SUBSYSTEM ||
                cmd == UPDATER_COMMANDS.INSERT_DEVICES ||
                cmd == UPDATER_COMMANDS.INSERT_SUBSYSTEMS;
    }

    /* Assuming vendors are equal */
    private static UPDATER_COMMANDS getCommand(VendorModel line, SubSystem to){
        UPDATER_COMMANDS ret = UPDATER_COMMANDS.CONTINUE;
        if(line.getSub().getvId() > to.getSubVendor()){
            ret = UPDATER_COMMANDS.INSERT_SUBSYSTEM;
        }else if(line.getSub().getvId().equals(to.getSubVendor())){
            if(line.getSub().getdId() > to.getSubDevice()){ ret = UPDATER_COMMANDS.INSERT_SUBSYSTEM; }
            else if(line.getSub().getdId().equals(to.getSubDevice())){ ret = UPDATER_COMMANDS.POLL_SUBSYSTEM; }
            else ret =UPDATER_COMMANDS.CONTINUE;
        }
        return ret;
    }

    /* Assuming vendors are equal */
    private static UPDATER_COMMANDS getCommand(VendorModel line, Device to, LineParser.LineType currentLineType, LineParser.LineType nextLineType){
        UPDATER_COMMANDS ret = UPDATER_COMMANDS.CONTINUE;
        if(line.getDev().getId() > to.getDevice()){
            ret = UPDATER_COMMANDS.INSERT_DEVICE;
        }else if(line.getDev().getId().equals(to.getDevice())) {
            if (to.size() == 0) ret = UPDATER_COMMANDS.POLL_DEVICE;
            else if (currentLineType == LineParser.LineType.DEVICE_LINE) {
                if (nextLineType == LineParser.LineType.DEVICE_LINE || nextLineType == LineParser.LineType.SUBSYSTEM_LINE)
                    ret = UPDATER_COMMANDS.CONTINUE;
                else if(nextLineType == LineParser.LineType.VENDOR_LINE)
                    ret = UPDATER_COMMANDS.INSERT_SUBSYSTEM;
            } else {
                ret = getCommand(line, to.getSubSystems().peek());
            }
        }


       return ret;
    }


    /* Handle nulls for command later */
    public static UPDATER_COMMANDS getCommand(VendorModel line, Vendor to, LineParser.LineType currLineType, LineParser.LineType nextLineType)
    throws LineOutOfOrderException
    {

        UPDATER_COMMANDS ret = UPDATER_COMMANDS.CONTINUE;
        if(line.getVen().getId() > to.getVendor()) {
            ret = UPDATER_COMMANDS.INSERT_VENDOR;
        }else if(line.getVen().getId().equals(to.getVendor())){
            /* Case 1 - check to see if there is nothing to write */
            if(to.size() == 0) ret = UPDATER_COMMANDS.POLL_VENDOR;
            else if(currLineType == LineParser.LineType.VENDOR_LINE){
                if(nextLineType == LineParser.LineType.VENDOR_LINE) ret = UPDATER_COMMANDS.INSERT_DEVICES; /* insert Device means poll vendor */
                else if(nextLineType == LineParser.LineType.DEVICE_LINE) ret = UPDATER_COMMANDS.CONTINUE;
            }else{
                ret = getCommand(line, to.getDevices().peek(), currLineType, nextLineType);
            }
        }
        return ret;
    }
    private static void runCommand(PriorityQueue<Vendor> pv, LineCountWriter writer, UPDATER_COMMANDS command) throws Exception{
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
    private static void writePendingVendors(PriorityQueue<Vendor> pv, LineCountWriter writer, LineParser.LineType currLine, LineParser.LineType nextLine) throws Exception{
        VendorModel vendorModel = LineParser.getInstance().getVendorModel();
        Vendor currVendor = pv.peek();
        Device currDevice;
        UPDATER_COMMANDS command = getCommand(vendorModel, currVendor, currLine, nextLine);

        while (currVendor != null  && isInsertCommand(command)) {
            runCommand(pv, writer, command);
            currVendor = pv.peek();
            command = getCommand(vendorModel, currVendor, currLine, nextLine);
        }
        if(!isInsertCommand(command)){
            runCommand(pv, writer, command);
        }

    }
    private static void writePendingClasses(PriorityQueue<Class> pc, LineCountWriter writer, LineParser.LineType line) throws Exception{
        int comp = CONTINUE;
        VendorModel vendorModel = LineParser.getInstance().getVendorModel();
        switch (line) {
            case CLASS_LINE:
                break;
            case SUB_CLASS_LINE:
                break;
            case PROG_IF_LINE:
                break;
        }
    }

    public static void update(PriorityQueue<Vendor> pv, PriorityQueue<Class> pc, BufferedReader reader, LineCountWriter writer) throws Exception
    {
        int lineNum = 1;
        String c, n;
        LineParser.LineType currLine, nextLine;
        c = reader.readLine();
        while( c != null && (n=reader.readLine()) != null){
            currLine = LineParser.getInstance().parseLine(c, lineNum);
            nextLine = LineParser.getInstance().getTypeLine(n).getKey();
            // Step 3 - write pending objects to pci.ids
            if(pv != null && pv.size() > 0 && LineParser.isVendorModel(currLine)){
                writePendingVendors(pv, writer, currLine, nextLine);
            }else if (pc != null && pc.size() > 0 && LineParser.isClassModel(currLine)){
                writePendingClasses(pc, writer, currLine);
            }else if(currLine == LineParser.LineType.INVALID_LINE){
                Logger.getInstance().println("line " + lineNum + ": invalid line, please correct before continuing");
                return;
            }
            writer.write(c + "\n");
            lineNum++;
            c = n;
        }

    }
}
