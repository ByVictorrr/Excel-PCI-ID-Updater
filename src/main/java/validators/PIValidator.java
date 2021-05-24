package validators;

import org.apache.commons.lang3.StringUtils;
import parsers.models.VendorModel;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PIValidator {
    public static final String VENDOR_PATTERN = "^([0-9a-f]{4})(\\s{2}(.*))?$";
    public static final String DEVICE_PATTERN = "^\\t([0-9a-f]{4})(\\s{2}(.*))?$";
    public static final String SUB_PATTERN = "^\\t{2}([0-9a-f]{4})\\s{1}([0-9a-f]{4})(\\s{2}(.*))?$";
    public static final String COMMENT_PATTERN = "^\\s{0,}#.*$";
    public static final String CLASS_PATTERN = "^C\\s([0-9a-f]{2})(\\s{2}(.*))?$";
    public static final String SUB_CLASS_PATTERN = "^\\t([0-9a-f]{2})(\\s{2}(.*))?$";
    public static final String PROG_IF_PATTERN = "^\\t{2}([0-9a-f]{2})(\\s{2}(.*))?$";


    /**
     * @param reader - the pci.ids file
     * @return true if the whole file has no invalid line
     * @throws Exception
     */
    public static boolean isValid(BufferedReader reader) throws Exception{
            for(String line: reader.lines().collect(Collectors.toList())) {
                if(!isLineValid(line))
                    return false;
                else
                    continue;
            }
            return true;
    }
    public static boolean isLineValid(String line) throws Exception{
        final Matcher vMatch = Pattern.compile(VENDOR_PATTERN).matcher(line);
        final Matcher dMatch = Pattern.compile(DEVICE_PATTERN).matcher(line);
        final Matcher sMatch = Pattern.compile(SUB_PATTERN).matcher(line);
        final Matcher cMatch = Pattern.compile(COMMENT_PATTERN).matcher(line);
        final Matcher classMatch = Pattern.compile(CLASS_PATTERN).matcher(line);
        final Matcher sClassMatch = Pattern.compile(SUB_CLASS_PATTERN).matcher(line);
        final Matcher progIFMatch = Pattern.compile(PROG_IF_PATTERN).matcher(line);
        if(!sMatch.find() && !dMatch.find() && !vMatch.find() && !progIFMatch.find()
                && !sClassMatch.find() && !classMatch.find() && !cMatch.find() && !StringUtils.isBlank(line)){

            return false;
        }
        return true;

    }








}
