public enum AlgorithmEnum {
    BASIC_COLLABORATIVE_FILTERING("1", "User-Based-Cosine-Similarity"),
    PEARSON_CORRELATION("2", "Pearson-Correlation"),
    PEARSON_CORRELATION_IUF_ONLY("3", "Pearson-Correlation-With-IUF-Only"),
    PEARSON_CORRELATION_CASE_MOD_ONLY("4", "Pearson-Correlation-With-CaseMod_Only"),
    ITEM_BASED_CF( "5", "Item-Based-Collaborative-Filtering"),
    EUCLIDEAN_DISTANCE_WITH_LOG_BASE("6", "Euclidean-Distance-With-Log-Base"),
    EUCLIDEAN_DISTANCE_WITH_JACCARD("7", "Euclidean-Distance-With-Jaccard"),
    COSINE_SIM_WITH_LOG_BASE("8", "Cosine-Similarity-With-Log-Base"),
    COSINE_SIM_WITH_JACCARD("9", "Cosine-Similarity-With-Jaccard"),
    EUCLIDEAN_DISTANCE("10", "Euclidean-Distance"),
    WEIGHTED_SLOPE_ONE("11", "Weighted Slope-One"),
    NORMALIZED_EUCLIDEAN_DISTANCE("12", "Normalized-Euclidean-Distance"),
    BASIC_AVERAGE_ALGORITHM("14", "Basic-Average-Algorithm"),
    BASIC_AVERAGE_ALGORITHM2("15", "Basic-Average-Algorithm2"),
    COSINE_SIMILARITY_AND_ITEM_BASED("16", "Cosine Similarity With Item Based"),
    COSINE_AND_JACCARD("17", "Cosine and Jaccard");

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
