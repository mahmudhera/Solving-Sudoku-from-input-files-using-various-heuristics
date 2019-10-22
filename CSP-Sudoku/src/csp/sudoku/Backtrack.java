package csp.sudoku;

import java.util.*;

/**
 *
 * @author HERA
 */
public class Backtrack {

    CSPProblem problem;
    Assignment initialAssignment;

    public Backtrack(CSPProblem prob) {
        this(prob, Assignment.blank());
    }

    public Backtrack(CSPProblem prob, Assignment initial) {
        problem = prob;
        initialAssignment = initial;
    }

    public Assignment solve() {
        // start solving from the initial state
        return recursiveSolve(initialAssignment);
    }

    private Assignment recursiveSolve(Assignment assign) {
        // base case, check if done
        if (problem.satisfiedByAssignment(assign)) {
            return assign;
        }

        // get an unassigned variable
        Variable v = unassignedVar(assign);
        if (v == null) {
            return null;
        }

        // get the domain values for a variable
        List<Object> values = problem.domainValues(assign, v);
        values = orderValues(assign, values, v);

        for (Object value : values) {
            // try a value for a variable
            Assignment newAssign = assign.assign(v, value);
            CSPProblem.timesValueSelected++;
            // make some inferences
            try {
                newAssign = problem.inference(newAssign, v);
            } catch (IllegalStateException e) {
                continue;
            }

            // check the consistency
            if (problem.consistentAssignment(newAssign)) {
            } else {
                continue;
            }

            // recurse
            newAssign = recursiveSolve(newAssign);
            if (newAssign != null) {
                return newAssign;
            }
        }

        // Failed
        return null;
    }

    // get an unassigned variable
    protected Variable unassignedVar(Assignment assign) {
        // find any non-assigned variable
        for (Variable v : problem.variables()) {
            if (assign.getValue(v) == null) {
                return v;
            }
        }
        return null;
    }

    // order the domain values of a variable to try out
    protected List<Object> orderValues(Assignment asign, List<Object> domain, Variable v) {
        return domain;
    }
}
