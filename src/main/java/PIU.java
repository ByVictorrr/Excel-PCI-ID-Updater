import com.google.gson.reflect.TypeToken;
import models.Classs;
import models.Vendor;
import parsers.LineParser;
import parsers.models.VendorModel;
import picocli.CommandLine;
import com.google.gson.Gson;
import utilities.LineCountWriter;

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

    @CommandLine.ArgGroup(exclusive = true, multiplicity = "1")
    private ExclusiveVendor exclusiveVendor;
    /*
    @CommandLine.ArgGroup( , multiplicity = "0")
    private ExclusiveClass exclusiveClass;

     */
    private PrintStream logger = System.err;


    static class ExclusiveVendor{
        @CommandLine.Option(names={"-p", "--pci"}, required = true, description="Json formatted pci id entries")
        private File piuJSON = null;
        @CommandLine.Option(names={"-e", "--entry-pci"}, required = true, description="single entry <ven:vname:dev:dname:sv:sd:sname>")
        private String piuEntry = null;
    }
    static class ExclusiveClass{
        @CommandLine.Option(names={"-c", "--class"}, required = true, description="Json formatted pci id entries")
        private File piuJSON = null;
        @CommandLine.Option(names={"-i", "--single-class"}, required = true, description="single entry <class:className:subclass:subclassName:prog-IF:prog-IF-name>")
        private String piuEntry = null;
    }

    private PriorityQueue<Vendor> buildPendingVendors() throws Exception{
        PriorityQueue<Vendor> pv = null;
        if(exclusiveVendor.piuEntry != null && exclusiveVendor.piuJSON != null){
            pv = new Gson().fromJson(
                    new FileReader(exclusiveVendor.piuJSON), new TypeToken<PriorityQueue<Vendor>>(){}.getType()
            );
            pv.add(new Vendor()); // parse here
        }else if(exclusiveVendor.piuJSON != null){
            pv = new Gson().fromJson(
                new FileReader(exclusiveVendor.piuJSON), new TypeToken<PriorityQueue<Vendor>>(){}.getType()
            );
        }else if(exclusiveVendor.piuEntry != null){
            pv = new PriorityQueue<>();
            pv.add(new Vendor()); // parse here
        }

        return pv;
    }
    private PriorityQueue<Classs> buildPendingClasses() throws Exception{
        PriorityQueue<Classs> pc = null;
        return pc;
    }





    /* Assumed to have at least one vendor in the queue*/
    private void writePendingVendors(PriorityQueue<Vendor> pv, LineCountWriter writer, LineParser.LineType line) throws Exception{
        int comp = CONTINUE;
        VendorModel vendorModel = LineParser.getInstance().getVendorModel();
        Vendor currVendor = pv.peek();
        switch (line){
            case VENDOR_LINE: {
                // check to see if we have contigious vendors to write
                while (pv.size() > 0  && (comp = vendorModel.compareTo(currVendor)) == WRITE) {
                    writer.write(currVendor.toString() + "\n");
                    logger.println("new output at line " + writer.getLineNumber() + ": " + currVendor);
                    pv.poll();
                    currVendor = pv.peek();
                }

            }
            break;
            case DEVICE_LINE:
                while (currVendor.size() > 0 && (comp=vendorModel.compareTo(currVendor)) == WRITE) {
                    writer.write(currVendor.getDevices().poll().toString());
                    // Check if this vendor is empty after getting rid of device
                    if(currVendor.size() == 0){
                        pv.poll();
                    }
                }

            break;
            case SUBSYSTEM_LINE:

                while( currVendor.size() > 0 && currVendor.getDevices().peek().size() > 0 && (comp = vendorModel.compareTo(currVendor)) == WRITE) {
                    writer.write(currVendor.getDevices().peek().getSubSystems().poll().toString() + "\n");
                    if (currVendor.getDevices().peek().getSubSystems().size() == 0) {
                        currVendor.getDevices().poll();
                    }
                    if (currVendor.size() == 0) {
                        pv.poll();
                    }
                }
        }
    }
    private void writePendingClasses(PriorityQueue<Classs> pc, LineCountWriter writer, LineParser.LineType line) throws Exception{
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

    private void update(PriorityQueue<Vendor> pv, PriorityQueue<Classs> pc, BufferedReader reader, LineCountWriter writer) throws Exception
    {
        int lineNum = 0;
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
                System.err.println("line " + lineNum + ": invalid line, please correct before continuing");
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

            PriorityQueue<Vendor> pendingVendors = buildPendingVendors();
            PriorityQueue<Classs> pendingClassses = buildPendingClasses();

            update(pendingVendors, pendingClassses, in, out);

            out.close();
            in.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
        }

    }



    public static void main(String [] args){

        int exitCode = new CommandLine(new PIU()).execute(args);
        System.exit(exitCode);
    }




}
