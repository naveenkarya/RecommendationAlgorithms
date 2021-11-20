public class WeightedRating {
    private double weight;
    private double rating;
    private double numOfCommonMovies;

    public WeightedRating(double weight, double rating, double numOfCommonMovies) {
        this.weight = weight;
        this.rating = rating;
        this.numOfCommonMovies = numOfCommonMovies;
    }

    public double getWeight() {
        return weight;
    }

    public double getRating() {
        return rating;
    }

    public double getNumOfCommonMovies() {
        return numOfCommonMovies;
    }
}