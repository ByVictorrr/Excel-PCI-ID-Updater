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
        String fieldName = null;
        Device d;
        int id;
        String name;

        reader.beginObject();
        while(reader.hasNext()){
                JsonToken token = reader.peek();
                if(token.equals(JsonToken.NAME)) fieldName = reader.nextName();

                if(VENDOR_ID_KEY.equals(fieldName)){
                    if((id=Integer.parseInt(reader.nextString(),16)) > 0xffff || id < 0){
                        System.err.println("Vendor : " + Integer.toHexString(id) + " is an invalid vendor");
                        v=null;
                    }else {
                        v.setVendor(id);
                    }
                }

                if(VENDOR_NAME_KEY.equals(fieldName)){
                    name = reader.nextString();
                    if(v != null) v.setName(name);
                }
                if(VENDOR_DEVICES_KEY.equals(fieldName)){
                    reader.beginArray();
                    while(reader.hasNext()){
                        TypeAdapter<Device> deviceTypeAdapter = new Gson().getAdapter(Device.class);
                        if(v != null && v.getDevices() == null) v.setDevices(new PriorityQueue<>());
                        if((d = deviceTypeAdapter.read(reader)) != null && v!=null) v.addDevice(d);
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
