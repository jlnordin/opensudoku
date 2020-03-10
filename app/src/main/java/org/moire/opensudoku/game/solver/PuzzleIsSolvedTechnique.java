package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.SudokuGame;

public class PuzzleIsSolvedTechnique extends AbstractTechnique {

    static public PuzzleIsSolvedTechnique create(Context context, SudokuGame game) {
        game.validate();
        if (game.isCompleted()) {
            return new PuzzleIsSolvedTechnique(context);
        } else {
            return null;
        }
    }

    PuzzleIsSolvedTechnique(Context context) {
        super(context);
        mExplanationSteps.add(new Explanation("", (view) -> {}));
    }

    @Override
    public void applyTechnique(SudokuGame game) {
    }

    @Override
    public String getName() {
        return mContext.getString(R.string.solved);
    }
}
