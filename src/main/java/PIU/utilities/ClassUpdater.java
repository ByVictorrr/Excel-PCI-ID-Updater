package PIU.utilities;

import PIU.exceptions.LineOutOfOrderException;
import PIU.models.*;
import PIU.models.Class;
import PIU.parsers.LineParser;
import PIU.parsers.models.ClassModel;

import java.util.PriorityQueue;

public class ClassUpdater {
    private enum COMMANDS {
        CONTINUE,
        INSERT_CLASS, INSERT_SUBCLASS, INSERT_PROGIF, INSERT_SUBCLASSES, INSERT_PROGIFS,
        POLL_CLASS, POLL_SUBCLASS, POLL_PROGIF
    };

    private static boolean isInsertCommand(COMMANDS cmd){
        return cmd == COMMANDS.INSERT_CLASS ||
                cmd == COMMANDS.INSERT_SUBCLASS ||
                cmd == COMMANDS.INSERT_PROGIF ||
                cmd == COMMANDS.INSERT_SUBCLASSES ||
                cmd == COMMANDS.INSERT_PROGIFS;
    }


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
            // Case where they are equal
        }else if(line.getSubClass().getId().equals(to.getSubClass())) {
            if (to.size() == 0) {
                ret = COMMANDS.POLL_SUBCLASS;
            }else if (currentLineType == LineParser.LineType.SUB_CLASS_LINE) {
                if (nextLineType == LineParser.LineType.SUB_CLASS_LINE) ret = COMMANDS.INSERT_PROGIFS;
                else if (nextLineType == LineParser.LineType.PROG_IF_LINE) ret = COMMANDS.CONTINUE;
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

    private static void runCommand(UniquePriorityQueue<Class> pc, LineCountWriter writer, COMMANDS command) throws Exception {
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
                Logger.getInstance().println("new output at line " + writer.getLineNumber() + ":\n" + tempC.subClassesToString());
                break;
            case INSERT_PROGIFS:
                tempC = pc.peek();
                tempS = tempC.getSubClasses().poll();
                writer.write(tempS.getProgIFS().toString() + "\n");
                Logger.getInstance().println("new output at line " + writer.getLineNumber() + ":\n" + tempS.progIFsToString());
                break;
            case POLL_CLASS:
                pc.poll();
                break;
            case POLL_SUBCLASS:
                tempC=pc.peek();
                tempS=tempC.getSubClasses().poll();
                if(tempC.size() == 0) pc.poll();
                break;
            case POLL_PROGIF:
                tempC=pc.peek();
                tempS=tempC.getSubClasses().peek();
                tempP = tempS.getProgIFS().poll();
                if(tempS.size() == 0) tempC.getSubClasses().poll();
                if(tempC.size() == 0) pc.poll();
                break;


        }
    }
    public static void update(UniquePriorityQueue<Class> pc, LineCountWriter writer,
                                           LineParser.LineType currLine, LineParser.LineType nextLine) throws Exception {

        ClassModel classModel = LineParser.getInstance().getClassModel();
        Class currClass = pc.peek();


        COMMANDS command = getCommand(classModel, currClass, currLine, nextLine);

        while (isInsertCommand(command)) {
            runCommand(pc, writer, command);
            if((currClass=pc.peek()) == null)
                return;
            else
                command = getCommand(classModel, currClass, currLine, nextLine);
        }
        if(!isInsertCommand(command))runCommand(pc, writer, command);

    }

}
