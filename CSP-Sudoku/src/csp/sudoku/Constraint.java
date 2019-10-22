package csp.sudoku;

import java.util.*;

/**
 *
 * @author HERA
 */
public abstract class Constraint {
    
    // return if the constraint is satisfied
    public abstract boolean isSatisfied(Assignment asgn);

    // return if the assignment is consistent
    public abstract boolean isConsistent(Assignment asgn);

    // returns the variables upon which the constraint is imposed
    public abstract List<Variable> constrainedVariables();
}
