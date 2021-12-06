import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RecommendationSystemMain {
    // TODO: Remove test files and extra variables
    private static final String OUTPUT_DIR = "result";
    private static final String TEST_DATA_DIR = "test-data";

    private static final String TRAINING_DATA_FILE_PATH = "training-data/custom-train-test.txt";
    private static final Map<String, String> TEST_INPUT_OUTPUT_FILE_MAP = Map.of("custom-test.txt",
    "custom-predicted" +
            "-result.txt");
    /*private static final String TRAINING_DATA_FILE_PATH = "training-data/train.txt";
    private static final Map<String, String> TEST_INPUT_OUTPUT_FILE_MAP = Map.of("test5.txt", "result5.txt",
            "test10" +
                    ".txt", "result10.txt", "test20.txt", "result20.txt");*/

    public static void main(String[] args) throws IOException {
        for (AlgorithmEnum algo : AlgorithmEnum.values()) {
            System.out.println("Enter " + algo.getAlgoCode() + " for " + algo.getAlgoName());
        }
        Scanner myObj = new Scanner(System.in);
        String algoCode = myObj.nextLine().trim();
        AlgorithmEnum algorithmEnum = AlgorithmEnum.fromCode(algoCode);
        System.out.println("Executing with algorithm: " + algorithmEnum.getAlgoName());
        System.out.println("...");
        Util.deleteDir(new File(OUTPUT_DIR));
        Files.createDirectories(Paths.get(OUTPUT_DIR));
        List<List<Double>> trainingData = Util.parseTrainingDataFile(TRAINING_DATA_FILE_PATH);
        List<Double> avgUserRatingsForMovies = Util.getEachMoviesAvgRatingWithThreshold(trainingData, 0);
        RatingAlgorithm ratingAlgorithm = RatingAlgorithm.getAlgorithm(algorithmEnum, trainingData,
                avgUserRatingsForMovies);
        for (Map.Entry<String, String> fileEntry : TEST_INPUT_OUTPUT_FILE_MAP.entrySet()) {
            String testInputFileName = TEST_DATA_DIR + "/" + fileEntry.getKey();
            String outputFileName = OUTPUT_DIR + "/" + fileEntry.getValue();
            File outputFile = new File(outputFileName);
            outputFile.createNewFile();
            FileWriter outputFileWriter = new FileWriter(outputFile);
            Scanner inputScanner = new Scanner(new File(testInputFileName));
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
        }
        System.out.println("Execution completed. Result files are generated in the result folder.");
        MAECalculator.printMAE("result/custom-predicted-result.txt", "test-data/custom-result.txt");
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
                System.out.println("less than 1: "+rating);
            }
            if (rating > 5){
                rating = 5;
                System.out.println("greater than 5: "+rating);
            }
            resultList.add(new OutputFormat(queryUserId, queryMovieId, rating));
        }
        resultList.sort(Comparator.comparingInt(OutputFormat::getUserId).thenComparingInt(OutputFormat::getMovieId));
        return resultList;
    }


}
