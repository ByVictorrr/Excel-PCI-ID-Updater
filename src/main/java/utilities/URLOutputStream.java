package utilities;


import adapters.ConnectionAdapter;
import models.Device;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import static adapters.ConnectionAdapter.*;

public class URLOutputStream extends URLStream{
    private OutputStream os;
    public URLOutputStream(String path) throws Exception{
        super();
        client.enterLocalPassiveMode();
        client.setFileType(FTP.ASCII_FILE_TYPE);
        client.setFileTransferMode(FTP.ASCII_FILE_TYPE);
        os = client.storeFileStream(path);
    }

    public OutputStream getOutputStream() {
        return os;
    }
    public OutputStreamWriter getWriter(){
       return new OutputStreamWriter(this.os);
    }
    public void write(StringBuilder chLog)throws Exception{
        OutputStreamWriter writer = this.getWriter();
        writer.write(chLog.toString());
        writer.close();
    }

    public void write(URLInputStream r, Queue<Device> devices) throws Exception{
        String line;
        Queue<String> prevLines = new LinkedList<>();
        long numLine = 1;
        Device curr = devices.poll();
        BufferedReader reader = r.getReader();
        OutputStreamWriter writer = this.getWriter();
        while ((line = reader.readLine()) != null) {
            if(curr != null && curr.getLineNum() == numLine){
                writer.write(curr.formatOutput() + "\n");
                Logger.addEntry(curr);
                curr = devices.poll();
                prevLines.add(line);
            }else {
                while(!prevLines.isEmpty()) {
                    writer.write(prevLines.poll() + "\n");
                }
                writer.write(line + "\n");
            }
            numLine++;
        }
        writer.close();

    }
    public void close() throws Exception{
        // step 1 - close
        this.os.close();

    }

    @Override
    protected void finalize() throws Throwable {
        this.os.close();
    }
}
