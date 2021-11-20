import java.util.*;

public class UserBasedCosineSimilarity extends RatingAlgorithm {

    public UserBasedCosineSimilarity(List<List<Double>> trainingData, List<Double> avgUserRatingsForMovies) {
        super(trainingData, avgUserRatingsForMovies);
    }

    public double estimateUserRatingForAMovie(Map<Integer, Double> queryUserRatingMap, int queryMovieId) {
        int minK = minKMap.get(queryUserRatingMap.size());
        int maxK = maxKMap.get(queryUserRatingMap.size());
        PriorityQueue<WeightedRating> kNearestUsers =
                new PriorityQueue<>(Comparator.comparingDouble(WeightedRating::getWeight).thenComparing(WeightedRating::getNumOfCommonMovies));
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
           if (trainingUserVector.size() >= 2) {
                double cosineSimilarity = Util.cosineSimilarity(queryUserVector, trainingUserVector);
                if(cosineSimilarity > 0.7) {
                    kNearestUsers.add(new WeightedRating(Math.round(cosineSimilarity * 100.0) / 100.0,
                            trainingUserRatings.get(queryMovieId - 1), trainingUserVector.size()));
                }
            }
        }
        if (kNearestUsers.size() > 1) {
            while (kNearestUsers.size() > minK && kNearestUsers.peek().getWeight() < 0.9) kNearestUsers.poll();
            while (kNearestUsers.size() > maxK) kNearestUsers.poll();
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
