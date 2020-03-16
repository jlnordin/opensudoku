package org.moire.opensudoku.gui;

public class HighlightOptions {
    public enum HighlightMode {
        DIM,
        NORMAL,
        HIGHLIGHT,
        SECONDARY_HIGHLIGHT
    }
    HighlightMode mCellHighlightMode;
    HighlightMode[] mNoteHighlightMode;

    public HighlightOptions() {
        mCellHighlightMode = HighlightMode.HIGHLIGHT;
        mNoteHighlightMode = new HighlightMode[9];
        for (HighlightMode mode : mNoteHighlightMode)
        {
             mode = HighlightMode.DIM;
        }
    }

    public HighlightOptions(HighlightMode mode) {
        mCellHighlightMode = mode;
        mNoteHighlightMode = new HighlightMode[9];
        for (HighlightMode noteMode : mNoteHighlightMode)
        {
            noteMode = HighlightMode.DIM;
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

    public HighlightMode getCellHighlightMode(int note) {
        return mNoteHighlightMode[note];
    }
}
