package parsers;

import exceptions.LineOutOfOrderException;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import parsers.models.ClassModel;
import parsers.models.VendorModel;
import parsers.models.classmodels.ClassLine;
import parsers.models.classmodels.ProgIFLine;
import parsers.models.classmodels.SubClassLine;
import parsers.models.vendormodels.DeviceLine;
import parsers.models.vendormodels.SubSystemLine;
import parsers.models.vendormodels.VendorLine;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LineParser {
    public static final String VENDOR_PATTERN = "^([0-9a-f]{4})(\\s{2}(.*))?$";
    public static final String DEVICE_PATTERN = "^\\t([0-9a-f]{4})(\\s{2}(.*))?$";
    public static final String SUB_PATTERN = "^\\t{2}([0-9a-f]{4})\\s{1}([0-9a-f]{4})(\\s{2}(.*))?$";
    public static final String COMMENT_PATTERN = "\\s*#.*";
    public static final String CLASS_PATTERN = "^C\\s([0-9a-f]{2})(\\s{2}(.*))?$";
    public static final String SUB_CLASS_PATTERN = "^\\t([0-9a-f]{2})(\\s{2}(.*))?$";
    public static final String PROG_IF_PATTERN = "^\\t{2}([0-9a-f]{2})(\\s{2}(.*))?$";

    /* For a Vendor model */
    private static final int VENDOR_ID_GROUP = 1;
    private static final int VENDOR_NAME_GROUP = 2;
    private static final int DEVICE_ID_GROUP = 1;
    private static final int DEVICE_NAME_GROUP = 2;
    private static final int SUBSYSTEM_VENDOR_ID_GROUP = 1;
    private static final int SUBSYSTEM_DEVICE_ID_GROUP = 2;
    private static final int SUBSYSTEM_NAME_GROUP = 3;



    /* For a class model */
    private static final int CLASS_ID_GROUP = 1;
    private static final int CLASS_NAME_GROUP = 2;
    private static final int SUB_CLASS_ID_GROUP = 1;
    private static final int SUB_CLASS_NAME_GROUP = 2;
    private static final int PROG_IF_ID_GROUP = 1;
    private static final int PROG_IF_NAME_GROUP = 2;


    /* For line types */
    public enum LineType {
        VENDOR_LINE, DEVICE_LINE, SUBSYSTEM_LINE, CLASS_LINE, SUB_CLASS_LINE,
        PROG_IF_LINE, COMMENT_LINE, BLANK_LINE, INVALID_LINE
    }
    public static boolean isVendorModel(LineType type){
        return type == LineType.VENDOR_LINE || type == LineType.DEVICE_LINE || type == LineType.SUBSYSTEM_LINE;
    }
    public static boolean isClassModel(LineType type){
        return type == LineType.CLASS_LINE || type == LineType.SUB_CLASS_LINE || type == LineType.PROG_IF_LINE;
    }


    private static final int EXPECTED_BASE = 16;



    private static LineParser instance;
    private VendorModel vendorModel;
    private ClassModel classModel;

    private LineParser(){
        if(vendorModel == null)
            this.vendorModel = VendorModel.getInstance();
        if(classModel == null)
            this.classModel = ClassModel.getInstance();
    }

    public static LineParser getInstance() {
        if(instance == null) {
            instance = new LineParser();
        }
        return instance;
    }
    public VendorModel getVendorModel(){return this.vendorModel;}
    public ClassModel getClassModel(){return this.classModel;}




    public Pair<LineType, Matcher> getTypeLine(String line){
        final Matcher vMatch = Pattern.compile(VENDOR_PATTERN).matcher(line);
        final Matcher dMatch = Pattern.compile(DEVICE_PATTERN).matcher(line);
        final Matcher sMatch = Pattern.compile(SUB_PATTERN).matcher(line);
        final Matcher commentMatch = Pattern.compile(COMMENT_PATTERN).matcher(line);
        final Matcher classMatch = Pattern.compile(CLASS_PATTERN).matcher(line);
        final Matcher sClassMatch = Pattern.compile(SUB_CLASS_PATTERN).matcher(line);
        final Matcher progIFMatch = Pattern.compile(PROG_IF_PATTERN).matcher(line);

        if(sMatch.find()){
            return new Pair<LineType, Matcher>(LineType.SUBSYSTEM_LINE, sMatch);
        }else if(dMatch.find()){
            return new Pair<LineType, Matcher>(LineType.DEVICE_LINE, dMatch);
        }else if(vMatch.find()){
            return new Pair<LineType, Matcher>(LineType.VENDOR_LINE, vMatch);
        }else if(progIFMatch.find()){
            return new Pair<LineType, Matcher>(LineType.PROG_IF_LINE, progIFMatch);
        }else if(sClassMatch.find()){
            return new Pair<LineType, Matcher>(LineType.SUB_CLASS_LINE, sClassMatch);
        }else if(classMatch.find()){
            return new Pair<LineType, Matcher>(LineType.CLASS_LINE, classMatch);
        }else if(commentMatch.find()){
            return new Pair<LineType, Matcher>(LineType.COMMENT_LINE, commentMatch);
        }else if(StringUtils.isBlank(line)){
            return new Pair<LineType, Matcher>(LineType.BLANK_LINE, null);
        }
        return new Pair<LineType, Matcher>(LineType.INVALID_LINE, null);
    }
    private void parseSubSystemLine(Matcher groups, int lineNum) throws LineOutOfOrderException{

        if(this.vendorModel.getVen() == null && vendorModel.getDev() == null){
            throw new LineOutOfOrderException("There is no associated vendor and device with the subsystem line(fix before continuing)", lineNum);
        }else if(vendorModel.getVen() == null){
            throw new LineOutOfOrderException("There is no associated vendor with the subsystem line (fix before continuing)", lineNum);
        }else if(vendorModel.getDev() == null){
            throw new LineOutOfOrderException("There is no associated device with the subsystem line (fix before continuing)", lineNum);
        }else {
            vendorModel.setSub(new SubSystemLine(
                    Integer.parseInt(groups.group(SUBSYSTEM_VENDOR_ID_GROUP), EXPECTED_BASE),
                    Integer.parseInt(groups.group(SUBSYSTEM_DEVICE_ID_GROUP), EXPECTED_BASE),
                    groups.group(SUBSYSTEM_NAME_GROUP)
            ));
        }
    }

    private void parseDeviceLine(Matcher groups, int lineNum) throws LineOutOfOrderException{
        if(this.vendorModel.getVen() == null){
            throw new LineOutOfOrderException("There is no associated vendor with the device line (fix before continuing) ", lineNum);
        }else{
            vendorModel.setSub(null);
            vendorModel.setDev(new DeviceLine(
                    Integer.parseInt(groups.group(DEVICE_ID_GROUP), EXPECTED_BASE),
                    groups.group(DEVICE_NAME_GROUP)
            ));
        }
    }
    private void parseVendorLine(Matcher groups, int lineNum){
        vendorModel.resetModel();
        vendorModel.setVen(new VendorLine(
                Integer.parseInt(groups.group(VENDOR_ID_GROUP), EXPECTED_BASE),
                groups.group(VENDOR_NAME_GROUP))
        );
    }

    private void parseProgIFLine(Matcher groups, int lineNum) throws LineOutOfOrderException{
        if(this.classModel.get_class() == null && this.classModel.getSubClass() == null){
           throw new LineOutOfOrderException("There is no associated class and subclass with the prog-if line (fix)", lineNum) ;
        }else if(this.classModel.get_class() == null){
            throw new LineOutOfOrderException("There is no associated class with the prog-if line (fix)", lineNum) ;
        }else if(this.classModel.getSubClass() == null){
            throw new LineOutOfOrderException("There is no associated sub-class with the prog-if line (fix)", lineNum) ;
        }else {
            classModel.setProgIF(new ProgIFLine(
                    Integer.parseInt(groups.group(PROG_IF_ID_GROUP), EXPECTED_BASE),
                    groups.group(PROG_IF_NAME_GROUP)
            ));
        }

    }
    private void parseSubClassLine(Matcher groups, int lineNum) throws LineOutOfOrderException{
        if(this.classModel.get_class() == null){
            throw new LineOutOfOrderException("There is no associated class and subclass with the prog-if line (fix)", lineNum) ;
        }else{
            classModel.setProgIF(null);
            classModel.setSubClass(new SubClassLine(
                    Integer.parseInt(groups.group(SUB_CLASS_ID_GROUP), EXPECTED_BASE),
                    groups.group(SUB_CLASS_NAME_GROUP))
            );
        }
    }
    private void parseClassLine(Matcher groups, int lineNum){
        classModel.resetModel();
        classModel.set_class(new ClassLine(
                Integer.parseInt(groups.group(CLASS_ID_GROUP),EXPECTED_BASE),
                groups.group(CLASS_NAME_GROUP)
        ));
    }

    /**
     * @param line : a String that is used to get the type of line
     * @return : the type of line
     */
    public LineType parseLine(String line, int lineNum) throws LineOutOfOrderException {

        Pair<LineType, Matcher> lineObj = getTypeLine(line);

        switch (lineObj.getKey()){
            case SUBSYSTEM_LINE:
                parseSubSystemLine(lineObj.getValue(), lineNum);
                break;
            case DEVICE_LINE:
                parseDeviceLine(lineObj.getValue(), lineNum);
                break;
            case VENDOR_LINE:
                parseVendorLine(lineObj.getValue(), lineNum);
                break;
            case PROG_IF_LINE:
                parseProgIFLine(lineObj.getValue(), lineNum);
               break;
            case SUB_CLASS_LINE:
                parseSubClassLine(lineObj.getValue(), lineNum);
                break;
            case CLASS_LINE:
                parseClassLine(lineObj.getValue(), lineNum);
               break;
            default:
        }

        return lineObj.getKey();
    }



}
