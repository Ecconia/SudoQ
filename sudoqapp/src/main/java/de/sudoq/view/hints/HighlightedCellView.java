/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.view.hints;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import de.sudoq.controller.sudoku.Symbol;
import de.sudoq.model.sudoku.Cell;
import de.sudoq.model.sudoku.Position;
import de.sudoq.view.sudoku.SudokuLayout;

/**
 * Diese Subklasse des von der Android API bereitgestellten Views stellt ein
 * einzelnes Feld innerhalb eines Sudokus dar. Es erweitert den Android View um
 * Funktionalität zur Benutzerinteraktion und Färben.
 */
public class HighlightedCellView extends View
{
	/* Attributes */
	
	/**
	 * The Cell, represented by this View
	 *
	 * @see Position
	 */
	private Position position;
	
	/**
	 * Color of the margin
	 */
	private int marginColor;
	
	private SudokuLayout sl;
	
	private Paint paint = new Paint();
	private RectF oval = new RectF();
	/* Constructors */
	
	/**
	 * Creates a SudokuCellView
	 *
	 * @param context the application context
	 * @param sl a sudokuLayout
	 * @param position cell represented
	 * @param color Color of the margin
	 * @throws IllegalArgumentException if context or position are null
	 */
	public HighlightedCellView(Context context, SudokuLayout sl, Position position, int color)
	{
		super(context);
		if(context == null || position == null)
		{
			throw new IllegalArgumentException();
		}
		
		this.position = position;
		this.marginColor = color;
		this.sl = sl;
		paint.setColor(marginColor);
		int thickness = 10;
		paint.setStrokeWidth(thickness * sl.getCurrentSpacing());
		style = paint.getStyle();
	}
	
	Paint.Style style;
	/* Methods */
	
	/**
	 * Draws the content of the cell on the canvas of this SudokuCellView.
	 * Sollte den AnimationHandler nutzen um vorab Markierungen/Färbung an dem
	 * Canvas Objekt vorzunehmen.
	 *
	 * @param canvas Das Canvas Objekt auf das gezeichnet wird
	 * @throws IllegalArgumentException Wird geworfen, falls das übergebene Canvas null ist
	 */
	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		//Todo use canvas.drawRoundRect();
		drawNewMethod(position, canvas, marginColor);//red
	}
	
	private void drawOldMethod(Position p, Canvas canvas)
	{
		float edgeRadius = sl.getCurrentCellViewSize() / 20.0f;
		paint.reset();
		int thickness = 10;
		paint.setStrokeWidth(thickness * sl.getCurrentSpacing());
		
		//deklariert hier, weil wir es nicht früher brauchen, effizienter wäre weiter oben
		int cellSizeAndSpacing = sl.getCurrentCellViewSize() + sl.getCurrentSpacing();
		/* these first 4 seem similar. drawing the black line around?*/
		/* cells that touch the edge: Paint your edge but leave space at the corners*/
		
		paint.reset();
		paint.setStrokeWidth(thickness * sl.getCurrentSpacing());
		paint.setColor(marginColor);
		
		float leftX, rightX, topY, bottomY;
		
		leftX = sl.getCurrentLeftMargin() + p.getX() * cellSizeAndSpacing - sl.getCurrentSpacing() / 2;
		rightX = sl.getCurrentLeftMargin() + (p.getX() + 1) * cellSizeAndSpacing - sl.getCurrentSpacing() / 2;
		
		topY = sl.getCurrentTopMargin() + p.getY() * cellSizeAndSpacing - sl.getCurrentSpacing() / 2;
		bottomY = sl.getCurrentTopMargin() + (p.getY() + 1) * cellSizeAndSpacing - sl.getCurrentSpacing() / 2;
		
		float startY, stopY, startX, stopX;
		
		/* left edge */
		startY = sl.getCurrentTopMargin() + p.getY() * cellSizeAndSpacing + edgeRadius;
		stopY = sl.getCurrentTopMargin() + (p.getY() + 1) * cellSizeAndSpacing - edgeRadius - sl.getCurrentSpacing();
		canvas.drawLine(leftX, startY, leftX, stopY, paint);
		
		/* right edge */
		canvas.drawLine(rightX, startY, rightX, stopY, paint);
		
		/* top edge */
		startX = sl.getCurrentLeftMargin() + p.getX() * cellSizeAndSpacing + edgeRadius;
		stopX = sl.getCurrentLeftMargin() + (p.getX() + 1) * cellSizeAndSpacing - edgeRadius - sl.getCurrentSpacing();
		canvas.drawLine(startX, topY, stopX, topY, paint);
		
		/* bottom edge */
		canvas.drawLine(startX, bottomY, stopX, bottomY, paint);
		
		/* Cells at corners of their block draw a circle for a round circumference*/
		
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		float radius = edgeRadius + sl.getCurrentSpacing() / 2;
		short angle = 90 + 10;
		
		float centerX, centerY;
		/*TopLeft*/
		centerX = sl.getCurrentLeftMargin() + p.getX() * cellSizeAndSpacing + edgeRadius;
		centerY = sl.getCurrentTopMargin() + p.getY() * cellSizeAndSpacing + edgeRadius;
		
		oval.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
		canvas.drawArc(oval, 180 - 5, angle, false, paint);
		
		/* Top Right*/
		centerX = sl.getCurrentLeftMargin() + (p.getX() + 1) * cellSizeAndSpacing - sl.getCurrentSpacing() - edgeRadius;
		centerY = sl.getCurrentTopMargin() + p.getY() * cellSizeAndSpacing + edgeRadius;
		
		oval.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
		canvas.drawArc(oval, 270 - 5, angle, false, paint);
		
		/*Bottom Left*/
		centerX = sl.getCurrentLeftMargin() + p.getX() * cellSizeAndSpacing + edgeRadius;
		centerY = sl.getCurrentTopMargin() + (p.getY() + 1) * cellSizeAndSpacing - edgeRadius - sl.getCurrentSpacing();
		
		oval.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
		canvas.drawArc(oval, 90 - 5, angle, false, paint);
		
		/*BottomRight*/
		centerX = sl.getCurrentLeftMargin() + (p.getX() + 1) * cellSizeAndSpacing - edgeRadius - sl.getCurrentSpacing();
		centerY = sl.getCurrentTopMargin() + (p.getY() + 1) * cellSizeAndSpacing - edgeRadius - sl.getCurrentSpacing();
		
		oval.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
		canvas.drawArc(oval, 0 - 5, angle, false, paint);
	}
	
	private void drawNewMethod(Position p, Canvas canvas, int color)
	{
		float edgeRadius = sl.getCurrentCellViewSize() / 20.0f;
		paint.reset();
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		int thickness = 10;
		paint.setStrokeWidth(thickness * sl.getCurrentSpacing());
		
		int cellSizeAndSpacing = sl.getCurrentCellViewSize() + sl.getCurrentSpacing();
		
		float left = sl.getCurrentLeftMargin() + p.getX() * cellSizeAndSpacing - sl.getCurrentSpacing() / 2;
		float top = sl.getCurrentTopMargin() + p.getY() * cellSizeAndSpacing - sl.getCurrentSpacing() / 2;
		float right = sl.getCurrentLeftMargin() + (p.getX() + 1) * cellSizeAndSpacing - sl.getCurrentSpacing() / 2;
		float bottom = sl.getCurrentTopMargin() + (p.getY() + 1) * cellSizeAndSpacing - sl.getCurrentSpacing() / 2;
		canvas.drawRoundRect(new RectF(left, top, right, bottom)
				, edgeRadius + sl.getCurrentSpacing() / 2
				, edgeRadius + sl.getCurrentSpacing() / 2, paint);
	}
	
	/**
	 * TODO may come in handy later for highlighting notes. or do that seperately
	 * Zeichnet die Notizen in dieses Feld
	 *
	 * @param canvas Das Canvas in das gezeichnet werde nsoll
	 * @param cell Das Canvas in das gezeichnet werde nsoll
	 */
	private void drawNotes(Canvas canvas, Cell cell)
	{
		Paint notePaint = new Paint();
		notePaint.setAntiAlias(true);
		int noteTextSize = getHeight() / Symbol.getInstance().getRasterSize();
		notePaint.setTextSize(noteTextSize);
		notePaint.setTextAlign(Paint.Align.CENTER);
		notePaint.setColor(Color.BLACK);
		for(int i = 0; i < Symbol.getInstance().getNumberOfSymbols(); i++)
		{
			if(cell.isNoteSet(i))
			{
				String note = Symbol.getInstance().getMapping(i);
				canvas.drawText(note + "", (i % Symbol.getInstance().getRasterSize()) * noteTextSize + noteTextSize / 2, (i / Symbol.getInstance().getRasterSize()) * noteTextSize + noteTextSize, notePaint);
			}
		}
	}
}
