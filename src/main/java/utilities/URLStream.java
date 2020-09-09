package utilities;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import static adapters.ConnectionAdapter.*;

public class URLStream {

    protected FTPClient client;

    public URLStream() throws Exception{
       this.client = new FTPClient();
        client = new FTPClient();
        client.connect(PCI_HOST, 21);
        client.login(PCI_USER, PCI_PASS);

    }
    public FTPClient getClient() {
        return client;
    }

    public int findLatestVersion(String parentDir)throws Exception{
        FTPFile[] dirs = client.listDirectories(parentDir);
        // Step 1 - Get the newest version
        int largest = -1;
        int curr;
        for (FTPFile dir: dirs){
            String dir_name = dir.getName();
            curr = Integer.parseInt(dir_name.substring(1, dir_name.length()));
            if( curr > largest ){
                largest = curr;
            }
        }
        return largest;
    }
}
