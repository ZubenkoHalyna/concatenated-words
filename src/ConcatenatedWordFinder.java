import java.util.ArrayList;
import java.util.List;

public class ConcatenatedWordFinder {
    private static final int A_OFFSET = 'a';

    private final String fileName;
    private final int[][] offsetsTable;

    public ConcatenatedWordFinder(String fileName) {
        this.fileName = fileName;
        offsetsTable = new OffsetTableReader(fileName).read();
    }

    public String[] find() {
        List<String> largestWords = new ArrayList<>(List.of("", "", ""));
        try (SessionFileReader reader = new SessionFileReader(fileName)) {
            for (String line = reader.read(); line != null; line = reader.read()) {
                if (line.length() >= largestWords.get(2).length()) {
                    if (isConcatenatedWord(line, reader)) {
                        insert(largestWords, line);
                    }
                }
            }
        }
        return largestWords.toArray(new String[3]);
    }

    private boolean isConcatenatedWord(String word, SessionFileReader reader) {
        if (word.length() < 2) {
            return false;
        }
        int charsOffset = offsetsTable[word.charAt(0) - A_OFFSET][word.charAt(1) - A_OFFSET];
        if (charsOffset == -1) {
            return false;
        }
        reader.startNewSession(charsOffset);

        for (String line = reader.read(); line != null; line = reader.read()) {
            if (!line.startsWith("" + word.charAt(0) + word.charAt(1))) {
                reader.returnToPreviousSession();
                return false;
            }
            if (word.equals(line)) {
                reader.returnToPreviousSession();
                return true;
            }
            if (word.startsWith(line) && isConcatenatedWord(word.substring(line.length()), reader)) {
                reader.returnToPreviousSession();
                return true;
            }
        }
        reader.returnToPreviousSession();
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
