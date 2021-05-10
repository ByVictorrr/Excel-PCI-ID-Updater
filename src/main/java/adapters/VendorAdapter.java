package adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import models.Device;
import models.Vendor;

import java.io.IOException;
import java.util.PriorityQueue;

public class VendorAdapter extends TypeAdapter<Vendor> {
    public static final String VENDOR_ID_KEY = "vendor";
    public static final String VENDOR_NAME_KEY = "name";
    public static final String VENDOR_DEVICES_KEY = "devices";


    @Override
    public Vendor read(JsonReader reader)throws IOException
    {
            Vendor v = new Vendor();
            reader.beginObject();
            String fieldname = null;
            while(reader.hasNext()){
                JsonToken token = reader.peek();
                if(token.equals(JsonToken.NAME)){
                    fieldname = reader.nextName();
                }
                if(VENDOR_ID_KEY.equals(fieldname)){
                    token = reader.peek();
                    v.setVendor(Integer.parseInt(reader.nextString() ,16));
                }

                if(VENDOR_NAME_KEY.equals(fieldname)){
                    token = reader.peek();
                    v.setName(reader.nextString());
                }
                if(VENDOR_DEVICES_KEY.equals(fieldname)){
                    token = reader.peek();
                    reader.beginArray();
                    while(reader.hasNext()){
                        JsonToken dt = reader.peek();
                        TypeAdapter<Device> deviceTypeAdapter = new Gson().getAdapter(Device.class);
                        // case 1 - first device
                        if(v.getDevices() == null){
                           v.setDevices(new PriorityQueue<>());
                        }
                        v.addDevice(deviceTypeAdapter.read(reader));

                    }
                    reader.endArray();
                }
            }

        reader.endObject();
        return v;
    }
    @Override
    public void write(JsonWriter writer, Vendor v) throws IOException
    {

    }

}
