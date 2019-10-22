package csp.sudoku;

import java.util.*;

/**
 *
 * @author HERA
 */
public class Assignment {

    // assignment of variables to objects
    Map<Variable, Object> assignments = null;

    // domain of a variable
    Map<Variable, List<Object>> domain = null;

    // new blank assignment
    static Assignment blank() {
        Assignment blank = new Assignment();
        blank.assignments = new HashMap<>();
        blank.domain = new HashMap<>();
        return blank;
    }

    // assign a variable to a value
    public Assignment assign(Variable v, Object value) {
        Assignment n = new Assignment();
        n.assignments = new HashMap<>(assignments);
        n.assignments.put(v, value);
        n.domain = new HashMap<>(domain);

        // Restrict the domain to only a single value
        List<Object> varDomain = new LinkedList<>();
        varDomain.add(value);
        n.restrictDomain(v, varDomain);

        return n;
    }

    // get the assigned value of a variable, null if not assigned
    public Object getValue(Variable v) {
        return assignments.get(v);
    }

    // restrict the domain of a variable. assign the restricted domain to it
    public void restrictDomain(Variable v, List<Object> dom) {
        domain.put(v, dom);
    }

    // get the domain of a variable
    public List<Object> getDomain(Variable v) {
        return domain.get(v);
    }

    // when size == boardsize, game is done
    public int size() {
        return assignments.size();
    }
}
