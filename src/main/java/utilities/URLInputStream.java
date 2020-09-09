package utilities;

import adapters.ConnectionAdapter;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.util.IOUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import static adapters.ConnectionAdapter.*;

public class URLInputStream extends URLStream{
    private InputStream is;

    public URLInputStream(String path) throws Exception{
            super();
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            is = client.retrieveFileStream(path);
    }

    public BufferedReader getReader(){
        return new BufferedReader(new InputStreamReader(this.is));
    }
    public InputStream getInputStream() {
        return this.is;
    }

    public void close()throws Exception{
        if(this.is != null) {
            this.is.close();
        }
    }
    @Override
    protected void finalize() throws Throwable {
        this.is.close();
    }
}
