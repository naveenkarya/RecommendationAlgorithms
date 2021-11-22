import java.util.*;

public class WeightedSlopeOneAlgorithm extends RatingAlgorithm {
    private double likeMatrix[][];
    private double dislikeMatrix[][];
    private int countLikeMatrix[][];
    private int countDislikeMatrix[][];
    private List<Double> usersAvgRating;

    public WeightedSlopeOneAlgorithm(List<List<Double>> trainingData, List<Double> avgUserRatingsForMovies) {
        super(trainingData, avgUserRatingsForMovies);
        this.usersAvgRating = Util.getEachUsersAvgRating(trainingData);
        int totalNumberOfMovies = trainingData.get(0).size();
        this.likeMatrix = new double[totalNumberOfMovies][totalNumberOfMovies];
        this.countLikeMatrix = new int[totalNumberOfMovies][totalNumberOfMovies];
        this.dislikeMatrix = new double[totalNumberOfMovies][totalNumberOfMovies];
        this.countDislikeMatrix = new int[totalNumberOfMovies][totalNumberOfMovies];
        for(int i = 0; i < totalNumberOfMovies; i++) {
            for(int j = 0; j < totalNumberOfMovies; j++) {
                if(i == j) {
                    likeMatrix[i][j] = 0;
                    dislikeMatrix[i][j] = 0;
                }
                else if (likeMatrix[j][i] != 0){
                    likeMatrix[i][j] = -1.0 * likeMatrix[j][i];
                    countLikeMatrix[i][j] = countLikeMatrix[j][i];
                    dislikeMatrix[i][j] = -1.0 * dislikeMatrix[j][i];
                    countDislikeMatrix[i][j] = countDislikeMatrix[j][i];
                }
                else {
                    double likeSum = 0.0;
                    int likeCount = 0;
                    double dislikeSum = 0.0;
                    int dislikeCount = 0;
                    for(int k = 0; k < trainingData.size(); k++) {
                        double iRating = trainingData.get(k).get(i);
                        double jRating = trainingData.get(k).get(j);
                        if(iRating != 0 && jRating != 0) {
                            double usersAvg = usersAvgRating.get(k);
                            if(iRating >= usersAvg && jRating >= usersAvg) {
                                likeSum = likeSum + iRating - jRating;
                                likeCount++;
                            }
                            else if(iRating < usersAvg && jRating < usersAvg) {
                                dislikeSum = dislikeSum + iRating - jRating;
                                dislikeCount++;
                            }
                        }
                    }
                    countMatrix[i][j] = count;
                    if(sum == 0) diffMatrix[i][j] = 0.0;
                    else diffMatrix[i][j] = sum/count;
                }
            }
        }
    }

    @Override
    public double estimateUserRatingForAMovie(Map<Integer, Double> queryUserRatingMap, int queryMovieId) {
        double queryUserActualAvgRating = queryUserRatingMap.values().stream().mapToDouble(a -> a).average().getAsDouble();
        double sum = 0.0;
        int count = 0;
        for(Map.Entry<Integer, Double> entry : queryUserRatingMap.entrySet()) {
            int movieId = entry.getKey();
            double movieRating = entry.getValue();
            sum = sum + ((diffMatrix[queryMovieId - 1][movieId - 1] + movieRating) * countMatrix[queryMovieId - 1][movieId - 1]);
            count = count + countMatrix[queryMovieId - 1][movieId - 1];
        }
        double rating = 0;
        if(sum >= 0 && count >= 1) rating = sum/count;
        if(rating != 0) {
            return rating;
        }
        else {
            double avgUserRatingForQueryMovie = avgUserRatingsForMovies.get(queryMovieId - 1);
            if(avgUserRatingForQueryMovie == 0) return queryUserActualAvgRating;
            else return (queryUserActualAvgRating + avgUserRatingForQueryMovie) / 2;
        }
    }

}
