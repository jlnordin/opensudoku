package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.CommandStack;

import java.util.ArrayList;

public class StepByStepSolver {

    static public AbstractTechnique getNextTechnique(Context context, SudokuGame game) {

        // Create an ordered list of all of the techniques we will apply to determine what the next
        // logical step is to solve the given sudoku game. The order is important. In particular,
        // the PuzzleIsUnsolvableTechnique, which checks if the sudoku is not solvable, must go
        // first and the BruteForceTechnique, which will always provide an answer to a non-solved
        // but solvable puzzle, should go last.
        AbstractTechnique.TechniqueFactory[] techniques = {
                PuzzleIsSolvedTechnique::create,
                PuzzleIsUnsolvableTechnique::create,
                CheckForMistakeTechnique::create,

                FullHouseTechnique::create,
                HiddenSingleTechnique::create,

                BruteForceTechnique::create
        };

        AbstractTechnique nextTechnique;
        for (AbstractTechnique.TechniqueFactory techniqueFactory : techniques) {
            nextTechnique = techniqueFactory.create(context, game);
            if (nextTechnique != null) {
                return nextTechnique;
            }
        }

        return null;
    }
}
