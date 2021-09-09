package de.sudoq.model.sudoku;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import de.sudoq.model.Utility;
import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class SumConstraintBehaviorTests {
	private static File sudokuDir  = new File(Utility.RES + File.separator + "tmp_suds");

	//this is a dummy so it compiles todo use xmls from resources
	private IRepo<SudokuType> sudokuTypeRepo = null;//new SudokuTypeRepo();

	@BeforeClass
	public static void init() {
		Utility.copySudokus();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalValue() {
		@SuppressWarnings("unused")
		SumConstraintBehavior c = new SumConstraintBehavior(-1);
	}

	@Test
	public void testConstraint() {
		
		TypeBuilder.get99();//just to force initialization of fileManager
		
		Sudoku sudoku = new SudokuBuilder(SudokuTypes.standard9x9, sudokuTypeRepo).createSudoku();

		sudoku.getCell(Position.get(0, 0)).setCurrentValue(1);
		sudoku.getCell(Position.get(0, 1)).setCurrentValue(2);
		sudoku.getCell(Position.get(0, 2)).setCurrentValue(3);
		sudoku.getCell(Position.get(1, 0)).setCurrentValue(1);
		sudoku.getCell(Position.get(1, 1)).setCurrentValue(2);
		sudoku.getCell(Position.get(1, 2)).setCurrentValue(3);

		Constraint constraint = new Constraint(new SumConstraintBehavior(12), ConstraintType.LINE);
		constraint.addPosition(Position.get(0, 0));
		constraint.addPosition(Position.get(0, 1));
		constraint.addPosition(Position.get(0, 2));
		constraint.addPosition(Position.get(1, 0));
		constraint.addPosition(Position.get(1, 1));
		constraint.addPosition(Position.get(1, 2));

		assertFalse("constraint has unique behavior", constraint.hasUniqueBehavior());
		assertTrue("constraint not saturated", constraint.isSaturated(sudoku));

		sudoku.getCell(Position.get(1, 2)).clearCurrentValue();
		assertTrue("constraint not saturated", constraint.isSaturated(sudoku));

		sudoku.getCell(Position.get(1, 1)).setCurrentValue(8);
		assertFalse("constraint not saturated", constraint.isSaturated(sudoku));

		sudoku.getCell(Position.get(1, 2)).setCurrentValue(2);
		assertFalse("constraint not saturated", constraint.isSaturated(sudoku));

		sudoku.getCell(Position.get(1, 2)).setCurrentValue(4);
		assertFalse("constraint not saturated", constraint.isSaturated(sudoku));
	}
}
