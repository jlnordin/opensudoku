package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.CellGroup;
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
    public static ArrayList<Cell> getCellCandidates(CellGroup group, int value) {
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

    public static void highlightGroup(CellGroup group, HashMap<Cell, HighlightOptions> highlightOverrides) {
        highlightOverrides.clear();
        for (Cell cell : group.getCells()) {
            highlightOverrides.put(cell, new HighlightOptions());
        }
    }
}
