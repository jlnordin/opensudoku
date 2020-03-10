package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.SudokuGame;

public class PuzzleIsUnsolvableTechnique extends AbstractTechnique {

    static public PuzzleIsUnsolvableTechnique create(Context context, SudokuGame game) {
        if (!game.isSolvable()) {
            return new PuzzleIsUnsolvableTechnique(context);
        } else {
            return null;
        }
    }

    PuzzleIsUnsolvableTechnique(Context context) {
        super(context);
        mExplanationSteps.add(new Explanation("", (view) -> {}));
    }

    @Override
    public void applyTechnique(SudokuGame game) {
    }

    @Override
    public String getName() {
        return mContext.getString(R.string.puzzle_not_solved);
    }
}
