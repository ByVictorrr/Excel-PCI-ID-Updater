package utilities;

import models.Device;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;

/* Creates a Change log in the corresponding folder*/
public class Logger {
    /* certain devices are only in the setDevice list arent added because they Already exist*/
    /* Print out a file */
    private static StringBuilder newChanges = new StringBuilder();
    private static StringBuilder oldLog = new StringBuilder();

    private Logger(){}

    private static void readLastChangeLog(URLInputStream inputStream)throws Exception{
       BufferedReader r = new BufferedReader(new InputStreamReader(inputStream.getInputStream()));
       String line;
       while((line=r.readLine()) != null){
           oldLog.append(line + "\n");
       }
    }
    public static void addEntry(Device d){
        newChanges.append("- " + d.toString() + "\n");
    }

    public static void createChangeLog(URLInputStream inputStream, URLOutputStream outputStream) throws Exception{
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        StringBuilder combined = new StringBuilder();
        // Step 0 - get last ch log
        if (inputStream.getInputStream() != null) {
            readLastChangeLog(inputStream);
            inputStream.close();
        }
        // Step 1 - get data time
        String dt = dtf.format(now);
        combined.append(dt + "\n" + "Changed\n");
        // Step 3 - merge the two sb's
        newChanges.append("\n"+oldLog.toString());
        combined.append(newChanges.toString());
        // Step 4 - output newChanges to a file
        outputStream.write(combined);
        // step 5 - close both streams
        outputStream.close();

    }
    public static void clear(){
        oldLog.setLength(0);
        newChanges.setLength(0);
    }



}
