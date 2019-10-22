package csp.sudoku;

public class Util {
    
    public static final int MRV = 1;
    public static final int DEGREE = 2;
    public static final int RANDOM = 3;
    public static final int FIRST_AVAILABLE = 4;
    public static final int LRV = 5;
    public static final int NONE = 6;
    public static final int FC = 7;
    public static final int MAC = 8;
    public static final int POWERFUL = 9;
    
    public static final String inputFileName = "problem.txt";
    public static final int VariableOrderingChoice = MRV;
    public static final boolean BreakMRVTieWithDegree = true;
        // MRV heuristic --
        // DEGREE --
        // RANDOM --
        // FIRST_AVAILABLE --
    
    public static final int ValueOrderingChoice = LRV;
        // LRV --
        // RANDOM --
        // FIRST_AVAILABLE --
    
    public static final int InferenceRule = POWERFUL;
        // MAC
        // FC
        // POWERFUL
    
}
