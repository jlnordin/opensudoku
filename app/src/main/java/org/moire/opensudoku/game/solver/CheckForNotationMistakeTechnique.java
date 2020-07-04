package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellNote;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.EditCellNoteCommand;
import org.moire.opensudoku.gui.HighlightOptions;

import java.util.HashMap;
import java.util.List;

public class CheckForNotationMistakeTechnique extends AbstractTechnique {

    static public CheckForNotationMistakeTechnique create(Context context, SudokuGame game) {
        List<int[]> notationMistakes = game.getCells().getSimpleNotationMistakes();
        if (!notationMistakes.isEmpty()) {
            return new CheckForNotationMistakeTechnique(context, game, notationMistakes);
        } else {
            return null;
        }
    }

    List<int[]> mNotationMistakes;
    HashMap<Cell, HighlightOptions> mHighlightedMistakes;

    CheckForNotationMistakeTechnique(Context context, SudokuGame game, List<int[]> notationMistakes) {
        super(context);

        mNotationMistakes = notationMistakes;

        mHighlightedMistakes = new HashMap<Cell, HighlightOptions>();
        for (int[] rowColVal : mNotationMistakes) {
            int row = rowColVal[0];
            int col = rowColVal[1];
            int noteValue = rowColVal[2];
            Cell cell = game.getCells().getCell(row, col);

            if (!mHighlightedMistakes.containsKey(cell)) {
                mHighlightedMistakes.put(cell, new HighlightOptions(HighlightOptions.HighlightMode.NONE));
            }
            mHighlightedMistakes.get(cell).setNoteHighlightMode(noteValue - 1, HighlightOptions.HighlightMode.HIGHLIGHT);
        }

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_check_for_notation_mistakes_step_1),
                (board) -> {}));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_check_for_notation_mistakes_step_2),
                (board) ->
                {
                    mHighlightOverrides.putAll(mHighlightedMistakes);
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_check_for_notation_mistakes_step_3, mContext.getString(R.string.apply_hint)),
                (board) ->
                {
                    mHighlightOverrides.putAll(mHighlightedMistakes);
                }));
    }

    @Override
    public void applyTechnique(SudokuGame game) {
        for (int[] rowColVal : mNotationMistakes) {
            int row = rowColVal[0];
            int col = rowColVal[1];
            int noteValue = rowColVal[2];
            Cell cell = game.getCells().getCell(row, col);
            CellNote newNote = cell.getNote().removeNumber(noteValue);

            game.getCommandStack().execute(new EditCellNoteCommand(game.getCells().getCell(row, col), newNote));
        }
    }

    @Override
    public String getName() { return mContext.getString(R.string.technique_check_for_notation_mistakes_title); }
}
