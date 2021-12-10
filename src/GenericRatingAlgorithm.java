import java.util.*;

/**
 * Generic Rating Algorithm that multiplies 2 similarity metrics. Any 2 of these metrics can be combined:
 * Cosine Similarity, Jaccard Similarity, Simple Log Similarity, Euclidean Similarity, Normalized-Euclidean_Distance
 */
public class GenericRatingAlgorithm extends RatingAlgorithm {
    public static final String COSINE_SIMILARITY = "Cosine_Similarity";
    public static final String JACCARD_SIMILARITY = "Jaccard_Similarity";
    public static final String SIMPLE_LOG_SIMILARITY = "Simple_Log_Similarity";
    public static final String EUCLIDEAN_SIMILARITY = "Euclidean_Similarity";
    public static final String NORMALIZED_EUCLIDEAN_DISTANCE = "Normalized-Euclidean_Distance";
    private final double threshold;
    private String similarityMetric1;
    private String similarityMetric2;
    private Map<Integer, Integer> kMap = Map.of(5, 10, 10, 20, 20, 25);

    public GenericRatingAlgorithm(List<List<Double>> trainingData, List<Double> avgUserRatingsForMovies, String similarityMetric1, String similarityMetric2, double threshold) {
        super(trainingData, avgUserRatingsForMovies);
        this.similarityMetric1 = similarityMetric1;
        this.similarityMetric2 = similarityMetric2;
        this.threshold = threshold;
    }

    public double estimateUserRatingForAMovie(Map<Integer, Double> queryUserRatingMap, int queryMovieId) {
        int K = kMap.get(queryUserRatingMap.size());
        PriorityQueue<WeightedRating> kNearestUsers =
                new PriorityQueue<>(Comparator.comparingDouble(WeightedRating::getWeight));
        double queryUserActualAvgRating =
                queryUserRatingMap.values().stream().mapToDouble(a -> a).average().getAsDouble();
        for (int trainingUserId = 1; trainingUserId <= trainingData.size(); trainingUserId++) {
            List<Double> trainingUserRatings = trainingData.get(trainingUserId - 1);
            // Compare with user only if that user has rated the movie we need to predict the rating for
            if (trainingUserRatings.get(queryMovieId - 1) == 0) continue;

            // Calculate current user and training user's vectors
            List<Double> queryUserVector = new ArrayList<>();
            List<Double> trainingUserVector = new ArrayList<>();
            for (Map.Entry<Integer, Double> queryUserRatingForAMovie : queryUserRatingMap.entrySet()) {
                int movieId = queryUserRatingForAMovie.getKey();
                double queryUserRating = queryUserRatingForAMovie.getValue();
                double trainingUserRating = trainingUserRatings.get(movieId - 1);
                // Add to vector only if the movie was rated by the user
                if (trainingUserRating != 0) {
                    queryUserVector.add(queryUserRating);
                    trainingUserVector.add(trainingUserRating);
                }
            }
            // ignore unit vectors
            if (trainingUserVector.size() >= 2) {
                double similarity1 = getSimilarity(similarityMetric1, queryUserVector, trainingUserVector, trainingUserRatings, queryUserRatingMap);
                if(similarityMetric1.equals(COSINE_SIMILARITY) && similarity1 < 0.7) continue;
                double similarity2 = getSimilarity(similarityMetric2, queryUserVector, trainingUserVector, trainingUserRatings, queryUserRatingMap);
                double combinedSimilarity = similarity1 * similarity2;
                if(combinedSimilarity > threshold) {
                    kNearestUsers.add(new WeightedRating(similarity1 * similarity2,
                            trainingUserRatings.get(queryMovieId - 1)));
                    if (kNearestUsers.size() > K) {
                        kNearestUsers.poll();
                    }
                }
            }
        }

        if (kNearestUsers.size() > 0) {
            double avgRating = 0.0;
            double totalWeight = 0.0;
            while (!kNearestUsers.isEmpty()) {
                WeightedRating weightedRating = kNearestUsers.poll();
                avgRating = avgRating + (weightedRating.getRating() * weightedRating.getWeight());
                totalWeight = totalWeight + weightedRating.getWeight();
            }
            double weightedAvgRating = 1.0 * avgRating / totalWeight;
            return weightedAvgRating;
        } else {
            // Not enough similar users found. Take average of <user's average rating> and <average rating of that
            // movie> in the corpus
            double avgUserRatingForQueryMovie = avgUserRatingsForMovies.get(queryMovieId - 1);
            if (avgUserRatingForQueryMovie == 0) return queryUserActualAvgRating;
            else return (avgUserRatingForQueryMovie + queryUserActualAvgRating)/2;
        }
    }

    private double getSimilarity(String similarityMetric, List<Double> queryUserVector, List<Double> trainingUserVector, List<Double> trainingUserRatings, Map<Integer, Double> queryUserRatingMap) {
        if(similarityMetric.equals(EUCLIDEAN_SIMILARITY)) {
            return Util.inverseEuclideanDistance(queryUserVector, trainingUserVector);
        }
        else if (similarityMetric.equals(SIMPLE_LOG_SIMILARITY)) {
            return Util.logBase(trainingUserVector.size(), queryUserRatingMap.size());
        }
        else if (similarityMetric.equals(COSINE_SIMILARITY)) {
            return Util.cosineSimilarity(queryUserVector, trainingUserVector);
        }
        else if (similarityMetric.equals(JACCARD_SIMILARITY)) {
            return Util.jaccardSimilarity(trainingUserRatings, queryUserRatingMap.keySet());
        }
        else if (similarityMetric.equals(NORMALIZED_EUCLIDEAN_DISTANCE)) {
            return Util.meanEuclideanDistance(queryUserVector, trainingUserVector);
        }
        return 1.0;
    }
}
