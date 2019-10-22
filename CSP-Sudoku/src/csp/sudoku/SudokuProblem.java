package csp.sudoku;

import java.util.*;

/**
 *
 * @author HERA
 */
public final class SudokuProblem extends CSPProblem {

    static int boardSize = 9;
    List<Variable> tiles;
    List<Constraint> constraints;

    private static final List<Object> domain = new LinkedList<>();

    public SudokuProblem(int size) {
        boardSize = size;
        for (int i = 1; i <= boardSize; i++) {
            domain.add(i);
        }
        SudokuTile.setDomain(domain);
        tiles = new ArrayList<>(boardSize * boardSize);
        constraints = new ArrayList<>(boardSize * 3);
        variables();
        constraints();
    }

    // get all the variables
    @Override
    public List<Variable> variables() {
        if (tiles.isEmpty()) {
            for (int i = 0; i < boardSize * boardSize; i++) {
                tiles.add(new SudokuTile(i));
            }
        }
        return tiles;
    }

    // get all the constraints
    @Override
    public List<Constraint> constraints() {
        if (constraints.isEmpty()) {
            
            // add the row constraints
            for (int row = 0; row < boardSize; row++) {
                AllDiff constraint = new AllDiff();
                for (int col = 0; col < boardSize; col++) {
                    constraint.variables.add(tiles.get(row * boardSize + col));
                }
                constraints.add(constraint);
            }

            // add the column constraints
            for (int col = 0; col < boardSize; col++) {
                AllDiff constraint = new AllDiff();
                for (int row = 0; row < boardSize; row++) {
                    constraint.variables.add(tiles.get(row * boardSize + col));
                }
                constraints.add(constraint);
            }

            // add the box constraints
            int numGrids = (int) Math.round(Math.sqrt(boardSize));
            for (int baseRow = 0; baseRow < numGrids; baseRow++) {
                for (int baseCol = 0; baseCol < numGrids; baseCol++) {
                    AllDiff constraint = new AllDiff();
                    for (int row = 0; row < numGrids; row++) {
                        for (int col = 0; col < numGrids; col++) {
                            int piece = (baseRow * numGrids + row) * boardSize + baseCol * numGrids + col;
                            constraint.variables.add(tiles.get(piece));
                        }
                    }
                    constraints.add(constraint);
                }
            }
        }
        return constraints;
    }

    // make inferences based on preferences set beforehand
    @Override
    public Assignment inference(Assignment assign, Variable v) throws IllegalStateException {
        if(Util.InferenceRule == Util.FC)
            return FC(assign, v);
        else if(Util.InferenceRule == Util.MAC)
            return MAC(assign, v);
        else if(Util.InferenceRule == Util.POWERFUL)
            return PowerfulConstraintPropagation(assign, v);
        return assign;
    }
    
    
    private Assignment FC(Assignment assignment, Variable v) throws IllegalStateException {
        
        // get all the affected constraints
        List<Constraint> constr = variableConstraints(v);
        // get the assigned value
        Object val = assignment.getValue(v);
        
        // get all the affected variables
        for (Constraint c : constr) {
            for (Variable rel : c.constrainedVariables()) {
                // Skip the current variable
                if (rel == v) {
                    continue;
                }
                List<Object> domainOfRelatedVariable;
                domainOfRelatedVariable = domainValues(assignment, rel);
                if (domainOfRelatedVariable.contains(val)) {
                    domainOfRelatedVariable = new LinkedList<>(domainOfRelatedVariable);
                    domainOfRelatedVariable.remove(val);
                    assignment.restrictDomain(rel, domainOfRelatedVariable);
                    if (assignment.getValue(rel) == null) {
                        if (domainOfRelatedVariable.size() == 1) {
                            assignment = assignment.assign(rel, domainOfRelatedVariable.get(0));
                        } else if (domainOfRelatedVariable.isEmpty()) {
                            throw new IllegalStateException("No solution" + rel.description());
                        }
                    }
                }
            }
        }

        return assignment;
    }
    
    private Assignment MAC(Assignment assignment, Variable v) throws IllegalStateException {
        
        // get all the affected constraints
        List<Constraint> constr = variableConstraints(v);
        // get the assigned value
        Object val = assignment.getValue(v);

        // get all the affected variables
        for (Constraint c : constr) {
            for (Variable rel : c.constrainedVariables()) {
                // Skip the current variable
                if (rel == v) {
                    continue;
                }

                List<Object> domainOfRelatedVariable = domainValues(assignment, rel);
                if (domainOfRelatedVariable.contains(val)) {
                    domainOfRelatedVariable = new LinkedList<>(domainOfRelatedVariable);
                    domainOfRelatedVariable.remove(val);
                    assignment.restrictDomain(rel, domainOfRelatedVariable);
                    if (assignment.getValue(rel) == null) {
                        if (domainOfRelatedVariable.size() == 1) {
                            assignment = assignment.assign(rel, domainOfRelatedVariable.get(0));
                            assignment = MAC(assignment, rel);
                        } else if (domainOfRelatedVariable.isEmpty()) {
                            throw new IllegalStateException("No remaining assignments for variable: " + rel.description());
                        }
                    }
                }
            }
        }
        
        return assignment;
    }
    
    private Assignment RemoveSingletons(Assignment assignment, Variable v) throws IllegalStateException {
        
        SudokuTile tile = (SudokuTile)v;
        int row = tile.getRow();
        int col = tile.getCol();
        
        int[] count = new int[tile.boardSize];
        int[] tileNoCandidate = new int[tile.boardSize];
        
        for (int i = 0; i < tile.boardSize; i++) {
            int newTileNo = row*tile.boardSize + i;
            if(assignment.getValue(variables().get(newTileNo)) != null)
                continue;
            for(Object obj : assignment.getDomain(variables().get(newTileNo))) {
                Integer value = (Integer)obj;
                count[value - 1]++;
                tileNoCandidate[value - 1] = newTileNo;
            }
        }
        
        for(int i = 0; i < tile.boardSize; i++) {
            if(count[i] == 1) {
                int tileNoWithSingleton = tileNoCandidate[i];
                Variable tileWithSingleton = variables().get(tileNoWithSingleton);
                assignment = assignment.assign(tileWithSingleton, i + 1);
                //assignment = PowerfulConstraintPropagation(assignment, tileWithSingleton);
            }
        }
        
        count = new int[tile.boardSize];
        tileNoCandidate = new int[tile.boardSize];
        
        for (int i = 0; i < tile.boardSize; i++) {
            int newTileNo = i*tile.boardSize + col;
            if(assignment.getValue(variables().get(newTileNo)) != null)
                continue;
            for(Object obj : assignment.getDomain(variables().get(newTileNo))) {
                Integer value = (Integer)obj;
                count[value - 1]++;
                tileNoCandidate[value - 1] = newTileNo;
            }
        }
        
        for (int i = 0; i < tile.boardSize; i++) {
            if(count[i] == 1) {
                int tileNoWithSingleton = tileNoCandidate[i];
                Variable tileWithSingleton = variables().get(tileNoWithSingleton);
                assignment = assignment.assign(tileWithSingleton, i + 1);
                //assignment = PowerfulConstraintPropagation(assignment, tileWithSingleton);
            }
        }
        
        count = new int[tile.boardSize];
        tileNoCandidate = new int[tile.boardSize];
        
        int baseRow = (row / (int)Math.sqrt(tile.boardSize)) * (int)Math.sqrt(tile.boardSize);
        int baseCol = (col / (int)Math.sqrt(tile.boardSize)) * (int)Math.sqrt(tile.boardSize);
        int sqrt_n = (int)Math.sqrt(tile.boardSize);
        for(int i = 0; i < sqrt_n; i++) {
            for(int j = 0; j < sqrt_n; j++) {
                int newTileNo = (baseRow + i) * tile.boardSize + (baseCol + j);
                if(assignment.getValue(variables().get(newTileNo)) != null)
                    continue;
                for(Object obj : assignment.getDomain(variables().get(newTileNo))) {
                    Integer value = (Integer)obj;
                    count[value - 1]++;
                    tileNoCandidate[value - 1] = newTileNo;
                }
            }
        }
        
        for (int i = 0; i < tile.boardSize; i++) {
            if(count[i] == 1) {
                int tileNoWithSingleton = tileNoCandidate[i];
                Variable tileWithSingleton = variables().get(tileNoWithSingleton);
                assignment = assignment.assign(tileWithSingleton, i + 1);
                //assignment = PowerfulConstraintPropagation(assignment, tileWithSingleton);
            }
        }
        
        return assignment;
    
    }
    
    private Assignment RemoveTwins(Assignment assignment, Variable v) throws IllegalStateException {
        
        List<Variable> variablesWithSize2 = new LinkedList<>();
                            
        int row = ((SudokuTile)v).getRow();
        int n = ((SudokuTile)v).boardSize;
        for(int i = 0; i < n; i++) {
            int tileNo = row*n + i;
            Variable tileVariable = variables().get(tileNo);
            if(assignment.getValue(tileVariable) == null && assignment.getDomain(tileVariable).size() == 2) {
                variablesWithSize2.add(tileVariable);
            }
        }

        int size = variablesWithSize2.size();
        for(int i = 0; i < size - 1; i++){
            for(int j = i + 1; j < size; j++) {
                List<Object> domain1 = assignment.getDomain(variablesWithSize2.get(i));
                List<Object> domain2 = assignment.getDomain(variablesWithSize2.get(j));
                boolean flag = true;
                for(Object obj : domain1) {
                    if(!domain2.contains(obj)) {
                        flag = false;
                        break;
                    }
                }
                if(flag == true) {
                    int iTileNo = ((SudokuTile)variablesWithSize2.get(i)).tileNumber;
                    int jTileNo = ((SudokuTile)variablesWithSize2.get(j)).tileNumber;
                    for(int k = 0; k < n; k++) {
                        int kTileNo = row*n + k;
                        if(kTileNo == iTileNo || kTileNo == jTileNo) 
                            continue;
                        List<Object> domaink = assignment.getDomain(variables().get(kTileNo));
                        for(Object obj : domain1) {
                            if(!domaink.contains(obj))
                                continue;
                            domaink.remove(obj);
                            if(domaink.size() == 1) {
                                Variable v_k = variables().get(kTileNo);
                                assignment = assignment.assign(v_k, obj);
                                assignment = PowerfulConstraintPropagation(assignment, v_k);
                            } else if(domaink.isEmpty()) {
                                throw new IllegalStateException("Not possible");
                            }
                        }
                    }
                }
            }
        }

        variablesWithSize2 = new LinkedList<>();

        int col = ((SudokuTile)v).getCol();
        for(int i = 0; i < n; i++) {
            int tileNo = i*n + col;
            Variable tileVariable = variables().get(tileNo);
            if(assignment.getValue(tileVariable) == null && assignment.getDomain(tileVariable).size() == 2) {
                variablesWithSize2.add(tileVariable);
            }
        }

        size = variablesWithSize2.size();
        for(int i = 0; i < size - 1; i++){
            for(int j = i + 1; j < size; j++) {
                List<Object> domain1 = assignment.getDomain(variablesWithSize2.get(i));
                List<Object> domain2 = assignment.getDomain(variablesWithSize2.get(j));
                boolean flag = true;
                for(Object obj : domain1) {
                    if(!domain2.contains(obj)) {
                        flag = false;
                        break;
                    }
                }
                if(flag == true) {
                    int iTileNo = ((SudokuTile)variablesWithSize2.get(i)).tileNumber;
                    int jTileNo = ((SudokuTile)variablesWithSize2.get(j)).tileNumber;
                    for(int k = 0; k < n; k++) {
                        int kTileNo = k*n + col;
                        if(kTileNo == iTileNo || kTileNo == jTileNo) 
                            continue;
                        List<Object> domaink = assignment.getDomain(variables().get(kTileNo));
                        for(Object obj : domain1) {
                            if(!domaink.contains(obj))
                                continue;
                            domaink.remove(obj);
                            if(domaink.size() == 1) {
                                Variable v_k = variables().get(kTileNo);
                                assignment = assignment.assign(v_k, obj);
                                assignment = PowerfulConstraintPropagation(assignment, v_k);
                                
                            } else if(domaink.isEmpty()) {
                                throw new IllegalStateException("Not possible");
                            }
                        }
                    }
                }
            }
        }

        variablesWithSize2 = new LinkedList<>();
        for(int i = 0; i < (int)Math.sqrt(n); i++) {
            for(int j = 0; j < (int)Math.sqrt(n); j++) {
                int baseRow = (row / (int)Math.sqrt(n))*(int)Math.sqrt(n);
                int baseCol = (col / (int)Math.sqrt(n))*(int)Math.sqrt(n);
                int tileNo = (baseRow + i)*n + (baseCol + j);
                Variable tileVariable = variables().get(tileNo);
                if(assignment.getValue(tileVariable) == null && assignment.getDomain(tileVariable).size() == 2) {
                    variablesWithSize2.add(tileVariable);
                }
            }
        }

        size = variablesWithSize2.size();
        for(int i = 0; i < size - 1; i++){
            for(int j = i + 1; j < size; j++) {
                List<Object> domain1 = assignment.getDomain(variablesWithSize2.get(i));
                List<Object> domain2 = assignment.getDomain(variablesWithSize2.get(j));
                boolean flag = true;
                for(Object obj : domain1) {
                    if(!domain2.contains(obj)) {
                        flag = false;
                        break;
                    }
                }
                if(flag == true) {
                    int iTileNo = ((SudokuTile)variablesWithSize2.get(i)).tileNumber;
                    int jTileNo = ((SudokuTile)variablesWithSize2.get(j)).tileNumber;
                    for(int k = 0; k < n; k++) {
                        int baseRow = (row / (int)Math.sqrt(n))*(int)Math.sqrt(n);
                        int baseCol = (col / (int)Math.sqrt(n))*(int)Math.sqrt(n);
                        int kRow = k / (int)Math.sqrt(n);
                        int kCol = k % (int)Math.sqrt(n);
                        int kTileNo = (baseRow + kRow) * n + (baseCol + kCol);
                        if(kTileNo == iTileNo || kTileNo == jTileNo) 
                            continue;
                        List<Object> domaink = assignment.getDomain(variables().get(kTileNo));
                        for(Object obj : domain1) {
                            if(!domaink.contains(obj))
                                continue;
                            domaink.remove(obj);
                            if(domaink.size() == 1) {
                                Variable v_k = variables().get(kTileNo);
                                assignment = assignment.assign(v_k, obj);
                                assignment = PowerfulConstraintPropagation(assignment, v_k);
                            } else if(domaink.isEmpty()) {
                                throw new IllegalStateException("Not possible");
                            }
                        }
                    }
                }
            }
        }
        
        return assignment;
        
    }
    
    private Assignment PowerfulConstraintPropagation(Assignment assignment, Variable v) throws IllegalStateException {
        // Get all the affected constraints
        List<Constraint> constr = variableConstraints(v);

        // Get the assigned value
        Object val = assignment.getValue(v);

        // Get all the affected variables
        for (Constraint c : constr) {
            for (Variable rel : c.constrainedVariables()) {
                // Skip the current variable
                if (rel == v) {
                    continue;
                }

                List<Object> domOfRelatedVar = domainValues(assignment, rel);
                if (domOfRelatedVar.contains(val)) {
                    domOfRelatedVar = new LinkedList<>(domOfRelatedVar);
                    domOfRelatedVar.remove(val);
                    assignment.restrictDomain(rel, domOfRelatedVar);
                    if (assignment.getValue(rel) == null) {
                        if (domOfRelatedVar.size() == 1) {
                            assignment = assignment.assign(rel, domOfRelatedVar.get(0));
                            assignment = PowerfulConstraintPropagation(assignment, rel);
                            RemoveTwins(assignment, rel);
                            RemoveSingletons(assignment, rel);
                        } else if (domOfRelatedVar.isEmpty()) {
                            throw new IllegalStateException("Not possible" + rel.description());
                        }
                    }
                }
            }
        }
        
        return assignment;
    }
    
}
