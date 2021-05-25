import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import models.Class;
import models.Device;
import models.SubSystem;
import models.Vendor;
import parsers.LineParser;
import parsers.models.VendorModel;
import picocli.CommandLine;
import com.google.gson.Gson;
import utilities.LineCountWriter;
import utilities.Logger;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static parsers.models.VendorModel.*;


@CommandLine.Command(name="piu", version="piu 1.0", mixinStandardHelpOptions = true)
public class PIU implements Runnable{

    @CommandLine.Parameters(index = "0", description = "This is the input pci.ids file")
    private File inputPCIidFile;

    @CommandLine.Parameters(index = "1", description = "This is the output pci.ids file")
    private File outputPCIidFile;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1..*")
    private UpdateOptions options;



    static class UpdateOptions{
        @CommandLine.Option(names={"-p", "--pci"}, description="Json formatted pci id entries")
        private File vendorJSON = null;
        @CommandLine.Option(names={"-e", "--entry-pci"}, description="single entry <ven:vname:dev:dname:sv:sd:sname>")
        private String vendorEntry = null;
        @CommandLine.Option(names={"-c", "--class"}, description="Json formatted pci id entries")
        private File classJSON = null;
        @CommandLine.Option(names={"-i", "--single-class"},  description="single entry <class:className:subclass:subclassName:prog-IF:prog-IF-name>")
        private String classEntry = null;
    }

    private PriorityQueue<Vendor> buildPendingVendors() throws Exception{
        PriorityQueue<Vendor> pv = null;
        if(options.vendorEntry != null && options.vendorJSON != null){
            pv = new GsonBuilder().setLenient().create().fromJson(
                    new FileReader(options.vendorJSON),
                    new TypeToken<PriorityQueue<Vendor>>(){}.getType()
            );
            pv.add(new Vendor()); // parse here
        }else if(options.vendorJSON != null){
            pv = new GsonBuilder().setLenient().create().fromJson(
                    new FileReader(options.vendorJSON),
                    new TypeToken<PriorityQueue<Vendor>>(){}.getType()
            );
        }else if(options.vendorEntry != null){
            pv = new PriorityQueue<>();
            pv.add(new Vendor()); // parse here
        }

        return pv;
    }
    private PriorityQueue<Class> buildPendingClasses() throws Exception{
        PriorityQueue<Class> pc = null;
        if(options.classEntry != null && options.classJSON != null){
            pc = new GsonBuilder().setLenient().create().fromJson(
                    new FileReader(options.classJSON),
                    new TypeToken<PriorityQueue<Class>>(){}.getType()
            );
            pc.add(new Class()); // parse after
        }else if(options.classJSON != null){
            pc = new GsonBuilder().setLenient().create().fromJson(
                    new FileReader(options.classJSON),
                    new TypeToken<PriorityQueue<Class>>(){}.getType()
            );
        }else if(options.classEntry != null){
            pc = new PriorityQueue<>();
            pc.add(new Class());

        }
        return pc;
    }





    /* Assumed to have at least one vendor in the queue*/
    private void writePendingVendors(PriorityQueue<Vendor> pv, LineCountWriter writer, LineParser.LineType line) throws Exception{
        int comp = CONTINUE;
        VendorModel vendorModel = LineParser.getInstance().getVendorModel();
        Vendor currVendor = pv.peek();
        switch (line){
            case VENDOR_LINE:
                // check to see if we have contigious vendors to write
                while (currVendor != null  && (comp = vendorModel.compareTo(currVendor)) == WRITE) {
                    writer.write(currVendor.toString() + "\n");
                    Logger.getInstance().println("new output at line " + writer.getLineNumber() + ": " + currVendor);
                    pv.poll();
                    currVendor = pv.peek();
                }
                if(comp == EQUAL) pv.poll();


            break;
            case DEVICE_LINE:
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

            break;
            case SUBSYSTEM_LINE:
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
    private void writePendingClasses(PriorityQueue<Class> pc, LineCountWriter writer, LineParser.LineType line) throws Exception{
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

    private void update(PriorityQueue<Vendor> pv, PriorityQueue<Class> pc, BufferedReader reader, LineCountWriter writer) throws Exception
    {
        int lineNum = 1;
        LineParser.LineType lineType;
        for(String line: reader.lines().collect(Collectors.toList())) {

            // Step 2 - parse that line
            lineType=LineParser.getInstance().parseLine(line, lineNum);
            // Step 3 - write pending objects to pci.ids
            if(pv != null && pv.size() > 0 && LineParser.isVendorModel(lineType)){
                writePendingVendors(pv, writer, lineType);
            }else if (pc != null && pc.size() > 0 && LineParser.isClassModel(lineType)){
                writePendingClasses(pc, writer, lineType);
            }else if(lineType == LineParser.LineType.INVALID_LINE){
                Logger.getInstance().println("line " + lineNum + ": invalid line, please correct before continuing");
                return;
            }
            writer.write(line + "\n");
            lineNum++;

        }

    }
    @Override
    public void run() {

        // This function builds our class and vendor models

        try {
            BufferedReader in = new BufferedReader(new FileReader(inputPCIidFile));
            LineCountWriter out = new LineCountWriter(new FileWriter(outputPCIidFile));
            //Logger.getInstance().init(new FileOutputStream("C:\\Users\\delaplai\\Excel-PCI-ID-Updater\\src\\main\\tests\\logs"));

            PriorityQueue<Vendor> pendingVendors = buildPendingVendors();
            PriorityQueue<Class> pendingClasses = buildPendingClasses();


            update(pendingVendors, pendingClasses, in, out);

            out.close();
            in.close();
        }catch (Exception e){
            e.printStackTrace();
            Logger.getInstance().println(e);
        }

    }



    public static void main(String [] args){

        int exitCode = new CommandLine(new PIU()).execute(args);
        System.exit(exitCode);
    }




}
