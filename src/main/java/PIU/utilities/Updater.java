package PIU.utilities;

import PIU.models.Class;
import PIU.models.Vendor;
import PIU.parsers.LineParser;

import java.io.BufferedReader;
import java.util.PriorityQueue;


public class Updater {



    public static void update(PriorityQueue<Vendor> pv, PriorityQueue<Class> pc, BufferedReader reader, LineCountWriter writer) throws Exception
    {
        int lineNum = 1;
        String c, n;
        LineParser.LineType currLine, nextLine;
        c = reader.readLine();
        while( c != null && (n=reader.readLine()) != null){
            currLine = LineParser.getInstance().parseLine(c, lineNum);
            nextLine = LineParser.getInstance().getTypeLine(n).getKey();
            // Step 3 - write pending objects to pci.ids
            if(pv != null && pv.size() > 0 && LineParser.isVendorModel(currLine)){
                VendorUpdater.update(pv, writer, currLine, nextLine);
            }else if (pc != null && pc.size() > 0 && LineParser.isClassModel(currLine)){
                ClassUpdater.update(pc, writer, currLine, nextLine);
            }else if(currLine == LineParser.LineType.INVALID_LINE){
                Logger.getInstance().println("line " + lineNum + ": invalid line, please correct before continuing");
                return;
            }
            writer.write(c + "\n");
            lineNum++;
            c = n;
        }

    }
}
