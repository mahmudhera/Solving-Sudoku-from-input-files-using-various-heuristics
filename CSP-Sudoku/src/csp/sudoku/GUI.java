package csp.sudoku;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author HERA
 */
public class GUI extends JFrame {

    private final JFrame jf = this;
    private final Grid grid;
    
    public GUI(List<Variable> vars, Assignment solution) {
        
        setTitle("Solution");
        this.setLayout(new BorderLayout());
        
        this.setSize(40 * ((SudokuTile)vars.get(0)).boardSize + 20, 60 + 40 * ((SudokuTile)vars.get(0)).boardSize);
        
        grid = new Grid(vars, solution);
        add(grid);
    
    }

    class Grid extends JPanel {

        private final Color c = Color.BLACK;
        private final JButton jButton1;
        
        Assignment solution;
        List<Variable> vars;
        
        public Grid(List<Variable> list, Assignment solution) {
            
            vars = list;
            this.solution = solution;
            
            setLayout(new FlowLayout());
            jButton1 = new JButton("Close");
            add(jButton1);
            repaint();
            
            jButton1.addActionListener((java.awt.event.ActionEvent evt) -> {
                jButton1ActionPerformed(evt);
            });
        
        }

        private void jButton1ActionPerformed(ActionEvent evt) {
            jf.dispose();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            vars.stream().map((v) -> {
                SudokuTile v_ = (SudokuTile)v;
                g.drawString(solution.getValue(v) + "" , 20 + v_.getRow() * 40, 60 + v_.getCol() * 40);
                return v;
            }).forEach((_item) -> {
                repaint();
            });
            
            int sqrt_n = (int)Math.sqrt(((SudokuTile)vars.get(0)).boardSize);
            for(int i = 0; i <= sqrt_n; i++) {
                for(int j = 0; j <= sqrt_n; j++) {
                    g.drawLine(0, 40+j*sqrt_n*40, sqrt_n*sqrt_n*40, 40+j*sqrt_n*40);
                    g.drawLine(sqrt_n*40*i, 40, sqrt_n*40*i, 40 + sqrt_n*sqrt_n*40);
                }
            }
            //g.drawLine(0, 40, 380, 40);
            //g.drawLine(0, 160, 380, 160);
            //g.drawLine(0, 280, 380, 280);
            //g.drawLine(0, 395, 380, 395);
            //g.drawLine(0, 40, 0, 395);
            //g.drawLine(120, 40, 120, 395);
            //g.drawLine(240, 40, 240, 395);
            //g.drawLine(375, 40, 375, 395);
            repaint();
        }
    }
}