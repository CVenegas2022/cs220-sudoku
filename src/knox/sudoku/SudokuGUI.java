package knox.sudoku;
import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import java.util.LinkedList;
import java.util.Stack;

/**
 * 
 * This is the GUI (Graphical User Interface) for Sudoku.
 * 
 * It extends JFrame, which means that it is a subclass of JFrame.
 * The JFrame is the main class, and owns the JMenuBar, which is 
 * the menu bar at the top of the screen with the File and Help 
 * and other menus.
 * 
 * 
 * One of the most important instance variables is a JCanvas, which is 
 * kind of like the canvas that we will paint all of the grid squared onto.
 * 
 * @author jaimespacco
 *
 */

//used some code from class
public class SudokuGUI extends JFrame {
	
	private Sudoku sudoku;
    
	private static final long serialVersionUID = 1L;
	
	// Sudoku boards have 9 rows and 9 columns
    private int numRows = 9;
    private int numCols = 9;
    
    // the current row and column we are potentially putting values into
    private int currentRow = -1;
    private int currentCol = -1;
    
    //ints to store the location of hint (code from class)
    private int hintRow=-1;
    private int hintCol=-1;
    
    //stack for undo function
    private Stack<Coordinate> hist = new Stack<Coordinate>();

    
    // figuring out how big to make each button
    // honestly not sure how much detail is needed here with margins
	protected final int MARGIN_SIZE = 5;
    protected final int DOUBLE_MARGIN_SIZE = MARGIN_SIZE*2;
    protected int squareSize = 90;
    private int width = DOUBLE_MARGIN_SIZE + squareSize * numCols;    		
    private int height = DOUBLE_MARGIN_SIZE + squareSize * numRows;  
    
    // for lots of fun, too much fun really, try "Wingdings"
    private static Font FONT = new Font("Verdana", Font.BOLD, 40);
    private static Color font_color = Color.BLACK;
    private static Color background_color = Color.GRAY;
    private static Color hint_color = Color.BLUE;
    
    // the canvas is a panel that gets drawn on
    private JPanel panel;

    // this is the menu bar at the top that owns all of the buttons
    private JMenuBar menuBar;
    
    // 2D array of buttons; each sudoku square is a button
    private JButton[][] buttons = new JButton[numRows][numCols];
    
    
    //helper class to allow row and column numbers to be stored in the stack together
    private class Coordinate {
    	int x;
    	int y;
    	
    	public Coordinate(int xVal, int yVal)
    	{
    		x=xVal;
    		y=yVal;
    	}
    	
    	public int getX()
    	{
    		return x;
    	}
    	
    	public int getY()
    	{
    		return y;
    	}
    	
    }
    
    private class MyKeyListener extends KeyAdapter {
    	public final int row;
    	public final int col;
    	public final Sudoku sudoku;
    	
    	MyKeyListener(int row, int col, Sudoku sudoku){
    		this.sudoku = sudoku;
    		this.row = row;
    		this.col = col;
    	}
    	
    	public void keyTyped(KeyEvent e) {
			char key = e.getKeyChar();
			//System.out.println(key);
			
			//checks legal values for a space by pressing '`'
			if(key=='`')
			{
				String vals="";
				LinkedList<Integer> legalVals=(LinkedList<Integer>) sudoku.getLegalValues(row, col);
				for(int i=0; i<legalVals.size(); i++)
				{
					vals+=legalVals.get(i)+"";
					if(i!=legalVals.size()-1)
					{
						vals+=", ";
					}
				}
				
				JOptionPane.showMessageDialog(null, "Legal Values for "+row+", "+col+":\n"+vals);
			}
			else if (Character.isDigit(key)) {
				// use ascii values to convert chars to ints
				int digit = key - '0';
				System.out.println(key);
				if (currentRow == row && currentCol == col) {
					//edited using code in class
					
					//if the space is not legal (checked with Sudoku class)
					if(!sudoku.isLegal(row, col, digit))
					{
						//print that it cannot be placed
						JOptionPane.showMessageDialog(null, "That value cannot be placed here");
					}
					//otherwise, place as normal
					else
					{
						sudoku.set(row, col, digit);
						hist.push(new Coordinate(row, col));
					}
				}
				update();
				
				//sends a victory message when all spaces are filled
				if(sudoku.gameOver())
				{
					JOptionPane.showMessageDialog(null, "Congratulations!  Puzzle Solved!");
				}
			}
		}
    }
    
    private class ButtonListener implements ActionListener {
    	public final int row;
    	public final int col;
    	public final Sudoku sudoku;
    	ButtonListener(int row, int col, Sudoku sudoku){
    		this.sudoku = sudoku;
    		this.row = row;
    		this.col = col;
    	}
		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.printf("row %d, col %d, %s\n", row, col, e);
			JButton button = (JButton)e.getSource();
			hintRow=-1;
			hintCol=-1;
			if (row == currentRow && col == currentCol) {
				currentRow = -1;
				currentCol = -1;
			} else if (sudoku.isBlank(row, col)) {
				// we can try to enter a value in a 
				currentRow = row;
				currentCol = col;
				
				
			} else {
				JOptionPane.showMessageDialog(null, "Can't enter a value here");
			}
			
			update();
		}
    }
    
    /**
     * Put text into the given JButton
     * 
     * @param row
     * @param col
     * @param text
     */
    private void setText(int row, int col, String text) {
    	buttons[row][col].setText(text);
    }
    
    /**
     * This is a private helper method that updates the GUI/view
     * to match any changes to the model
     */
    private void update() {
    	for (int row=0; row<numRows; row++) {
    		for (int col=0; col<numCols; col++) {
    			if(hintRow == row && hintCol == col)
    			{
    				buttons[row][col].setBackground(hint_color);
    				setText(row, col, "");
    			}
    			else if (row == currentRow && col == currentCol && sudoku.isBlank(row, col)) {
    				// draw this grid square special!
    				// this is the grid square we are trying to enter value into
    				buttons[row][col].setForeground(Color.RED);
    				// I can't figure out how to change the background color of a grid square, ugh
    				// Maybe I should have used JLabel instead of JButton?
    				buttons[row][col].setBackground(Color.CYAN);
    				setText(row, col, "_");
    			} else {
    				buttons[row][col].setForeground(font_color);
    				buttons[row][col].setBackground(background_color);
	    			int val = sudoku.get(row, col);
	    			if (val == 0) {
	    				setText(row, col, "");
	    			} else {
	    				setText(row, col, val+"");
	    			}
    			}
    		}
    	}
    	repaint();
    }
    
	
    private void createMenuBar() {
    	menuBar = new JMenuBar();
        
    	//
    	// File menu
    	//
    	JMenu file = new JMenu("File");
        menuBar.add(file);
        
        // anonymous inner class
        // basically, immediately create a class that implements actionlistener
        // and then give it the given actionPerformed method.
        addToMenu(file, "New Game", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	hist=new Stack<Coordinate>();
                sudoku.load("easy1.txt");
                update();
            }
        });
        
        addToMenu(file, "Save", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// HINT: Check the Util.java class for helpful methods
            	// HINT: check out JFileChooser
            	// https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
            	
            	String board=sudoku.toFileString();
            	
            	JFileChooser jfc=new JFileChooser(new File("."));
            	
            	int returnValue = jfc.showSaveDialog(null);
            	
            	if(returnValue == JFileChooser.APPROVE_OPTION)
            	{
            		File selectedFile=jfc.getSelectedFile();
            		Util.writeToFile(selectedFile, board);
            		System.out.println(selectedFile.getAbsolutePath());
            	}
            	
            	
                update();
            }
        });
        
        addToMenu(file, "Load", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            			
            	JFileChooser jfc=new JFileChooser(new File("."));
            	
            	int returnValue = jfc.showOpenDialog(null);
            	
            	if(returnValue == JFileChooser.APPROVE_OPTION)
            	{
            		File selectedFile=jfc.getSelectedFile();
            		String board = Util.readFromFile(selectedFile);
            		hist=new Stack<Coordinate>();
            		sudoku.load(selectedFile);
            		System.out.println("Loaded game from file "+selectedFile.getAbsolutePath());
            	}
            	
            	
                update();
            }
        });
        
        //
        // Help menu
        //
        JMenu help = new JMenu("Help");
        menuBar.add(help);
        
        addToMenu(help, "Hint", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//JOptionPane.showMessageDialog(null, "Give the user a hint! Highlight the most constrained square\n" + 
				//		"which is the square where the fewest posssible values can go");
				
				//These were implemented before rewatching the class recording
				//int tempR=-1;
				//int tempC=-1;
				
				for(int i=0; i<numRows; i++)
				{
					for(int j=0; j<numCols; j++)
					{
						if(sudoku.isBlank(i, j) && sudoku.getLegalValues(i, j).size()==1)
						{
							hintRow=i;
							hintCol=j;
							update();
							return;
						}
					}
				}
			}
		});
        
        JMenu edit = new JMenu("Edit");
        menuBar.add(edit);
        
        addToMenu(edit, "Undo", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!hist.empty())
				{
					Coordinate lastChanged = hist.pop();
					int x=lastChanged.getX();
					int y=lastChanged.getY();
					//System.out.println(x+", "+y);
					sudoku.set(x, y, 0);
					//System.out.println(sudoku.get(x, y));
					
					update();
				}
			}
		});
        
        //code to change the bg and font color (one of the two new features!)
        JMenu theme = new JMenu("Theme");
        menuBar.add(theme);
        
        //default theme
        addToMenu(theme, "Default", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				background_color=Color.gray;
				font_color=Color.black;
				update();
			}
		});
        
        //colors themed from my alma mater
        addToMenu(theme, "Green", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				background_color=Color.green;
				font_color=Color.white;
				update();
			}
		});
        
        //colors themed from the college I first attended
        addToMenu(theme, "Orange", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				background_color=Color.orange;
				font_color=Color.black;
				update();
			}
		});
        
        //Based on Knox.  Purple is not available so text becomes yellow and bg is light grey instead of purple
        addToMenu(theme, "Light Gray", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				background_color=Color.lightGray;
				font_color=Color.yellow;
				update();
			}
		});
        
        
        this.setJMenuBar(menuBar);
    }
    
    
    /**
     * Private helper method to put 
     * 
     * @param menu
     * @param title
     * @param listener
     */
    private void addToMenu(JMenu menu, String title, ActionListener listener) {
    	JMenuItem menuItem = new JMenuItem(title);
    	menu.add(menuItem);
    	menuItem.addActionListener(listener);
    }
    
    private void createMouseHandler() {
    	MouseAdapter a = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.printf("%s\n", e.getButton());
			}
    		
    	};
        this.addMouseMotionListener(a);
        this.addMouseListener(a);
    }
    
    
    private void createKeyboardHandlers() {
    	for (int r=0; r<buttons.length; r++) {
    		for (int c=0; c<buttons[r].length; c++) {
    			buttons[r][c].addKeyListener(new MyKeyListener(r, c, sudoku));
    		}
    	}
    }
    
    public SudokuGUI() {
        sudoku = new Sudoku();
        // load a puzzle from a text file
        // right now we only have 1 puzzle, but we could always add more!
        sudoku.load("easy1.txt");
        
        setTitle("Sudoku!");

        this.setSize(width, height);
        
        // the JPanel where everything gets painted
        panel = new JPanel();
        // set up a 9x9 grid layout, since sudoku boards are 9x9
        panel.setLayout(new GridLayout(9, 9));
        // set the preferred size
        // If we don't do this, often the window will be minimized
        // This is a weird quirk of Java GUIs
        panel.setPreferredSize(new Dimension(width, height));
        
        // This sets up 81 JButtons (9 rows * 9 columns)
        for (int r=0; r<numRows; r++) {
        	for (int c=0; c<numCols; c++) {
        		JButton b = new JButton();
        		b.setPreferredSize(new Dimension(squareSize, squareSize));
        		
        		b.setFont(FONT);
        		b.setForeground(font_color);
        		b.setBackground(background_color);
        		b.setOpaque(true);
        		buttons[r][c] = b;
        		// add the button to the canvas
        		// the layout manager (the 9x9 GridLayout from a few lines earlier)
        		// will make sure we get a 9x9 grid of these buttons
        		panel.add(b);

        		// thicker borders in some places
        		// sudoku boards use 3x3 sub-grids
        		int top = 1;
        		int left = 1;
        		int right = 1;
        		int bottom = 1;
        		if (r % 3 == 2) {
        			bottom = 5;
        		}
        		if (c % 3 == 2) {
        			right = 5;
        		}
        		if (r == 0) {
        			top = 5;
        		}
        		if (c == 9) {
        			bottom = 5;
        		}
        		b.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.black));
        		
        		//
        		// button handlers!
        		//
        		// check the ButtonListener class to see what this does
        		//
        		b.addActionListener(new ButtonListener(r, c, sudoku));
        	}
        }
        
        this.getContentPane().add(panel, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(width, height));
        this.setResizable(false);
        this.pack();
        this.setLocation(100,100);
        this.setFocusable(true);
        
        createMenuBar();
        createKeyboardHandlers();
        createMouseHandler();
        
        // close the GUI application when people click the X to close the window
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        update();
        repaint();
    }
    
    public static void main(String[] args) {
        SudokuGUI g = new SudokuGUI();
        g.setVisible(true);
    }

}
