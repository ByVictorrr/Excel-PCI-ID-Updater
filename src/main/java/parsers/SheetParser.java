package parsers;


import models.Device;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.net.URL;
import java.util.PriorityQueue;


public class SheetParser {
    private Sheet sheet;
    public static final Integer DESC = 2;
    public static final Integer IDS = 14;
    public SheetParser(String excelPathName, String sheetName){
        FileInputStream i;
        try {
            i = new FileInputStream(new File(excelPathName));
            Workbook wb = WorkbookFactory.create(i);
            this.sheet = wb.getSheet(sheetName);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public Sheet getSheet() {
        return sheet;
    }

    /**
     * @return a Queue sorted by pci_ids from the sheet_name (lineNumber not set yet)
     * @throws Exception
     */
    public PriorityQueue<Device> getSheetDevices(){

        PriorityQueue unSetDeviceList = new PriorityQueue();
        Device curr;
        // Step 1 - go through excel sheet and read each device with pci_ids and desc
        for (Row row: this.sheet){
            Cell ids = row.getCell(IDS);
            Cell desc = row.getCell(DESC);
            String sId;
            String sDesc;

            if (ids == null || desc == null)
                continue;
            sId = ids.getStringCellValue();
            sDesc = desc.getStringCellValue();
            curr = new Device(sDesc, sId);
            if (curr.isValid()) {
                unSetDeviceList.add(curr);
            }
        }

        return unSetDeviceList;
    }


}
