# RecommendationAlgorithms

This project implements Collaborative filtering algorithms with a few variations. In addition to those, I have also implemented few other algorithms like Slope-One and Ensemble (Simple Average).
The purpose of this project is not to compare the efficiency of these algorithms. It only compares the effectiveness of these algorithms based on **MAE (Mean Absolute Error)**.

### How to Run the program:
**Prerequisite**: Java 9 or newer must be installed and configured in System's PATH.

1. The extracted project files will contain "src", "test-data", "training-data" folders as well as a "sources.txt" file.
2. Go to command prompt and under the current directory where all these files are present.
3. Compile the java files using this command: javac -d . @sources.txt
4. Run the program using this command: java RecommendationSystemMain
5. The program displays a list of algorithms to choose from. Type the number for the algorithm to execute and press enter. For example, press 1 and enter to execute with User based cosine similarity.
6. The results get generated in the "result" folder.
7. The project folder can also be easily imported in an IDE.
8. See Report.pdf for more details on algorithms.

#### Data reference:
The program uses a subset (200 users, 10K movies) of the dataset from below source:  
https://grouplens.org/datasets/movielens/  
F. Maxwell Harper and Joseph A. Konstan. 2015. The MovieLens Datasets: History and Context. ACM Transactions on Interactive Intelligent Systems (TiiS) 5, 4: 19:1–19:19. https://doi.org/10.1145/2827872


### Results
<img src="https://github.com/naveenkarya/RecommendationAlgorithms/blob/main/images/Final_Results.png"  width="70%" height="40%">

### Report
[Final Report](https://github.com/naveenkarya/RecommendationAlgorithms/blob/main/Report.pdf)

