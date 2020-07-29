package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.CellGroup;
import org.moire.opensudoku.game.CellNote;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.EditCellNoteCommand;
import org.moire.opensudoku.game.solver.TechniqueHelpers.GroupType;
import org.moire.opensudoku.gui.HighlightOptions;
import org.moire.opensudoku.gui.HighlightOptions.HighlightMode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class HiddenSubsetTechnique extends AbstractTechnique {

    static Boolean noneMatch(int[] integers, int integerToTest) {
        for (int i : integers) {
            if (i == integerToTest) {
                return false;
            }
        }
        return true;
    }

    static ArrayList<int[]> getNotesToRemoveForHiddenSubset(Collection<Cell> subset, int[] hiddenSubsetCandidates) {
        ArrayList<int[]> notesToRemove = new ArrayList<int[]>();

        for (Cell c : subset) {
            if (c.getValue() == 0) {
                for (int note : c.getNote().getNotedNumbers()) {
                    if (noneMatch(hiddenSubsetCandidates, note - 1)) {
                        int[] rowColumnValue = new int[3];
                        rowColumnValue[0] = c.getRowIndex();
                        rowColumnValue[1] = c.getColumnIndex();
                        rowColumnValue[2] = note;
                        notesToRemove.add(rowColumnValue);
                    }
                }
            }
        }

        return notesToRemove;
    }

    static ArrayList<HiddenSubsetTechnique> getHiddenSubsetTechniquesFromGroup(Context context, GroupType groupType, CellGroup group, int cardinality) {

        ArrayList<HiddenSubsetTechnique> techniques = new ArrayList<HiddenSubsetTechnique>();

        ArrayList<Cell>[] cellsWithCandidateValue = new ArrayList[CellCollection.SUDOKU_SIZE];
        for (int i = 0; i < cellsWithCandidateValue.length; i++) {
            cellsWithCandidateValue[i] = TechniqueHelpers.getCellsWithCandidateValueFromNotes(group, i + 1);
        }

        NextSubset:
        for (int subsetMask = TechniqueHelpers.getFirstSubsetMask(cardinality);
             subsetMask < TechniqueHelpers.getMaximumSubsetMask(cellsWithCandidateValue.length);
             subsetMask = TechniqueHelpers.getNextSubsetMask(subsetMask, cardinality)) {
            HashSet<Cell> potentialSubset = new HashSet<Cell>();
            int[] indices = TechniqueHelpers.getIndicesFromSubsetMask(subsetMask);
            for (int i : indices) {
                if (cellsWithCandidateValue[i].isEmpty()) {
                    continue NextSubset;
                }
                potentialSubset.addAll(cellsWithCandidateValue[i]);
            }

            if (potentialSubset.size() == cardinality) {
                ArrayList<int[]> notesToRemove = getNotesToRemoveForHiddenSubset(potentialSubset, indices);
                if (!notesToRemove.isEmpty()) {
                    techniques.add(new HiddenSubsetTechnique(
                            context,
                            indices,
                            notesToRemove,
                            groupType,
                            TechniqueHelpers.getGroupIndex(groupType, notesToRemove.get(0)[0], notesToRemove.get(0)[1])));
                }
            }
        }

        return techniques;
    }

    static HiddenSubsetTechnique[] createAllForCardinality(Context context, SudokuGame game, int cardinality) {

        ArrayList<HiddenSubsetTechnique> techniques = new ArrayList<HiddenSubsetTechnique>();
        ArrayList<int[]> rowColumnValuesToRemove = null;

        for (CellGroup box : game.getCells().getSectors()) {
            techniques.addAll(getHiddenSubsetTechniquesFromGroup(context, GroupType.Box, box, cardinality));
        }

        for (CellGroup row : game.getCells().getRows()) {
            techniques.addAll(getHiddenSubsetTechniquesFromGroup(context, GroupType.Row, row, cardinality));
        }

        for (CellGroup column : game.getCells().getColumns()) {
            techniques.addAll(getHiddenSubsetTechniquesFromGroup(context, GroupType.Column, column, cardinality));
        }

        return techniques.toArray(new HiddenSubsetTechnique[0]);
    }

    public static HiddenSubsetTechnique createPair(Context context, SudokuGame game) {
        HiddenSubsetTechnique[] techniques = createAllForCardinality(context, game, 2);
        if (techniques.length > 0) {
            return techniques[0];
        } else {
            return null;
        }
    }

    public static HiddenSubsetTechnique createTriple(Context context, SudokuGame game) {
        HiddenSubsetTechnique[] techniques = createAllForCardinality(context, game, 3);
        if (techniques.length > 0) {
            return techniques[0];
        } else {
            return null;
        }
    }

    public static HiddenSubsetTechnique createQuadruple(Context context, SudokuGame game) {
        HiddenSubsetTechnique[] techniques = createAllForCardinality(context, game, 4);
        if (techniques.length > 0) {
            return techniques[0];
        } else {
            return null;
        }
    }

    ArrayList<int[]> mRowColumnValuesToRemove;
    int[] mNotesInSubset;
    GroupType mGroupType;
    int mGroupIndex;

    Cardinality mCardinality;

    HiddenSubsetTechnique(Context context, int[] notesInSubset, ArrayList<int[]> rowColumnValuesToRemove, GroupType groupType, int groupIndex) {
        super(context);

        mRowColumnValuesToRemove = rowColumnValuesToRemove;
        mNotesInSubset = notesInSubset;
        mGroupType = groupType;
        mGroupIndex = groupIndex;

        if (notesInSubset.length == 2) {
            mCardinality = Cardinality.Pair;
        } else if (notesInSubset.length == 3) {
            mCardinality = Cardinality.Triple;
        } else {
            mCardinality = Cardinality.Quadruple;
        }

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_hidden_subset_step_1),
                (board) -> {}));

        /*mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_hidden_subset_step_2,
                        TechniqueHelpers.getGroupString(context, mGroupType),
                        mGroupIndex + 1),
                (board) -> {
                    TechniqueHelpers.highlightGroup(TechniqueHelpers.getGroupFromIndex(board.getCells(), mGroupType, mGroupIndex), mHighlightOverrides);
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_hidden_subset_step_3,
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
                mContext.getString(R.string.technique_hidden_subset_step_4,
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
                }));*/
    }

    @Override
    public void applyTechnique(SudokuGame game) {
        for (int[] rowColumnValue : mRowColumnValuesToRemove) {
            Cell cell = game.getCells().getCell(rowColumnValue[0], rowColumnValue[1]);
            CellNote newNote = cell.getNote().removeNumber(rowColumnValue[2]);
            game.getCommandStack().execute(new EditCellNoteCommand(cell, newNote));
        }
    }

    @Override
    public String getName() {
        switch (mCardinality) {
            case Pair:
                return mContext.getString(R.string.technique_hidden_pair_title);
            case Triple:
                return mContext.getString(R.string.technique_hidden_triple_title);
            default:
            case Quadruple:
                return mContext.getString(R.string.technique_hidden_quadruple_title);
        }
    }
}
