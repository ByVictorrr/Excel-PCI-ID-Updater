import adapters.VendorAdapter;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import models.Device;
import models.SubSystem;
import models.Vendor;
import parsers.LineParser;
import picocli.CommandLine;
import com.google.gson.Gson;
import utilities.LineCountWriter;

import javax.sound.sampled.Line;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static parsers.LineParser.TYPES_LINES.*;
import static parsers.LineParser.VendorModel.*;


@CommandLine.Command(name="piu", version="piu 1.0", mixinStandardHelpOptions = true)
public class PIU implements Runnable{

    @CommandLine.Parameters(index = "0", description = "This is the input pci.ids file")
    private File inputPCIidFile;

    @CommandLine.Parameters(index = "1", description = "This is the output pci.ids file")
    private File outputPCIidFile;

    @CommandLine.Option(names={"-v"}, description = "Override the vendor names of the pending entries")
    private boolean overrideVendors = false;

    @CommandLine.Option(names={"-d"}, description = "Override the device names of the pending entries")
    private boolean overrideDevices = false;

    @CommandLine.Option(names={"-s"}, description = "Override the subsystem names of the pending entries")
    private boolean overrideSubSystems = false;
    @CommandLine.Option(names={"-l", "--log"}, arity="1", description = "The file names to write changes to")
    private File logFile = null;

    @CommandLine.ArgGroup(exclusive = true, multiplicity = "1")
    private Exclusive exclusive;

    static class Exclusive{
        @CommandLine.Option(names={"-f", "--file"}, required = true, description="Json formatted pci id entries")
        private File piuJSON = null;
        @CommandLine.Option(names={"-e", "--entry"}, required = true, description="single entry <ven:vname:dev:dname:sv:sd:sname>")
        private String piuEntry = null;
    }


    @Override
    public void run() {
        try {

            // Step 0 - open both files to be read and writen to/from
            BufferedReader in = new BufferedReader(new FileReader(inputPCIidFile));
            LineCountWriter out = new LineCountWriter(new FileWriter(outputPCIidFile));
            // new OutputStreamWriter(System.out)
            //BufferedWriter log = logFile == null ? null : new BufferedWriter(logFile);

            if(exclusive.piuEntry != null){
                // Case 1 - if no piu json input

            }else {

                //Gson gson = new GsonBuilder().serializeNulls().create();
                PriorityQueue<Vendor>pendingVendors = new Gson().fromJson(new FileReader(exclusive.piuJSON),
                                                      new TypeToken<PriorityQueue<Vendor>>(){}.getType());

                // Step 2 - read each line
                update(pendingVendors, in, out);
                in.close();
                out.close();

            }

        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
        }


    }
    // Guarenteed at least on que
    private void writePending(PriorityQueue<Vendor> pv, LineCountWriter writer, String line) throws Exception{
        // line parser is a singleton there it doesnt change
        LineParser lineParser = LineParser.getInstance();
        switch (lineParser.getType(line)) {
            case VENDOR_LINE: {
                Vendor currPending;
                int comp = CONTINUE;
                LineParser.VendorModel vendorModel = LineParser.getInstance().getModel();
                // While pv isnt empty and we can write
                while (pv.size() > 0  && (comp = vendorModel.compareTo(currPending = pv.peek())) == WRITE) {
                    // Write the whole vendor
                    writer.write(currPending.toString() + "\n");
                    // get rid of the vendor (pv.size() = pv.size() -1)
                    pv.poll();
                    System.out.println("new output at line " + writer.getLineNumber() + ": " + currPending);
                }
                if(pv.size() > 0 && comp == EQUAL && overrideVendors) {
                    writer.write(pv.poll().toLine() + "\n");
                    return; // dont write line vendor
                }
            }
            break;
            case DEVICE_LINE: {
                LineParser.VendorModel vendorModel = lineParser.getModel();
                int comp = 1;
                Vendor currVendor = pv.peek();

                while (currVendor.size() > 0 && vendorModel.compareTo(currVendor) == WRITE) {
                    writer.write(currVendor.getDevices().poll().toString());
                    // Check if this vendor is empty after getting rid of device
                    if(currVendor.size() == 0){
                        pv.poll();
                    }
                }

                // Case 1 - no more devices, thus vendor has a size() = 0
                if( currVendor.size() > 0 && comp == EQUAL && overrideDevices) {
                    writer.write(currVendor.getDevices().poll().toLine() + "\n");
                    if(currVendor.size() == 0)
                        pv.poll();
                    return; // dont write next line
                }
            }
            break;
            case SUB_LINE: {
                LineParser.VendorModel vendorModel = lineParser.getModel();
                Vendor currVendor = pv.peek(); // guarenteed

                int comp = LineParser.VendorModel.CONTINUE;
                int overRideSub = CONTINUE;
                while( currVendor.size() > 0 && currVendor.getDevices().peek().size() > 0 && (comp = vendorModel.compareTo(currVendor)) == LineParser.VendorModel.WRITE) {
                    writer.write(currVendor.getDevices().peek().getSubSystems().poll().toString() + "\n");
                    if (currVendor.getDevices().peek().getSubSystems().size() == 0) {
                        currVendor.getDevices().poll();
                    }
                    if (currVendor.size() == 0) {
                        pv.poll();
                    }
                }
                // Case 1 - no more devices, thus vendor has a size() = 0
                if ( currVendor.size() > 0 && currVendor.getDevices().peek().size() > 0 && comp == EQUAL && overrideSubSystems) {
                    writer.write(currVendor.getDevices().poll().toLine() + "\n");
                    if (currVendor.getDevices().peek().getSubSystems().size() == 0) {
                        currVendor.getDevices().poll();
                    }
                    if (currVendor.size() == 0) {
                        pv.poll();
                    }
                    return; // dont write next line
                }


            }
            break;
            /*
            case CLASS_LINE:
            case SUB_CLASS_LINE:
            case PROG_IF_LINE:

             */

            break;
            case COMMENT_LINE: {
                System.out.println("comment line: " + line);
            }
            break;
            case BLANK_LINE: {
                System.out.println("blank line: " + line);
            }
            break;

            case INVALID_LINE: {
                System.out.println("invalid line: " + line);
            }

        }
        writer.write(line + "\n");


}


    private void update(PriorityQueue<Vendor> pv, BufferedReader reader, LineCountWriter writer) throws Exception
    {
        LineParser lineParser = LineParser.getInstance();


        for(String line: reader.lines().collect(Collectors.toList())) {

            if (pv.isEmpty()) {
                writer.write(line + "\n");
            } else {
                // assumed pv isnt empty
                writePending(pv, writer, line);
            }

        }

    }
    public static void main(String [] args){

        int exitCode = new CommandLine(new PIU()).execute(args);
        System.exit(exitCode);
    }




}
