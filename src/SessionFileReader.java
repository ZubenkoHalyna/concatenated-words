import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;

class SessionFileReader implements Closeable {
    private static final int BYTE_BUFFER_SIZE = 4096;

    private final FileInputStream fis;
    private final FileChannel channel;
    private final LinkedList<Session> sessions;
    private ByteBuffer buffer;

    public SessionFileReader(String fileName) {
        try {
            fis = new FileInputStream(new File(fileName));
            channel = fis.getChannel();
            buffer = ByteBuffer.allocate(BYTE_BUFFER_SIZE);
            sessions = new LinkedList<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Returns next word from buffer (words divided by characters different from letters a-z).
     * If buffer ended reads new one from chanel.
     */
    public String read() {
        try {
            StringBuilder sb = new StringBuilder();
            for (; ; ) {
                if (!buffer.hasRemaining()) {
                    buffer.clear();
                    if (channel.read(buffer) == -1)
                        return null;
                    buffer.flip();
                }
                char ch = (char) buffer.get();
                if (ch >= 'a' && ch <= 'z')
                    sb.append(ch);
                else {
                    if (sb.length() != 0) {
                        return sb.toString();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Returns total offset in file considering channel offset and byteBuffer offset
     */
    public int getOffset() {
        try {
            return (int) channel.position() + buffer.position() - buffer.limit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startNewSession(int newOffset) {
        try {
            sessions.addLast(new Session(buffer, channel.position()));
            channel.position(newOffset);
            buffer = ByteBuffer.allocate(BYTE_BUFFER_SIZE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void returnToPreviousSession() {
        try {
            channel.position(sessions.getLast().channelOffset);
            buffer = sessions.getLast().buffer;
            sessions.removeLast();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            fis.close();
            channel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Session {
        private ByteBuffer buffer;
        private long channelOffset;

        private Session(ByteBuffer buffer, long channelOffset) {
            this.buffer = buffer;
            this.channelOffset = channelOffset;
        }
    }
}
