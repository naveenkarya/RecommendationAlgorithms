public class PearsonWeightedRating {
    private double weight;
    private double rating;
    private double trainingUserAverageRating;

    public PearsonWeightedRating(double weight, double rating, double trainingUserAverageRating) {
        this.weight = weight;
        this.rating = rating;
        this.trainingUserAverageRating = trainingUserAverageRating;
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

}