import java.util.List;
import java.util.Map;

public class BipolarSlopeOneAlgorithm extends RatingAlgorithm {
    private float likeMatrix[][];
    private float dislikeMatrix[][];
    private short countLikeMatrix[][];
    private short countDislikeMatrix[][];
    private List<Double> usersAvgRating;

    public BipolarSlopeOneAlgorithm(List<List<Double>> trainingData, List<Double> avgUserRatingsForMovies) {
        super(trainingData, avgUserRatingsForMovies);
        this.usersAvgRating = Util.getEachUsersAvgRating(trainingData);
        int totalNumberOfMovies = trainingData.get(0).size();
        this.likeMatrix = new float[totalNumberOfMovies][totalNumberOfMovies];
        this.countLikeMatrix = new short[totalNumberOfMovies][totalNumberOfMovies];
        this.dislikeMatrix = new float[totalNumberOfMovies][totalNumberOfMovies];
        this.countDislikeMatrix = new short[totalNumberOfMovies][totalNumberOfMovies];
        for (int i = 0; i < totalNumberOfMovies; i++) {
            for (int j = 0; j < totalNumberOfMovies; j++) {
                if (i == j) {
                    likeMatrix[i][j] = 0;
                    dislikeMatrix[i][j] = 0;
                } else if (likeMatrix[j][i] != 0) {
                    likeMatrix[i][j] = (float) (-1.0 * likeMatrix[j][i]);
                    countLikeMatrix[i][j] = countLikeMatrix[j][i];
                    dislikeMatrix[i][j] = (float) (-1.0 * dislikeMatrix[j][i]);
                    countDislikeMatrix[i][j] = countDislikeMatrix[j][i];
                } else {
                    float likeSum = 0.0f;
                    short likeCount = 0;
                    float dislikeSum = 0.0f;
                    short dislikeCount = 0;
                    for (int k = 0; k < trainingData.size(); k++) {
                        double iRating = trainingData.get(k).get(i);
                        double jRating = trainingData.get(k).get(j);
                        if (iRating != 0 && jRating != 0) {
                            double usersAvg = usersAvgRating.get(k);
                            if (iRating >= usersAvg && jRating >= usersAvg) {
                                likeSum = (float) (likeSum + iRating - jRating);
                                likeCount++;
                            } else if (iRating < usersAvg && jRating < usersAvg) {
                                dislikeSum = (float) (dislikeSum + iRating - jRating);
                                dislikeCount++;
                            }
                        }
                    }
                    countLikeMatrix[i][j] = likeCount;
                    if (likeSum == 0) likeMatrix[i][j] = 0.0f;
                    else likeMatrix[i][j] = likeSum / likeCount;
                    countDislikeMatrix[i][j] = dislikeCount;
                    if (dislikeSum == 0) dislikeMatrix[i][j] = 0.0f;
                    else dislikeMatrix[i][j] = dislikeSum / dislikeCount;
                }
            }
        }
    }

    @Override
    public double estimateUserRatingForAMovie(Map<Integer, Double> queryUserRatingMap, int queryMovieId) {
        double queryUserActualAvgRating =
                queryUserRatingMap.values().stream().mapToDouble(a -> a).average().getAsDouble();
        double sum = 0.0;
        int count = 0;
        for (Map.Entry<Integer, Double> entry : queryUserRatingMap.entrySet()) {
            int movieId = entry.getKey();
            double movieRating = entry.getValue();
            if (movieRating >= queryUserActualAvgRating) {
                sum = sum + ((likeMatrix[queryMovieId - 1][movieId - 1] + movieRating) * countLikeMatrix[queryMovieId - 1][movieId - 1]);
                count = count + countLikeMatrix[queryMovieId - 1][movieId - 1];
            } else {
                sum = sum + ((dislikeMatrix[queryMovieId - 1][movieId - 1] + movieRating) * countDislikeMatrix[queryMovieId - 1][movieId - 1]);
                count = count + countDislikeMatrix[queryMovieId - 1][movieId - 1];
            }
        }
        double rating = 0;
        if (sum >= 0 && count >= 5) rating = sum / count;
        if (rating != 0) {
            return rating;
        } else {
            double avgUserRatingForQueryMovie = avgUserRatingsForMovies.get(queryMovieId - 1);
            if (avgUserRatingForQueryMovie == 0) return queryUserActualAvgRating;
            else return (queryUserActualAvgRating + avgUserRatingForQueryMovie) / 2;
        }
    }

}
