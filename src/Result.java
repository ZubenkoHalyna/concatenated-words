import java.util.ArrayList;
import java.util.List;

public class Result {
    private int totalNumberOfWords;
    private int[] largestWordsStart;
    private int[] largestWordsEnd;
    private List<String> largestWords;

    public Result() {
        largestWordsStart = new int[2];
        largestWordsEnd = new int[2];
    }

    synchronized public void addWord(int startOffset, int endOffset) {
        totalNumberOfWords++;
        for (int i = 0; i < largestWordsStart.length; i++) {
            if (largestWordsEnd[i] - largestWordsStart[i] < endOffset - startOffset) {
                largestWordsStart[i] = startOffset;
                largestWordsEnd[i] = endOffset;
                break;
            }
        }
    }

    public void fillLargestWords(String fileContent) {
        largestWords = new ArrayList<>(largestWordsStart.length);
        for (int i = 0; i < largestWordsStart.length; i++) {
            largestWords.add(fileContent.substring(largestWordsStart[i], largestWordsEnd[i]));
        }
    }

    public int getTotalNumberOfWords() {
        return totalNumberOfWords;
    }

    public List<String> getLargestWords() {
        return List.copyOf(largestWords);
    }
}
