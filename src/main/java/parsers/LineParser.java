package parsers;

import models.Device;
import models.SubSystem;
import models.Vendor;
import org.apache.commons.lang3.StringUtils;
import parsers.models.ClassModel;
import parsers.models.VendorModel;

import javax.sound.sampled.Line;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineParser {

    private static final String VENDOR_PATTERN = "^([0-9a-f]{4})(\\s{2}(.*))?$";
    private static final String DEVICE_PATTERN = "^\\t([0-9a-f]{4})(\\s{2}(.*))?$";
    private static final String SUB_PATTERN = "^\\t{2}([0-9a-f]{4})\\s{1}([0-9a-f]{4})(\\s{2}(.*))?$";
    private static final String COMMENT_PATTERN = "^\\s{0,}#.*$";
    private static final String CLASS_PATTERN = "^([0-9a-f]{2})(\\s{2}(.*))?$";
    private static final String SUB_CLASS_PATTERN = "^\\t([0-9a-f]{2})(\\s{2}(.*))?$";
    private static final String PROG_IF_PATTERN = "^\\t{2}([0-9a-f]{2})(\\s{2}(.*))?$";

    /*
    # Syntax:
            # C class	class_name
#	subclass	subclass_name  		<-- single tab
#		prog-if  prog-if_name  	<-- two tabs

     */
    private static LineParser instance = null;
    private VendorModel vendorModel;
    private ClassModel classModel;

    private LineParser(){
        if(model == null)
            this.model = new VendorModel();
    }

    public static LineParser getInstance() {
        if(instance == null) {
            instance = new LineParser();
        }
        return instance;
    }
    public VendorModel getModel(){return this.model;}


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
                model.sVendor = Integer.parseInt(sMatch.group(1), 16);
                model.sDevice = Integer.parseInt(sMatch.group(2), 16);
                model.sName = sMatch.group(3);
            } else if (dMatch.find()) {
                lineType = TYPES_LINES.DEVICE_LINE;
                model.sVendor = null;
                model.sDevice = null;
                model.device = Integer.parseInt(dMatch.group(1), 16);
                model.dName = dMatch.group(2);
            } else if (vMatch.find()) {
                lineType = TYPES_LINES.VENDOR_LINE;
                model.resetModel();
                model.vendor = Integer.parseInt(vMatch.group(1), 16);
                model.vName = vMatch.group(2);
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
