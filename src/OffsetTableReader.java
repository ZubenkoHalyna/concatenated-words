import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

class OffsetTableReader {
    private static final int A_OFFSET = 'a';
    private static final int CHAR_SIZE = 2;
    private static final int NUMBER_OF_CHARS = 'z' - 'a' + 1;
    private static final int LARGE_SKIP_BYTES = 3000;
    private static final int SMALL_SKIP_BYTES = 300;

    public int[][] read(File textFile) {
        int[][] offsetsTable = new int[NUMBER_OF_CHARS][NUMBER_OF_CHARS];
        try (RandomAccessFile file = new RandomAccessFile(textFile, "r")) {

            String previousFirstChars = "";
            String line = file.readLine();
            for (char i = 'a'; i <= 'z'; i++) {
                for (char j = 'a'; j <= 'z'; j++) {
                    String currentFirstChars = "" + i + j;
                    for (; ; ) {
                        line = readNewNotEmptyLineIfNeeded(file, previousFirstChars, line);
                        if (line != null && line.startsWith(currentFirstChars)) {
                            offsetsTable[i - A_OFFSET][j - A_OFFSET] =
                                    (int) file.getFilePointer() - line.length() - CHAR_SIZE;
                            previousFirstChars = currentFirstChars;
                            break;
                        }
                        if (line == null || !line.startsWith(previousFirstChars)) {
                            offsetsTable[i - A_OFFSET][j - A_OFFSET] = -1;
                            break;
                        }
                    }
                    tryToSkip(file, currentFirstChars, LARGE_SKIP_BYTES);
                    tryToSkip(file, currentFirstChars, SMALL_SKIP_BYTES);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return offsetsTable;
    }

    private String readNewNotEmptyLineIfNeeded(RandomAccessFile file,
                                               String charsToSkip, String currentLine)
            throws IOException {
        if (currentLine.startsWith(charsToSkip)) {
            currentLine = file.readLine();
            while (currentLine != null && currentLine.isEmpty()) {
                currentLine = file.readLine();
            }
        }
        return currentLine;
    }

    private void tryToSkip(RandomAccessFile file, String charsToSkip, int bytes) throws IOException {
        long initialOffset = file.getFilePointer();
        file.skipBytes(bytes);
        file.readLine();
        String wholeWord = file.readLine();
        if (wholeWord != null && wholeWord.startsWith(charsToSkip)) {
            tryToSkip(file, charsToSkip, bytes);
        } else {
            file.seek(initialOffset);
        }
    }
}
