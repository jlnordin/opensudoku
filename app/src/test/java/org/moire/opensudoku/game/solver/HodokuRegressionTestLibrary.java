package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.junit.ExceptionFactory;
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

    static Boolean isPlacementCorrect(HodokuRegressionTestInfo testInfo, int row, int column, int value) {
        // Placements are [row, column, value] triples.
        int[] placement = testInfo.Placements.get(0);
        if (placement[0] == row && placement[1] == column && placement[2] == value) {
            return true;
        } else {
            return false;
        }
    }

    static void assertPlacement(HodokuRegressionTestInfo testInfo, int row, int column, int value) {
        assertTrue(isPlacementCorrect(testInfo, row, column, value));
    }

    @Test
    void fullHouseTechnique() {
        final int fullHouseTechniqueId = 0;

        for (HodokuRegressionTestInfo testInfo : mHodokuTests) {
            if (testInfo.TechniqueId == fullHouseTechniqueId) {
                SudokuGame game = HodokuRegressionTestLibraryHelpers.createGameFromTestInfo(testInfo);
                FullHouseTechnique technique = FullHouseTechnique.create(mContext, game);
                assertNotNull(technique);
                assertPlacement(testInfo, technique.mRow, technique.mColumn, technique.mValue);
            }
        }
    }

    @Test
    void hiddenSingleTechnique() {
        final int hiddenSingleTechniqueId = 2;

        int tests = 0;
        for (HodokuRegressionTestInfo testInfo : mHodokuTests) {
            // The HiddenSingleTechnique only operates on given cells, not candidates. As such, we
            // filter the Hodoku tests here to "hidden single" tests that don't require deleting
            // candidates. The HiddenSingleFromNotesTechnique can handle those regression tests.
            if (testInfo.TechniqueId == hiddenSingleTechniqueId &&
                    testInfo.DeletedCandidates.size() == 0) {
                SudokuGame game = HodokuRegressionTestLibraryHelpers.createGameFromTestInfo(testInfo);
                HiddenSingleTechnique[] techniques = HiddenSingleTechnique.createAll(mContext, game);
                assertNotNull(techniques);
                assertNotEquals(0, techniques.length);

                // Assert that the Hodoku library's expected placement is one of the possible
                // techniques for the given board. Some games will have multiple valid instances of
                // the same technique available, and the OpenSudoku solver presents them in a
                // different order than the Hodoku library. Instead of worrying about what order is
                // first, we instead just assert that the Hodoku test library is referring to at
                // least one valid technique deduction.
                Boolean onePlacementIsCorrect = false;
                for (HiddenSingleTechnique technique : techniques) {
                    onePlacementIsCorrect = isPlacementCorrect(testInfo, technique.mRow, technique.mColumn, technique.mValue);
                    if (onePlacementIsCorrect) {
                        break;
                    }
                }
                assertTrue(onePlacementIsCorrect);
                tests++;
            }
        }

        System.out.println(String.format("INFO: Verified %d hidden single tests from the Hodoku test library.", tests));
    }

    @Test
    void nakedSingleTechnique() {
        final int nakedSingleTechniqueId = 3;

        for (HodokuRegressionTestInfo testInfo : mHodokuTests) {
            if (testInfo.TechniqueId == nakedSingleTechniqueId) {
                SudokuGame game = HodokuRegressionTestLibraryHelpers.createGameFromTestInfo(testInfo);
                NakedSingleTechnique technique = NakedSingleTechnique.create(mContext, game);
                assertNotNull(technique);
                assertPlacement(testInfo, technique.mRow, technique.mColumn, technique.mValue);
            }
        }
    }
}