package adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import models.Device;
import models.SubSystem;
import models.Vendor;
import utilities.Logger;

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
        String fieldName = null;
        int id;
        SubSystem s;
        String name;

        reader.beginObject();
        while(reader.hasNext()){
            JsonToken token = reader.peek();
            if(token.equals(JsonToken.NAME)){
                fieldName = reader.nextName();
            }
            if(DEVICE_ID_KEY.equals(fieldName)){
                if((id=Integer.parseInt(reader.nextString(), 16)) > 0xffff || id < 0){
                    Logger.getInstance().println("Device : " + Integer.toHexString(id) + " is an invalid device");
                    d = null;
                }else {
                    d.setDevice(id);
                }
            } else if(DEVICE_NAME_KEY.equals(fieldName)){
                if(reader.peek() == JsonToken.STRING) {
                    name = reader.nextString();
                    if (d != null) d.setName(name);
                }else if(reader.peek() == JsonToken.NULL){
                    reader.nextNull();
                }
            }else if(DEVICE_SUBSYSTEMS_KEY.equals(fieldName)){
                if(reader.peek() == JsonToken.BEGIN_ARRAY) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        TypeAdapter<SubSystem> subSystemTypeAdapter = new Gson().getAdapter(SubSystem.class);
                        if (d != null && d.getSubSystems() == null) d.setSubSystems(new PriorityQueue<>());
                        if ((s = subSystemTypeAdapter.read(reader)) != null && d != null) d.addSubSystem(s);
                    }
                    reader.endArray();
                }else if(reader.peek() == JsonToken.NULL){
                    reader.nextNull();
                }

            }
        }
        reader.endObject();
        return d;
    }



}
