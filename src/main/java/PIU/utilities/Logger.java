package PIU.utilities;

import java.io.OutputStream;
import java.io.PrintStream;

public class Logger extends PrintStream{
    private static  Logger instance;
    private Logger(OutputStream outputStream){
        super(outputStream);
    }
    public static Logger getInstance(){
        if(instance == null){
           instance = new Logger(System.err);
        }
        return instance;
    }
    public void init(OutputStream outputStream){
        super.out = outputStream;
    }

}
