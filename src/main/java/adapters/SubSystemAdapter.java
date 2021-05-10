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

@JsonAdapter(SubSystem.class)
public class SubSystemAdapter extends TypeAdapter<SubSystem> {
    public static final String SUBSYSTEM_VENDOR_KEY = "subVendor";
    public static final String SUBSYSTEM_DEVICE_KEY = "subDevice";
    public static final String SUBSYSTEM_NAME_KEY = "name";

    public SubSystem read(JsonReader reader)throws IOException
    {
        SubSystem s = new SubSystem();
        reader.beginObject();
        String fieldname = null;
        while(reader.hasNext()){
            JsonToken token = reader.peek();
            if(token.equals(JsonToken.NAME)){
                fieldname = reader.nextName();
            }
            if(SUBSYSTEM_VENDOR_KEY.equals(fieldname)){
                token = reader.peek();
                s.setSubVendor(Integer.parseInt(reader.nextString() ,16));
            }

            if(SUBSYSTEM_DEVICE_KEY.equals(fieldname)){
                token = reader.peek();
                s.setSubDevice(Integer.parseInt(reader.nextString() ,16));
            }

            if(SUBSYSTEM_NAME_KEY.equals(fieldname)){
                token = reader.peek();
                s.setName(reader.nextString());
            }
        }
        reader.endObject();
        return s;
    }


    @Override
    public void write(JsonWriter writer, SubSystem s) throws IOException{}


}
