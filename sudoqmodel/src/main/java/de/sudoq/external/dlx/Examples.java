package de.sudoq.external.dlx;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.*;

import de.sudoq.model.solverGenerator.FastSolver.DLX1.DancingLinks;
import de.sudoq.model.solverGenerator.FastSolver.DLX1.Sudoku16DLX;
import de.sudoq.model.solverGenerator.FastSolver.DLX1.SudokuDLX;

public class Examples
{
	static void runCoverExample()
	{
		int[][] example = {
				{0, 0, 1, 0, 1, 1, 0},
				{1, 0, 0, 1, 0, 0, 1},
				{0, 1, 1, 0, 0, 1, 0},
				{1, 0, 0, 1, 0, 0, 0},
				{0, 1, 0, 0, 0, 0, 1},
				{0, 0, 0, 1, 1, 0, 1}
		};
		
		DancingLinks DLX = new DancingLinks(example);
		DLX.runSolver();
	}
	
	private static int[][] fromString(String s)
	{
		int[][] board = new int[9][9];
		for(int i = 0; i < 81; i++)
		{
			char c = s.charAt(i);
			int row = i / 9;
			int col = i % 9;
			if(c != '.')
			{
				board[row][col] = c - '0';
			}
		}
		return board;
	}
	
	private static void runExample()
	{
		String[] diffs = {
				"simple.txt",
				"easy.txt",
				"intermediate.txt",
				"expert.txt"
		};
		
		BufferedReader reader = null;
		String text = null;
		
		for(String diff : diffs)
		{
			String filename = "boards/" + diff;
			
			List<Long> timings = new ArrayList<Long>();
			
			try
			{
				reader = new BufferedReader(new FileReader(filename));
				
				while((text = reader.readLine()) != null)
				{
					int[][] sudoku = fromString(text);
					
					// change below to whichever one you want to try out
					SudokuDLX solver = new SudokuDLX();
					//NaiveSudokuSolver solver = new NaiveSudokuSolver();
					
					long milis = System.nanoTime();
					
					solver.solve(sudoku);
					
					long elapsed = System.nanoTime() - milis;
					
					timings.add(elapsed);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			System.out.println("STATS: " + diff + "\n");
			printStats(timings);
		}
	}
	
	private static void printStats(List<Long> timings)
	{
		long min = timings.get(0);
		long max = timings.get(0);
		
		long sum = 0;
		long sqsum = 0;
		
		for(long ll : timings)
		{
			min = Math.min(min, ll);
			max = Math.max(max, ll);
			sum += ll;
		}
		
		double avg = sum / timings.size();
		
		for(long ll : timings)
		{
			sqsum += (ll - avg) * (ll - avg);
		}
		
		double std = Math.sqrt(sqsum / timings.size());
		
		System.out.println("min: " + min * 1e-6);
		System.out.println("max: " + max * 1e-6);
		System.out.println("avg: " + avg * 1e-6);
		System.out.println("std: " + std * 1e-6);
	}
	
	static void runSudokuExample()
	{
		int[][] hardest = {
				{0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 6, 0, 0, 0, 0, 0},
				{0, 7, 0, 0, 9, 0, 2, 0, 0},
				{0, 5, 0, 0, 0, 7, 0, 0, 0},
				{0, 0, 0, 0, 4, 5, 7, 0, 0},
				{0, 0, 0, 1, 0, 0, 0, 3, 0},
				{0, 0, 1, 0, 0, 0, 0, 6, 8},
				{0, 0, 8, 5, 0, 0, 0, 1, 0},
				{0, 9, 0, 0, 0, 0, 4, 0, 0}
		}; // apparently the hardest sudoku
		
		SudokuDLX sudoku = new SudokuDLX();
		sudoku.solve(hardest);
	}
	
	static void runInvalidSudokuExample()
	{
		int[][] hardest = {
				{1, 2, 3, 4, 5, 6, 7, 8, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 7, 0, 0, 9, 0, 2, 0, 0},
				{0, 5, 0, 0, 0, 7, 0, 0, 0},
				{0, 0, 0, 0, 4, 5, 0, 0, 0},
				{0, 0, 0, 1, 0, 0, 0, 3, 0},
				{0, 0, 1, 0, 0, 0, 0, 6, 8},
				{0, 0, 8, 5, 0, 0, 0, 1, 0},
				{0, 0, 0, 0, 0, 0, 4, 0, 9}
		}; // apparently the hardest sudoku
		
		SudokuDLX sudoku = new SudokuDLX();
		sudoku.solve(hardest);
	}
	
	static void runSudoku16Example()
	{
		int[][] hardest = {
				{0, 0, 0, 0, 0, 0, 15, 0, 6, 0, 4, 0, 0, 0, 0, 0},
				{0, 0, 2, 0, 0, 0, 0, 3, 0, 0, 9, 0, 12, 0, 0, 0},
				{0, 13, 0, 0, 0, 14, 10, 0, 0, 0, 15, 0, 5, 0, 1, 4},
				{0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 5, 0, 0, 0, 0, 0},
				{0, 9, 0, 0, 0, 8, 0, 0, 0, 0, 0, 10, 1, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 12, 2, 0, 6, 0, 5, 0, 8, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 10, 0},
				{10, 0, 5, 0, 0, 6, 0, 0, 0, 9, 0, 8, 0, 0, 0, 12},
				{7, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 1},
				{0, 0, 0, 0, 0, 0, 0, 0, 11, 16, 0, 0, 0, 0, 12, 0},
				{0, 0, 0, 0, 13, 0, 0, 5, 0, 0, 1, 0, 7, 0, 0, 8},
				{0, 0, 0, 10, 16, 7, 0, 0, 0, 2, 8, 0, 0, 0, 13, 0},
				{0, 0, 0, 0, 1, 0, 0, 14, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 12, 6, 0, 4, 10, 0, 13},
				{0, 0, 13, 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 0, 14, 0},
				{0, 11, 0, 0, 0, 0, 0, 0, 0, 10, 0, 0, 0, 9, 15, 0}
		};
		
		Sudoku16DLX sudoku = new Sudoku16DLX();
		sudoku.solve(hardest);
	}
	
	public static void main(String[] args)
	{
		//runCoverExample();
		//runSudokuExample();
		//runSudoku16Example();
		runInvalidSudokuExample();
		//runExample();
	}
}
