/*
 * Copyright (C) 2009 Roman Masek
 *
 * This file is part of OpenSudoku.
 *
 * OpenSudoku is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenSudoku is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenSudoku.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.moire.opensudoku.gui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.CellNote;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.utils.ThemeUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Sudoku board widget.
 *
 * @author romario
 */
public class SudokuBoardView extends View {

    public static final int DEFAULT_BOARD_SIZE = 100;

    /**
     * "Color not set" value. (In relation to {@link Color}, it is in fact black color with
     * alpha channel set to 0 => that means it is completely transparent).
     */
    private static final int NO_COLOR = 0;

    private float mCellWidth;
    private float mCellHeight;

    private Cell mTouchedCell;
    // TODO: should I synchronize access to mSelectedCell?
    private Cell mSelectedCell;
    private int mHighlightedValue = 0;
    private boolean mReadonly = false;
    private boolean mHighlightWrongVals = true;
    private boolean mHighlightTouchedCell = true;
    private boolean mAutoHideTouchedCellHint = true;
    public enum HighlightMode {
        NONE,
        NUMBERS,
        NUMBERS_AND_NOTES,
        OVERRIDE
    };
    private HighlightMode mHighlightSimilarCells = HighlightMode.NONE;
    private Map<Cell, HighlightOptions> mHighlightCellOverrides;
    private boolean mDimCellsThatAreNotHighlighted = false;

    private SudokuGame mGame;
    private CellCollection mCells;

    private OnCellTappedListener mOnCellTappedListener;
    private OnCellSelectedListener mOnCellSelectedListener;

    private Paint mLinePaint;
    private Paint mSectorLinePaint;
    private Paint mCellValuePaint;
    private Paint mCellValueReadonlyPaint;
    private Paint mCellNotePaint;
    private int mNumberLeft;
    private int mNumberTop;
    private float mNoteTop;
    private int mSectorLineWidth;
    private Paint mBackgroundColorSecondary;
    private Paint mBackgroundColorReadOnly;
    private Paint mBackgroundColorTouched;
    private Paint mBackgroundColorSelected;
    private Paint mBackgroundColorHighlighted;

    private Paint mCellValueInvalidPaint;

    public SudokuBoardView(Context context) {
        this(context, null);
    }

    //	public SudokuBoardView(Context context, AttributeSet attrs) {
    //		this(context, attrs, R.attr.sudokuBoardViewStyle);
    //	}

    // TODO: do I need an defStyle?
    public SudokuBoardView(Context context, AttributeSet attrs/*, int defStyle*/) {
        super(context, attrs/*, defStyle*/);

        setFocusable(true);
        setFocusableInTouchMode(true);

        mLinePaint = new Paint();
        mSectorLinePaint = new Paint();
        mCellValuePaint = new Paint();
        mCellValueReadonlyPaint = new Paint();
        mCellValueInvalidPaint = new Paint();
        mCellNotePaint = new Paint();
        mBackgroundColorSecondary = new Paint();
        mBackgroundColorReadOnly = new Paint();
        mBackgroundColorTouched = new Paint();
        mBackgroundColorSelected = new Paint();
        mBackgroundColorHighlighted = new Paint();

        mCellValuePaint.setAntiAlias(true);
        mCellValueReadonlyPaint.setAntiAlias(true);
        mCellValueInvalidPaint.setAntiAlias(true);
        mCellNotePaint.setAntiAlias(true);
        mCellValueInvalidPaint.setColor(Color.RED);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SudokuBoardView/*, defStyle, 0*/);

        setLineColor(a.getColor(R.styleable.SudokuBoardView_lineColor, Color.BLACK));
        setSectorLineColor(a.getColor(R.styleable.SudokuBoardView_sectorLineColor, Color.BLACK));
        setTextColor(a.getColor(R.styleable.SudokuBoardView_textColor, Color.BLACK));
        setTextColorReadOnly(a.getColor(R.styleable.SudokuBoardView_textColorReadOnly, Color.BLACK));
        setTextColorNote(a.getColor(R.styleable.SudokuBoardView_textColorNote, Color.BLACK));
        setBackgroundColor(a.getColor(R.styleable.SudokuBoardView_backgroundColor, Color.WHITE));
        setBackgroundColorSecondary(a.getColor(R.styleable.SudokuBoardView_backgroundColorSecondary, NO_COLOR));
        setBackgroundColorReadOnly(a.getColor(R.styleable.SudokuBoardView_backgroundColorReadOnly, NO_COLOR));
        setBackgroundColorTouched(a.getColor(R.styleable.SudokuBoardView_backgroundColorTouched, Color.rgb(50, 50, 255)));
        setBackgroundColorSelected(a.getColor(R.styleable.SudokuBoardView_backgroundColorSelected, Color.YELLOW));
        setBackgroundColorHighlighted(a.getColor(R.styleable.SudokuBoardView_backgroundColorHighlighted, Color.GREEN));

        a.recycle();
    }

    public int getLineColor() {
        return mLinePaint.getColor();
    }

    public void setLineColor(int color) {
        mLinePaint.setColor(color);
    }

    public int getSectorLineColor() {
        return mSectorLinePaint.getColor();
    }

    public void setSectorLineColor(int color) {
        mSectorLinePaint.setColor(color);
    }

    public int getTextColor() {
        return mCellValuePaint.getColor();
    }

    public void setTextColor(int color) {
        mCellValuePaint.setColor(color);
    }

    public int getTextColorReadOnly() {
        return mCellValueReadonlyPaint.getColor();
    }

    public void setTextColorReadOnly(int color) {
        mCellValueReadonlyPaint.setColor(color);
    }

    public int getTextColorNote() {
        return mCellNotePaint.getColor();
    }

    public void setTextColorNote(int color) {
        mCellNotePaint.setColor(color);
    }

    public int getBackgroundColorSecondary() {
        return mBackgroundColorSecondary.getColor();
    }

    public void setBackgroundColorSecondary(int color) {
        mBackgroundColorSecondary.setColor(color);
    }

    public int getBackgroundColorReadOnly() {
        return mBackgroundColorReadOnly.getColor();
    }

    public void setBackgroundColorReadOnly(int color) {
        mBackgroundColorReadOnly.setColor(color);
    }

    public int getBackgroundColorTouched() {
        return mBackgroundColorTouched.getColor();
    }

    public void setBackgroundColorTouched(int color) {
        mBackgroundColorTouched.setColor(color);
    }

    public int getBackgroundColorSelected() {
        return mBackgroundColorSelected.getColor();
    }

    public void setBackgroundColorSelected(int color) {
        mBackgroundColorSelected.setColor(color);
    }

    public int getBackgroundColorHighlighted() {
        return mBackgroundColorHighlighted.getColor();
    }

    public void setBackgroundColorHighlighted(int color) {
        mBackgroundColorHighlighted.setColor(color);
    }

    public void setGame(SudokuGame game) {
        mGame = game;
        setCells(game.getCells());
    }

    public void setCells(CellCollection cells) {
        mCells = cells;

        if (mCells != null) {
            if (!mReadonly) {
                mSelectedCell = mCells.getCell(0, 0); // first cell will be selected by default
                onCellSelected(mSelectedCell);
            }

            mCells.addOnChangeListener(this::postInvalidate);
        }

        postInvalidate();
    }

    public CellCollection getCells() {
        return mCells;
    }

    public Cell getSelectedCell() {
        return mSelectedCell;
    }

    public void setReadOnly(boolean readonly) {
        mReadonly = readonly;
        postInvalidate();
    }

    public boolean isReadOnly() {
        return mReadonly;
    }

    public void setHighlightWrongVals(boolean highlightWrongVals) {
        mHighlightWrongVals = highlightWrongVals;
        postInvalidate();
    }

    public boolean getHighlightWrongVals() {
        return mHighlightWrongVals;
    }

    public void setHighlightTouchedCell(boolean highlightTouchedCell) {
        mHighlightTouchedCell = highlightTouchedCell;
    }

    public boolean getHighlightTouchedCell() {
        return mHighlightTouchedCell;
    }

    public void setAutoHideTouchedCellHint(boolean autoHideTouchedCellHint) {
        mAutoHideTouchedCellHint = autoHideTouchedCellHint;
    }

    public boolean getAutoHideTouchedCellHint() {
        return mAutoHideTouchedCellHint;
    }

    public void setHighlightSimilarCell(HighlightMode highlightSimilarCell) {
        mHighlightSimilarCells = highlightSimilarCell;
        if (mHighlightSimilarCells != HighlightMode.OVERRIDE) {
            mHighlightCellOverrides = null;
        }
    }

    public void setHighlightCellOverrides(Map<Cell, HighlightOptions> overrides) {
        mHighlightSimilarCells = HighlightMode.OVERRIDE;
        mHighlightCellOverrides = overrides;
    }

    public HighlightMode getHighlightSimilarCells() {
        return mHighlightSimilarCells;
    }

    public void setDimCellsThatAreNotHighlighted(boolean dim) {
        mDimCellsThatAreNotHighlighted = dim;
    }

    public boolean getDimCellsThatAreNotHighlighted() {
        return mDimCellsThatAreNotHighlighted;
    }

    public void setHighlightedValue(int value) {
        mHighlightedValue = value;
    }

    public int getHighlightedValue() {
        return mHighlightedValue;
    }

    /**
     * Registers callback which will be invoked when user taps the cell.
     *
     * @param l
     */
    public void setOnCellTappedListener(OnCellTappedListener l) {
        mOnCellTappedListener = l;
    }

    protected void onCellTapped(Cell cell) {
        if (mOnCellTappedListener != null) {
            mOnCellTappedListener.onCellTapped(cell);
        }
    }

    /**
     * Registers callback which will be invoked when cell is selected. Cell selection
     * can change without user interaction.
     *
     * @param l
     */
    public void setOnCellSelectedListener(OnCellSelectedListener l) {
        mOnCellSelectedListener = l;
    }

    public void hideTouchedCellHint() {
        mTouchedCell = null;
        postInvalidate();
    }


    protected void onCellSelected(Cell cell) {
        if (mOnCellSelectedListener != null) {
            mOnCellSelectedListener.onCellSelected(cell);
        }
    }

    public void invokeOnCellSelected() {
        onCellSelected(mSelectedCell);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


//        Log.d(TAG, "widthMode=" + getMeasureSpecModeString(widthMode));
//        Log.d(TAG, "widthSize=" + widthSize);
//        Log.d(TAG, "heightMode=" + getMeasureSpecModeString(heightMode));
//        Log.d(TAG, "heightSize=" + heightSize);

        int width, height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = DEFAULT_BOARD_SIZE;
            if (widthMode == MeasureSpec.AT_MOST && width > widthSize) {
                width = widthSize;
            }
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = DEFAULT_BOARD_SIZE;
            if (heightMode == MeasureSpec.AT_MOST && height > heightSize) {
                height = heightSize;
            }
        }

        if (widthMode != MeasureSpec.EXACTLY) {
            width = height;
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            height = width;
        }

        if (widthMode == MeasureSpec.AT_MOST && width > widthSize) {
            width = widthSize;
        }
        if (heightMode == MeasureSpec.AT_MOST && height > heightSize) {
            height = heightSize;
        }

        mCellWidth = (width - getPaddingLeft() - getPaddingRight()) / 9.0f;
        mCellHeight = (height - getPaddingTop() - getPaddingBottom()) / 9.0f;

        setMeasuredDimension(width, height);

        float cellTextSize = mCellHeight * 0.75f;
        mCellValuePaint.setTextSize(cellTextSize);
        mCellValueReadonlyPaint.setTextSize(cellTextSize);
        mCellValueInvalidPaint.setTextSize(cellTextSize);
        // compute offsets in each cell to center the rendered number
        mNumberLeft = (int) ((mCellWidth - mCellValuePaint.measureText("9")) / 2);
        mNumberTop = (int) ((mCellHeight - mCellValuePaint.getTextSize()) / 2);

        // add some offset because in some resolutions notes are cut-off in the top
        mNoteTop = mCellHeight / 50.0f;
        mCellNotePaint.setTextSize((mCellHeight - mNoteTop * 2) / 3.0f);

        computeSectorLineWidth(width, height);
    }

    private void computeSectorLineWidth(int widthInPx, int heightInPx) {
        int sizeInPx = widthInPx < heightInPx ? widthInPx : heightInPx;
        float dipScale = getContext().getResources().getDisplayMetrics().density;
        float sizeInDip = sizeInPx / dipScale;

        float sectorLineWidthInDip = 2.0f;

        if (sizeInDip > 150) {
            sectorLineWidthInDip = 3.0f;
        }

        mSectorLineWidth = (int) (sectorLineWidthInDip * dipScale);
    }

    void drawReadOnlyCellBackground(Canvas canvas, Cell cell, float cellLeft, float cellTop, float cellRight, float cellBottom) {
        boolean hasBackgroundColorReadOnly = mBackgroundColorReadOnly.getColor() != NO_COLOR;
        boolean cellIsNotSelected = (mSelectedCell == null || mSelectedCell != cell);
        
        if (!cell.isEditable() && hasBackgroundColorReadOnly && cellIsNotSelected) {
            canvas.drawRect(cellLeft, cellTop, cellRight, cellBottom, mBackgroundColorReadOnly);
        }
    }

    void drawHighlightedCellBackground(Canvas canvas, Cell cell, float cellLeft, float cellTop, float cellRight, float cellBottom) {
        boolean cellIsNotSelected = (mSelectedCell == null || mSelectedCell != cell);
        boolean highlightedValueIsValid = mHighlightedValue != 0;
        boolean shouldHighlightCell = false;
        Paint highlightColor = mBackgroundColorHighlighted;

        switch (mHighlightSimilarCells) {
            default:
            case NONE: {
                shouldHighlightCell = false;
                break;
            }

            case NUMBERS: {
                shouldHighlightCell =
                        cellIsNotSelected &&
                        highlightedValueIsValid &&
                        mHighlightedValue == cell.getValue();
                break;
            }

            case NUMBERS_AND_NOTES: {
                shouldHighlightCell =
                        cellIsNotSelected &&
                        highlightedValueIsValid &&
                        (mHighlightedValue == cell.getValue() ||
                                (cell.getNote().getNotedNumbers().contains(mHighlightedValue)) &&
                                cell.getValue() == 0);
                break;
            }

            case OVERRIDE: {
                if (mHighlightCellOverrides != null && mHighlightCellOverrides.containsKey(cell)) {
                    HighlightOptions.HighlightMode mode = mHighlightCellOverrides.get(cell).getCellHighlightMode();
                    if (mode == HighlightOptions.HighlightMode.HIGHLIGHT) {
                        shouldHighlightCell = true;
                    } else if (mode == HighlightOptions.HighlightMode.SECONDARY_HIGHLIGHT) {
                        shouldHighlightCell = true;
                        highlightColor = mBackgroundColorTouched;
                    }
                }
                break;
            }
        }

        if (shouldHighlightCell) {
            if (highlightColor.getColor() != NO_COLOR) {
                canvas.drawRect(cellLeft, cellTop, cellRight, cellBottom, highlightColor);
            }
        }
    }

    void drawSelectedCellBackground(Canvas canvas, Cell cell, float cellLeft, float cellTop, float cellRight, float cellBottom) {
        if (!mReadonly && cell == mSelectedCell) {
            canvas.drawRect(cellLeft, cellTop, cellRight, cellBottom, mBackgroundColorSelected);
        }
    }

    void drawTouchedCellBackground(Canvas canvas, Cell cell, float cellLeft, float cellTop, float cellRight, float cellBottom) {
        if (mHighlightTouchedCell && mTouchedCell != null) {
            if (cell.getColumnIndex() == mTouchedCell.getColumnIndex() ||
                cell.getRowIndex() == mTouchedCell.getRowIndex()) {
                canvas.drawRect(cellLeft, cellTop, cellRight, cellBottom, mBackgroundColorTouched);
            }
        }
    }

    void drawCellNumbers(Canvas canvas, Cell cell, float cellLeft, float cellTop) {
        int value = cell.getValue();
        if (value != 0) {
            Paint cellValuePaint = cell.isEditable() ? mCellValuePaint : mCellValueReadonlyPaint;

            if (mHighlightWrongVals && !cell.isValid()) {
                cellValuePaint = mCellValueInvalidPaint;
            }

            canvas.drawText(Integer.toString(value),
                    cellLeft + mNumberLeft,
                    cellTop + mNumberTop - mCellValuePaint.ascent(),
                    cellValuePaint);
        }
    }

    void drawCellNotes(Canvas canvas, Cell cell, float cellLeft, float cellTop) {
        int value = cell.getValue();
        if (value == 0 && !cell.getNote().isEmpty()) {
            float noteWidth = mCellWidth / 3f;
            Collection<Integer> numbers = cell.getNote().getNotedNumbers();
            for (Integer number : numbers) {
                int n = number - 1;
                int c = n % 3;
                int r = n / 3;
                canvas.drawText(Integer.toString(number), cellLeft + c * noteWidth + 2, cellTop + mNoteTop - mCellNotePaint.ascent() + r * noteWidth - 1, mCellNotePaint);
            }
        }
    }

    void drawDimmedCellForeground(Canvas canvas, Cell cell, float cellLeft, float cellTop, float cellRight, float cellBottom) {
        if (mHighlightSimilarCells == HighlightMode.OVERRIDE &&
                mHighlightCellOverrides != null &&
                (!mHighlightCellOverrides.containsKey(cell) ||
                    mHighlightCellOverrides.get(cell).getCellHighlightMode() == HighlightOptions.HighlightMode.DIM)) {
            boolean isLightTheme = ThemeUtils.isLightTheme(ThemeUtils.getCurrentThemeFromPreferences(getContext()));
            Paint dimmingColor = new Paint();
            if (isLightTheme) {
                dimmingColor.setColor(Color.WHITE);
            } else {
                dimmingColor.setColor(Color.BLACK);
            }
            dimmingColor.setAlpha(0xC0);
            canvas.drawRect(cellLeft, cellTop, cellRight, cellBottom, dimmingColor);
        }
    }

    void drawEmphasizedCellBackground(Canvas canvas, Cell cell, float cellLeft, float cellTop, float cellRight, float cellBottom) {
        if (mHighlightSimilarCells == HighlightMode.OVERRIDE &&
                mHighlightCellOverrides != null &&
                mHighlightCellOverrides.containsKey(cell) &&
                mHighlightCellOverrides.get(cell).getCellHighlightMode() == HighlightOptions.HighlightMode.EMPHASIZE) {
            boolean isLightTheme = ThemeUtils.isLightTheme(ThemeUtils.getCurrentThemeFromPreferences(getContext()));
            Paint emphasisColor = new Paint();
            if (isLightTheme) {
                emphasisColor.setColor(0xFFC0C0C0);
            } else {
                emphasisColor.setColor(0xFF404040);
            }
            canvas.drawRect(cellLeft, cellTop, cellRight, cellBottom, emphasisColor);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // some notes:
        // Drawable has its own draw() method that takes your Canvas as an argument

        // TODO: I don't get this, why do I need to substract padding only from one side?
        int width = getWidth() - getPaddingRight();
        int height = getHeight() - getPaddingBottom();

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        // draw secondary background
        if (mBackgroundColorSecondary.getColor() != NO_COLOR) {
            canvas.drawRect(3 * mCellWidth, 0, 6 * mCellWidth, 3 * mCellWidth, mBackgroundColorSecondary);
            canvas.drawRect(0, 3 * mCellWidth, 3 * mCellWidth, 6 * mCellWidth, mBackgroundColorSecondary);
            canvas.drawRect(6 * mCellWidth, 3 * mCellWidth, 9 * mCellWidth, 6 * mCellWidth, mBackgroundColorSecondary);
            canvas.drawRect(3 * mCellWidth, 6 * mCellWidth, 6 * mCellWidth, 9 * mCellWidth, mBackgroundColorSecondary);
        }

        // draw cells
        if (mCells != null) {
            float cellLeft, cellTop, cellRight, cellBottom;

            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    Cell cell = mCells.getCell(row, col);

                    cellLeft = Math.round((col * mCellWidth) + paddingLeft);
                    cellTop = Math.round((row * mCellHeight) + paddingTop);
                    cellRight = cellLeft + mCellWidth;
                    cellBottom = cellTop + mCellHeight;

                    drawReadOnlyCellBackground(canvas, cell, cellLeft, cellTop, cellRight, cellBottom);
                    drawHighlightedCellBackground(canvas, cell, cellLeft, cellTop, cellRight, cellBottom);
                    drawSelectedCellBackground(canvas, cell, cellLeft, cellTop, cellRight, cellBottom);
                    drawTouchedCellBackground(canvas, cell, cellLeft, cellTop, cellRight, cellBottom);
                    drawEmphasizedCellBackground(canvas, cell, cellLeft, cellTop, cellRight, cellBottom);

                    drawCellNumbers(canvas, cell, cellLeft, cellTop);
                    drawCellNotes(canvas, cell, cellLeft, cellTop);

                    drawDimmedCellForeground(canvas, cell, cellLeft, cellTop, cellRight, cellBottom);
                }
            }
        }

        // draw vertical lines
        for (int c = 0; c <= 9; c++) {
            float x = (c * mCellWidth) + paddingLeft;
            canvas.drawLine(x, paddingTop, x, height, mLinePaint);
        }

        // draw horizontal lines
        for (int r = 0; r <= 9; r++) {
            float y = r * mCellHeight + paddingTop;
            canvas.drawLine(paddingLeft, y, width, y, mLinePaint);
        }

        int sectorLineWidth1 = mSectorLineWidth / 2;
        int sectorLineWidth2 = sectorLineWidth1 + (mSectorLineWidth % 2);

        // draw sector (thick) lines
        for (int c = 0; c <= 9; c = c + 3) {
            float x = (c * mCellWidth) + paddingLeft;
            canvas.drawRect(x - sectorLineWidth1, paddingTop, x + sectorLineWidth2, height, mSectorLinePaint);
        }

        for (int r = 0; r <= 9; r = r + 3) {
            float y = r * mCellHeight + paddingTop;
            canvas.drawRect(paddingLeft, y - sectorLineWidth1, width, y + sectorLineWidth2, mSectorLinePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!mReadonly) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    mTouchedCell = getCellAtPoint(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    mSelectedCell = getCellAtPoint(x, y);
                    invalidate(); // selected cell has changed, update board as soon as you can

                    if (mSelectedCell != null) {
                        onCellTapped(mSelectedCell);
                        onCellSelected(mSelectedCell);
                    }

                    if (mAutoHideTouchedCellHint) {
                        mTouchedCell = null;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mTouchedCell = null;
                    break;
            }
            postInvalidate();
        }

        return !mReadonly;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!mReadonly) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    return moveCellSelection(0, -1);
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    return moveCellSelection(1, 0);
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    return moveCellSelection(0, 1);
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    return moveCellSelection(-1, 0);
                case KeyEvent.KEYCODE_0:
                case KeyEvent.KEYCODE_SPACE:
                case KeyEvent.KEYCODE_DEL:
                    // clear value in selected cell
                    // TODO: I'm not really sure that this is thread-safe
                    if (mSelectedCell != null) {
                        if (event.isShiftPressed() || event.isAltPressed()) {
                            setCellNote(mSelectedCell, CellNote.EMPTY);
                        } else {
                            setCellValue(mSelectedCell, 0);
                            moveCellSelectionRight();
                        }
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (mSelectedCell != null) {
                        onCellTapped(mSelectedCell);
                    }
                    return true;
            }

            if (keyCode >= KeyEvent.KEYCODE_1 && keyCode <= KeyEvent.KEYCODE_9 && mSelectedCell != null) {
                int selNumber = keyCode - KeyEvent.KEYCODE_0;
                Cell cell = mSelectedCell;

                if (event.isShiftPressed() || event.isAltPressed()) {
                    // add or remove number in cell's note
                    setCellNote(cell, cell.getNote().toggleNumber(selNumber));
                } else {
                    // enter number in cell
                    setCellValue(cell, selNumber);
                    moveCellSelectionRight();
                }
                return true;
            }
        }


        return false;
    }


    /**
     * Moves selected cell by one cell to the right. If edge is reached, selection
     * skips on beginning of another line.
     */
    public void moveCellSelectionRight() {
        if (!moveCellSelection(1, 0)) {
            int selRow = mSelectedCell.getRowIndex();
            selRow++;
            if (!moveCellSelectionTo(selRow, 0)) {
                moveCellSelectionTo(0, 0);
            }
        }
        postInvalidate();
    }

    private void setCellValue(Cell cell, int value) {
        if (cell.isEditable()) {
            if (mGame != null) {
                mGame.setCellValue(cell, value);
            } else {
                cell.setValue(value);
            }
        }
    }

    private void setCellNote(Cell cell, CellNote note) {
        if (cell.isEditable()) {
            if (mGame != null) {
                mGame.setCellNote(cell, note);
            } else {
                cell.setNote(note);
            }
        }
    }


    /**
     * Moves selected by vx cells right and vy cells down. vx and vy can be negative. Returns true,
     * if new cell is selected.
     *
     * @param vx Horizontal offset, by which move selected cell.
     * @param vy Vertical offset, by which move selected cell.
     */
    private boolean moveCellSelection(int vx, int vy) {
        int newRow = 0;
        int newCol = 0;

        if (mSelectedCell != null) {
            newRow = mSelectedCell.getRowIndex() + vy;
            newCol = mSelectedCell.getColumnIndex() + vx;
        }

        return moveCellSelectionTo(newRow, newCol);
    }


    /**
     * Moves selection to the cell given by row and column index.
     *
     * @param row Row index of cell which should be selected.
     * @param col Columnd index of cell which should be selected.
     * @return True, if cell was successfuly selected.
     */
    public boolean moveCellSelectionTo(int row, int col) {
        if (col >= 0 && col < CellCollection.SUDOKU_SIZE
                && row >= 0 && row < CellCollection.SUDOKU_SIZE) {
            mSelectedCell = mCells.getCell(row, col);
            onCellSelected(mSelectedCell);

            postInvalidate();
            return true;
        }

        return false;
    }

    public void clearCellSelection() {
        mSelectedCell = null;
        onCellSelected(mSelectedCell);
        postInvalidate();
    }

    /**
     * Returns cell at given screen coordinates. Returns null if no cell is found.
     *
     * @param x
     * @param y
     * @return
     */
    private Cell getCellAtPoint(int x, int y) {
        // take into account padding
        int lx = x - getPaddingLeft();
        int ly = y - getPaddingTop();

        int row = (int) (ly / mCellHeight);
        int col = (int) (lx / mCellWidth);

        if (col >= 0 && col < CellCollection.SUDOKU_SIZE
                && row >= 0 && row < CellCollection.SUDOKU_SIZE) {
            return mCells.getCell(row, col);
        } else {
            return null;
        }
    }

    /**
     * Occurs when user tap the cell.
     *
     * @author romario
     */
    public interface OnCellTappedListener {
        void onCellTapped(Cell cell);
    }

    /**
     * Occurs when user selects the cell.
     *
     * @author romario
     */
    public interface OnCellSelectedListener {
        void onCellSelected(Cell cell);
    }

//	private String getMeasureSpecModeString(int mode) {
//		String modeString = null;
//		switch (mode) {
//		case MeasureSpec.AT_MOST:
//			modeString = "MeasureSpec.AT_MOST";
//			break;
//		case MeasureSpec.EXACTLY:
//			modeString = "MeasureSpec.EXACTLY";
//			break;
//		case MeasureSpec.UNSPECIFIED:
//			modeString = "MeasureSpec.UNSPECIFIED";
//			break;
//		}
//		
//		if (modeString == null)
//			modeString = new Integer(mode).toString();
//		
//		return modeString;
//	}

}
