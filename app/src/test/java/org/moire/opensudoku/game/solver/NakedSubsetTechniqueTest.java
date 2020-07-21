package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(MockitoExtension.class)
class NakedSubsetTechniqueTest {

    @Mock
    Context mContext;

    @Test
    void createPair() {
        assertNotNull(NakedSubsetTechnique.createPair(mContext, TechniqueTestHelpers.createGameWithNakedPair()));
    }

    @Test
    void createTriple() {
        assertNotNull(NakedSubsetTechnique.createTriple(mContext, TechniqueTestHelpers.createGameWithNakedTriple()));
    }

    @Test
    void createQuadruple() {
        assertNotNull(NakedSubsetTechnique.createQuadruple(mContext, TechniqueTestHelpers.createGameWithNakedQuadruple()));
    }

    @Test
    void create_returnNullIfNoNakedSingle() {
        assertNull(NakedSubsetTechnique.createPair(mContext, TechniqueTestHelpers.createSolvedGame()));
        assertNull(NakedSubsetTechnique.createTriple(mContext, TechniqueTestHelpers.createSolvedGame()));
        assertNull(NakedSubsetTechnique.createQuadruple(mContext, TechniqueTestHelpers.createSolvedGame()));
    }
}