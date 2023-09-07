import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    static final int SCREEN_WIDTH = 900;
    static final int SCREEN_HEIGHT = 700;
    static final Border panelBorder = BorderFactory.createLineBorder(Color.black, 1);
    static final JTextField[][] gridBoxes = new JTextField[9][9];
    static final int[][] boardNumbers = new int[9][9];
    static Stack<save> saveQueue = new Stack<save>();


    GamePanel() {

        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(145, 180, 219));

        //add 'Sudoku' title
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(145, 180, 219));
        titlePanel.setPreferredSize(new Dimension(SCREEN_WIDTH,60));

        JLabel title = new JLabel("Sudoku!");
        title.setForeground(Color.black);
        title.setFont(new Font(Font.SANS_SERIF,Font.BOLD,50));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(title,BorderLayout.CENTER);
        this.add(titlePanel, BorderLayout.NORTH);
        this.add(createSidePanel(), BorderLayout.WEST);

        //add a bottom margin
        JPanel bottomMargin = new JPanel();
        bottomMargin.setPreferredSize(new Dimension(SCREEN_WIDTH,40));
        bottomMargin.setBackground(new Color(145, 180, 219));
        this.add(bottomMargin, BorderLayout.SOUTH);

        //add sudoku board panel
        JPanel boardPanel = createSudokuBoardPanel();

        boardPanel.add(createGrid());

        this.add(boardPanel, BorderLayout.EAST);
    }

    public boolean checkEntered(int row, int col, int entered) {

        if (checkRow(entered,row) && checkCol(entered,col)) {
            return true;
        } else {
            return false;
        }


    }

    public boolean checkRow(int entered, int row) {
        for (int i = 0; i < 9; i++) {
            if (boardNumbers[row][i] == entered) {
                return false;
            }
        }
        return true;
    }

    public boolean checkCol(int entered, int col) {
        for (int i = 0; i < 9; i++) {
            if (boardNumbers[i][col] == entered) {
                return false;
            }
        }
        return true;
    }

    public JPanel createGrid() {
        JPanel grid = new JPanel();
        grid.setBackground(new Color(145, 180, 219));
        grid.setLayout(new GridLayout(9,9));
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                JTextField box = new JTextField();

                if (i == 0 || i == 3 || i == 6) {
                    if (j == 0 ) {
                        box.setBorder(BorderFactory.createMatteBorder(6, 6, 1, 1, Color.BLACK));
                    } else if (j == 3 || j == 6) {
                        box.setBorder(BorderFactory.createMatteBorder(6, 6, 1, 1, Color.BLACK));
                    } else if (j == 8) {
                        box.setBorder(BorderFactory.createMatteBorder(6, 1, 1, 6, Color.BLACK));
                    } else {
                        box.setBorder(BorderFactory.createMatteBorder(6, 1, 1, 1, Color.BLACK));
                    }
                } else if (i == 8){
                    if (j == 0) {
                        box.setBorder(BorderFactory.createMatteBorder(1, 6, 6, 1, Color.BLACK));
                    } else if (j == 3 || j == 6) {
                        box.setBorder(BorderFactory.createMatteBorder(1, 6, 6, 1, Color.BLACK));
                    } else if (j == 8) {
                        box.setBorder(BorderFactory.createMatteBorder(1, 1, 6, 6, Color.BLACK));
                    } else {
                        box.setBorder(BorderFactory.createMatteBorder(1, 1, 6, 1, Color.BLACK));
                    }
                } else if (j == 0 || j == 3 || j == 6) {
                    box.setBorder(BorderFactory.createMatteBorder(1, 6, 1, 1, Color.BLACK));
                } else if (j == 8) {
                    box.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 6, Color.BLACK));
                } else {
                    box.setBorder(panelBorder);
                }

                box.setFont(new Font(Font.DIALOG,Font.BOLD, 25));
                box.setHorizontalAlignment(JTextField.CENTER);
                box.setDisabledTextColor(Color.black);
                gridBoxes[i][j] = box;
                grid.add(box);
                box.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            if (Integer.parseInt(box.getText()) > 9 || Integer.parseInt(box.getText())
                                    < 1) {
                                box.setText("");
                            } else {
                                box.setEnabled(false);
                                updateGame();
                            }
                        } catch (NumberFormatException err) {
                            box.setText("");
                        }

                    }
                });
            }
        }
        return grid;
    }

    public void updateGame() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!gridBoxes[i][j].isEnabled() && !isPreviouslySaved(i,j)) {
                    boardNumbers[i][j] = Integer.parseInt(gridBoxes[i][j].getText());
                    saveQueue.add(new save(i,j));
                }
            }
        }
    }

    public JButton undoButton() {
        JButton undoButton = new JButton();
        undoButton.setBackground(new Color(227, 136, 39));
        undoButton.setBorder(null);
        //undoButton.setForeground(Color.white);
        undoButton.setOpaque(false);
        //undoButton.setContentAreaFilled(false);
        undoButton.setBorderPainted(false);
        undoButton.setFocusPainted(false);
        undoButton.setFont(new Font("Comic Sans", Font.BOLD, 20));
        ImageIcon icon = new ImageIcon(getClass().getResource("undo.png"));
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(70,70, java.awt.Image.SCALE_SMOOTH );
        icon = new ImageIcon(newimg);
        undoButton.setIcon(icon);
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (saveQueue.size() != 0) {
                    int row = saveQueue.peek().getRow();
                    int col = saveQueue.peek().getCol();
                    boardNumbers[row][col] = 0;
                    gridBoxes[row][col].setEnabled(true);
                    gridBoxes[row][col].setText("");
                    saveQueue.pop();
                }
            }
        });
        return undoButton;
    }

    public boolean isPreviouslySaved(int row, int col) {
        for (save save : saveQueue) {
            if (save.getRow() == row && save.getCol() == col) {
                return true;
            }
        }
        return false;
    }

    public JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        Border sideBorder = BorderFactory.createMatteBorder(6,1,6,6,Color.black);
        sidePanel.setLayout(new GridLayout(5,1));
        sidePanel.setPreferredSize(new Dimension(140,SCREEN_HEIGHT));
        sidePanel.setBackground(new Color(227, 136, 39));
        sidePanel.setBorder(sideBorder);
        for (int i = 0; i < 5; i++) {
            if (i == 3) {
                sidePanel.add(undoButton());
            } else {
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                panel.setBackground(new Color(227, 136, 39));
                if (i == 2) {
                    JLabel label = new JLabel("Undo");
                    label.setFont(new Font(Font.SANS_SERIF,Font.BOLD, 20));
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    panel.add(label, BorderLayout.SOUTH);
                }
                sidePanel.add(panel);
            }
        }
        return sidePanel;
    }

    public JPanel createSudokuBoardPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(730,630));
        mainPanel.setBackground(new Color(145, 180, 219));

        JPanel rightMargin = new JPanel();
        rightMargin.setPreferredSize(new Dimension(75,SCREEN_HEIGHT));
        rightMargin.setBackground(new Color(145, 180, 219));
        mainPanel.add(rightMargin, BorderLayout.EAST);

        JPanel leftMargin = new JPanel();
        leftMargin.setPreferredSize(new Dimension(35,SCREEN_HEIGHT));
        leftMargin.setBackground(new Color(145, 180, 219));
        mainPanel.add(leftMargin, BorderLayout.WEST);

        JPanel margin = new JPanel();
        margin.setPreferredSize(new Dimension(SCREEN_WIDTH,40));
        margin.setBackground(new Color(145, 180, 219));
        mainPanel.add(margin, BorderLayout.NORTH);



        return mainPanel;
    }

}
