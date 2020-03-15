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
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class FullHouseTechniqueTest {

    @Mock
    Context mContext;

    @Test
    void create() {
        assertNotNull(FullHouseTechnique.create(mContext, TechniqueTestHelpers.createGameWithFullHouses()));
    }

    @Test
    void create_returnNullIfNoFullHouse() {
        assertNull(FullHouseTechnique.create(mContext, TechniqueTestHelpers.createGameInProgress()));
    }

    @Test
    void applyTechnique_4FullHousesInSuccession() {
        SudokuGame game = TechniqueTestHelpers.createGameWithFullHouses();
        final TechniqueHelpers.GroupType[] expectedFullHouseTypes = {
                TechniqueHelpers.GroupType.Box,
                TechniqueHelpers.GroupType.Box,
                TechniqueHelpers.GroupType.Row,
                TechniqueHelpers.GroupType.Column,
        };

        FullHouseTechnique technique;
        for (TechniqueHelpers.GroupType type : expectedFullHouseTypes)
        {
            technique = FullHouseTechnique.create(mContext, game);
            assertNotNull(technique);
            assertEquals(type, technique.mType);
            technique.applyTechnique(game);
        }

        technique = FullHouseTechnique.create(mContext, game);
        assertNull(technique);
    }
}