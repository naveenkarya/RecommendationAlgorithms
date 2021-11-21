public enum AlgorithmEnum {
    BASIC_COLLABORATIVE_FILTERING("1", "User-Based-Cosine-Similarity"),
    PEARSON_CORRELATION("2", "Pearson-Correlation"),
    PEARSON_CORRELATION_IUF_ONLY("3", "Pearson-Correlation-With-IUF-Only"),
    PEARSON_CORRELATION_CASE_MOD_ONLY("4", "Pearson-Correlation-With-CaseMod_Only"),
    ITEM_BASED_CF( "5", "Item-Based-Collaborative-Filtering"),
    USER_AND_ITEMS_AVERAGE("6", "User's and Item's Average"),
    COSINE_SIMILARITY_AND_ITEM_BASED("7", "Cosine Similarity With Item Based"),
    NORMALIZED_EUCLIDEAN_DISTANCE("8", "Normalized-Euclidean-Distance"),
    EUCLIDEAN_DISTANCE_WITH_LOG_BASE("9", "Euclidean-Distance-With-Log-Base"),
    EUCLIDEAN_DISTANCE_WITH_JACCARD("10", "Euclidean-Distance-With-Jaccard"),
    WEIGHTED_SLOPE_ONE("11", "Weighted Slope-One");

    private final String algoCode;
    private final String algoName;


    AlgorithmEnum(String algoCode, String algoName) {
        this.algoCode = algoCode;
        this.algoName = algoName;
    }

    public String getAlgoName() {
        return algoName;
    }

    public String getAlgoCode() {
        return algoCode;
    }

    public static AlgorithmEnum fromCode(String algoCode) {
        for(AlgorithmEnum algo : values()) {
            if(algo.getAlgoCode().equals(algoCode)) return algo;
        }
        throw new IllegalArgumentException(algoCode);
    }
}
