package org.moire.opensudoku.game.solver;

import android.content.Context;
import android.preference.SwitchPreference;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.moire.opensudoku.game.SudokuGame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

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

    final static int FullHouseTechniqueId = 0;
    final static int HiddenSingleTechniqueId = 2;
    final static int NakedSingleTechniqueId = 3;
    final static int LockedCandidateType1TechniqueId = 100;
    final static int LockedCandidateType2TechniqueId = 101;
    final static int NakedPairTechniqueId = 200;
    final static int NakedTripleTechniqueId = 201;
    final static int NakedQuadrupleTechniqueId = 202;
    final static int HiddenPairTechniqueId = 210;
    final static int HiddenTripleTechniqueId = 211;
    final static int HiddenQuadrupleTechniqueId = 212;
    final static int XWingTechniqueId = 300;
    final static int SwordfishTechniqueId = 301;
    final static int JellyfishTechniqueId = 302;

    String getTestInfoName(String techniqueName, HodokuRegressionTestInfo testInfo) {
        return String.format("%s - \"%s\"", techniqueName, testInfo.OriginalLine);
    }

    @TestFactory
    public Collection<DynamicTest> fullHouseTechniqueTestFactory() {
        Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
        for (HodokuRegressionTestInfo testInfo : mHodokuTests) {
            if (testInfo.TechniqueId == FullHouseTechniqueId) {
                tests.add(DynamicTest.dynamicTest(
                        getTestInfoName("FullHouseTechnique", testInfo),
                        () -> testFullHouseTechnique(testInfo)));
            }
        }
        return tests;
    }

    void testFullHouseTechnique(HodokuRegressionTestInfo testInfo) {
        SudokuGame game = HodokuRegressionTestLibraryHelpers.createGameFromTestInfo(testInfo);
        FullHouseTechnique technique = FullHouseTechnique.create(mContext, game);
        assertNotNull(technique);
        assertPlacement(testInfo, technique.mRow, technique.mColumn, technique.mValue);
    }

    @TestFactory
    public Collection<DynamicTest> hiddenSingleTechniqueTestFactory() {
        Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
        for (HodokuRegressionTestInfo testInfo : mHodokuTests) {
            if (testInfo.TechniqueId == HiddenSingleTechniqueId) {
                // The HiddenSingleTechnique only operates on given cells, not candidates. As
                // such, we filter the Hodoku tests here to "hidden single" tests that don't
                // require deleting candidates. The HiddenSingleFromNotesTechnique can handle
                // those regression tests.
                if (testInfo.DeletedCandidates.size() == 0) {
                    tests.add(DynamicTest.dynamicTest(
                            getTestInfoName("HiddenSingleTechnique", testInfo),
                            () -> testHiddenSingleTechnique(testInfo)));
                }
            }
        }
        return tests;
    }

    void testHiddenSingleTechnique(HodokuRegressionTestInfo testInfo) {
        SudokuGame game = HodokuRegressionTestLibraryHelpers.createGameFromTestInfo(testInfo);
        HiddenSingleTechnique[] techniques = HiddenSingleTechnique.createAll(mContext, game);
        assertNotNull(techniques);
        assertNotEquals(0, techniques.length);
        assertNotNull(techniques[0]);

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
    }

    @TestFactory
    public Collection<DynamicTest> hiddenSingleFromNotesTechniqueTestFactory() {
        Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
        for (HodokuRegressionTestInfo testInfo : mHodokuTests) {
            if (testInfo.TechniqueId == HiddenSingleTechniqueId) {
                tests.add(DynamicTest.dynamicTest(
                        getTestInfoName("HiddenSingleFromNotesTechnique", testInfo),
                        () -> testHiddenSingleFromNotesTechnique(testInfo)));
            }
        }
        return tests;
    }

    void testHiddenSingleFromNotesTechnique(HodokuRegressionTestInfo testInfo) {
        SudokuGame game = HodokuRegressionTestLibraryHelpers.createGameFromTestInfo(testInfo);
        HiddenSingleFromNotesTechnique[] techniques = HiddenSingleFromNotesTechnique.createAll(mContext, game);
        assertNotNull(techniques);
        assertNotEquals(0, techniques.length);
        assertNotNull(techniques[0]);

        Boolean onePlacementIsCorrect = false;
        for (HiddenSingleFromNotesTechnique technique : techniques) {
            onePlacementIsCorrect = isPlacementCorrect(testInfo, technique.mRow, technique.mColumn, technique.mValue);
            if (onePlacementIsCorrect) {
                break;
            }
        }
        assertTrue(onePlacementIsCorrect);
    }

    @TestFactory
    public Collection<DynamicTest> nakedSingleTechniqueTestFactory() {
        Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
        for (HodokuRegressionTestInfo testInfo : mHodokuTests) {
            if (testInfo.TechniqueId == NakedSingleTechniqueId) {
                // The NakedSingleTechnique only operates on given cells, not candidates. As
                // such, we filter the Hodoku tests here to "naked single" tests that don't
                // require deleting candidates. The NakedSingleFromNotesTechnique can handle
                // those regression tests.
                if (testInfo.DeletedCandidates.size() == 0) {
                    tests.add(DynamicTest.dynamicTest(
                            getTestInfoName("NakedSingleTechnique", testInfo),
                            () -> testNakedSingleTechnique(testInfo)));
                }
            }
        }
        return tests;
    }

    void testNakedSingleTechnique(HodokuRegressionTestInfo testInfo) {
        SudokuGame game = HodokuRegressionTestLibraryHelpers.createGameFromTestInfo(testInfo);
        NakedSingleTechnique[] techniques = NakedSingleTechnique.createAll(mContext, game);
        assertNotNull(techniques);
        assertNotEquals(0, techniques.length);
        assertNotNull(techniques[0]);

        Boolean onePlacementIsCorrect = false;
        for (NakedSingleTechnique technique : techniques) {
            onePlacementIsCorrect = isPlacementCorrect(testInfo, technique.mRow, technique.mColumn, technique.mValue);
            if (onePlacementIsCorrect) {
                break;
            }
        }
        assertTrue(onePlacementIsCorrect);
    }

    @TestFactory
    public Collection<DynamicTest> nakedSingleFromNotesTechniqueTestFactory() {
        Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
        for (HodokuRegressionTestInfo testInfo : mHodokuTests) {
            if (testInfo.TechniqueId == NakedSingleTechniqueId) {
                tests.add(DynamicTest.dynamicTest(
                        getTestInfoName("NakedSingleFromNotesTechnique", testInfo),
                        () -> testNakedSingleFromNotesTechnique(testInfo)));
            }
        }
        return tests;
    }

    void testNakedSingleFromNotesTechnique(HodokuRegressionTestInfo testInfo) {
        SudokuGame game = HodokuRegressionTestLibraryHelpers.createGameFromTestInfo(testInfo);
        NakedSingleFromNotesTechnique[] techniques = NakedSingleFromNotesTechnique.createAll(mContext, game);
        assertNotNull(techniques);
        assertNotEquals(0, techniques.length);
        assertNotNull(techniques[0]);

        Boolean onePlacementIsCorrect = false;
        for (NakedSingleFromNotesTechnique technique : techniques) {
            onePlacementIsCorrect = isPlacementCorrect(testInfo, technique.mRow, technique.mColumn, technique.mValue);
            if (onePlacementIsCorrect) {
                break;
            }
        }
        assertTrue(onePlacementIsCorrect);
    }

    @TestFactory
    public Collection<DynamicTest> lockedCandidateTechniqueTestFactory() {
        Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
        for (HodokuRegressionTestInfo testInfo : mHodokuTests) {
            if (testInfo.TechniqueId == LockedCandidateType1TechniqueId ||
                testInfo.TechniqueId == LockedCandidateType2TechniqueId) {
                tests.add(DynamicTest.dynamicTest(
                        getTestInfoName("LockedCandidateTechnique", testInfo),
                        () -> testLockedCandidateTechnique(testInfo)));
            }
        }
        return tests;
    }

    void testLockedCandidateTechnique(HodokuRegressionTestInfo testInfo) {
        SudokuGame game = HodokuRegressionTestLibraryHelpers.createGameFromTestInfo(testInfo);
        LockedCandidateTechnique[] techniques = LockedCandidateTechnique.createAll(mContext, game);
        assertNotNull(techniques);
        assertNotEquals(0, techniques.length);

        assertEquals(1, testInfo.Candidates.length);

        Boolean oneTechniqueMatches = false;
        for (LockedCandidateTechnique technique : techniques) {

            if (testInfo.Candidates[0] != technique.mValue) {
                continue;
            }

            int matchingEliminations = 0;
            for (int[] rowColumnValue : testInfo.Eliminations) {
                for (int i = 0; i < technique.mRows.length; i++) {
                    if (technique.mRows[i] == rowColumnValue[0] &&
                            technique.mColumns[i] == rowColumnValue[1] &&
                            technique.mValue == rowColumnValue[2]) {
                        matchingEliminations++;
                    }
                }
            }

            if (testInfo.Eliminations.size() == matchingEliminations) {
                oneTechniqueMatches = true;
                break;
            }
        }
        assertTrue(oneTechniqueMatches);
    }

    @TestFactory
    public Collection<DynamicTest> nakedSubsetTechniqueTestFactory() {
        Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
        for (HodokuRegressionTestInfo testInfo : mHodokuTests) {
            if (testInfo.TechniqueId == NakedPairTechniqueId ||
                    testInfo.TechniqueId == NakedTripleTechniqueId ||
                    testInfo.TechniqueId == NakedQuadrupleTechniqueId) {
                tests.add(DynamicTest.dynamicTest(
                        getTestInfoName("NakedSubsetTechnique", testInfo),
                        () -> testNakedSubsetTechnique(testInfo)));
            }
        }
        return tests;
    }

    void testNakedSubsetTechnique(HodokuRegressionTestInfo testInfo) {
        SudokuGame game = HodokuRegressionTestLibraryHelpers.createGameFromTestInfo(testInfo);
        final int cardinality = testInfo.TechniqueId - NakedPairTechniqueId + 2;
        NakedSubsetTechnique[] techniques = NakedSubsetTechnique.createAllForCardinality(mContext, game, cardinality);
        assertNotNull(techniques);
        assertNotEquals(0, techniques.length);
        assertNotNull(techniques[0]);

        Boolean oneTechniqueMatches = false;
        for (NakedSubsetTechnique technique : techniques) {

            ArrayList<int[]> filteredEliminations = HodokuRegressionTestLibraryHelpers.getFilteredEliminations(technique, game.getCells());

            int matchingEliminations = 0;
            for (int[] expectedElimination : testInfo.Eliminations) {
                for (int[] actualElimination : filteredEliminations) {
                    if (expectedElimination[0] == actualElimination[0] &&
                            expectedElimination[1] == actualElimination[1] &&
                            expectedElimination[2] == actualElimination[2]) {
                        matchingEliminations++;
                    }
                }
            }

            if (testInfo.Eliminations.size() == matchingEliminations) {
                oneTechniqueMatches = true;
                break;
            }
        }
        assertTrue(oneTechniqueMatches);
    }

    @TestFactory
    public Collection<DynamicTest> hiddenSubsetTechniqueTestFactory() {
        Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
        for (HodokuRegressionTestInfo testInfo : mHodokuTests) {
            if (testInfo.TechniqueId == HiddenPairTechniqueId ||
                    testInfo.TechniqueId == HiddenTripleTechniqueId ||
                    testInfo.TechniqueId == HiddenQuadrupleTechniqueId) {
                tests.add(DynamicTest.dynamicTest(
                        getTestInfoName("HiddenSubsetTechnique", testInfo),
                        () -> testHiddenSubsetTechnique(testInfo)));
            }
        }
        return tests;
    }

    void testHiddenSubsetTechnique(HodokuRegressionTestInfo testInfo) {
        SudokuGame game = HodokuRegressionTestLibraryHelpers.createGameFromTestInfo(testInfo);
        final int cardinality = testInfo.TechniqueId - HiddenPairTechniqueId + 2;
        HiddenSubsetTechnique[] techniques = HiddenSubsetTechnique.createAllForCardinality(mContext, game, cardinality);
        assertNotNull(techniques);
        assertNotEquals(0, techniques.length);
        assertNotNull(techniques[0]);

        Boolean oneTechniqueMatches = false;
        for (HiddenSubsetTechnique technique : techniques) {

            int matchingEliminations = 0;
            for (int[] expectedElimination : testInfo.Eliminations) {
                for (int[] actualElimination : technique.mRowColumnValuesToRemove) {
                    if (expectedElimination[0] == actualElimination[0] &&
                            expectedElimination[1] == actualElimination[1] &&
                            expectedElimination[2] == actualElimination[2]) {
                        matchingEliminations++;
                    }
                }
            }

            if (testInfo.Eliminations.size() == matchingEliminations) {
                oneTechniqueMatches = true;
                break;
            }
        }
        assertTrue(oneTechniqueMatches);
    }

    @TestFactory
    public Collection<DynamicTest> FishTechniqueTestFactory() {
        Collection<DynamicTest> tests = new ArrayList<DynamicTest>();
        for (HodokuRegressionTestInfo testInfo : mHodokuTests) {
            if (testInfo.TechniqueId == XWingTechniqueId ||
                    testInfo.TechniqueId == SwordfishTechniqueId ||
                    testInfo.TechniqueId == JellyfishTechniqueId) {
                tests.add(DynamicTest.dynamicTest(
                        getTestInfoName("FishTechnique", testInfo),
                        () -> testFishTechnique(testInfo)));
            }
        }
        return tests;
    }

    void testFishTechnique(HodokuRegressionTestInfo testInfo) {
        SudokuGame game = HodokuRegressionTestLibraryHelpers.createGameFromTestInfo(testInfo);
        final int cardinality = testInfo.TechniqueId - XWingTechniqueId + 2;
        FishTechnique[] techniques = FishTechnique.createAllForCardinality(mContext, game, cardinality);
        assertNotNull(techniques);
        assertNotEquals(0, techniques.length);
        assertNotNull(techniques[0]);

        Boolean oneTechniqueMatches = false;
        for (FishTechnique technique : techniques) {

            if (technique.mNote != testInfo.Candidates[0]) {
                continue;
            }

            int matchingEliminations = 0;
            for (int[] expectedElimination : testInfo.Eliminations) {
                for (int[] actualElimination : technique.mRowColumnValuesToRemove) {
                    if (expectedElimination[0] == actualElimination[0] &&
                            expectedElimination[1] == actualElimination[1] &&
                            expectedElimination[2] == actualElimination[2]) {
                        matchingEliminations++;
                    }
                }
            }

            if (testInfo.Eliminations.size() == matchingEliminations) {
                oneTechniqueMatches = true;
                break;
            }
        }
        assertTrue(oneTechniqueMatches);
    }
}