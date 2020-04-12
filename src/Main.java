import java.util.Scanner;

public class Main {
    private static final String FILE_NAME_REQUEST = "Input file name: ";
    private static final String TIME_CONSUMED_FOR_ALGORITHM = "Time consumed for algorithm: %.2f s\n";
    private static final String RESULT_TITLE = "Largest words:";
    private static final String RESULT_ITEM_FORMAT = "%d. Length %d: %s\n";
    private static final String TOTAL_NUMBER_OF_CONCATENATED_WORDS = "Total number of concatenated words %s\n";

    public static void main(String[] args) {
        System.out.print(FILE_NAME_REQUEST);
        String fileName = new Scanner(System.in).nextLine();
        long start = System.currentTimeMillis();

        ConcatenatedWordFinder.Result result = new ConcatenatedWordFinder(fileName).find();

        long finish = System.currentTimeMillis();
        double timeConsumed = (finish - start) / 1000.0;
        System.out.format(TIME_CONSUMED_FOR_ALGORITHM, timeConsumed);

        System.out.println(RESULT_TITLE);
        for (int i = 0; i < result.largestWords.length; i++) {
            System.out.format(RESULT_ITEM_FORMAT, i + 1, result.largestWords[i].length(), result.largestWords[i]);
        }
        System.out.format(TOTAL_NUMBER_OF_CONCATENATED_WORDS, result.totalNumberOfConcatenatedWords);
    }
}
