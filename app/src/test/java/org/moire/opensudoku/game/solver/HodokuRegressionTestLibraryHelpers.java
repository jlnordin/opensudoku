package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.SudokuGame;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

class HodokuRegressionTestInfo {
    public String OriginalLine = null;
    public int TechniqueId = 0;
    public int TechniqueVariant = 0;
    public int[] Candidates = null;
    public String GivenCells = null;
    public ArrayList<int[]> DeletedCandidates = null;
    public ArrayList<int[]> Eliminations = null;
    public ArrayList<int[]> Placements = null;
    public String ExtraData = null;
}

class HodokuRegressionTestLibraryHelpers {

    // Input: "114 225 999" (value-row-column)
    // Output: [[0, 3, 1], [1, 4, 2], [8, 8, 9]] (row-column-value)
    static ArrayList<int[]> getRowColumnValueTriplesFromValueRowColumnString(String valueRowColumnTriples) {
        if (valueRowColumnTriples == null ||
            valueRowColumnTriples.length() == 0) {
            return new ArrayList<int[]>();
        }

        String[] splitValues = valueRowColumnTriples.split(" ");
        ArrayList<int[]> rowColumnValueTriples = new ArrayList<int[]>(splitValues.length);
        for (int i = 0; i < splitValues.length; i++) {
            int[] rowColumnValue = new int[3];
            rowColumnValue[0] =  Character.getNumericValue(splitValues[i].charAt(1)) - 1;
            rowColumnValue[1] =  Character.getNumericValue(splitValues[i].charAt(2)) - 1;
            rowColumnValue[2] =  Character.getNumericValue(splitValues[i].charAt(0));
            rowColumnValueTriples.add(rowColumnValue);
        }
        return rowColumnValueTriples;
    }

    static HodokuRegressionTestInfo createRegressionTestInfoFromString(String regressionTestString) {
        // Basic format for the Hodoku regression test suite is:
        // :<technique>:<candidate(s)>:<givens>:<deleted candidates>:<eliminations>:<placements>:<extra>
        //
        // E.g.:
        // :0003:8:6...+53+4.7....4+2+56.+254867+3+9+1.+4..+9+6.1.162+38+5749.3..+1+4.+5....42193..1.+57...+44+2.6+3.+1+75:::829:
        //
        // Any line that doesn't start with a ':' is ignored. See reglib-1.4.txt for a more detailed
        // explanation of the format.

        final String delimiter = ":";

        if (!regressionTestString.startsWith(delimiter)) {
            return null;
        }

        String[] testComponents = regressionTestString.split(delimiter);
        if (testComponents.length < 7 ||
            testComponents.length > 8) {
            return null;
        }

        HodokuRegressionTestInfo testInfo = new HodokuRegressionTestInfo();

        // OriginalLine
        testInfo.OriginalLine = regressionTestString;

        // TechniqueId and TechniqueVariant
        String[] idAndVariant = testComponents[1].split("-");
        testInfo.TechniqueId = Integer.parseInt(idAndVariant[0]);
        if (idAndVariant.length > 1) {
            testInfo.TechniqueVariant = Integer.parseInt(idAndVariant[1]);
        } else {
            testInfo.TechniqueVariant = 0;
        }

        // Candidates
        testInfo.Candidates = new int[testComponents[2].length()];
        for (int c = 0; c < testComponents[2].length(); c++) {
            testInfo.Candidates[c] = Character.getNumericValue(testComponents[2].charAt(c)) - 1;
        }

        // GivenCells
        testInfo.GivenCells = testComponents[3]
                .replaceAll("\\+", "")
                .replaceAll("\\.", "0");

        // DeletedCandidates
        testInfo.DeletedCandidates = getRowColumnValueTriplesFromValueRowColumnString(testComponents[4]);

        // Eliminations
        testInfo.Eliminations = getRowColumnValueTriplesFromValueRowColumnString(testComponents[5]);

        // Placements
        testInfo.Placements = getRowColumnValueTriplesFromValueRowColumnString(testComponents[6]);

        // ExtraData
        if (testComponents.length == 8) {
            testInfo.ExtraData = testComponents[7];
        } else {
            testInfo.ExtraData = "";
        }

        return testInfo;
    }

    static final String HodokuRegressionTestLibraryFileName = "reglib-1.4.txt";

    public static HodokuRegressionTestInfo[] getAllHodokuRegressionTests() throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(HodokuRegressionTestLibraryFileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        ArrayList<HodokuRegressionTestInfo> tests = new ArrayList<HodokuRegressionTestInfo>();
        String line = reader.readLine();
        while (line != null) {
            HodokuRegressionTestInfo testInfo = createRegressionTestInfoFromString(line);
            if (testInfo != null) {
                tests.add(testInfo);
            }
            line = reader.readLine();
        }

        return tests.toArray(new HodokuRegressionTestInfo[0]);
    }

    public static SudokuGame createGameFromTestInfo(HodokuRegressionTestInfo testInfo) {
        SudokuGame game = new SudokuGame();
        game.setCells(CellCollection.fromString(testInfo.GivenCells));
        game.getCells().fillInNotes();

        for (int[] rowColValue : testInfo.DeletedCandidates) {
            Cell cell = game.getCells().getCell(rowColValue[0], rowColValue[1]);
            cell.setNote(cell.getNote().removeNumber(rowColValue[2]));
        }

        return game;
    }
}