package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.CellGroup;
import org.moire.opensudoku.game.CellNote;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.EditCellNoteCommand;
import org.moire.opensudoku.game.command.SetCellValueCommand;
import org.moire.opensudoku.gui.HighlightOptions;
import org.moire.opensudoku.gui.HighlightOptions.HighlightMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class NakedSubsetTechnique extends AbstractTechnique {

    static int getUnionOfAllCandidates(Cell[] set) {
        int candidateMask = 0;
        for (Cell cell : set) {
            if (cell.getValue() == 0) {
                candidateMask |= cell.getNote().getNotedNumbersAsBitSet();
            }
        }
        return candidateMask;
    }

    static int[] getNotedNumbersFromBitMask(int mask) {
        int[] notes = new int[Integer.bitCount(mask)];
        int outputIndex = 0;
        int bitIndex = 0;
        for (long powerOfTwo = 1; powerOfTwo <= mask; powerOfTwo *= 2) {
            if ((mask & powerOfTwo) != 0) {
                notes[outputIndex] = bitIndex;
                outputIndex++;
            }
            bitIndex++;
        }
        return notes;
    }

    static Cell[] getNakedSubsetFromGroup(CellGroup group, int cardinality) {
        List<Cell> allCellsInGroup = Arrays.asList(group.getCells());
        ArrayList<Cell> cellsToCheck = new ArrayList<Cell>();

        for (Cell cell : allCellsInGroup) {
            if (cell.getValue() == 0 && cell.getNote().getNotedNumbersCount() <= cardinality) {
                cellsToCheck.add(cell);
            }
        }

        // If the number of cells to check is less than the cardinality, there's no possibility for
        // finding a naked subset. If the number of cells matches the cardinality, then we wouldn't
        // be finding a subset at all and no logical deductions could be made. In both cases return
        // early.
        if (cellsToCheck.size() <= cardinality) {
            return null;
        }

        Cell[] subset = new Cell[cardinality];
        for (int subsetMask = TechniqueHelpers.getFirstSubsetMask(cardinality);
             subsetMask < TechniqueHelpers.getMaximumSubsetMask(cellsToCheck.size());
             subsetMask = TechniqueHelpers.getNextSubsetMask(subsetMask, cardinality)) {
            TechniqueHelpers.fillSubset(cellsToCheck, subsetMask, subset);

            // If the union of all of the candidates for the given subset has the same number of
            // bits as the desired cardinality, that means this subset represents a naked subset
            // in the larger group being tested.
            int candidateMask = getUnionOfAllCandidates(subset);
            if (Integer.bitCount(candidateMask) == cardinality) {
                return subset;
            }
        }

        return null;
    }

    public static NakedSubsetTechnique createPair(Context context, SudokuGame game) {

        final int cardinality = 2;

        Cell[] cellsWithNakedSubset = null;

        for (CellGroup box : game.getCells().getSectors()) {
            cellsWithNakedSubset = getNakedSubsetFromGroup(box, cardinality);
            if (cellsWithNakedSubset != null) {
                return new NakedSubsetTechnique(context, cellsWithNakedSubset, getNotedNumbersFromBitMask(getUnionOfAllCandidates(cellsWithNakedSubset)));
            }
        }

        for (CellGroup row : game.getCells().getRows()) {
            cellsWithNakedSubset = getNakedSubsetFromGroup(row, cardinality);
            if (cellsWithNakedSubset != null) {
                return new NakedSubsetTechnique(context, cellsWithNakedSubset, getNotedNumbersFromBitMask(getUnionOfAllCandidates(cellsWithNakedSubset)));
            }
        }

        for (CellGroup column : game.getCells().getColumns()) {
            cellsWithNakedSubset = getNakedSubsetFromGroup(column, cardinality);
            if (cellsWithNakedSubset != null) {
                return new NakedSubsetTechnique(context, cellsWithNakedSubset, getNotedNumbersFromBitMask(getUnionOfAllCandidates(cellsWithNakedSubset)));
            }
        }

        return null;
    }

    int[] mRows;
    int[] mColumns;
    int[] mNotesToRemove;

    public enum Cardinality
    {
        Pair,
        Triple,
        Quadruple
    };
    Cardinality mCardinality;

    NakedSubsetTechnique(Context context, Cell[] cellsToRemoveNotesFrom, int[] notesToRemove) {
        super(context);

        mRows = new int[cellsToRemoveNotesFrom.length];
        mColumns = new int[cellsToRemoveNotesFrom.length];
        mNotesToRemove = notesToRemove;

        if (notesToRemove.length == 2) {
            mCardinality = Cardinality.Pair;
        } else if (notesToRemove.length == 3) {
            mCardinality = Cardinality.Triple;
        } else {
            mCardinality = Cardinality.Quadruple;
        }

        for (int c = 0; c < cellsToRemoveNotesFrom.length; c++) {
            mRows[c] = cellsToRemoveNotesFrom[c].getRowIndex();
            mColumns[c] = cellsToRemoveNotesFrom[c].getColumnIndex();
        }

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_naked_subset_step_1),
                (board) -> {}));
/*
        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_naked_subset_step_2, mValue),
                (board) -> {
                    HighlightOptions options = new HighlightOptions(HighlightMode.EMPHASIZE);
                    options.setNoteHighlightMode(mValue - 1, HighlightMode.HIGHLIGHT);
                    mHighlightOverrides.put(board.getCells().getCell(mRow, mColumn), options);
                }));
*/
    }

    @Override
    public void applyTechnique(SudokuGame game) {
        for (int c = 0; c < mRows.length; c++) {
            Cell cell = game.getCells().getCell(mRows[c], mColumns[c]);
            CellNote newNote = cell.getNote();

            for (int noteToRemove : mNotesToRemove) {
                newNote = newNote.removeNumber(noteToRemove);
            }

            game.getCommandStack().execute(new EditCellNoteCommand(cell, newNote));
        }
    }

    @Override
    public String getName() {
        switch (mCardinality) {
            case Pair:
                return mContext.getString(R.string.technique_naked_pair_title);
            case Triple:
                return mContext.getString(R.string.technique_naked_triple_title);
            default:
            case Quadruple:
                return mContext.getString(R.string.technique_naked_quadruple_title);
        }
    }
}
