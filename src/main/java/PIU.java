import adapters.ConnectionAdapter;
import models.Device;
import org.graalvm.compiler.options.Option;
import parsers.IDParser;
import parsers.SheetParser;
import picocli.CommandLine;
import utilities.*;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static adapters.ConnectionAdapter.*;


@CommandLine.Command(name="piu", version="piu 1.0", mixinStandardHelpOptions = true)
public class PIU implements Runnable{

            //"\t-f <json-file> : input multiple pci-id models\n" +
            //"\t-s <vendor_id:vendor_name:device_id:device_name:svendor:sdevice:subsystem_name>: add single model";
    @Option(names={"-f", "--file"}, description="Json formatted pci id entries")
    File piuList = null;
    @Option(names={"-s", "--entry"}, description="single entry <ven:vname:dev:dname:sv:sd:sname>")
    String piuEntry;

    @Override
    public void run() {
        // The business logic of the command goes here...
        // In this case, code for generation of ASCII art graphics
        // (omitted for the sake of brevity).

        PriorityQueue<Device> unSetDeviceList;
        Queue<Device> setDeviceList;
        String pciID_srcPath,pciID_destPath, srcCL_Path, destCL_Path;
        int latestVersion;
        try {

            // Step 2 - Get a sheet parser object (excel reader)
            SheetParser sheetParser = new SheetParser("hi", "hi");
            //SheetParser sheetParser = new SheetParser(args[0], PCI_EXCEL_SHEET);

            // Step 3 - make new directory and create urls
            URLStream urlStream = new URLStream();
            latestVersion = urlStream.findLatestVersion(PCI_ROOT_DIR);
            urlStream.getClient().makeDirectory(PCI_ROOT_DIR+"/v"+(latestVersion+1));

            pciID_srcPath = PCI_ROOT_DIR+"/pci.idds";
            pciID_destPath= PCI_ROOT_DIR + "/v"+(latestVersion+1)+"/pci.ids";
            destCL_Path = PCI_ROOT_DIR + "/v"+(latestVersion+1)+"/CHANGELOG";
            srcCL_Path = PCI_ROOT_DIR+"/v"+(latestVersion)+"/CHANGELOG";

            // Step 4 - open the connection

            unSetDeviceList = sheetParser.getSheetDevices();
            // Step 4 - go through the unsetDeviceList and find the line number given the pci_id_file
            //setDeviceList = IDParser.getSetLineNumberDevices(unSetDeviceList, urlInputStream);
            // Step 6 - go and merge the setDeviceList and pci.ids file together
            // Step 7 - copy from file from new dir to root
            URLUtils.copy(pciID_destPath, pciID_srcPath);
            // Step 8 - read log
            //Logger.createChangeLog((urlInputStream=new URLInputStream(srcCL_Path)), (urlOutputStream=new URLOutputStream(destCL_Path)));
            // Step 5 - close url streams


        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void main(String [] args){

        int exitCode = new CommandLine(new PIU()).execute(args);

    }




}
