import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Creates a subset of training data (200 users) in a format that this program can run.
 */
public class DataCreation {

    public static final String UNFORMATTED_RATINGS_PATH = "training-data/ratings.csv";
    public static final String UNFORMATTED_MOVIES_PATH = "training-data/movies.csv";
    public static final String FORMATTED_TRAINING_DATA_PATH = "training-data/training-data.txt";
    public static final String TEST_DATA_PATH = "test-data/custom-test.txt";
    public static final String TEST_RESULT_PATH = "test-data/custom-result.txt";

    private static class Pair {
        int movieId;
        String rating;

        public Pair(int movieId, String rating) {
            this.movieId = movieId;
            this.rating = rating;
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Enter 1 to create data. movies.csv and ratings.csv should be " +
                "present inside training-data folder");
        Scanner inScanner = new Scanner(System.in);
        int code = inScanner.nextInt();
        inScanner.close();
        if (code == 1) {
            Util.deleteFileAtPath(TEST_DATA_PATH);
            Util.deleteFileAtPath(TEST_RESULT_PATH);
            Util.deleteFileAtPath(FORMATTED_TRAINING_DATA_PATH);
            Map<Integer, Integer> movieIdMap = getMovieIdMap();
            createTrainingData(movieIdMap);
            createTestData(movieIdMap);
        } else {
            System.out.println("Skipping data creation");
        }
    }

    private static Map<Integer, Integer> getMovieIdMap() throws IOException {
        File file = new File(UNFORMATTED_MOVIES_PATH);
        Scanner scanner = new Scanner(file);
        List<String> t = new ArrayList<>();
        Map<Integer, Integer> movieIdMap = new HashMap<>();
        int c = 0;
        while (scanner.hasNextLine()) {
            String userLine = scanner.nextLine();
            String[] ratingsAsStrings = userLine.split(",");
            if (ratingsAsStrings.length == 0) continue;
            int movieId = Integer.parseInt(ratingsAsStrings[0]);
            movieIdMap.put(movieId, ++c);
        }
        return movieIdMap;
    }

    private static void createTestData(Map<Integer, Integer> movieIdMap) throws IOException {
        File file = new File(UNFORMATTED_RATINGS_PATH);
        FileWriter fileWriterTestData = new FileWriter(TEST_DATA_PATH);
        FileWriter fileWriterResult = new FileWriter(TEST_RESULT_PATH);
        Scanner scanner = new Scanner(file);
        Map<Integer, List<Pair>> map = new LinkedHashMap<>();
        while (scanner.hasNextLine()) {
            String userLine = scanner.nextLine();
            String[] ratingsAsStrings = userLine.split(",");
            if (ratingsAsStrings.length == 0) continue;
            int userId = Integer.parseInt(ratingsAsStrings[0]);
            int movieId = Integer.parseInt(ratingsAsStrings[1]);
            if (userId > 200) {
                List<Pair> list = map.getOrDefault(userId, new ArrayList<>());
                list.add(new Pair(movieIdMap.get(movieId), ratingsAsStrings[2]));
                map.put(userId, list);
            }
        }
        for (Map.Entry<Integer, List<Pair>> entry : map.entrySet()) {
            int userId = entry.getKey();
            List<Pair> movies = entry.getValue();
            if (movies.size() < 20) continue;
            int ratingsToCopy = 5;
            if (userId > 400) {
                ratingsToCopy = 10;
            }
            for (int i = 0; i < ratingsToCopy; i++) {
                fileWriterTestData.write(userId + " " + movies.get(i).movieId + " " + movies.get(i).rating + "\n");
            }
            for (int i = ratingsToCopy; i < movies.size(); i++) {
                fileWriterTestData.write(userId + " " + movies.get(i).movieId + " 0\n");
                fileWriterResult.write(userId + " " + movies.get(i).movieId + " " + movies.get(i).rating + "\n");
            }
        }
        fileWriterTestData.close();
        fileWriterResult.close();
    }

    private static void createTrainingData(Map<Integer, Integer> movieIdMap) throws IOException {
        File file = new File(UNFORMATTED_RATINGS_PATH);
        FileWriter fileWriter = new FileWriter(FORMATTED_TRAINING_DATA_PATH);
        Scanner scanner = new Scanner(file);
        List<String> t = new ArrayList<>();
        String[][] data = new String[200][9742];
        while (scanner.hasNextLine()) {
            String userLine = scanner.nextLine();
            String[] ratingsAsStrings = userLine.split(",");
            if (ratingsAsStrings.length == 0) continue;
            int userId = Integer.parseInt(ratingsAsStrings[0]);
            int movieId = Integer.parseInt(ratingsAsStrings[1]);
            int actualMovieId = movieIdMap.get(movieId);
            if (userId <= 200) data[userId - 1][actualMovieId - 1] = ratingsAsStrings[2];
        }
        for (int i = 0; i < data.length; i++) {
            String toWrite = Arrays.stream(data[i]).map(s -> {
                if (s == null) return "0";
                else return s;
            }).collect(Collectors.joining(" "));
            fileWriter.write(toWrite + "\n");
        }
        fileWriter.close();
    }
}
