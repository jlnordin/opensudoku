package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(MockitoExtension.class)
class PuzzleIsUnsolvableTechniqueTest {

    @Mock
    Context mContext;

    @Test
    void create() {
        assertNotNull(PuzzleIsUnsolvableTechnique.create(mContext, TechniqueTestHelpers.createUnsolvableGame()));
    }

    @Test
    void create_returnNullIfSolveable() {
        assertNull(PuzzleIsUnsolvableTechnique.create(mContext, TechniqueTestHelpers.createGameInProgress()));
    }
}