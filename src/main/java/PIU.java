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
import utilities.Updater;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static parsers.models.VendorModel.*;
import static utilities.Updater.update;


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






    @Override
    public void run() {

        try {
            BufferedReader in = new BufferedReader(new FileReader(inputPCIidFile));
            LineCountWriter out = new LineCountWriter(new FileWriter(outputPCIidFile));
            //Logger.getInstance().init(new FileOutputStream("C:\\Users\\delaplai\\Excel-PCI-ID-Updater\\src\\main\\tests\\logs"));

            PriorityQueue<Vendor> pendingVendors = buildPendingVendors();
            PriorityQueue<Class> pendingClasses = buildPendingClasses();


            Updater.update(pendingVendors, pendingClasses, in, out);

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
