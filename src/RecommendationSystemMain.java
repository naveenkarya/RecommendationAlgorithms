import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Source of the data:
 * https://grouplens.org/datasets/movielens/
 * F. Maxwell Harper and Joseph A. Konstan. 2015. The MovieLens Datasets: History and Context. ACM Transactions on Interactive Intelligent Systems (TiiS) 5, 4: 19:1–19:19. https://doi.org/10.1145/2827872
 */
public class RecommendationSystemMain {
    private static final String OUTPUT_DIR = "result";
    public static final String PREDICTED_RESULT_PATH = OUTPUT_DIR + "/" + "custom-predicted-result.txt";

    // Main method to be executed
    public static void main(String[] args) throws IOException {
        // Read input from command line
        for (AlgorithmEnum algo : AlgorithmEnum.values()) {
            System.out.println("Enter " + algo.getAlgoCode() + " for " + algo.getAlgoName());
        }
        Scanner inScanner = new Scanner(System.in);
        String algoCode = inScanner.nextLine().trim();
        inScanner.close();
        AlgorithmEnum algorithmEnum = AlgorithmEnum.fromCode(algoCode);
        System.out.println("Executing with algorithm: " + algorithmEnum.getAlgoName());
        System.out.println("...");
        // Delete already existing output directory and create new output directory
        Util.deleteDir(new File(OUTPUT_DIR));
        Files.createDirectories(Paths.get(OUTPUT_DIR));
        // Parse training data and store it as a matrix
        List<List<Double>> trainingData = Util.parseTrainingDataFile(DataCreation.FORMATTED_TRAINING_DATA_PATH);
        List<Double> avgUserRatingsForMovies = Util.getEachMoviesAvgRatingWithThreshold(trainingData);
        RatingAlgorithm ratingAlgorithm = RatingAlgorithm.getAlgorithm(algorithmEnum, trainingData,
                avgUserRatingsForMovies);
        File outputFile = new File(PREDICTED_RESULT_PATH);
        outputFile.createNewFile();
        FileWriter outputFileWriter = new FileWriter(outputFile);
        Scanner inputScanner = new Scanner(new File(DataCreation.TEST_DATA_PATH));
        // Parse each line of test-data one line at a time.
        try {
            int lastUserId = -1;
            Map<Integer, Double> userRatingMap = null;
            List<Integer> queryList = null;
            do {
                String userLine = inputScanner.nextLine();
                String[] userData = userLine.split(" ");
                int userId = Integer.parseInt(userData[0]);
                if (userId != lastUserId) {
                    List<OutputFormat> resultList = estimateRatingsForUserBlock(userRatingMap, lastUserId,
                            queryList,
                            ratingAlgorithm);
                    Util.writeToOutputFile(outputFileWriter, resultList);
                    userRatingMap = new HashMap<>();
                    queryList = new ArrayList<>();
                    lastUserId = userId;
                }
                int movieId = Integer.parseInt(userData[1]);
                double movieRating = Double.parseDouble(userData[2]);
                if (movieRating == 0) queryList.add(movieId);
                else userRatingMap.put(movieId, movieRating);
            }
            while (inputScanner.hasNextLine());
            List<OutputFormat> resultList = estimateRatingsForUserBlock(userRatingMap, lastUserId, queryList,
                    ratingAlgorithm);
            Util.writeToOutputFile(outputFileWriter, resultList);
        } finally {
            inputScanner.close();
            outputFileWriter.close();
        }
        System.out.println("Execution completed. Result files are generated in the result folder.");
        MAECalculator.printMAE(PREDICTED_RESULT_PATH, DataCreation.TEST_RESULT_PATH);
    }


    private static List<OutputFormat> estimateRatingsForUserBlock(Map<Integer, Double> userRatingMap, int queryUserId,
                                                                  List<Integer> movieQueryList,
                                                                  RatingAlgorithm ratingAlgorithm) {

        if (queryUserId == -1) return Collections.emptyList();
        List<OutputFormat> resultList = new ArrayList<>();
        for (int queryMovieId : movieQueryList) {
            short rating = (short) Math.round(ratingAlgorithm.estimateUserRatingForAMovie(userRatingMap, queryMovieId));
            // Handle overflows and underflows
            if (rating < 1) {
                rating = 1;
            }
            if (rating > 5) {
                rating = 5;
            }
            resultList.add(new OutputFormat(queryUserId, queryMovieId, rating));
        }
        resultList.sort(Comparator.comparingInt(OutputFormat::getUserId).thenComparingInt(OutputFormat::getMovieId));
        return resultList;
    }


}
