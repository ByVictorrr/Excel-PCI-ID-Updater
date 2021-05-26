package utilities;

import exceptions.LineOutOfOrderException;
import models.*;
import models.Class;
import parsers.LineParser;
import parsers.models.ClassModel;
import parsers.models.VendorModel;

import java.util.PriorityQueue;

public class ClassUpdater {
    private enum COMMANDS {
        CONTINUE,
        INSERT_CLASS, INSERT_SUBCLASS, INSERT_PROGIF, INSERT_SUBCLASSES, INSERT_PROGIFS,
        POLL_CLASS, POLL_SUBCLASS, POLL_PROGIF
    };


    /* Assuming vendors are equal */
    private static COMMANDS getCommand(ClassModel line, ProgIF to){
        COMMANDS ret = COMMANDS.CONTINUE;
        if(line.getProgIF().getId() > to.getProgIF()){
            ret = COMMANDS.INSERT_PROGIF;
        }else if(line.getProgIF().getId().equals(to.getProgIF())){
            ret = COMMANDS.POLL_PROGIF;
        }
        return ret;
    }

    /* Assuming vendors are equal */
    private static COMMANDS getCommand(ClassModel line, SubClass to, LineParser.LineType currentLineType, LineParser.LineType nextLineType){
        COMMANDS ret = COMMANDS.CONTINUE;
        if(line.getSubClass().getId() > to.getSubClass()){
            ret = COMMANDS.INSERT_SUBCLASS;
        }else if(line.getSubClass().getId().equals(to.getSubClass())) {
            if (to.size() == 0) ret = COMMANDS.POLL_SUBCLASS;
            else if (currentLineType == LineParser.LineType.DEVICE_LINE) {
                if (nextLineType == LineParser.LineType.DEVICE_LINE) ret = COMMANDS.INSERT_PROGIFS;
                else if (nextLineType == LineParser.LineType.SUBSYSTEM_LINE) ret = COMMANDS.CONTINUE;
                else if (nextLineType == LineParser.LineType.VENDOR_LINE) ret = COMMANDS.INSERT_PROGIFS;
            } else {
                ret = getCommand(line, to.getProgIFS().peek());
            }
        }


        return ret;
    }


    /* Handle nulls for command later */
    private static COMMANDS getCommand(ClassModel line, Class to, LineParser.LineType currLineType, LineParser.LineType nextLineType)
            throws LineOutOfOrderException
    {

        COMMANDS ret = COMMANDS.CONTINUE;
        if(line.get_class().getId() > to.get_class()) {
            ret = COMMANDS.INSERT_CLASS;
        }else if(line.get_class().getId().equals(to.get_class())){
            /* Case 1 - check to see if there is nothing to write */
            if(to.size() == 0) ret = COMMANDS.POLL_CLASS;
            else if(currLineType == LineParser.LineType.CLASS_LINE){
                if(nextLineType == LineParser.LineType.CLASS_LINE) ret = COMMANDS.INSERT_SUBCLASSES;
                else if(nextLineType == LineParser.LineType.SUB_CLASS_LINE) ret = COMMANDS.CONTINUE;
            }else{
                ret = getCommand(line, to.getSubClasses().peek(), currLineType, nextLineType);
            }
        }
        return ret;
    }

    private static void runCommand(PriorityQueue<Class> pc, LineCountWriter writer, COMMANDS command) throws Exception {
        Class tempC;
        SubClass tempS;
        ProgIF tempP;
        switch (command) {
            case INSERT_CLASS:
                tempC = pc.poll();
                writer.write(tempC.toString() + "\n");
                Logger.getInstance().println("new output at line " + writer.getLineNumber() + ":\n" + tempC);
                break;
            case INSERT_SUBCLASS:
                tempC = pc.peek();
                tempS = tempC.getSubClasses().poll();
                writer.write(tempS.toString() + "\n");
                Logger.getInstance().println("new output at line " + writer.getLineNumber() + ":\n" + tempS);
                if(tempC.size() == 0) pc.poll();
                break;
            case INSERT_PROGIF:
                tempC = pc.peek();
                tempS = tempC.getSubClasses().peek();
                tempP = tempS.getProgIFS().poll();
                writer.write(tempP.toString() + "\n");
                Logger.getInstance().println("new output at line " + writer.getLineNumber() + ":\n" + tempS);
                if(tempS.size() == 0) tempC.getSubClasses().poll();
                if(tempC.size() == 0) pc.poll();
                break;
            case INSERT_SUBCLASSES:
                tempC = pc.poll();
                writer.write(tempC.getSubClasses().toString() + "\n");
                Logger.getInstance().println("new output at line " + writer.getLineNumber() + ":\n" + tempC.getSubClasses().toString());
                break;
            case INSERT_PROGIFS:
                tempC = pc.peek();
                tempS = tempC.getSubClasses().poll();
                writer.write(tempS.getProgIFS().toString() + "\n");
                Logger.getInstance().println("new output at line " + writer.getLineNumber() + ":\n" + tempS.getProgIFS().toString());
                break;
            case POLL_CLASS:

                break;
            case POLL_SUBCLASS:
                break;
            case POLL_PROGIF:
                break;


        }
    }
    public static void update(PriorityQueue<Class> pc, LineCountWriter writer,
                                           LineParser.LineType currLine, LineParser.LineType nextLine) throws Exception {

        ClassModel classModel = LineParser.getInstance().getClassModel();
        Class currClass = pc.peek();

    }

}
