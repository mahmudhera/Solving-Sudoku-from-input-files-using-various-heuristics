package csp.sudoku;

import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author HERA
 */
public class Main {

    public static void main(String[] args) {
        
        String filename = Util.inputFileName;
        Solver solver = new Solver(filename);
        Assignment solution = solver.solve();

        if (solution == null) {
            System.out.println("Failed to find a solution!");
            System.exit(1);
        }

        List<Variable> vars = solver.sud.variables();
        
        GUI gui = new GUI(vars, solution);
        gui.setVisible(true);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        System.out.println("Checks: " + CSPProblem.consistencyChecks);
        System.out.println("Var selected: " + CSPProblem.timesVariableSelected);
        System.out.println("Value selected: " + CSPProblem.timesValueSelected);

    }

}
