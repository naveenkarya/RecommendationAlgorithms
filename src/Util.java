import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Util {

    public static List<List<Double>> parseTrainingDataFile(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);
        List<List<Double>> trainingData = new ArrayList<>();
        while (scanner.hasNextLine()) {
            List<Double> userRatings = new ArrayList<>();
            String userLine = scanner.nextLine();
            String[] ratingsAsStrings = userLine.split("\\s+");
            if (ratingsAsStrings.length == 0) continue;
            for (String rating : ratingsAsStrings) {
                userRatings.add(Double.parseDouble(rating));
            }
            trainingData.add(userRatings);
        }
        return trainingData;
    }

    public static List<Double> getIUFList(List<List<Double>> trainingData) {
        List<Double> iufList = new ArrayList<>();
        for (int i = 0; i < trainingData.get(0).size(); i++) {
            int numberOfUsersThatRatedThisMovie = 0;
            for (int j = 0; j < trainingData.size(); j++) {
                if (trainingData.get(j).get(i) != 0) numberOfUsersThatRatedThisMovie++;
            }
            if (numberOfUsersThatRatedThisMovie == 0) iufList.add(logBase(trainingData.size(), 10));
            else iufList.add(logBase(1.0 * trainingData.size() / numberOfUsersThatRatedThisMovie, 10));
        }
        return iufList;
    }

    public static double logBase(double number, int base) {
        return Math.log(number) / Math.log(base);
    }

    /**
     *
     * @param vector1
     * @param vector2
     * @return Returns cosine similarity of two vectors
     */
    public static double cosineSimilarity(List<Double> vector1, List<Double> vector2) {
        double dotProduct = 0.0;
        double vector1Size = 0;
        double vector2Size = 0;
        for (int i = 0; i < vector2.size(); i++) {
            double vector1Value = vector1.get(i);
            double vector2Value = vector2.get(i);
            dotProduct = dotProduct + (vector1Value * vector2Value);
            vector1Size = vector1Size + Math.pow(vector1Value, 2);
            vector2Size = vector2Size + Math.pow(vector2Value, 2);
        }
        if (dotProduct == 0) return 0;
        double cosineSimilarity =
                1.0 * dotProduct / (Math.sqrt(vector1Size) * Math.sqrt(vector2Size));
        return cosineSimilarity;
    }

    /**
     *
     * @param vector1
     * @param vector2
     * @return Returns Inverse Euclidean distance between the two vectors
     */
    public static double inverseEuclideanDistance(List<Double> vector1, List<Double> vector2) {
        double sum = 0.0;
        for (int i = 0; i < vector2.size(); i++) {
            double vector1Value = vector1.get(i);
            double vector2Value = vector2.get(i);
            sum = sum + Math.pow(vector1Value - vector2Value, 2);
        }
        return 1 / (1 + Math.sqrt(sum));
    }

    /**
     *
     * @param vector1
     * @param vector2
     * @return Returns the mean Euclidean distance between the two vectors
     */
    public static double meanEuclideanDistance(List<Double> vector1, List<Double> vector2) {
        double sum = 0.0;
        double max = 0.0;
        for (int i = 0; i < vector2.size(); i++) {
            double vector1Value = vector1.get(i);
            double vector2Value = vector2.get(i);
            sum = sum + Math.pow(vector1Value - vector2Value, 2);
            max = max + 16.0;
        }
        return 1 - (Math.sqrt(sum)/Math.sqrt(max));
    }

    /**
     * Writes the result list to the output file
     */
    public static void writeToOutputFile(FileWriter outputFileWriter, List<OutputFormat> resultList) throws IOException {
        for (OutputFormat outputFormat : resultList) {
            outputFileWriter.write(outputFormat.getUserId() + " " +
                    outputFormat.getMovieId() + " " + outputFormat.getRating() + "\n");
        }
    }

    /**
     * Deletes a directory with all its contents recursively
     */
    public static boolean deleteDir(File dirToDelete) {
        File[] files = dirToDelete.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteDir(file);
            }
        }
        return dirToDelete.delete();
    }


    /**
     * @param trainingData Training Data matrix
     * @return A list of average ratings. The item at i-th index of the list represents the average
     * rating that the users have given to the movie number (i + 1)
     */
    public static List<Double> getEachMoviesAvgRatingWithThreshold(List<List<Double>> trainingData) {
        List<Double> avgUserRatings = new ArrayList<>();
        for (int i = 0; i < trainingData.get(0).size(); i++) {
            int numberOfUsersThatRatedMovieI = 0;
            double ratingSumForMovieI = 0.0;
            for (int j = 0; j < trainingData.size(); j++) {
                double userJRatingForMovieI = trainingData.get(j).get(i);
                if (userJRatingForMovieI != 0) {
                    ratingSumForMovieI += userJRatingForMovieI;
                    numberOfUsersThatRatedMovieI++;
                }
            }
            // Add average only if sufficient users have rated that movie
            if (numberOfUsersThatRatedMovieI > 0) {
                avgUserRatings.add(1.0 * ratingSumForMovieI / numberOfUsersThatRatedMovieI);
            } else {
                avgUserRatings.add(0.0);
            }
        }
        return avgUserRatings;
    }
    /**
     * @param trainingData Training Data matrix
     * @return A list of average ratings. The item at i-th index of the list represents the average
     * rating that the user with ID (i + 1) has given to all the movies that he has rated.
     */
    public static List<Double> getEachUsersAvgRating(List<List<Double>> trainingData) {
        List<Double> avgList =
                trainingData.stream()
                        .map(list -> IntStream.range(0, list.size()).filter(i -> list.get(i) != 0).mapToDouble(i -> list.get(i)).average().getAsDouble())
                        .collect(Collectors.toList());
        return avgList;
    }

    public static double jaccardSimilarity(List<Double> trainingUserRatings, Set<Integer> keySet) {
        int a = 0;
        int b = 0;
        int c = 0;
        for (int movieId = 1; movieId <= trainingUserRatings.size(); movieId++) {
            if (keySet.contains(movieId) && trainingUserRatings.get(movieId - 1) != 0) {
                a++;
            } else if (!keySet.contains(movieId) && trainingUserRatings.get(movieId - 1) != 0) {
                b++;
            } else if (keySet.contains(movieId) && trainingUserRatings.get(movieId - 1) == 0) {
                c++;
            }
        }
        return (double) a / (a + b + c);
    }

    public static void deleteFileAtPath(String filePath) {
        File file = new File(filePath);
        if(file.exists()) file.delete();
    }
}
