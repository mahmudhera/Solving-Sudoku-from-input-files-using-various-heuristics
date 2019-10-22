package csp.sudoku;

import java.util.LinkedList;
import java.util.List;

public class SudokuTile extends Variable {

        public int tileNumber; // Between 0 and 80
        public int boardSize;

        private static List<Object> domain = new LinkedList<>();
        
        public SudokuTile(int i) {
            tileNumber = i;
            boardSize = SudokuProblem.boardSize;
        }

        @Override
        public String description() {
            return "Tile " + tileNumber + " (" + (tileNumber / boardSize) + "," + (tileNumber % boardSize) + ")";
        }
        
        public int getRow() {
            return tileNumber / boardSize;
        }
        
        public int getCol() {
            return tileNumber % boardSize;
        }
        
        public int getTile() {
            return tileNumber;
        }
        
        public static void setDomain(List<Object> l) {
            domain = l;
        }

        @Override
        public List<Object> domain() {
            return domain;
        }
    }