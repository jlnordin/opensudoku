package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.command.CommandStack;

import java.util.ArrayList;

public class StepByStepSolver {

    static boolean isSolved(CellCollection cells) {
        return cells.validate() && cells.isCompleted();
    }

    static public AbstractTechnique getNextTechnique(Context context, CellCollection cells) {
        return BruteForceTechnique.create(context, cells, SudokuSolver.solve(cells));
    }

    static public AbstractTechnique getNextTechnique(Context context, CellCollection cells, ArrayList<int[]> solution) {
        if (isSolved(cells)) {
            return new PuzzleIsSolvedTechnique(context);
        } else {
            return BruteForceTechnique.create(context, cells, solution);
        }
    }
}
