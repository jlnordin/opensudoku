package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.CellGroup;
import org.moire.opensudoku.game.CellNote;
import org.moire.opensudoku.gui.HighlightOptions;
import org.moire.opensudoku.gui.SudokuBoardView;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.RandomAccess;

public class TechniqueHelpers {

    public enum GroupType {
        Box,
        Row,
        Column
    }

    public static String getGroupString(Context context, GroupType group) {
        switch (group) {
            default:
            case Box:
                return context.getString(R.string.box);
            case Row:
                return context.getString(R.string.row);
            case Column:
                return context.getString(R.string.column);
        }
    }

    public static CellGroup getGroup(Cell cell, GroupType group) {
        switch (group) {
            default:
            case Box:
                return cell.getSector();
            case Row:
                return cell.getRow();
            case Column:
                return cell.getColumn();
        }
    }

    public static CellGroup getGroupFromIndex(CellCollection cells, GroupType group, int index)
    {
        switch (group) {
            default:
            case Box:
                // boxes (sectors) are stored in this order internally:
                //
                //   0 3 6
                //   1 4 7
                //   2 5 8
                //
                // But the conventional enumeration is this:
                //
                //   0 1 2
                //   3 4 5
                //   6 7 8
                //
                // So we flip the x and y coordinates before returning the CellGroup.
                int xPosition = index % 3;
                int yPosition = index / 3;
                return cells.getSectors()[xPosition * 3 + yPosition];
            case Row:
                return cells.getRows()[index];
            case Column:
                return cells.getColumns()[index];
        }
    }

    public static int getGroupIndex(GroupType group, int row, int column)
    {
        switch (group) {
            default:
            case Box:
                return ((row / 3) * 3) + (column / 3);
            case Row:
                return row;
            case Column:
                return column;
        }
    }

    /**
     * Returns a list of all of the cells in the given group that have the given value as a
     * candidate.
     */
    public static ArrayList<Cell> getCellsWithCandidateValue(CellGroup group, int value) {
        ArrayList<Cell> candidates = new ArrayList<Cell>();
        for (Cell cell : group.getCells()) {
            if (cell.getValue() == 0) {

                CellGroup row = cell.getRow();
                CellGroup column = cell.getColumn();
                CellGroup box = cell.getSector();

                if (row.doesNotContain(value) && column.doesNotContain(value) && box.doesNotContain(value)) {
                    candidates.add(cell);
                }
            }
        }

        return candidates;
    }

    /**
     * Returns a list of all of the cells in the given group that have the given value as a
     * note. This is similar to getCellsWithCandidateValue except that the candidate values are
     * taken only from the notes and not calculated.
     */
    public static ArrayList<Cell> getCellsWithCandidateValueFromNotes(CellGroup group, int value) {
        ArrayList<Cell> cells = new ArrayList<Cell>();
        for (Cell cell : group.getCells()) {
            if (cell.getValue() == 0) {
                if (cell.getNote().hasNumber(value)) {
                    cells.add(cell);
                }
            }
        }

        return cells;
    }

    /**
     * Returns an array of all of the valid candidates for a given cell.
     */
    public static ArrayList<Integer> getCandidatesForCell(Cell cell) {
        ArrayList<Integer> candidates = new ArrayList<Integer>();
        if (cell.getValue() == 0) {
            CellGroup row = cell.getRow();
            CellGroup column = cell.getColumn();
            CellGroup box = cell.getSector();

            for (int value = 1; value <= CellCollection.SUDOKU_SIZE; value++) {
                if (row.doesNotContain(value) && column.doesNotContain(value) && box.doesNotContain(value)) {
                    candidates.add(value);
                }
            }
        }

        return candidates;
    }

    public static void highlightGroup(CellGroup group, HashMap<Cell, HighlightOptions> highlightOverrides) {
        for (Cell cell : group.getCells()) {
            highlightOverrides.put(cell, new HighlightOptions(HighlightOptions.HighlightMode.EMPHASIZE));
        }
    }

    public static void highlightNotesInGroup(CellGroup group, int note, HashMap<Cell, HighlightOptions> highlightOverrides) {
        for (Cell cell : group.getCells()) {
            if (cell.getValue() == 0 && cell.getNote().hasNumber(note)) {
                HighlightOptions options = new HighlightOptions(HighlightOptions.HighlightMode.NONE);
                if (highlightOverrides.containsKey(cell)) {
                    options = highlightOverrides.get(cell);
                }

                options.setNoteHighlightMode(note - 1, HighlightOptions.HighlightMode.HIGHLIGHT);
                highlightOverrides.put(cell, options);
            }
        }
    }

    public static void highlightNotes(CellCollection cells, int note, HashMap<Cell, HighlightOptions> highlightOverrides) {
        for (CellGroup row : cells.getRows()) {
            highlightNotesInGroup(row, note, highlightOverrides);
        }
    }

    public static HighlightOptions createHighlightOptionsForNotes(Cell cell, int[] notes) {
        HighlightOptions noteHighlight = new HighlightOptions(HighlightOptions.HighlightMode.EMPHASIZE);
        for (int note : notes) {
            if (cell.getValue() == 0 && cell.getNote().hasNumber(note + 1)) {
                noteHighlight.setNoteHighlightMode(note, HighlightOptions.HighlightMode.HIGHLIGHT);
            }
        }
        return noteHighlight;
    }

    public static String noteArrayToString(int[] notes) {
        int[] notesPlusOne = new int[notes.length];
        for (int n = 0; n < notesPlusOne.length; n++) {
            notesPlusOne[n] = notes[n] + 1;
        }
        return Arrays.toString(notesPlusOne);
    }

    /**
     * Get the next subset mask for a given starting point. A sub set mask is a number where each
     * bit of the binary representation corresponds to the element of a set. For example, if we had
     * a set of 4 cells, a value of 0101 would mean "select cells[0] and cells[2]", where a value of
     * 1100 would mean "select cells[2] and cells[3]".
     *
     * This function also takes a cardinality so that the returned mask value will have exactly that
     * many elements. For example, passing in a cardinality of "2" would iterate though all pairs of
     * a given set.
     */
    public static int getNextSubsetMask(int currentSubsetMask, int cardinality) {
        int nextSubsetMask = currentSubsetMask + 1;
        while (Integer.bitCount(nextSubsetMask) != cardinality) {
            nextSubsetMask++;
        }
        return nextSubsetMask;
    }

    public static int getFirstSubsetMask(int cardinality) {
        return getNextSubsetMask(0, cardinality);
    }

    public static int getMaximumSubsetMask(int setSize) {
        return (1 << setSize);
    }

    public static void fillSubset(Collection<Cell> superSet, int subsetMask, Cell[] subset) {
        int outputIndex = 0;
        int inputIndex = 0;
        Iterator<Cell> iterator = superSet.iterator();

        while (iterator.hasNext()) {
            Cell cell = iterator.next();
            if ((subsetMask & (1 << inputIndex)) != 0) {
                if (outputIndex >= subset.length) {
                    return;
                }
                subset[outputIndex] = cell;
                outputIndex++;
            }
            inputIndex++;
        }
    }
}
