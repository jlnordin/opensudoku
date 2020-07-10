package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellGroup;
import org.moire.opensudoku.game.CellNote;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.FillInNotesCommand;
import org.moire.opensudoku.gui.HighlightOptions;

import java.util.HashMap;

public class FillInNotesTechnique extends AbstractTechnique {

    static public FillInNotesTechnique create(Context context, SudokuGame game) {

        // We will only recommend filling in notes if we determine that the game board currently has
        // an invalid set of notes. In order to do deductions on candidates we need to
        // have a valid set of notes as a starting point, and most advanced techniques assume the
        // provided notes are valid. For this purpose we define "valid notes" as any game board
        // where the following conditions are met:
        //   1. Every cell that does not have a value has at least 1 note.
        //   2. Every cell that has a note has the solved value as one of the candidates.

        for (int[] rowColVal : game.getSolutionValues()) {
            int row = rowColVal[0];
            int col = rowColVal[1];
            int val = rowColVal[2];
            Cell cell = game.getCells().getCell(row, col);

            if (cell.getValue() == 0 && !cell.getNote().hasNumber(val)) {
                return new FillInNotesTechnique(context, game);
            }
        }

        return null;
    }

    FillInNotesTechnique(Context context, SudokuGame game) {
        super(context);

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_fill_in_notes_step_1),
                (board) -> {}));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_fill_in_notes_step_2, mContext.getString(R.string.apply_hint)),
                (board) -> {}));
    }

    @Override
    public void applyTechnique(SudokuGame game) {
        game.getCommandStack().execute(new FillInNotesCommand());
    }

    @Override
    public String getName() { return mContext.getString(R.string.technique_fill_in_notes_title); }
}
