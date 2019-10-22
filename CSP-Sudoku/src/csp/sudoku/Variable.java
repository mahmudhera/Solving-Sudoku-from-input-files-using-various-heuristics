package csp.sudoku;

import java.util.*;

/**
 *
 * @author HERA
 */
public abstract class Variable {

    public abstract String description();
    public abstract List<Object> domain();
}
