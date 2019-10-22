package csp.sudoku;

import java.util.*;

/**
 *
 * @author HERA
 */
public class ExtendedBacktrack extends Backtrack {

    public ExtendedBacktrack(CSPProblem prob) {
        super(prob);
    }

    public ExtendedBacktrack(CSPProblem prob, Assignment initial) {
        super(prob, initial);
    }

    // get the unassigned variable based on the preferences
    @Override
    protected Variable unassignedVar(Assignment assign) {
        CSPProblem.timesVariableSelected++;
        if(Util.VariableOrderingChoice == Util.MRV)
            return getVariableByMRV(assign);
        else if(Util.VariableOrderingChoice == Util.FIRST_AVAILABLE)
            return unassignedVarFirstAvailabe(assign);
        else if(Util.VariableOrderingChoice == Util.RANDOM)
            return getRandomUnassignedVariable(assign);
        else if(Util.VariableOrderingChoice == Util.DEGREE)
            return getVariableWithMaxDegree(assign).get(0);
        return null;
    }
    
    protected List<Variable> getVariableWithMaxDegree(Assignment assign) {
        PriorityQueue pq = new PriorityQueue<>();
        List<Variable> list = problem.variables();
        list.stream().filter((v) -> !(assign.getValue(v) != null)).map((v) -> new VariableDegreeTuple(v, assign)).forEach((object) -> {
            pq.add(object);
        });
        List<Variable> list1 = new LinkedList<>();
        int size = pq.size();
        for(int i = 0; i < size; i++)
            list1.add(((VariableDegreeTuple)pq.poll()).V);
        return list1;
    }
    
    protected Variable getRandomUnassignedVariable(Assignment assign) {
        List<Variable> vars = problem.variables();
        if(vars.size() == assign.size())
            return null;
        Variable v;
        while(true) {
            int index = (int)Math.floor(Math.random()*vars.size());
            if(index == vars.size()) index--;
            v = vars.get(index);
            if(assign.getValue(v) == null)
                break;
        }
        return v;
    }
    
    protected Variable unassignedVarFirstAvailabe(Assignment assign) {
        return super.unassignedVar(assign);
    }
    
    // get an unassigned variable by min remaining value heuristic
    protected Variable getVariableByMRV(Assignment assign) {
        
        int minDomain = Integer.MAX_VALUE;
        Variable minVar = null;

        // Find any non-assigned variable
        List<Variable> vars = problem.variables();
        if (vars.size() == assign.size()) {
            return null;
        }
        
        if(Util.BreakMRVTieWithDegree)
            vars = getVariableWithMaxDegree(assign);

        for (Variable v : vars) {
            if (assign.getValue(v) == null) {
                int domSize = problem.domainValues(assign, v).size();
                if (domSize < minDomain) {
                    minDomain = domSize;
                    minVar = v;
                }
            }
        }

        return minVar;
    }

    // order the available valus of a variable to make the search faster
    @Override
    protected List<Object> orderValues(Assignment asign, List<Object> domain, Variable v) {
        if(Util.ValueOrderingChoice == Util.LRV)
            return orderValuesByLRV(asign, domain, v);
        else if(Util.ValueOrderingChoice == Util.RANDOM)
            return orderValuesRandomly(domain);
        else if(Util.ValueOrderingChoice == Util.FIRST_AVAILABLE)
            return domain;
        else
            return null;
    }
    
    protected List<Object> orderValuesRandomly(List<Object> domain) {
        List<Object> domainCopy = new LinkedList(domain);
        List<Object> list = new LinkedList<>();
        while(!domainCopy.isEmpty()) {
            int index = (int)Math.floor(Math.random()*domainCopy.size());
            if(index == domainCopy.size())
                index--;
            list.add(domainCopy.remove(index));
        }
        return list;
    }
    
    // ordered values by "least constraining value" heuristic
    protected List<Object> orderValuesByLRV(Assignment asign, List<Object> domain, Variable v) {
        
        PriorityQueue<ValueDomainTuple> pq = new PriorityQueue<>();
        
        domain.stream().forEach((val) -> {
            try {
                pq.add(new ValueDomainTuple(asign, v, val));
            } catch (IllegalStateException e) {
            }
        });

        LinkedList<Object> ordered = new LinkedList<>();
        int size = pq.size();
        // the pq returns the values with minimum remaining domain.
        // but we add them in a linked list. so we get the first one which allows maximum
        // domain size for the others
        for (int i = 0; i < size; i++) {
            ValueDomainTuple next = pq.poll();
            ordered.add(next.value);
        }

        return ordered;
    }

    class ValueDomainTuple implements Comparable<ValueDomainTuple> {

        public Integer domain = 0;
        public Object value;

        public ValueDomainTuple(Assignment init, Variable v, Object val) throws IllegalStateException {
            
            value = val;
            Assignment newAssign = init.assign(v, val);

            if (!problem.consistentAssignment(newAssign)) {
                throw new IllegalStateException("Bad Value");
            }

            newAssign = problem.inference(newAssign, v);
            
            // calculate total domain size after assignment
            for (Variable var : problem.variables()) {
                domain += problem.domainValues(newAssign, var).size();
            }
        }

        @Override
        public int compareTo(ValueDomainTuple other) {
            return domain.compareTo(other.domain);
        }
        
    }
    
    class VariableDegreeTuple implements Comparable<VariableDegreeTuple> {
        
        public Variable V;
        public Integer degree = 0;
        
        VariableDegreeTuple(Variable V, Assignment assignment) {
            this.V = V;
            List<Integer> list = getAdjacentIndices();
            list.stream().map((i) -> problem.variables().get(i)).filter((tile) -> (assignment.getValue(tile) == null)).forEach((_item) -> {
                degree++;
            });
        }
        
        // get the 20 adjacent tile numbers in a linked list.
        private List<Integer> getAdjacentIndices() {
            List<Integer> list = new LinkedList<>();
            SudokuTile variable = (SudokuTile)this.V;
            int tileNo = variable.tileNumber;
            int rowNo = tileNo / variable.boardSize;
            int colNo = tileNo % variable.boardSize;
            for(int i = 0; i < variable.boardSize; i++) {
                int rowTileNo = rowNo * variable.boardSize + i;
                int colTileNo = i * variable.boardSize + colNo;
                list.add(rowTileNo);
                list.add(colTileNo);
            }
            int n = (int)Math.sqrt(variable.boardSize);
            int blockRowNo = rowNo % n;
            int blockColNo = colNo % n;
            for(int i = 0; i < n; i++) {
                for(int j = 0; j < n; j++) {
                    int row = blockRowNo * n + i;
                    int col = blockColNo * n + j;
                    list.add(row * variable.boardSize + col);
                }
            }
            return list;
        }
        
        @Override
        public int compareTo(VariableDegreeTuple other) {
            return degree.compareTo(other.degree);
        }
        
    }
    
}
