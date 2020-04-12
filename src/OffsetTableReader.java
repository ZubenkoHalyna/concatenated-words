class OffsetTableReader {
    private static final int A_OFFSET = 'a';
    private static final int NUMBER_OF_CHARS = 'z' - 'a' + 1;

    private final String fileName;

    public OffsetTableReader(String fileName) {
        this.fileName = fileName;
    }

    public int[][] read() {
        int[][] offsetsTable = new int[NUMBER_OF_CHARS][NUMBER_OF_CHARS];
        try (SessionFileReader reader = new SessionFileReader(fileName)) {
            String previousFirstChars = "";
            String line = "";
            for (char i = 'a'; i <= 'z'; i++) {
                for (char j = 'a'; j <= 'z'; j++) {
                    String currentFirstChars = "" + i + j;
                    for (; ; ) {
                        if (line.startsWith(previousFirstChars))
                            line = reader.read(); // read new line only if previous one was correctly processed

                        if (line != null && line.startsWith(currentFirstChars)) {
                            // Offset for string "" + i + j was found
                            offsetsTable[i - A_OFFSET][j - A_OFFSET] = reader.getOffset() - line.length() - 1;
                            previousFirstChars = currentFirstChars;
                            break;
                        }
                        if (line == null || !line.startsWith(previousFirstChars)) {
                            // Offset for string "" + i + j doesn't exist in file
                            offsetsTable[i - A_OFFSET][j - A_OFFSET] = -1;
                            break;
                        }
                    }
                }
            }
        }
        return offsetsTable;
    }
}
