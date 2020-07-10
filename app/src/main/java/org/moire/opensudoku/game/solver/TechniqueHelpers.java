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
import java.util.HashMap;

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
}
