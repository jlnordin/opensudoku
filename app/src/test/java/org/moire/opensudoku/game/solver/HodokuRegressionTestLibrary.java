package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.moire.opensudoku.game.SudokuGame;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HodokuRegressionTestLibrary {

    HodokuRegressionTestLibrary() throws IOException {
        mHodokuTests = HodokuRegressionTestLibraryHelpers.getAllHodokuRegressionTests();
    }

    @Mock
    Context mContext;

    HodokuRegressionTestInfo[] mHodokuTests = null;

    static void assertPlacement(HodokuRegressionTestInfo testInfo, int row, int column, int value) {
        // Placements are [row, column, value] triples.
        assertEquals(testInfo.Placements.get(0)[0], row);
        assertEquals(testInfo.Placements.get(0)[1], column);
        assertEquals(testInfo.Placements.get(0)[2], value);
    }

    @Test
    void fullHouseTechnique() {
        final int fullHouseTechniqueId = 0;

        for (HodokuRegressionTestInfo testInfo : mHodokuTests) {
            if (testInfo.TechniqueId == fullHouseTechniqueId) {
                SudokuGame game = TechniqueTestHelpers.createGameFromString(testInfo.GivenCells);
                FullHouseTechnique technique = FullHouseTechnique.create(mContext, game);
                assertNotNull(technique);
                assertPlacement(testInfo, technique.mRow, technique.mColumn, technique.mValue);
            }
        }
    }

    @Test
    void hiddenSingleTechnique() {
        final int hiddenSingleTechniqueId = 2;

        for (HodokuRegressionTestInfo testInfo : mHodokuTests) {
            if (testInfo.TechniqueId == hiddenSingleTechniqueId) {
                SudokuGame game = TechniqueTestHelpers.createGameFromString(testInfo.GivenCells);
                HiddenSingleTechnique technique = HiddenSingleTechnique.create(mContext, game);
                assertNotNull(technique);
                assertPlacement(testInfo, technique.mRow, technique.mColumn, technique.mValue);
            }
        }
    }

    @Test
    void nakedSingleTechnique() {
        final int nakedSingleTechniqueId = 3;

        for (HodokuRegressionTestInfo testInfo : mHodokuTests) {
            if (testInfo.TechniqueId == nakedSingleTechniqueId) {
                SudokuGame game = TechniqueTestHelpers.createGameFromString(testInfo.GivenCells);
                NakedSingleTechnique technique = NakedSingleTechnique.create(mContext, game);
                assertNotNull(technique);
                assertPlacement(testInfo, technique.mRow, technique.mColumn, technique.mValue);
            }
        }
    }
}