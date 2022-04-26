package knox.sudoku;
import java.io.File;
import java.io.FileInputStream;
//fixed the next import with help from Stack Overflow
//https://stackoverflow.com/questions/15416186/eclipse-the-import-java-io-cannot-be-resolved
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JOptionPane;

/**
 * 
 * This is the MODEL class. This class knows all about the
 * underlying state of the Sudoku game. We can VIEW the data
 * stored in this class in a variety of ways, for example,
 * using a simple toString() method, or using a more complex
 * GUI (Graphical User Interface) such as the SudokuGUI 
 * class that is included.
 * 
 * @author jaimespacco
 *
 */

//used some code from class

public class Sudoku {
	int[][] board = new int[9][9];
	
	public int get(int row, int col) {
		if((row>9 || row<0) || (col>9 || col<0))
		{
			System.out.println("invalid space");
			return -1;
		}
		return board[row][col];
	}
	
	public void set(int row, int col, int val) {
		
		//checks to make sure val is 1-9 and is valid
		if(val<0 || val>9)
		{
			System.out.println("invalid number");
			return;
		}
		
		board[row][col] = val;
	}
	
	public boolean isLegal(int row, int col, int val) {
		
		LinkedList<Integer> legalVals=(LinkedList<Integer>) getLegalValues(row, col);
		
		if(!isBlank(row, col))
		{
			System.out.println("Space is not blank");
			return false;
		}
		
		for(int i=0; i<legalVals.size(); i++)
		{
			if(val==legalVals.get(i))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public Collection<Integer> getLegalValues(int row, int col) {
		
		Collection<Integer> vals = new LinkedList<Integer>();
		
		for(int i=1; i<10; i++)
		{
			vals.add(i);
		}
		
		for(int k=1; k<10; k++)
		{
			//check to see if number exists in column
			for(int i=0; i<9; i++)
			{
				if(get(i, col)==k)
				{
					vals.remove(k);
				}
			}
			
			//check to see if number exists in row
			for(int i=0; i<9; i++)
			{
				if(get(row, i)==k)
				{
					vals.remove(k);
				}
			}
					
			//check to see if number exists in box
			int boxR = get3x3row(row);
			int boxC = get3x3row(col);
			for(int i=0; i<9; i++)
			{
				for(int j=0; j<9; j++)
				{
					if(get3x3row(i)==boxR && get3x3row(j)==boxC)
					{
						if(get(i, j)==k)
						{
							vals.remove(k);
						}
					}
				}
			}
		}
		
		return vals;
	}
	
/**

_ _ _ 3 _ 4 _ 8 9
1 _ 3 2 _ _ _ _ _
etc


0 0 0 3 0 4 0 8 9

 */
	public void load(File file) {
		try {
			Scanner scan = new Scanner(new FileInputStream(file));
			// read the file
			for (int r=0; r<9; r++) {
				for (int c=0; c<9; c++) {
					int val = scan.nextInt();
					board[r][c] = val;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void load(String filename) {
		load(new File(filename));
	}
	
	/**
	 * Return which 3x3 grid this row is contained in.
	 * 
	 * @param row
	 * @return
	 */
	public int get3x3row(int row) {
		return row / 3;
	}
	
	/**
	 * Convert this Sudoku board into a String
	 */
	public String toString() {
		String result = "";
		for (int r=0; r<9; r++) {
			for (int c=0; c<9; c++) {
				int val = get(r, c);
				if (val == 0) {
					result += "_ ";
				} else {
					result += val + " ";
				}
			}
			result += "\n";
		}
		return result;
	}
	
	public String toFileString() {
		String result = "";
		for (int r=0; r<9; r++) {
			for (int c=0; c<9; c++) {
				int val = get(r, c);
				result += val + " ";
				}
			result += "\n";
			}
			
		return result;
	}
	
	public static void main(String[] args) {
		Sudoku sudoku = new Sudoku();
		sudoku.load("easy1.txt");
		System.out.println(sudoku);
		
		Scanner scan = new Scanner(System.in);
		while (!sudoku.gameOver()) {
			System.out.println("enter value r, c, v :");
			int r = scan.nextInt();
			int c = scan.nextInt();
			int v = scan.nextInt();
			sudoku.set(r, c, v);

			System.out.println(sudoku);
		}
	}

	public boolean gameOver() {
		
		boolean isGGEZ=true;	//boolean to keep track of whether all spaces are filled
		for(int i=0; i<9; i++)
		{
			for(int j=0; j<9; j++)
			{
				//if the current space is blank...
				if(isBlank(i, j))
				{
					//set the boolean to false and break
					isGGEZ=false;
					break;
				}
			}
			//if all spaces aren't filled...
			if(!isGGEZ)
			{
				//exit the loop
				break;
			}
		}
		
		//return the boolean
		return isGGEZ;
	}

	public boolean isBlank(int row, int col) {
		return board[row][col] == 0;
	}

}
