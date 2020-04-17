import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcatenatedWordFinder {
    private static final int A_OFFSET = 'a';
    private static final int NUMBER_OF_CHARS = 'z' - 'a' + 1;
    private static final String SEPARATOR = "\r\n";
    private static final char END_WORD_CHAR = '\r';

    private final int[][] offsetsTable;
    private final String fileContent;
    private final Result result;

    public ConcatenatedWordFinder(String fileName) {
        try {
            fileContent = SEPARATOR + Files.readString(new File(fileName).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        offsetsTable = new int[NUMBER_OF_CHARS][NUMBER_OF_CHARS];
        int lastIndex = 0;
        for (char i = 'a'; i <= 'z'; i++) {
            for (char j = 'a'; j <= 'z'; j++) {
                offsetsTable[i - A_OFFSET][j - A_OFFSET] = fileContent.indexOf(
                        SEPARATOR + i + j, lastIndex) + SEPARATOR.length();
                if (offsetsTable[i - A_OFFSET][j - A_OFFSET] != -1) {
                    lastIndex = offsetsTable[i - A_OFFSET][j - A_OFFSET];
                }
            }
        }
        result = new Result();
    }

    public Result find() {
        ExecutorService threadPool = Executors.newFixedThreadPool(8);

        int currentPositionInFileContent = 0;
        while (currentPositionInFileContent != -1) {
            int newIndex = newWordIndex(currentPositionInFileContent);
            if (newIndex == -1)
                break;

            int index = currentPositionInFileContent;
            threadPool.submit(() -> processWord(newIndex, index));

            currentPositionInFileContent = newIndex + SEPARATOR.length();
        }

        threadPool.shutdown();
        try {
            threadPool.awaitTermination(10L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        result.fillLargestWords(fileContent);
        return result;
    }

    private void processWord(int newIndex, int index) {
        if (isConcatenatedWord(index, newIndex - index)) {
            result.addWord(index, newIndex);
        }
    }

    private boolean isConcatenatedWord(int startIndex, int length) {
        int[] wordPartsLength = new int[15];
        int[] wordPartsOffsets = new int[15];
        int iteration = 0;

        int currentLength = 0;
        int currentOffset = -1;
        int minLength = 2;
        for (; currentLength != length; ) {
            int offset = wordMatchOffset(startIndex + currentLength, length - currentLength, currentOffset, minLength);
            if (offset == -1) {
                iteration--;
                currentLength -= wordPartsLength[iteration];
                minLength = wordPartsLength[iteration] + 1;
                currentOffset = wordPartsOffsets[iteration];
                continue;
            }
            int matchWordLength = newWordIndex(offset) - offset;
            wordPartsLength[iteration] = matchWordLength;
            wordPartsOffsets[iteration] = offset + matchWordLength + SEPARATOR.length();
            iteration++;
            currentLength += matchWordLength;
            currentOffset = -1;
            minLength = 2;
        }
        return iteration > 1;
    }

    private int wordMatchOffset(int startIndex, int length, int startOffset, int minLength) {
        if (length < 2)
            return -1;

        if (startOffset == -1)
            startOffset = offsetsTable[fileContent.charAt(startIndex) - A_OFFSET]
                                      [fileContent.charAt(startIndex + 1) - A_OFFSET];

        if (startOffset == -1)
            return -1;

        for (; ; ) {
            if (fileContent.length() < startOffset + length + SEPARATOR.length()) {
                return -1;
            }
            if (fileContent.startsWith(SEPARATOR, startOffset)) {
                startOffset += SEPARATOR.length();
            }
            if (fileContent.charAt(startOffset) != fileContent.charAt(startIndex)
                    || fileContent.charAt(startOffset + 1) != fileContent.charAt(startIndex + 1)) {
                return -1;
            }

            boolean matchFind = true;
            for (int i = 2; i < length; i++) {
                if (fileContent.charAt(startOffset + i) == END_WORD_CHAR) {
                    if (i >= minLength) {
                        return startOffset;
                    } else {
                        startOffset += i + 1;
                        matchFind = false;
                        break;
                    }
                }
                if (fileContent.charAt(startOffset + i) != fileContent.charAt(startIndex + i)) {
                    startOffset = newWordIndex(startOffset + i) + SEPARATOR.length();
                    matchFind = false;
                    break;
                }
            }
            if (matchFind) {
                return (fileContent.charAt(startOffset + length) == END_WORD_CHAR) ? startOffset : -1;
            }
        }
    }

    private int newWordIndex(int offset) {
        for (; offset < fileContent.length(); offset++) {
            if (fileContent.charAt(offset) == END_WORD_CHAR)
                return offset;
        }
        return -1;
    }
}
