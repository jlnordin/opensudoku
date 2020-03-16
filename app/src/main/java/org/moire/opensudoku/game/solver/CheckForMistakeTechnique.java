package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.AbstractCellCommand;
import org.moire.opensudoku.gui.HighlightOptions;
import org.moire.opensudoku.gui.SudokuBoardView;

import java.util.HashMap;

public class CheckForMistakeTechnique extends AbstractTechnique {

    static public CheckForMistakeTechnique create(Context context, SudokuGame game) {
        if (game.getCells().hasMistake(game.getSolutionValues())) {
            return new CheckForMistakeTechnique(context, game);
        } else {
            return null;
        }
    }

    HashMap<Cell, HighlightOptions> mHighlightedMistakes;

    CheckForMistakeTechnique(Context context, SudokuGame game) {
        super(context);

        mHighlightedMistakes = new HashMap<Cell, HighlightOptions>();
        for (int[] rowColVal : game.getSolutionValues()) {
            int row = rowColVal[0];
            int col = rowColVal[1];
            int val = rowColVal[2];
            Cell cell = game.getCells().getCell(row, col);

            if (cell.getValue() != val && cell.getValue() != 0) {
                mHighlightedMistakes.put(cell, new HighlightOptions());
            }
        }

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_check_for_mistakes_step_1),
                (board) -> {}));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_check_for_mistakes_step_2),
                (board) ->
                {
                    mHighlightOverrides.putAll(mHighlightedMistakes);
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_check_for_mistakes_step_3, mContext.getString(R.string.apply_hint), mContext.getString(R.string.close)),
                (board) ->
                {
                    mHighlightOverrides.putAll(mHighlightedMistakes);
                }));
    }

    @Override
    public void applyTechnique(SudokuGame game) {
        game.getCommandStack().undoToSolvableState();
    }

    @Override
    public String getName() { return mContext.getString(R.string.technique_check_for_mistakes_title); }
}
