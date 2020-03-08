package org.moire.opensudoku.game.solver;

import android.test.mock.MockContext;

import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.SudokuGame;

import static org.junit.jupiter.api.Assertions.*;

class PuzzleIsSolvedTechniqueTest {

    @org.junit.jupiter.api.Test
    void create() {
        SudokuGame game = new SudokuGame();
        game.setCells(CellCollection.createDebugGame());

        AbstractTechnique technique;
        technique = StepByStepSolver.getNextTechnique(new MockContext(), game);
        assertFalse(technique instanceof PuzzleIsSolvedTechnique);

        game.solve();

        technique = StepByStepSolver.getNextTechnique(new MockContext(), game);
        assertTrue(technique instanceof PuzzleIsSolvedTechnique);
    }
}