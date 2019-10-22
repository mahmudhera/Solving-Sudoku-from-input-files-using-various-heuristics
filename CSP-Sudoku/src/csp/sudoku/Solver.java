package csp.sudoku;

import java.util.*;
import java.io.*;

/**
 *
 * @author HERA
 */
public class Solver {

    /**
     * The problem we are solving.
     */
    public SudokuProblem sud;

    /**
     * The initial assignment state.
     */
    public Assignment initial = Assignment.blank();

    /**
     * Setup from an input file.
     * @param filename
     */
    public Solver(String filename) {
        try {
            Scanner in = new Scanner(new File(filename));
            int N = in.nextInt();
            sud = new SudokuProblem(N);
            List<Variable> vars = sud.variables();
            
            for(int i = 0; i < N; i++) {
                for(int j = 0; j < N; j++) {
                   int value = in.nextInt();
                   if(value > 0) {
                       Variable var = vars.get(i * N + j);
                       initial = initial.assign(var, value);
                       //initial = problem.inference(initial, var);
                   }
                }
            }
        } catch (IOException e) {
            System.out.println("Error in input format");
            System.exit(-1);
        }
    }

    public Assignment solve() {
        Backtrack solve = new ExtendedBacktrack(sud, initial);
        return solve.solve();
    }

}
