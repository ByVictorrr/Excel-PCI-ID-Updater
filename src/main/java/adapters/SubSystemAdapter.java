package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import models.Device;
import models.SubSystem;
import models.Vendor;

import java.io.IOException;

public class SubSystemAdapter extends TypeAdapter<SubSystem> {
    public static final String SUBSYSTEM_VENDOR_KEY = "subVendor";
    public static final String SUBSYSTEM_DEVICE_KEY = "subDevice";
    public static final String SUBSYSTEM_NAME_KEY = "name";

    @Override
    public SubSystem read(JsonReader reader)throws IOException
    {
        SubSystem s = new SubSystem();
        String fieldName = null;
        int vID, dID;
        String name;

        reader.beginObject();
        while(reader.hasNext()){
            JsonToken token = reader.peek();
            if(token.equals(JsonToken.NAME)){
                fieldName = reader.nextName();
            }
            if(SUBSYSTEM_VENDOR_KEY.equals(fieldName)){
                if((vID = Integer.parseInt(reader.nextString() ,16)) > 0xFFFF || vID < 0) {
                    System.err.println("Subsystem Vendor : " + Integer.toHexString(vID) + " is an invalid sub-vendor");
                    s = null;
                }else {
                    s.setSubVendor(vID);
                }
            }

            if(SUBSYSTEM_DEVICE_KEY.equals(fieldName)){
                if((dID = Integer.parseInt(reader.nextString() ,16)) > 0xFFFF || dID < 0) {
                    System.err.println("Subsystem Device : " + Integer.toHexString(dID) + " is an invalid sub-device");
                    s = null;
                }else {
                    s.setSubDevice(dID);
                }
            }

            if(SUBSYSTEM_NAME_KEY.equals(fieldName)){
                name = reader.nextString();
               if(s != null) s.setName(name);
            }
        }
        reader.endObject();
        return s;
    }


    @Override
    public void write(JsonWriter writer, SubSystem s) throws IOException{}


}
