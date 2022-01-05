/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.actionTree;

import de.sudoq.model.sudoku.Cell;

/**
 * Diese Klasse repräsentiert eine Aktion auf Eintrag eines Sudokufeldes.
 */
public class SolveAction extends Action
{
	/**
	 * Ein geschützter Konstruktor um die Instanziierung von Actions außerhalb
	 * dieses Packages zu vermeiden. Wird er aufgerufen wird eine neue
	 * SolveAction mit den gegebenen Parametern instanziiert. Ist das
	 * spezifizierte Field null, so wird eine IllegalArgumentException geworfen.
	 *
	 * @param diff Der Unterschied zwischen altem und neuem Wert
	 * @param cell Das zu bearbeitende Feld
	 * @throws IllegalArgumentException Wird geworfen, falls das übergebene Field null ist
	 */
	protected SolveAction(int diff, Cell cell)
	{
		super(diff, cell);
	}
	
	@Override
	public void execute()
	{
		cell.setCurrentValue(cell.getCurrentValue() + diff);
	}
	
	@Override
	public void undo()
	{
		cell.setCurrentValue(cell.getCurrentValue() - diff);
	}
	
	public boolean inverse(Action a)
	{
		if(a instanceof SolveAction)
		{
			return cell.equals(a.cell) && diff + a.diff == 0;
		}
		return false;
	}
	
	/**
	 * in case we want to move an action up one level (field stays the same) we need to add the diffs
	 * TODO refactor this, break with diffs entirely maybe?
	 *
	 * @param action another action
	 * @return a new SolveAction that sums up both actions diffs
	 */
	public SolveAction add(SolveAction action)
	{
		return new SolveAction(diff + action.diff, cell);
	}
}
