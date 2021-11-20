public class PearsonWeightedRating {
    private double weight;
    private double rating;
    private double trainingUserAverageRating;
    private double numOfCommonMovies;

    public PearsonWeightedRating(double weight, double rating, double trainingUserAverageRating,
                                 double numOfCommonMovies) {
        this.weight = weight;
        this.rating = rating;
        this.trainingUserAverageRating = trainingUserAverageRating;
        this.numOfCommonMovies = numOfCommonMovies;
    }

    public double getWeight() {
        return weight;
    }

    public double getRating() {
        return rating;
    }

    public double getTrainingUserAverageRating() {
        return trainingUserAverageRating;
    }

    public double getNumOfCommonMovies() {
        return numOfCommonMovies;
    }
}