import java.util.*;

public class ItemBasedFiltering extends RatingAlgorithm {
    List<Double> eachUsersAvgRatings;

    public ItemBasedFiltering(List<List<Double>> trainingData, List<Double> avgUserRatingsForMovies) {
        super(trainingData, avgUserRatingsForMovies);
        this.eachUsersAvgRatings = Util.getEachUsersAvgRating(trainingData);
    }

    public double estimateUserRatingForAMovie(Map<Integer, Double> queryUserRatingMap, int queryMovieId) {
        PriorityQueue<WeightedRating> kNearestMovies =
                new PriorityQueue<>(Comparator.comparingDouble(WeightedRating::getWeight).thenComparing(WeightedRating::getNumOfCommonMovies));
        double queryUserActualAvgRating =
                queryUserRatingMap.values().stream().mapToDouble(a -> a).average().getAsDouble();
        for (Map.Entry<Integer, Double> queryUserRatingForAMovie : queryUserRatingMap.entrySet()) {
            int currMovieId = queryUserRatingForAMovie.getKey();
            double queryUserRatingForCurrMovie = queryUserRatingForAMovie.getValue();
            List<Double> queryMovieVector = new ArrayList<>();
            List<Double> currMovieVector = new ArrayList<>();
            for (int userId = 1; userId <= trainingData.size(); userId++) {
                double queryMovieRatingByUser = trainingData.get(userId - 1).get(queryMovieId - 1);
                double currMovieRatingByUser = trainingData.get(userId - 1).get(currMovieId - 1);
                if (queryMovieRatingByUser != 0 && currMovieRatingByUser != 0) {
                    queryMovieVector.add(queryMovieRatingByUser - eachUsersAvgRatings.get(userId - 1));
                    currMovieVector.add(currMovieRatingByUser - eachUsersAvgRatings.get(userId - 1));
                }
            }
            if (queryMovieVector.size() >= 2) {
                double cosineSimilarity = Util.cosineSimilarity(queryMovieVector, currMovieVector);
                kNearestMovies.add(new WeightedRating(Math.round(cosineSimilarity * 100.0) / 100.0,
                            queryUserRatingForCurrMovie, queryMovieVector.size()));

            }
        }
        if (kNearestMovies.size() > 0) {
            double weightedRatingSum = 0.0;
            double totalWeight = 0.0;
            while (!kNearestMovies.isEmpty()) {
                WeightedRating weightedRating = kNearestMovies.poll();
                double weightOfRating = weightedRating.getWeight();
                weightedRatingSum =
                        weightedRatingSum + ((weightedRating.getRating() - queryUserActualAvgRating) * weightOfRating);
                totalWeight = totalWeight + Math.abs(weightOfRating);
            }
            double weightedAvgRating = queryUserActualAvgRating + (weightedRatingSum / totalWeight);
            return weightedAvgRating;
        } else {
            // Not enough similar users found. Take user's average rating
            double avgUserRatingForQueryMovie = avgUserRatingsForMovies.get(queryMovieId - 1);
            if (avgUserRatingForQueryMovie == 0) return queryUserActualAvgRating;
            else return avgUserRatingForQueryMovie;
        }
    }
}
