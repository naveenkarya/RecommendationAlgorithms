public class OutputFormat {
    public int getUserId() {
        return userId;
    }

    public int getMovieId() {
        return movieId;
    }

    public short getRating() {
        return rating;
    }

    private int userId;
    private int movieId;
    private short rating;

    public OutputFormat(int userId, int movieId, short rating) {
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
    }
}