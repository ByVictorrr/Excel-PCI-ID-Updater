package PIU.adapters;

import PIU.utilities.UniquePriorityQueue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import PIU.models.ProgIF;
import PIU.models.SubClass;
import PIU.utilities.Logger;

import java.io.IOException;
import java.util.PriorityQueue;

public class SubClassAdapter extends TypeAdapter<SubClass>{
    public static final String SUBCLASS_ID_KEY = "subClass";
    public static final String SUBCLASS_NAME_KEY = "name";
    public static final String SUBCLASS_PROGIF_KEY = "progIFs";
    @Override
    public SubClass read(JsonReader reader)throws IOException
    {
        SubClass sc = new SubClass();
        String fieldName = null;
        ProgIF progIF;
        int id;
        String name;

        reader.beginObject();
        while(reader.hasNext()){
            JsonToken token = reader.peek();
            if(token.equals(JsonToken.NAME)) fieldName = reader.nextName();

            if(SUBCLASS_ID_KEY.equals(fieldName)){
                if((id=Integer.parseInt(reader.nextString(),16)) > 0xff || id < 0){
                    Logger.getInstance().println(" : " + Integer.toHexString(id) + " is an invalid vendor");
                    sc=null;
                }else {
                    sc.setSubClass(id);
                }

            }else if(SUBCLASS_NAME_KEY.equals(fieldName)){
                if(reader.peek() == JsonToken.STRING) {
                    name = reader.nextString();
                    if (sc != null) sc.setName(name);
                }else if(reader.peek() == JsonToken.NULL){
                    reader.nextNull();
                }
            }else if(SUBCLASS_PROGIF_KEY.equals(fieldName)){
                if(reader.peek() == JsonToken.BEGIN_ARRAY) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        TypeAdapter<ProgIF> progIFTypeAdapter = new Gson().getAdapter(ProgIF.class);
                        if (sc != null && sc.getProgIFS() == null) sc.setProgIFS(new UniquePriorityQueue<>());
                        if ((progIF = progIFTypeAdapter.read(reader)) != null && sc != null) sc.addProgIF(progIF);
                    }
                    reader.endArray();
                }else if(reader.peek() == JsonToken.NULL){
                    reader.nextNull();
                }
            }
        }

        reader.endObject();
        return sc;
    }
    @Override
    public void write(JsonWriter writer, SubClass c) throws IOException
    {

    }
}
