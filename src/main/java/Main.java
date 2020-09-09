import adapters.ConnectionAdapter;
import models.Device;
import parsers.IDParser;
import parsers.SheetParser;
import utilities.*;

import java.nio.file.Path;
import java.util.*;

import static adapters.ConnectionAdapter.*;


public class Main {

    private final static String USAGE = "Usage: xpciup [EXCEL FILE]";
    public static void main(String [] args){

        PriorityQueue<Device> unSetDeviceList;
        Queue<Device> setDeviceList;
        URLOutputStream urlOutputStream;
        URLInputStream urlInputStream;
        // Step 1 - Parse arguments
        if(args.length != 1){
            System.err.println(USAGE);
            return;
        }else if (args[0].equals("--help")){
            System.out.println(USAGE);
            return;
        }

        String pciID_srcPath,pciID_destPath, srcCL_Path, destCL_Path;
        int latestVersion;
        try {

            // Step 2 - Get a sheet parser object (excel reader)
            SheetParser sheetParser = new SheetParser(args[0], PCI_EXCEL_SHEET);

            // Step 3 - make new directory and create urls
            URLStream urlStream = new URLStream();
            latestVersion = urlStream.findLatestVersion(PCI_ROOT_DIR);
            urlStream.getClient().makeDirectory(PCI_ROOT_DIR+"/v"+(latestVersion+1));

            pciID_srcPath = PCI_ROOT_DIR+"/pci.ids";
            pciID_destPath= PCI_ROOT_DIR + "/v"+(latestVersion+1)+"/pci.ids";
            destCL_Path = PCI_ROOT_DIR + "/v"+(latestVersion+1)+"/CHANGELOG";
            srcCL_Path = PCI_ROOT_DIR+"/v"+(latestVersion)+"/CHANGELOG";

            // Step 4 - open the connection
            urlOutputStream = new URLOutputStream(pciID_destPath);
            urlInputStream = new URLInputStream(pciID_srcPath);

            unSetDeviceList = sheetParser.getSheetDevices();
            // Step 4 - go through the unsetDeviceList and find the line number given the pci_id_file
            setDeviceList = IDParser.getSetLineNumberDevices(unSetDeviceList, urlInputStream);
            urlInputStream.close();
           // Step 6 - go and merge the setDeviceList and pci.ids file together
            urlOutputStream.write((urlInputStream=new URLInputStream(pciID_srcPath)), setDeviceList);
            urlInputStream.close();
            urlOutputStream.close();
           // Step 7 - copy from file from new dir to root
            URLUtils.copy(pciID_destPath, pciID_srcPath);
            // Step 8 - read log
            Logger.createChangeLog((urlInputStream=new URLInputStream(srcCL_Path)), (urlOutputStream=new URLOutputStream(destCL_Path)));
            // Step 5 - close url streams
            urlInputStream.close();
            urlOutputStream.close();


        }catch (Exception e){
            e.printStackTrace();
        }

    }




}
