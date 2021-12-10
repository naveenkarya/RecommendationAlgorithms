import java.util.*;
import java.util.stream.IntStream;

public class PearsonCorrelation extends RatingAlgorithm {
    private static final double AMPLIFICATION = 2;
    private List<Double> iufList;
    boolean useInverseUserFrequency;
    boolean useCaseModification;
    int K = 20;

    public PearsonCorrelation(List<List<Double>> trainingData, List<Double> avgUserRatingsForMovies) {
        this(trainingData, avgUserRatingsForMovies, false, false);
    }

    public PearsonCorrelation(List<List<Double>> trainingData, List<Double> avgUserRatingsForMovies,
                              boolean useInverseUserFrequency,
                              boolean useCaseModification) {
        super(trainingData, avgUserRatingsForMovies);
        this.useInverseUserFrequency = useInverseUserFrequency;
        this.useCaseModification = useCaseModification;
        if (useInverseUserFrequency) {
            this.iufList = Util.getIUFList(trainingData);
        }
    }

    public double estimateUserRatingForAMovie(final Map<Integer, Double> queryUserRatingMapOrig, int queryMovieId) {
        PriorityQueue<PearsonWeightedRating> kNearestUsers =
                new PriorityQueue<>(Comparator.comparingDouble(PearsonWeightedRating::getWeight));
        double queryUserActualAvgRating =
                queryUserRatingMapOrig.values().stream().mapToDouble(a -> a).average().getAsDouble();
        // Normalize ratings if IUF is being used
        Map<Integer, Double> queryUserRatingMap = normalizeUsingIUF(queryUserRatingMapOrig);
        double queryUserAvgRating = queryUserRatingMap.values().stream().mapToDouble(a -> a).average().getAsDouble();

        for (int trainingUserId = 1; trainingUserId <= trainingData.size(); trainingUserId++) {
            double trainingUserRatingForQueryMovie = trainingData.get(trainingUserId - 1).get(queryMovieId - 1);
            if (trainingUserRatingForQueryMovie == 0) continue;
            double actualTrainingUserAvgRating = getUserOverallAverageRating(trainingData.get(trainingUserId - 1));
            List<Double> trainingUserRatings = normalizeTrainingUserRatings(trainingData.get(trainingUserId - 1));
            // Compare with user only if that user has rated the movie we need to predict the rating for
            double trainingUserAvgRating = getUserOverallAverageRating(trainingUserRatings);
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
                // Subtract average from the ratings
                for (int i = 0; i < queryUserVector.size(); i++) {
                    queryUserVector.set(i, queryUserVector.get(i) - queryUserAvgRating);
                    trainingUserVector.set(i, trainingUserVector.get(i) - trainingUserAvgRating);
                }
                double cosineSimilarity = Util.cosineSimilarity(queryUserVector, trainingUserVector);
                // Threshold similarity 0.2
                if (Math.abs(cosineSimilarity) > 0.2) {
                    kNearestUsers.add(new PearsonWeightedRating(cosineSimilarity,
                            trainingData.get(trainingUserId - 1).get(queryMovieId - 1), actualTrainingUserAvgRating));
                    if (kNearestUsers.size() > K) {
                        kNearestUsers.poll();
                    }
                }
            }
        }
        if (kNearestUsers.size() > 0) {
            double weightedRatingSum = 0.0;
            double totalWeight = 0.0;
            while (!kNearestUsers.isEmpty()) {
                PearsonWeightedRating weightedRating = kNearestUsers.poll();
                double weightOfRating = weightedRating.getWeight();
                if (useCaseModification) {
                    weightOfRating = weightOfRating * Math.pow(Math.abs(weightOfRating), AMPLIFICATION - 1);
                }
                weightedRatingSum =
                        weightedRatingSum + ((weightedRating.getRating() - weightedRating.getTrainingUserAverageRating()) * weightOfRating);
                totalWeight = totalWeight + Math.abs(weightOfRating);
            }
            double weightedAvgRating = queryUserActualAvgRating + (weightedRatingSum / totalWeight);
            return weightedAvgRating;
        } else {
            // Not enough similar users found. Take average of <user's average rating> and <average rating of that
            // movie> in the corpus
            double avgUserRatingForQueryMovie = avgUserRatingsForMovies.get(queryMovieId - 1);
            if (avgUserRatingForQueryMovie == 0) return queryUserActualAvgRating;
            else return (queryUserActualAvgRating + avgUserRatingForQueryMovie) / 2;
        }
    }

    private double getUserOverallAverageRating(List<Double> trainingUserRatings) {
        return IntStream.range(0, trainingUserRatings.size()).filter(i -> trainingUserRatings.get(i) != 0).mapToDouble(i -> trainingUserRatings.get(i)).average().getAsDouble();
    }

    private List<Double> normalizeTrainingUserRatings(final List<Double> trainingUserRatings) {
        if (useInverseUserFrequency) {
            List<Double> normalizedList = new ArrayList<>();
            for (int i = 0; i < trainingUserRatings.size(); i++) {
                normalizedList.add(trainingUserRatings.get(i) * iufList.get(i));
            }
            return normalizedList;
        }
        return trainingUserRatings;
    }

    private Map<Integer, Double> normalizeUsingIUF(Map<Integer, Double> queryUserRatingMap) {
        if (useInverseUserFrequency) {
            Map<Integer, Double> mapWithIUFValues = new HashMap<>();
            for (Map.Entry<Integer, Double> entry : queryUserRatingMap.entrySet()) {
                mapWithIUFValues.put(entry.getKey(), entry.getValue() * iufList.get(entry.getKey() - 1));
            }
            return mapWithIUFValues;
        }
        return queryUserRatingMap;
    }
}
