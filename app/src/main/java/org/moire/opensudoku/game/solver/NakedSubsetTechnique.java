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
import org.moire.opensudoku.game.solver.TechniqueHelpers.GroupType;
import org.moire.opensudoku.gui.HighlightOptions;
import org.moire.opensudoku.gui.HighlightOptions.HighlightMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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

    static boolean groupHasDeductionsFromSubset(CellGroup group, Cell[] subset) {
        Cell[] cellsNotInSubset = getCellsFromGroupNotInSet(group, subset);
        int candidateUnionInSubset = getUnionOfAllCandidates(subset);
        int candidateUnionNotInSubset = getUnionOfAllCandidates(cellsNotInSubset);
        return (candidateUnionInSubset & candidateUnionNotInSubset) != 0;
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
            if (Integer.bitCount(candidateMask) == cardinality && groupHasDeductionsFromSubset(group, subset)) {
                return subset;
            }
        }

        return null;
    }

    static Cell[] getCellsFromGroupNotInSet(CellGroup group, Cell[] cellsToExclude) {
        HashSet<Cell> set = new HashSet<Cell>(Arrays.asList(cellsToExclude));
        Cell[] inverseSet = new Cell[group.getCells().length - cellsToExclude.length];
        int outputIndex = 0;
        for (Cell cell : group.getCells()) {
            if (!set.contains(cell)) {
                inverseSet[outputIndex] = cell;
                outputIndex++;
            }
        }
        return inverseSet;
    }

    static NakedSubsetTechnique createForCardinality(Context context, SudokuGame game, int cardinality) {

        Cell[] cellsWithNakedSubset = null;

        for (CellGroup box : game.getCells().getSectors()) {
            cellsWithNakedSubset = getNakedSubsetFromGroup(box, cardinality);
            if (cellsWithNakedSubset != null) {
                Cell[] cellsToRemoveNotesFrom = getCellsFromGroupNotInSet(box, cellsWithNakedSubset);
                return new NakedSubsetTechnique(
                        context,
                        cellsToRemoveNotesFrom,
                        getNotedNumbersFromBitMask(getUnionOfAllCandidates(cellsWithNakedSubset)),
                        GroupType.Box,
                        TechniqueHelpers.getGroupIndex(GroupType.Box, cellsWithNakedSubset[0].getRowIndex(), cellsWithNakedSubset[0].getColumnIndex()));
            }
        }

        for (CellGroup row : game.getCells().getRows()) {
            cellsWithNakedSubset = getNakedSubsetFromGroup(row, cardinality);
            if (cellsWithNakedSubset != null) {
                return new NakedSubsetTechnique(
                        context,
                        getCellsFromGroupNotInSet(row, cellsWithNakedSubset),
                        getNotedNumbersFromBitMask(getUnionOfAllCandidates(cellsWithNakedSubset)),
                        GroupType.Row,
                        TechniqueHelpers.getGroupIndex(GroupType.Row, cellsWithNakedSubset[0].getRowIndex(), cellsWithNakedSubset[0].getColumnIndex()));
            }
        }

        for (CellGroup column : game.getCells().getColumns()) {
            cellsWithNakedSubset = getNakedSubsetFromGroup(column, cardinality);
            if (cellsWithNakedSubset != null) {
                return new NakedSubsetTechnique(
                        context,
                        getCellsFromGroupNotInSet(column, cellsWithNakedSubset),
                        getNotedNumbersFromBitMask(getUnionOfAllCandidates(cellsWithNakedSubset)),
                        GroupType.Column,
                        TechniqueHelpers.getGroupIndex(GroupType.Column, cellsWithNakedSubset[0].getRowIndex(), cellsWithNakedSubset[0].getColumnIndex()));
            }
        }

        return null;
    }

    public static NakedSubsetTechnique createPair(Context context, SudokuGame game) {
        return createForCardinality(context, game, 2);
    }

    public static NakedSubsetTechnique createTriple(Context context, SudokuGame game) {
        return createForCardinality(context, game, 3);
    }

    public static NakedSubsetTechnique createQuadruple(Context context, SudokuGame game) {
        return createForCardinality(context, game, 4);
    }

    int[] mRows;
    int[] mColumns;
    int[] mNotesToRemove;
    GroupType mGroupType;
    int mGroupIndex;

    public enum Cardinality
    {
        Pair,
        Triple,
        Quadruple
    };
    Cardinality mCardinality;

    NakedSubsetTechnique(Context context, Cell[] cellsToRemoveNotesFrom, int[] notesToRemove, GroupType groupType, int groupIndex) {
        super(context);

        mRows = new int[cellsToRemoveNotesFrom.length];
        mColumns = new int[cellsToRemoveNotesFrom.length];
        mNotesToRemove = notesToRemove;
        mGroupType = groupType;
        mGroupIndex = groupIndex;

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

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_naked_subset_step_2,
                        TechniqueHelpers.getGroupString(context, mGroupType),
                        mGroupIndex + 1),
                (board) -> {
                    TechniqueHelpers.highlightGroup(TechniqueHelpers.getGroupFromIndex(board.getCells(), mGroupType, mGroupIndex), mHighlightOverrides);
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_naked_subset_step_3,
                        TechniqueHelpers.noteArrayToString(mNotesToRemove)),
                (board) -> {
                    CellGroup group = TechniqueHelpers.getGroupFromIndex(board.getCells(), mGroupType, mGroupIndex);
                    for (Cell cell : group.getCells()) {
                        mHighlightOverrides.put(cell, new HighlightOptions(HighlightMode.NONE));
                    }

                    Cell[] cellsWithNotesToRemove = new Cell[mRows.length];
                    for (int c = 0; c < mRows.length; c++) {
                        cellsWithNotesToRemove[c] = board.getCells().getCell(mRows[c], mColumns[c]);
                    }

                    Cell[] cellsWithSubset = getCellsFromGroupNotInSet(group, cellsWithNotesToRemove);
                    for (Cell cell : cellsWithSubset) {
                        mHighlightOverrides.put(cell, TechniqueHelpers.createHighlightOptionsForNotes(cell, mNotesToRemove));
                    }
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_naked_subset_step_4,
                        TechniqueHelpers.noteArrayToString(mNotesToRemove),
                        TechniqueHelpers.getGroupString(context, mGroupType),
                        mGroupIndex + 1),
                (board) -> {
                    CellGroup group = TechniqueHelpers.getGroupFromIndex(board.getCells(), mGroupType, mGroupIndex);
                    for (Cell cell : group.getCells()) {
                        mHighlightOverrides.put(cell, new HighlightOptions(HighlightMode.NONE));
                    }

                    for (int c = 0; c < mRows.length; c++) {
                        Cell cell = board.getCells().getCell(mRows[c], mColumns[c]);
                        mHighlightOverrides.put(cell, TechniqueHelpers.createHighlightOptionsForNotes(cell, mNotesToRemove));
                    }
                }));
    }

    @Override
    public void applyTechnique(SudokuGame game) {
        for (int c = 0; c < mRows.length; c++) {
            Cell cell = game.getCells().getCell(mRows[c], mColumns[c]);
            CellNote newNote = cell.getNote();

            for (int noteToRemove : mNotesToRemove) {
                newNote = newNote.removeNumber(noteToRemove + 1);
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
