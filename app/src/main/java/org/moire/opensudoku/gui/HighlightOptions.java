package org.moire.opensudoku.gui;

import org.moire.opensudoku.db.SudokuDatabase;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.SudokuGame;

public class HighlightOptions {
    public enum HighlightMode {
        DIM,
        EMPHASIZE,
        HIGHLIGHT,
        SECONDARY_HIGHLIGHT,
        NONE
    }
    HighlightMode mCellHighlightMode;
    HighlightMode[] mNoteHighlightMode;

    public HighlightOptions() {
        mCellHighlightMode = HighlightMode.HIGHLIGHT;
        mNoteHighlightMode = new HighlightMode[CellCollection.SUDOKU_SIZE];
        for (int n = 0; n < mNoteHighlightMode.length; n++) {
            mNoteHighlightMode[n] = HighlightMode.NONE;
        }
    }

    public HighlightOptions(HighlightMode mode) {
        mCellHighlightMode = mode;
        mNoteHighlightMode = new HighlightMode[CellCollection.SUDOKU_SIZE];
        for (int n = 0; n < mNoteHighlightMode.length; n++) {
            mNoteHighlightMode[n] = HighlightMode.NONE;
        }
    }

    public void setCellHighlightMode(HighlightMode mode) {
        mCellHighlightMode = mode;
    }

    public HighlightMode getCellHighlightMode() {
        return mCellHighlightMode;
    }

    public void setNoteHighlightMode(int note, HighlightMode mode) {
        mNoteHighlightMode[note] = mode;
    }

    public HighlightMode getNoteHighlightMode(int note) {
        return mNoteHighlightMode[note];
    }
}
