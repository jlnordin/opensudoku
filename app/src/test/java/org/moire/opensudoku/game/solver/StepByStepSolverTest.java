package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.SudokuGame;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class StepByStepSolverTest {

    @Mock
    Context mContext;

    @Test
    void getNextTechnique_puzzleAlreadySolved() {
        AbstractTechnique technique = StepByStepSolver.getNextTechnique(mContext, TechniqueTestHelpers.createSolvedGame());
        assertSame(PuzzleIsSolvedTechnique.class, technique.getClass());
    }

    @Test
    void getNextTechnique_mistakeInPuzzle() {
        AbstractTechnique technique = StepByStepSolver.getNextTechnique(mContext, TechniqueTestHelpers.createGameWithMistake());
        assertSame(CheckForMistakeTechnique.class, technique.getClass());
    }

    @Test
    void getNextTechnique_puzzleIsUnsolvable() {
        AbstractTechnique technique = StepByStepSolver.getNextTechnique(mContext, TechniqueTestHelpers.createUnsolvableGame());
        assertSame(PuzzleIsUnsolvableTechnique.class, technique.getClass());
    }
}