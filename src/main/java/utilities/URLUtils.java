package utilities;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import static adapters.ConnectionAdapter.*;
import static adapters.ConnectionAdapter.PCI_ROOT_DIR;

public class URLUtils{

    private static final String FTP_URL_FORMAT = "ftp://%s:%s@%s";
    private static final String BASE_URL = String.format(FTP_URL_FORMAT, PCI_USER, PCI_PASS, PCI_HOST);

    public static String getURL(String path){
       return String.format(BASE_URL+"/%s;type=i", path);
    }
    public static boolean exists(String URLName){
        boolean result = false;
        URL url;
        try {
            url = new URL(URLName);
            InputStream inputStream = url.openStream();
            System.out.println("SUCCESS");
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    static public void copy(String from, String to){
        final int BUFFER_SIZE = 4096;
        try {
            URLInputStream i = new URLInputStream(from);
            URLOutputStream o = new URLOutputStream(to);
            OutputStream os = o.getOutputStream();
            InputStream is = i.getInputStream();

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
