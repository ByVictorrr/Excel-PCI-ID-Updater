package adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import models.Device;
import models.SubSystem;
import models.Vendor;

import java.io.IOException;
import java.util.PriorityQueue;

public class DeviceAdapter extends TypeAdapter<Device> {
    public static final String DEVICE_ID_KEY = "device";
    public static final String DEVICE_NAME_KEY = "name";
    public static final String DEVICE_SUBSYSTEMS_KEY = "subSystems";
    @Override
    public void write(JsonWriter writer, Device d) throws IOException {}
    public Device read(JsonReader reader)throws IOException
    {
        Device d = new Device();
        reader.beginObject();
        String fieldname = null;
        int id;
        SubSystem s;
        while(reader.hasNext()){
            JsonToken token = reader.peek();
            if(token.equals(JsonToken.NAME)){
                fieldname = reader.nextName();
            }
            if(DEVICE_ID_KEY.equals(fieldname)){
                reader.peek();
                if((id=Integer.parseInt(reader.nextString(), 16)) > 0xffff || id < 0){
                   System.out.println("Incorrect id");
                   d = null;
                }else {
                    d.setDevice(id);
                }
            }

            if(DEVICE_NAME_KEY.equals(fieldname)){
                reader.peek();
                if(d != null)
                    d.setName(reader.nextString());
            }
            if(DEVICE_SUBSYSTEMS_KEY.equals(fieldname)){
                reader.peek();
                reader.beginArray();
                while(reader.hasNext()){
                    JsonToken dt = reader.peek();
                    TypeAdapter<SubSystem> subSystemTypeAdapter = new Gson().getAdapter(SubSystem.class);
                    // case 1 - first device
                    if(d != null && d.getSubSystems() == null){
                        d.setSubSystems(new PriorityQueue<>());
                    }
                    if((s=subSystemTypeAdapter.read(reader)) != null && d != null){
                        d.addSubSystem(s);
                    }
                }
                reader.endArray();
            }
        }
        reader.endObject();
        return d;
    }



}
