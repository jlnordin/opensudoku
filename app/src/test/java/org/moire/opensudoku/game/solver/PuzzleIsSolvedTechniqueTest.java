package org.moire.opensudoku.game.solver;

import android.content.Context;
import android.os.SystemClock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.SudokuGame;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PuzzleIsSolvedTechniqueTest {

    @Mock
    Context mContext;

    @Test
    void create() {
        SudokuGame game = new SudokuGame();
        game.setCells(CellCollection.createDebugGame());

        AbstractTechnique technique;
        technique = StepByStepSolver.getNextTechnique(mContext, game);
        assertFalse(technique instanceof PuzzleIsSolvedTechnique);

        game.solve();

        technique = StepByStepSolver.getNextTechnique(mContext, game);
        assertTrue(technique instanceof PuzzleIsSolvedTechnique);
    }
}