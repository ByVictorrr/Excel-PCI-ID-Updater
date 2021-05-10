package utilities;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class LineCountWriter extends BufferedWriter {


    private Logger logger;
    private int lineNumber;
    public LineCountWriter(Writer out){
        super(out);
        logger = null;
        lineNumber = 0;
    }
    public LineCountWriter(Writer out, int sz){
        super(out, sz);
        lineNumber = 0;
    }
    public void write(char[] cbuf, int off, int len)throws IOException{
        if(containsNewLine(cbuf.toString()))
            lineNumber++;
        super.write(cbuf, off, len);
    }

    public void write(String str)throws IOException{
        if(containsNewLine(str))
            lineNumber++;

        super.write(str);
    }

    public void write(char [] buf)throws IOException{
        if(containsNewLine(buf.toString()))
            lineNumber++;
        super.write(buf);
    }
    public void write(String s, int off, int len)throws IOException{
        if(containsNewLine(s))
            lineNumber++;
        super.write(s, off, len);
    }
    private boolean containsNewLine(String s){
        if(s.contains("\n"))
            return true;
        return false;
    }
    private boolean isNewLine(char c){
       if(c == '\n')
           return true;
       return false;
    }
    public void newLine() throws IOException{
        lineNumber++;
        super.newLine();
    }
    public int getLineNumber(){ return this.lineNumber;}










}
