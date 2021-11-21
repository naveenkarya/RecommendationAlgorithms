import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DataCreation {
    private static class Pair {
        int movieId;
        String rating;

        public Pair(int movieId, String rating) {
            this.movieId = movieId;
            this.rating = rating;
        }
    }
    //TODO: Remove it and other related files.
    public static void main(String[] args) throws IOException {
        Map<Integer, Integer> movieIdMap = getMovieIdMap();
        //createTrainingData(movieIdMap);
        //createTestData(movieIdMap);
    }

    private static Map<Integer, Integer> getMovieIdMap() throws IOException {
        File file = new File("training-data/custom-movies-unformatted.txt");
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
        File file = new File("training-data/custom-ratings-unformatted.txt");
        FileWriter fileWriterTestData = new FileWriter("test-data/custom-test.txt");
        FileWriter fileWriterResult = new FileWriter("test-data/custom-result.txt");
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
            if(userId > 400) {
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
        File file = new File("training-data/custom-ratings-unformatted.txt");
        FileWriter fileWriter = new FileWriter("training-data/custom-train-test.txt");
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
