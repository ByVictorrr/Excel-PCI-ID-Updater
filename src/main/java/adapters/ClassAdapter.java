package adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import models.Class;
import models.Device;
import models.SubClass;
import models.Vendor;
import utilities.Logger;

import java.io.IOException;
import java.util.PriorityQueue;

public class ClassAdapter extends TypeAdapter<Class> {
    public static final String CLASS_ID_KEY = "class";
    public static final String CLASS_NAME_KEY = "name";
    public static final String CLASS_SUBCLASS_KEY = "subClasses";
    @Override
    public Class read(JsonReader reader)throws IOException
    {
        Class c = new Class();
        String fieldName = null;
        SubClass sc;
        int id;
        String name;

        reader.beginObject();
        while(reader.hasNext()){
            JsonToken token = reader.peek();
            if(token.equals(JsonToken.NAME)) fieldName = reader.nextName();

            if(CLASS_ID_KEY.equals(fieldName)){
                if((id=Integer.parseInt(reader.nextString(),16)) > 0xff || id < 0){
                    Logger.getInstance().println(" : " + Integer.toHexString(id) + " is an invalid vendor");
                    c=null;
                }else {
                    c.set_class(id);
                }

            }else if(CLASS_NAME_KEY.equals(fieldName)){
                if(reader.peek() == JsonToken.STRING) {
                    name = reader.nextString();
                    if (c != null) c.setName(name);
                }else if(reader.peek() == JsonToken.NULL){
                    reader.nextNull();
                }
            }else if(CLASS_SUBCLASS_KEY.equals(fieldName)){
                if(reader.peek() == JsonToken.BEGIN_ARRAY) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        TypeAdapter<SubClass> subClassTypeAdapter = new Gson().getAdapter(SubClass.class);
                        if (c != null && c.getSubClasses() == null) c.setSubClasses(new PriorityQueue<>());
                        if ((sc = subClassTypeAdapter.read(reader)) != null && sc != null) c.addSubClass(sc);
                    }
                    reader.endArray();
                }else if(reader.peek() == JsonToken.NULL){
                    reader.nextNull();
                }
            }
        }

        reader.endObject();
        return c;
    }
    @Override
    public void write(JsonWriter writer, Class c) throws IOException
    {

    }
}
