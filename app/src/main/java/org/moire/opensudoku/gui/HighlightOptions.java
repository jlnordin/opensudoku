package org.moire.opensudoku.gui;

public class HighlightOptions {
    boolean mHighlightCell;
    boolean[] mHighlightNote;

    public HighlightOptions() {
        mHighlightCell = true;
        mHighlightNote = new boolean[9];
    }

    public boolean isCellHighlighted() {
        return mHighlightCell;
    }

    public void highlightNote(int note) {
        mHighlightNote[note] = true;
    }

    public boolean isNoteHighlighted(int note) {
        return mHighlightNote[note];
    }
}
