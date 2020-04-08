import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class Main {
    private static final int A_OFFSET = 'a';
    private static int[][] offsetsTable;

    public static void main(String[] args) {
        System.out.print("Input file name: ");
        String fileName = new Scanner(System.in).nextLine();
        File textFile = new File(fileName);
        offsetsTable = new OffsetTableReader().read(textFile);

        String[] largestWords = {"", "", ""};
        try (RandomAccessFile file = new RandomAccessFile(textFile, "r")) {
            for (String line = file.readLine(); line != null; line = file.readLine()) {
                if (line.length() >= largestWords[2].length()) {
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

        printResult(largestWords, 0);
        printResult(largestWords, 1);
        printResult(largestWords, 2);
    }

    private static boolean isConcatenatedWord(String word, RandomAccessFile file) throws IOException {
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

    private static void printResult(String[] largestWord, int i) {
        System.out.println(largestWord[i].length() + ": " + largestWord[i]);
    }

    private static void insert(String[] largestWords, String newWord) {
        if (newWord.length() > largestWords[0].length()) {
            largestWords[2] = largestWords[1];
            largestWords[1] = largestWords[0];
            largestWords[0] = newWord;
            return;
        }
        if (newWord.length() > largestWords[1].length()) {
            largestWords[2] = largestWords[1];
            largestWords[1] = newWord;
            return;
        }
        if (newWord.length() > largestWords[2].length()) {
            largestWords[2] = newWord;
        }
    }
}
