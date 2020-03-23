package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.moire.opensudoku.game.SudokuGame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(MockitoExtension.class)
class NakedSingleTechniqueTest {

    @Mock
    Context mContext;

    @Test
    void create() {
        assertNotNull(NakedSingleTechnique.create(mContext, TechniqueTestHelpers.createGameWithNakedSingle()));
    }

    @Test
    void create_returnNullIfNoNakedSingle() {
        assertNull(NakedSingleTechnique.create(mContext, TechniqueTestHelpers.createSolvedGame()));
    }
}