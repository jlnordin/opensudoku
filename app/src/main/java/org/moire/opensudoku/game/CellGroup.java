/*
 * Copyright (C) 2009 Roman Masek
 *
 * This file is part of OpenSudoku.
 *
 * OpenSudoku is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenSudoku is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenSudoku.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.moire.opensudoku.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


/**
 * Represents group of cells which must each contain unique number.
 * <p/>
 * Typical examples of instances are sudoku row, column or sector (3x3 group of cells).
 *
 * @author romario
 */
public class CellGroup {
    private Cell[] mCells = new Cell[CellCollection.SUDOKU_SIZE];
    private int mPos = 0;

    public void addCell(Cell cell) {
        mCells[mPos] = cell;
        mPos++;
    }


    /**
     * Validates numbers in given sudoku group - numbers must be unique. Cells with invalid
     * numbers are marked (see {@link Cell#isValid}).
     * <p/>
     * Method expects that cell's invalid properties has been set to false
     * ({@link CellCollection#validate} does this).
     *
     * @return True if validation is successful.
     */
    protected boolean validate() {
        boolean valid = true;

        Map<Integer, Cell> cellsByValue = new HashMap<>();
        for (Cell cell : mCells) {
            int value = cell.getValue();
            if (cellsByValue.get(value) != null) {
                cell.setValid(false);
                cellsByValue.get(value).setValid(false);
                valid = false;
            } else {
                cellsByValue.put(value, cell);
                // we cannot set cell as valid here, because same cell can be invalid
                // as part of another group
            }
        }

        return valid;
    }

    public boolean doesNotContain(int value) {
        for (Cell mCell : mCells) {
            if (mCell.getValue() == value) {
                return false;
            }
        }
        return true;
    }

    public boolean containsValue(int value) {
        for (Cell mCell : mCells) {
            if (mCell.getValue() == value) {
                return true;
            }
        }
        return false;
    }

    public Cell[] getCells() {
        return mCells;
    }

    public boolean contains(Cell cell) {
        return Arrays.asList(mCells).contains(cell);
    }

    public static Cell[] union(CellGroup a, CellGroup b) {
        HashSet<Cell> set = new HashSet<Cell>(Arrays.asList(a.mCells));
        set.addAll(Arrays.asList(b.mCells));
        return set.toArray(new Cell[0]);
    }

    public static void fillArrayWithIntersection(CellGroup a, CellGroup b, Cell[] intersectingCells) {
        Arrays.fill(intersectingCells, null);
        int outputIndex = 0;
        for (Cell cellA : a.getCells()) {
            for (Cell cellB : b.getCells()) {
                if (cellA == cellB) {
                    intersectingCells[outputIndex] = cellA;
                    outputIndex++;
                }
            }
        }
    }

    public static Cell[] intersection(CellGroup a, CellGroup b) {
        HashSet<Cell> set = new HashSet<Cell>(Arrays.asList(a.mCells));
        set.retainAll(Arrays.asList(b.mCells));
        return set.toArray(new Cell[0]);
    }

    public static Cell[] difference(CellGroup a, CellGroup b) {
        HashSet<Cell> set = new HashSet<Cell>(Arrays.asList(a.mCells));
        set.removeAll(Arrays.asList(b.mCells));
        return set.toArray(new Cell[0]);
    }
}
