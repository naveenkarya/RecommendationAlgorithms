import java.util.*;

public class UserBasedCosineSimilarity extends RatingAlgorithm {

    public UserBasedCosineSimilarity(List<List<Double>> trainingData, List<Double> avgUserRatingsForMovies) {
        super(trainingData, avgUserRatingsForMovies);
    }

    public double estimateUserRatingForAMovie(Map<Integer, Double> queryUserRatingMap, int queryMovieId) {
        PriorityQueue<WeightedRating> kNearestUsers =
                new PriorityQueue<>(Comparator.comparingDouble(WeightedRating::getWeight));
        double queryUserActualAvgRating =
                queryUserRatingMap.values().stream().mapToDouble(a -> a).average().getAsDouble();
        for (int trainingUserId = 1; trainingUserId <= trainingData.size(); trainingUserId++) {
            List<Double> trainingUserRatings = trainingData.get(trainingUserId - 1);
            // Compare with user only if that user has rated the movie we need to predict the rating for
            if (trainingUserRatings.get(queryMovieId - 1) == 0) continue;

            // Calculate current user vector (for whom we need to rate) and training user's vector (the user with
            // which we are comparing)
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
            //Ignore unit vectors
            if (trainingUserVector.size() >= 2) {
                double cosineSimilarity = Util.cosineSimilarity(queryUserVector, trainingUserVector);
                if (cosineSimilarity > 0.7) {
                    kNearestUsers.add(new WeightedRating(cosineSimilarity, trainingUserRatings.get(queryMovieId - 1)));
                    if (kNearestUsers.size() > K) kNearestUsers.poll();
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
            else return (queryUserActualAvgRating + avgUserRatingForQueryMovie) / 2;
        }
    }
}
