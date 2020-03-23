package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(MockitoExtension.class)
class HiddenSingleTechniqueTest {

    @Mock
    Context mContext;

    @Test
    void create() {
        HiddenSingleTechnique technique = HiddenSingleTechnique.create(mContext, TechniqueTestHelpers.createGameWithHiddenSingleInColumn());
        assertNotNull(technique);
        assertEquals(TechniqueHelpers.GroupType.Column, technique.mGroup);

        technique = HiddenSingleTechnique.create(mContext, TechniqueTestHelpers.createGameWithHiddenSingleInBox());
        assertNotNull(technique);
        assertEquals(TechniqueHelpers.GroupType.Box, technique.mGroup);
    }

    @Test
    void create_returnNullIfNoHiddenSingle() {
        assertNull(HiddenSingleTechnique.create(mContext, TechniqueTestHelpers.createSolvedGame()));
    }
}