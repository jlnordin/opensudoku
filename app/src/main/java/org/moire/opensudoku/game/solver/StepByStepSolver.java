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
                // Techniques to check for mistakes and edge cases.
                PuzzleIsSolvedTechnique::create,
                PuzzleIsUnsolvableTechnique::create,
                CheckForMistakeTechnique::create,
                CheckForNotationMistakeTechnique::create,

                // Techniques that don't rely on notes and solve to a concrete value.
                FullHouseTechnique::create,
                HiddenSingleTechnique::create,
                NakedSingleTechnique::create,

                // This technique ensures that the notes are valid for the next set of techniques.
                FillInNotesTechnique::create,

                // Techniques that rely on notes and solve to a concrete value.
                HiddenSingleFromNotesTechnique::create,

                // Techniques that eliminate notes (candidate values).
                //...

                // Last resort technique.
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
