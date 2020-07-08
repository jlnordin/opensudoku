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
        // incomplete notes for candidate values. In order to do deductions on candidates we need to
        // have the complete set of notes as a starting point, but since the purpose of advanced
        // solving techniques os precisely to remove notes, we must be careful in how we determine
        // what "incomplete notes" means. If we are too aggressive here, then we will potentially
        // recommend filling in notes after just eliminating some notes with a different technique.
        //
        // The solution is to define "incomplete notes" as any board where there is at least one
        // cell that has no notes nor a value.
        for (CellGroup row : game.getCells().getRows()) {
            for (Cell cell : row.getCells()) {
                if (cell.getValue() == 0 && cell.getNote().isEmpty()) {
                    return new FillInNotesTechnique(context, game);
                }
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
