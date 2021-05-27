package PIU.utilities;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineCountWriter extends BufferedWriter {


    private int lineNumber;
    public LineCountWriter(Writer out){
        super(out);
        lineNumber = 0;
    }
    public LineCountWriter(Writer out, int sz){
        super(out, sz);
        lineNumber = 0;
    }

    public void write(String str)throws IOException{
        Matcher matcher = Pattern.compile("\n").matcher(str);
        while(matcher.find()){
            lineNumber++;
        }
        super.write(str);

    }


    public void newLine() throws IOException{
        lineNumber++;
        super.newLine();
    }
    public int getLineNumber(){ return this.lineNumber;}










}
