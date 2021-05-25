package utilities;

import models.Class;
import models.Device;
import models.SubSystem;
import models.Vendor;
import parsers.LineParser;
import parsers.models.VendorModel;

import java.io.BufferedReader;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import static parsers.models.VendorModel.*;
import static parsers.models.VendorModel.CONTINUE;

public class Updater {

    private static void writeVendor(Vendor vendor, LineCountWriter writer){
        VendorModel vendorModel = LineParser.getInstance().getVendorModel();


    }

    /* Assumed to have at least one vendor in the queue*/
    private static void writePendingVendors(PriorityQueue<Vendor> pv, LineCountWriter writer, LineParser.LineType currLine, LineParser.LineType nextLine) throws Exception{
        int comp = CONTINUE;
        VendorModel vendorModel = LineParser.getInstance().getVendorModel();
        Vendor currVendor = pv.peek();
        if(currLine == LineParser.LineType.VENDOR_LINE){
            while (currVendor != null  && (comp = vendorModel.compareTo(currVendor)) == WRITE) {
                writer.write(currVendor.toString() + "\n");
                Logger.getInstance().println("new output at line " + writer.getLineNumber() + ": " + currVendor);
                pv.poll();
                currVendor = pv.peek();
            }
            //if(comp == EQUAL &&) pv.poll();
            if(comp == EQUAL && pv.peek().size() > 0 && vendorModel.getDev() == null){

            }

        }else if(currLine == LineParser.LineType.DEVICE_LINE){

            Device currDevice;
            while (currVendor.size() > 0 && (comp=vendorModel.compareTo(currVendor)) == WRITE) {
                currDevice = currVendor.getDevices().poll();
                writer.write(currDevice.toString() + "\n");
                Logger.getInstance().println("new output at line " + writer.getLineNumber() + ": " + currDevice);
                if(currVendor.size() == 0) pv.poll();
            }
            if(comp == EQUAL){
                pv.peek().getDevices().poll();
                if(currVendor.size() == 0) pv.poll();
            }


        }else if(currLine == LineParser.LineType.SUBSYSTEM_LINE){
            SubSystem currSub;
            while( currVendor.size() > 0 && currVendor.getDevices().peek().size() > 0 && (comp = vendorModel.compareTo(currVendor)) == WRITE) {
                currSub = currVendor.getDevices().peek().getSubSystems().poll();
                writer.write(currSub.toString() + "\n");
                Logger.getInstance().println("new output at line " + writer.getLineNumber() + ": " + currSub);
                if (currVendor.getDevices().peek().getSubSystems().size() == 0) currVendor.getDevices().poll();
                if (currVendor.size() == 0) pv.poll();
            }
            if(comp == EQUAL){
                pv.peek().getDevices().peek().getSubSystems().poll();
                if (currVendor.getDevices().peek().getSubSystems().size() == 0) currVendor.getDevices().poll();
                if (currVendor.size() == 0) pv.poll();
            }

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
                writePendingClasses(pc, writer, currLine, nextLine);
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
