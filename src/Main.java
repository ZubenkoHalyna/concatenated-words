import java.util.Scanner;

public class Main {
    private static final int A_OFFSET = 'a';
    private static int[][] offsetsTable;

    public static void main(String[] args) {
        System.out.print("Input file name: ");
        String fileName = new Scanner(System.in).nextLine();
        long start = System.currentTimeMillis();

        String[] largestWords = new  ConcatenatedWordFinder(fileName).find();

        long finish = System.currentTimeMillis();
        double timeConsumed = (finish - start) / 1000.0;
        System.out.println("Time consumed for algorithm: " + timeConsumed + " s");

        printResult(largestWords, 0);
        printResult(largestWords, 1);
        printResult(largestWords, 2);
    }

    private static void printResult(String[] largestWord, int i) {
        System.out.println(largestWord[i].length() + ": " + largestWord[i]);
    }
}
