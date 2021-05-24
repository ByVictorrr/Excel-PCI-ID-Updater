package exceptions;

public class LineOutOfOrderException extends Exception{
   // when parsing and find one line before another
    public LineOutOfOrderException(String message, int lineNum){
        super("line: " + lineNum + ": "+ message);
    }


}
