package csp.sudoku;

import java.util.*;

/**
 *
 * @author HERA
 */
public abstract class CSPProblem {

    // get the variables
    public abstract List<Variable> variables();

    // get the constraints
    public abstract List<Constraint> constraints();

    // performance mtrics
    public static int consistencyChecks = 0;
    public static int timesVariableSelected = 0;
    public static int timesValueSelected = 0;

    // check if the problem is isSatisfied by an assignment
    public boolean satisfiedByAssignment(Assignment asign) {
        CSPProblem.consistencyChecks++;
        // if not enough assignments made, return false
        if (variables().size() > asign.size()) {
            return false;
        }
        return constraints().stream().noneMatch((c) -> (!c.isSatisfied(asign)));
    }

    // the constraints on some variable
    Map<Variable, List<Constraint>> varConstraints = null;

    // return the constraints on some variable
    public List<Constraint> variableConstraints(Variable v) {
        
        // if we already have the map, the read the map and return
        if (varConstraints != null) {
            return varConstraints.get(v);
        }
        
        // do not have the map, make one
        varConstraints = new HashMap<>();

        // get the constraints, add it for the variables in it
        constraints().stream().forEach((c) -> {
            // get its variables
            List<Variable> vars = c.constrainedVariables();
            vars.stream().forEach((constrVar) -> {
                // add the constraint if we have a mapping
                if (varConstraints.containsKey(constrVar)) {
                    varConstraints.get(constrVar).add(c);
                    // Create a mapping between the variable and this constraint
                } else {
                    List<Constraint> constr = new LinkedList<>();
                    constr.add(c);
                    varConstraints.put(constrVar, constr);
                }
            });
        });
        return varConstraints.get(v);
    }

    // return a list of domain values for a variable. this is not the initial domain,
    // rather the domain restricted to the variable in an assignment, so it is assignment specific
    public List<Object> domainValues(Assignment assign, Variable v) {
        List<Object> domain = assign.getDomain(v);
        if (domain != null) {
            return domain;
        }
        return v.domain();
    }

    // check if the assignment is isConsistent
    public boolean consistentAssignment(Assignment assign) {
        // Check everything is isConsistent
        return constraints().stream().noneMatch((c) -> (!c.isConsistent(assign)));
    }

    // return new assignment on some inferences
    public Assignment inference(Assignment assign, Variable v) throws IllegalStateException {
        return assign;
    }
}
