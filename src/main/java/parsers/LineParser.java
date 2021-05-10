package parsers;

import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import models.Device;
import models.SubSystem;
import models.Vendor;
import org.apache.commons.lang3.StringUtils;
import parsers.models.ClassModel;
import parsers.models.VendorModel;

import javax.sound.sampled.Line;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static parsers.validators.PIValidator.*;

public class LineParser {


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


    public enum TYPES_LINES{
        VENDOR_LINE, DEVICE_LINE , SUB_LINE, COMMENT_LINE, BLANK_LINE, INVALID_LINE
    }

    /**
     *
     * @param line : a String that is used to get the type of line
     * @return : the type of line
     */
    public TYPES_LINES getType(String line){
        /* Below shows all the matchers to determine what line is */
        final Matcher vMatch = Pattern.compile(VENDOR_PATTERN).matcher(line);
        final Matcher dMatch = Pattern.compile(DEVICE_PATTERN).matcher(line);
        final Matcher sMatch = Pattern.compile(SUB_PATTERN).matcher(line);
        final Matcher cMatch = Pattern.compile(COMMENT_PATTERN).matcher(line);
        final Matcher classMatch = Pattern.compile(CLASS_PATTERN).matcher(line);
        final Matcher sClassMatch = Pattern.compile(SUB_CLASS_PATTERN).matcher(line);
        final Matcher progIFMatch = Pattern.compile(PROG_IF_PATTERN).matcher(line);

        TYPES_LINES lineType = TYPES_LINES.INVALID_LINE;
        String m;
        // see what matches
        try {
            if (sMatch.find()) {
                lineType = TYPES_LINES.SUB_LINE;
                vendorModel.setsVendor(Integer.parseInt(sMatch.group(1), 16));
                vendorModel.setsDevice(Integer.parseInt(sMatch.group(2), 16));
                vendorModel.setsName(sMatch.group(3));
            } else if (dMatch.find()) {
                lineType = TYPES_LINES.DEVICE_LINE;
                vendorModel.setsVendor(null);
                vendorModel.setsDevice(null);
                vendorModel.setDevice(Integer.parseInt(dMatch.group(1), 16));
                vendorModel.setdName(dMatch.group(2));
            } else if (vMatch.find()) {
                lineType = TYPES_LINES.VENDOR_LINE;
                vendorModel.resetModel();
                vendorModel.setVendor(Integer.parseInt(vMatch.group(1), 16));
                vendorModel.setvName(vMatch.group(2));
            }else if(progIFMatch.find()){


            }else if(sClassMatch.find()){

            }else if(classMatch.find()){

            }else if (cMatch.find()) {
                lineType = TYPES_LINES.COMMENT_LINE;
            } else if (StringUtils.isBlank(line)) {
                lineType = TYPES_LINES.BLANK_LINE;
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return lineType;

    }




}
