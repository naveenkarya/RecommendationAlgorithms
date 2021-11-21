import java.util.List;
import java.util.Map;

public abstract class RatingAlgorithm {
    protected final List<List<Double>> trainingData;
    protected List<Double> avgUserRatingsForMovies;
    public Map<Integer, Integer> minKMap = Map.of(3, 3, 5, 5, 10, 7, 20, 10);
    public Map<Integer, Integer> maxKMap = Map.of(3, 5, 5, 10, 10, 15, 20, 25);
    /*public Map<Integer, Integer> minKMap = Map.of(3, 3, 5, 5, 10, 10, 20, 15);
    public Map<Integer, Integer> maxKMap = Map.of(3, 5, 5, 15, 10, 20, 20, 25);*/
    public RatingAlgorithm(List<List<Double>> trainingData, List<Double> avgUserRatingsForMovies) {
        this.trainingData = trainingData;
        this.avgUserRatingsForMovies = avgUserRatingsForMovies;
    }
    int K = 25;
    static RatingAlgorithm getAlgorithm(AlgorithmEnum algo, List<List<Double>> trainingData, List<Double> avgUserRatingsForMovies) {
        switch (algo) {
            case BASIC_COLLABORATIVE_FILTERING:
                return new UserBasedCosineSimilarity(trainingData, avgUserRatingsForMovies);
            case PEARSON_CORRELATION:
                return new PearsonCorrelation(trainingData, avgUserRatingsForMovies);
            case PEARSON_CORRELATION_IUF_ONLY:
                return new PearsonCorrelation(trainingData, avgUserRatingsForMovies, true, false);
            case PEARSON_CORRELATION_CASE_MOD_ONLY:
                return new PearsonCorrelation(trainingData, avgUserRatingsForMovies, false, true);
            case ITEM_BASED_CF:
                return new ItemBasedFiltering(trainingData, avgUserRatingsForMovies);
            case USER_AND_ITEMS_AVERAGE:
                return new UserAndItemAverage(trainingData, avgUserRatingsForMovies);
            case COSINE_SIMILARITY_AND_ITEM_BASED:
                return new CosineSimilarityAndItemBased(trainingData, avgUserRatingsForMovies);
            case NORMALIZED_EUCLIDEAN_DISTANCE:
                return new GenericRatingAlgorithm(trainingData, avgUserRatingsForMovies, GenericRatingAlgorithm.NORMALIZED_EUCLIDEAN_DISTANCE, "", 0.3);
            case EUCLIDEAN_DISTANCE_WITH_LOG_BASE:
                return new GenericRatingAlgorithm(trainingData, avgUserRatingsForMovies, GenericRatingAlgorithm.EUCLIDEAN_SIMILARITY, GenericRatingAlgorithm.SIMPLE_LOG_SIMILARITY, 0.0);
            case EUCLIDEAN_DISTANCE_WITH_JACCARD:
                return new GenericRatingAlgorithm(trainingData, avgUserRatingsForMovies, GenericRatingAlgorithm.EUCLIDEAN_SIMILARITY, GenericRatingAlgorithm.JACCARD_SIMILARITY, 0.0);
            case WEIGHTED_SLOPE_ONE:
                return new WeightedSlopeOneAlgorithm(trainingData, avgUserRatingsForMovies);
        }
        throw new IllegalArgumentException("Algorithm not found");
    }

    public abstract double estimateUserRatingForAMovie(Map<Integer, Double> userRatingMap, int queryMovieId);
}
