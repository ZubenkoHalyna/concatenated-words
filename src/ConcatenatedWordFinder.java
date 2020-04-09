import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class ConcatenatedWordFinder {
    private static final int A_OFFSET = 'a';
    private static final String FILE_READ_MODE = "r";

    private final String fileName;
    private final int[][] offsetsTable;

    public ConcatenatedWordFinder(String fileName) {
        this.fileName = fileName;
        offsetsTable = new OffsetTableReader().read(new File(fileName));
    }

    public String[] find() {
        File textFile = new File(fileName);

        List<String> largestWords = new ArrayList<>(List.of("", "", ""));
        try (RandomAccessFile file = new RandomAccessFile(textFile, FILE_READ_MODE)) {
            for (String line = file.readLine(); line != null; line = file.readLine()) {
                if (line.length() >= largestWords.get(2).length()) {
                    long offset = file.getFilePointer();
                    if (isConcatenatedWord(line, file)) {
                        insert(largestWords, line);
                    }
                    file.seek(offset);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return largestWords.toArray(new String[3]);
    }


    private boolean isConcatenatedWord(String word, RandomAccessFile file) throws IOException {
        if (word.length() < 2) {
            return false;
        }
        int charsOffset = offsetsTable[word.charAt(0) - A_OFFSET][word.charAt(1) - A_OFFSET];
        if (charsOffset == -1) {
            return false;
        }
        file.seek(charsOffset);

        for (String line = file.readLine(); line != null; line = file.readLine()) {
            if (!line.startsWith("" + word.charAt(0) + word.charAt(1))) {
                return false;
            } else if (word.equals(line)) {
                return true;
            } else if (word.startsWith(line)) {
                long offset = file.getFilePointer();
                if (isConcatenatedWord(word.substring(line.length()), file)) {
                    return true;
                }
                file.seek(offset);
            }
        }
        return false;
    }

    private static void insert(List<String> largestWords, String newWord) {
        for (int i = 0; i < largestWords.size(); i++) {
            if (largestWords.get(i).length() < newWord.length()) {
                largestWords.add(i, newWord);
                largestWords.remove(largestWords.size() - 1);
                break;
            }
        }
    }
}
