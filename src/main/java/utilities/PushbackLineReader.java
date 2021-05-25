package utilities;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

public class PushbackLineReader extends PushbackReader {

    public PushbackLineReader(Reader in, int size) {
        super(in, size);
    }

    public PushbackLineReader(Reader in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        return super.read();
    }
}
