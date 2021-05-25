package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import models.ProgIF;
import utilities.Logger;

import java.io.IOException;

public class ProgIFAdapter extends TypeAdapter<ProgIF>{
    public static final String PROGIF_ID_KEY = "subClass";
    public static final String PROGIF_NAME_KEY = "name";
    @Override
    public ProgIF read(JsonReader reader)throws IOException
    {
        ProgIF prog = new ProgIF();
        String fieldName = null;
        int id;
        String name;

        reader.beginObject();
        while(reader.hasNext()){
            JsonToken token = reader.peek();
            if(token.equals(JsonToken.NAME)) fieldName = reader.nextName();

            if(PROGIF_ID_KEY.equals(fieldName)){
                if((id=Integer.parseInt(reader.nextString(),16)) > 0xff || id < 0){
                    Logger.getInstance().println(" : " + Integer.toHexString(id) + " is an invalid vendor");
                    prog=null;
                }else {
                    prog.setProgIF(id);
                }

            }else if(PROGIF_ID_KEY.equals(fieldName)){
                if(reader.peek() == JsonToken.STRING) {
                    name = reader.nextString();
                    if (prog != null) prog.setName(name);
                }else if(reader.peek() == JsonToken.NULL){
                    reader.nextNull();
                }
            }
        }

        reader.endObject();
        return prog;
    }
    @Override
    public void write(JsonWriter writer, ProgIF f) throws IOException
    {

    }
}
