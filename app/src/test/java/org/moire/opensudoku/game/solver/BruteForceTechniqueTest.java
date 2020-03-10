package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.moire.opensudoku.game.SudokuGame;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class BruteForceTechniqueTest {

    @Mock
    Context mContext;

    @Test
    void create() {
        assertNotNull(BruteForceTechnique.create(mContext, TechniqueTestHelpers.createGameInProgress()));
    }

    @Test
    void create_returnNullIfNotSolvable() {
        assertNull(BruteForceTechnique.create(mContext, TechniqueTestHelpers.createUnsolvableGame()));
    }

    @Test
    void create_returnNullIfAlreadySolved() {
        assertNull(BruteForceTechnique.create(mContext, TechniqueTestHelpers.createSolvedGame()));
    }

    @Test
    void applyTechnique_untilSolved() {
        SudokuGame game = TechniqueTestHelpers.createGameInProgress();
        BruteForceTechnique technique;

        for (int i = 0; i < 81; i++)
        {
            technique = BruteForceTechnique.create(mContext, game);
            if (technique == null) {
                break;
            }

            technique.applyTechnique(game);
        }

        game.validate();
        assertTrue(game.isCompleted());
    }
}