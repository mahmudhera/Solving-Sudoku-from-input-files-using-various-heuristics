package csp.sudoku;

import static csp.sudoku.SudokuProblem.boardSize;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author HERA
 */
public class AllDiff extends Constraint {

    public List<Variable> variables = new LinkedList<>();

    @Override
    public boolean isSatisfied(Assignment asign) {
        boolean[] seen = new boolean[boardSize + 1];
        for(Variable v : variables) {
            Integer val = (Integer) asign.getValue(v);
            if (val == null || seen[val]) {
                return false;
            }
            seen[val] = true;
        }
        return true;
    }

    @Override
    public boolean isConsistent(Assignment asign) {
        boolean[] seen = new boolean[boardSize + 1];
        boolean[] avail = new boolean[boardSize + 1];
        int constraintDomain = 0;

        for (Variable v : variables) {
            // check if this variable adds to the domain of the constraint
            List<Object> domain = asign.getDomain(v);
            if (domain == null) domain = v.domain();
            for (Object val : domain) {
                if (!avail[(Integer) val]) {
                    constraintDomain++;
                    avail[(Integer) val] = true;
                }
            }

            // check for a duplicate value
            Integer val = (Integer) asign.getValue(v);
            if (val != null) {
                if (seen[val]) {
                    return false;
                }
                seen[val] = true;
            }
        }

        // check if there are not enough values
        return variables.size() <= constraintDomain;
    
    }

    @Override
    public List<Variable> constrainedVariables() {
        return variables;
    }

}
