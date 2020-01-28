package org.moire.opensudoku.game.command;

import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.CellGroup;
import org.moire.opensudoku.game.CellNote;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FillInNotesCommand extends AbstractCellCommand {

    private List<NoteEntry> mOldNotes = new ArrayList<>();

    public FillInNotesCommand() {
    }

    @Override
    public void serialize(StringBuilder data) {
        super.serialize(data);

        data.append(mOldNotes.size()).append("|");

        for (NoteEntry ne : mOldNotes) {
            data.append(ne.rowIndex).append("|");
            data.append(ne.colIndex).append("|");
            ne.note.serialize(data);
        }
    }

    @Override
    protected void _deserialize(StringTokenizer data) {
        super._deserialize(data);

        int notesSize = Integer.parseInt(data.nextToken());
        for (int i = 0; i < notesSize; i++) {
            int row = Integer.parseInt(data.nextToken());
            int col = Integer.parseInt(data.nextToken());

            mOldNotes.add(new NoteEntry(row, col, CellNote.deserialize(data.nextToken())));
        }
    }

    protected void saveOldNotes() {
        CellCollection cells = getCells();

        mOldNotes.clear();
        for (int r = 0; r < CellCollection.SUDOKU_SIZE; r++) {
            for (int c = 0; c < CellCollection.SUDOKU_SIZE; c++) {
                mOldNotes.add(new NoteEntry(r, c, cells.getCell(r, c).getNote()));
            }
        }
    }

    @Override
    void execute() {
        saveOldNotes();
        getCells().fillInNotes();
    }

    @Override
    void undo() {
        CellCollection cells = getCells();

        for (NoteEntry ne : mOldNotes) {
            cells.getCell(ne.rowIndex, ne.colIndex).setNote(ne.note);
        }
    }

    private static class NoteEntry {
        public int rowIndex;
        public int colIndex;
        public CellNote note;

        public NoteEntry(int rowIndex, int colIndex, CellNote note) {
            this.rowIndex = rowIndex;
            this.colIndex = colIndex;
            this.note = note;
        }

    }
}
