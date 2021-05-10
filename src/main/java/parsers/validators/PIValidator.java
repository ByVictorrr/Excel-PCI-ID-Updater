package parsers.validators;

public class PIValidator {
    public static final String VENDOR_PATTERN = "^([0-9a-f]{4})(\\s{2}(.*))?$";
    public static final String DEVICE_PATTERN = "^\\t([0-9a-f]{4})(\\s{2}(.*))?$";
    public static final String SUB_PATTERN = "^\\t{2}([0-9a-f]{4})\\s{1}([0-9a-f]{4})(\\s{2}(.*))?$";
    public static final String COMMENT_PATTERN = "^\\s{0,}#.*$";
    public static final String CLASS_PATTERN = "^([0-9a-f]{2})(\\s{2}(.*))?$";
    public static final String SUB_CLASS_PATTERN = "^\\t([0-9a-f]{2})(\\s{2}(.*))?$";
    public static final String PROG_IF_PATTERN = "^\\t{2}([0-9a-f]{2})(\\s{2}(.*))?$";

    public enum TYPES_LINES{
        VENDOR_LINE, DEVICE_LINE, SUB_LINE, COMMENT_LINE, BLANK_LINE, INVALID_LINE
    }




}
