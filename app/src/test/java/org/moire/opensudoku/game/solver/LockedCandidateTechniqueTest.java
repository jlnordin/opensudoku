package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(MockitoExtension.class)
class LockedCandidateTechniqueTest {

    @Mock
    Context mContext;

    @Test
    void create() {
        assertNotNull(LockedCandidateTechnique.create(mContext, TechniqueTestHelpers.createGameWithLockedCandidates()));
    }

    @Test
    void create_returnNullIfNoNakedSingle() {
        assertNull(LockedCandidateTechnique.create(mContext, TechniqueTestHelpers.createSolvedGame()));
    }
}