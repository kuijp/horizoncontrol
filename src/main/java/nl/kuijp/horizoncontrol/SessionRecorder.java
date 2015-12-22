package nl.kuijp.horizoncontrol;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

class SessionRecorder {

    protected FileOutputStream f;
    protected DataOutputStream df;
    protected long startTime, lastTimeOffset;
    protected byte[] buffer;
    protected int bufferSize;
    protected int bufferBytes;

    public SessionRecorder(String name, int bufsize) throws IOException {
        f = new FileOutputStream(name);
        df = new DataOutputStream(f);
        startTime = System.currentTimeMillis();
        lastTimeOffset = 0;

        bufferSize = bufsize;
        bufferBytes = 0;
        buffer = new byte[bufferSize];
    }

    public void close() throws IOException {
        flush();
        df = null;
        f.close();
        f = null;
        buffer = null;
    }

    public void flush(boolean updateTimeOffset) throws IOException {
        if (bufferBytes > 0) {
            df.writeInt(bufferBytes);
            df.write(buffer, 0, (bufferBytes + 3) & 0x7FFFFFFC);
            df.writeInt((int) lastTimeOffset);
            bufferBytes = 0;
            if (updateTimeOffset)
                lastTimeOffset = -1;
        }
    }

    public void flush() throws IOException {
        flush(true);
    }
}

