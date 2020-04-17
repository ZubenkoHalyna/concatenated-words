import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.print("Input file name: ");
        String fileName = new Scanner(System.in).nextLine();
        long start = System.currentTimeMillis();

        Result result = new ConcatenatedWordFinder(fileName).find();

        long finish = System.currentTimeMillis();
        double timeConsumed = (finish - start) / 1000.0;

        System.out.format("Time consumed for algorithm: %.2f s\n", timeConsumed);
        System.out.println("Largest words:");
        List<String> largestWords = result.getLargestWords();
        for (int i = 0; i < largestWords.size(); i++) {
            System.out.format("%d. Length %d: %s\n", i + 1, largestWords.get(i).length(), largestWords.get(i));
        }
        System.out.format("Total number of concatenated words %s\n", result.getTotalNumberOfWords());
    }
}

