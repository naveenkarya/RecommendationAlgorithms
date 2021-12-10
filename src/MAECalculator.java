import java.io.File;
import java.util.Scanner;

public class MAECalculator {
    public static void printMAE(String predictedResultFileName, String actualResultFileName) {
        Scanner predictedResultScanner = null;
        Scanner actualResultScanner = null;
        try {
            File predictedResultFile = new File(predictedResultFileName);
            File actualResultFile = new File(actualResultFileName);
            predictedResultScanner = new Scanner(predictedResultFile);
            actualResultScanner = new Scanner(actualResultFile);
            int count = 0;
            double sum = 0.0;
            while (predictedResultScanner.hasNextLine() && actualResultScanner.hasNextLine()) {
                String predictedLine = predictedResultScanner.nextLine();
                String actualLine = actualResultScanner.nextLine();
                String[] predictedLineSplit = predictedLine.split(" ");
                String[] actualLineSplit = actualLine.split(" ");
                if (predictedLineSplit.length == 0 || actualLineSplit.length == 0) continue;
                count++;
                int predictedUserId = Integer.parseInt(predictedLineSplit[0]);
                int actualUserId = Integer.parseInt(actualLineSplit[0]);
                if (predictedUserId != actualUserId) {
                    continue;
                }
                int predictedMovieId = Integer.parseInt(predictedLineSplit[1]);
                int actualMovieId = Integer.parseInt(actualLineSplit[1]);
                if (predictedMovieId != actualMovieId) {
                    continue;
                }
                double predictedRating = Double.parseDouble(predictedLineSplit[2]);
                double actualRating = Double.parseDouble(actualLineSplit[2]);
                sum = sum + Math.abs(predictedRating - actualRating);
            }
            sum = sum / count;
            //sum = Math.sqrt(sum);
            System.out.println("MAE: " + sum);
        } catch (Exception e) {
        } finally {
            if (predictedResultScanner != null) predictedResultScanner.close();
            if (actualResultScanner != null) actualResultScanner.close();
        }
    }
}
