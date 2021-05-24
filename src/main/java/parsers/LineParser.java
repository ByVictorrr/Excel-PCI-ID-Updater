package parsers;

import exceptions.LineOutOfOrderException;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import parsers.models.ClassModel;
import parsers.models.VendorModel;
import parsers.models.vendormodels.DeviceLine;
import parsers.models.vendormodels.SubSystemLine;
import parsers.models.vendormodels.VendorLine;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static validators.PIValidator.*;

public class LineParser {

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
    public enum TYPES_LINES{
        VENDOR_LINE, DEVICE_LINE, SUBSYSTEM_LINE, CLASS_LINE, SUB_CLASS_LINE,
        PROG_IF_LINE, COMMENT_LINE, BLANK_LINE, INVALID_LINE
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

    public boolean isVendorModelLine(TYPES_LINES lineT){
        return lineT == TYPES_LINES.DEVICE_LINE ||
                lineT == TYPES_LINES.VENDOR_LINE ||
                lineT == TYPES_LINES.SUB_CLASS_LINE;
    }
    public boolean isClassModelLine(TYPES_LINES lineT){
        return lineT == TYPES_LINES.PROG_IF_LINE ||
                lineT == TYPES_LINES.SUB_CLASS_LINE ||
                lineT == TYPES_LINES.CLASS_LINE;
    }


    public Pair<TYPES_LINES, Matcher> getTypeLine(String line){
        final Matcher vMatch = Pattern.compile(VENDOR_PATTERN).matcher(line);
        final Matcher dMatch = Pattern.compile(DEVICE_PATTERN).matcher(line);
        final Matcher sMatch = Pattern.compile(SUB_PATTERN).matcher(line);
        final Matcher commentMatch = Pattern.compile(COMMENT_PATTERN).matcher(line);
        final Matcher classMatch = Pattern.compile(CLASS_PATTERN).matcher(line);
        final Matcher sClassMatch = Pattern.compile(SUB_CLASS_PATTERN).matcher(line);
        final Matcher progIFMatch = Pattern.compile(PROG_IF_PATTERN).matcher(line);

        if(sMatch.find()){
            return new Pair<TYPES_LINES, Matcher>(TYPES_LINES.SUBSYSTEM_LINE, sMatch);
        }else if(dMatch.find()){
            return new Pair<TYPES_LINES, Matcher>(TYPES_LINES.DEVICE_LINE, dMatch);
        }else if(vMatch.find()){
            return new Pair<TYPES_LINES, Matcher>(TYPES_LINES.VENDOR_LINE, dMatch);
        }else if(progIFMatch.find()){
            return new Pair<TYPES_LINES, Matcher>(TYPES_LINES.PROG_IF_LINE, progIFMatch);
        }else if(sClassMatch.find()){
            return new Pair<TYPES_LINES, Matcher>(TYPES_LINES.SUB_CLASS_LINE, sClassMatch);
        }else if(classMatch.find()){
            return new Pair<TYPES_LINES, Matcher>(TYPES_LINES.CLASS_LINE, classMatch);
        }else if(commentMatch.find()){
            return new Pair<TYPES_LINES, Matcher>(TYPES_LINES.COMMENT_LINE, commentMatch);
        }else if(StringUtils.isBlank(line)){
            return new Pair<TYPES_LINES, Matcher>(TYPES_LINES.BLANK_LINE, null);
        }
        return new Pair<TYPES_LINES, Matcher>(TYPES_LINES.INVALID_LINE, null);
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
            classModel.setProgIF(Integer.parseInt(groups.group(PROG_IF_ID_GROUP), EXPECTED_BASE));
            classModel.setProgIFName(groups.group(PROG_IF_NAME_GROUP));
        }
    }
    private void parseSubClassLine(Matcher groups, int lineNum) throws LineOutOfOrderException{
        if(this.classModel.get_class() == null){
            throw new LineOutOfOrderException("There is no associated class and subclass with the prog-if line (fix)", lineNum) ;
        }else{
            classModel.setProgIFName(null);
            classModel.setProgIF(null);
            classModel.setSubClass(Integer.parseInt(groups.group(SUB_CLASS_ID_GROUP), EXPECTED_BASE));
            classModel.setSubClassName(groups.group(SUB_CLASS_NAME_GROUP));
        }
    }
    private void parseClassLine(Matcher groups, int lineNum){
        classModel.resetModel();
        classModel.set_class(Integer.parseInt(groups.group(CLASS_ID_GROUP),EXPECTED_BASE));
        classModel.setClassName(groups.group(CLASS_NAME_GROUP));
    }

    /**
     * @param line : a String that is used to get the type of line
     * @return : the type of line
     */
    public TYPES_LINES parseLine(String line, int lineNum) throws LineOutOfOrderException {

        Pair<TYPES_LINES, Matcher> lineType = getTypeLine(line);

        switch (lineType.getKey()){
            case SUBSYSTEM_LINE:
                parseSubSystemLine(lineType.getValue(), lineNum);
                break;
            case DEVICE_LINE:
                parseDeviceLine(lineType.getValue(), lineNum);
                break;
            case VENDOR_LINE:
                parseVendorLine(lineType.getValue(), lineNum);
                break;
            case PROG_IF_LINE:
                parseProgIFLine(lineType.getValue(), lineNum);
               break;
            case SUB_CLASS_LINE:
                parseSubClassLine(lineType.getValue(), lineNum);
                break;
            case CLASS_LINE:
               break;
            default:
        }

        return lineType.getKey();
    }



}
