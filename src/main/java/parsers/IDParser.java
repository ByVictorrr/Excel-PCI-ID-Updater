package parsers;


import models.Device;
import models.PCI_ID;
import utilities.URLInputStream;

import java.io.BufferedReader;
import java.util.*;

public class IDParser {
    public static final int VEN =0;
    public static final int DEV =1;
    public static final int SVEN =2;
    public static final int SDEV =3;

    static public List<Long> getExcelIDS(String unparsed) {
        String []ids = unparsed.replaceAll("(h|0x)","").split(" ");
        List<Long> pIds = new ArrayList<>();
        try {
            for (String id : ids) {
                pIds.add(Long.parseLong(id, 16));
            }
            return pIds;
        }catch (Exception e){
            return null;
        }
    }

    static public Long getDatabaseID(Integer type, String line) {
        Long id = null;
        try {
            switch (type) {
                case VEN:
                    id = Long.parseLong(line.substring(0, 4), 16);
                    break;
                case DEV:
                    id = Long.parseLong(line.substring(1, 5), 16);
                    break;
                case SVEN:
                    id = Long.parseLong(line.substring(2, 6), 16);
                    break;
                case SDEV:
                    id = Long.parseLong(line.substring(7, 11), 16);
                    break;
                default:
                    System.out.println("Wrong type; No such found");
            }
        }catch (Exception e){
            return null;
        }
        return id;
    }
    /**
     * @param unSetDeviceList - unset linenumber devices
     * @param in - Used to compare where each device should be placed
     * @return returns a Queue with each device with a lineNumber
     * @throws Exception
     */
    public static Queue<Device> getSetLineNumberDevices(PriorityQueue<Device> unSetDeviceList, URLInputStream in) throws Exception{
        Queue<Device> setDeviceList = new LinkedList<>();
        Device curr;
        long lineNum=1;
        String line;
        long numTabs=0;
        Long id, id1;
        PCI_ID lineID = new PCI_ID();
        curr = (Device)unSetDeviceList.poll();
        PCI_ID currID = curr.getIds();

        BufferedReader reader = in.getReader();
        while((line=reader.readLine()) != null && !unSetDeviceList.isEmpty()){
            // Case 1 - comment (skip)
            if(line.equals("") || line.charAt(0) == '#'){
                lineNum++;
                continue;
            }
            // case 2 - count the number of tabs
            if((numTabs=countTabs(line)) == 0){
                // VENDOR ID
                id = IDParser.getDatabaseID(IDParser.VEN, line);
                if(id != null && id.equals(currID.getVen_id()))
                    lineID.setVen_id(id);
            }else if(numTabs == 1) {
                // DEVICE ID
                id = IDParser.getDatabaseID(IDParser.DEV, line);
                if(lineID.getVen_id().equals(currID.getVen_id()) && id != null && id.equals(currID.getDev_id()))
                    lineID.setDev_id(id);
                else if(lineID.getVen_id().equals(currID.getVen_id())  && lineID.getDev_id().equals(currID.getDev_id())){
                    curr.setLineNum(lineNum);
                    setDeviceList.add(curr);
                    curr=(Device)unSetDeviceList.poll();
                    currID = curr.getIds();
                }
            }else{
                // sub-system
                id = IDParser.getDatabaseID(IDParser.SVEN, line);
                id1 = IDParser.getDatabaseID(IDParser.SDEV, line);
                // Check to see if the entry exists
                if(lineID.getVen_id().equals(currID.getVen_id()) && lineID.getDev_id().equals(currID.getDev_id()) &&
                        id!=null &&  id1!= null
                ){
                    if (id.equals(currID.getSven_id()) && id1.equals(currID.getSdev_id())) {
                        System.out.println("Device " + currID.toString() + " Already Exists");
                        curr = (Device)unSetDeviceList.poll();
                        currID = curr.getIds();

                    }
                    else if ((id.longValue() > currID.getSven_id().longValue()) ||
                            (id.equals(currID.getSven_id()) && id1.longValue() > currID.getSdev_id().longValue())){
                        curr.setLineNum(lineNum);
                        // Add to the parsed list
                        setDeviceList.add(curr);
                        // poll the next from unparsed list
                        curr = (Device) unSetDeviceList.poll();
                        currID = curr.getIds();
                    }
                }

            }
            lineNum++;
        }

        return setDeviceList;
    }
    private static long countTabs(String s){
        return s.chars().filter(c->c=='\t').count();
    }

}
