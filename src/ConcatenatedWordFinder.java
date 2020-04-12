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

    public Result find() {
        List<String> largestWords = new ArrayList<>(List.of("", ""));
        int totalNumberOfWords = 0;
        try (SessionFileReader reader = new SessionFileReader(fileName)) {
            for (String line = reader.read(); line != null; line = reader.read()) {
                if (isConcatenatedWord(line, reader, line.length())) {
                    if (line.length() >= largestWords.get(1).length())
                        insert(largestWords, line);
                    totalNumberOfWords++;
                }
            }
        }
        return new Result(largestWords.toArray(new String[2]), totalNumberOfWords);
    }

    private boolean isConcatenatedWord(String word, SessionFileReader reader, int initLength) {
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
                return word.length() < initLength;
            }
            if (word.startsWith(line) && isConcatenatedWord(word.substring(line.length()), reader, initLength)) {
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

    public static class Result {
        public final String[] largestWords;
        public final int totalNumberOfConcatenatedWords;

        public Result(String[] largestStrings, int totalNumberOfConcatenatedWords) {
            this.largestWords = largestStrings;
            this.totalNumberOfConcatenatedWords = totalNumberOfConcatenatedWords;
        }
    }
}
