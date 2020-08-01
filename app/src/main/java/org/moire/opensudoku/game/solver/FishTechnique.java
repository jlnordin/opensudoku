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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class FishTechnique extends AbstractTechnique {

    static boolean doesFishExistForNoteAndGroups(int note, CellGroup[] baseSets, CellGroup[] coverSets) {
        boolean[] baseSetIntersectionsHaveNote = new boolean[baseSets.length];
        boolean[] coverSetIntersectionsHaveNote = new boolean[coverSets.length];
        Arrays.fill(baseSetIntersectionsHaveNote, false);
        Arrays.fill(coverSetIntersectionsHaveNote, false);

        // To be a valid fish pattern, we first consider the grid of cells formed by the
        // intersections of all of the base and cover sets. For example, a Swordfish (cardinality 3)
        // would have 9 intersections in a 3 x 3 grid pattern. To be a valid fish pattern, each of
        // the rows and columns of this intersection grid must contain at least one of the target
        // note value.
        for (int b = 0; b < baseSets.length; b++) {
            for (int c = 0; c < coverSets.length; c++) {
                for (Cell cell : CellGroup.intersection(baseSets[b], coverSets[c])) {
                    if (cell.getValue() == 0 && cell.getNote().hasNumber(note)) {
                        baseSetIntersectionsHaveNote[b] = true;
                        coverSetIntersectionsHaveNote[c] = true;
                    }
                }
            }
        }

        for (boolean hasNote : baseSetIntersectionsHaveNote) {
            if (!hasNote) {
                return false;
            }
        }

        for (boolean hasNote : coverSetIntersectionsHaveNote) {
            if (!hasNote) {
                return false;
            }
        }

        // We now know that the intersections contain the right note values, but this is still not
        // sufficient. We must also ensure that no other notes of the note value exist in any of the
        // base sets that are _not_ in the cover sets.
        HashSet<Cell> cellsInBaseSetsButNotInCoverSets = new HashSet<Cell>();
        for (CellGroup baseSet : baseSets) {
            cellsInBaseSetsButNotInCoverSets.addAll(Arrays.asList(baseSet.getCells()));
        }

        for (CellGroup coverSet : coverSets) {
            cellsInBaseSetsButNotInCoverSets.removeAll(Arrays.asList(coverSet.getCells()));
        }

        for (Cell cell : cellsInBaseSetsButNotInCoverSets) {
            if (cell.getValue() == 0 && cell.getNote().hasNumber(note)) {
                return false;
            }
        }

        return true;
    }

    static ArrayList<int[]> getNotesToRemoveFromFish(int note, CellGroup[] baseSets, CellGroup[] coverSets) {
        ArrayList<int[]> rowColumnValuesToRemove = new ArrayList<int[]>();

        if (doesFishExistForNoteAndGroups(note, baseSets, coverSets)) {

            HashSet<Cell> cellsInCoverSetsButNotInBaseSets = new HashSet<Cell>();
            for (CellGroup coverSet : coverSets) {
                cellsInCoverSetsButNotInBaseSets.addAll(Arrays.asList(coverSet.getCells()));
            }

            for (CellGroup baseSet : baseSets) {
                cellsInCoverSetsButNotInBaseSets.removeAll(Arrays.asList(baseSet.getCells()));
            }

            for (Cell cell : cellsInCoverSetsButNotInBaseSets) {
                if (cell.getValue() == 0 && cell.getNote().hasNumber(note)) {
                    int[] rowColumnValue = new int[3];
                    rowColumnValue[0] = cell.getRowIndex();
                    rowColumnValue[1] = cell.getColumnIndex();
                    rowColumnValue[2] = note;
                    rowColumnValuesToRemove.add(rowColumnValue);
                }
            }
        }

        return rowColumnValuesToRemove;
    }

    static void fillCellGroupArrayFromSubsetMask(CellCollection cells, GroupType groupType, int subsetMask, CellGroup[] cellGroups) {
        int outputIndex = 0;
        for (int i : TechniqueHelpers.getIndicesFromSubsetMask(subsetMask)) {
            switch (groupType) {
                default:
                case Row:
                    cellGroups[outputIndex] = cells.getRows()[i];
                    break;

                case Column:
                    cellGroups[outputIndex] = cells.getColumns()[i];
                    break;

                case Box:
                    cellGroups[outputIndex] = cells.getSectors()[i];
                    break;
            }
            outputIndex++;
        }
    }

    public static FishTechnique[] createAllForCardinality(Context context, SudokuGame game, int cardinality) {

        ArrayList<FishTechnique> techniques = new ArrayList<FishTechnique>();
        CellGroup[] baseSets = new CellGroup[cardinality];
        CellGroup[] coverSets = new CellGroup[cardinality];
        ArrayList<int[]> rowColumnValuesToRemove = null;

        // Looping through the subset masks will ensure we check all possible subsets of rows and
        // columns for possible fish.
        //
        // Using a cardinality of 2 as an example, this means we will loop through all possible
        // pairs of rows as base sets against all possible pairs of columns as cover sets. The
        // subset mask represents the indices of the rows and/or columns to use.
        for (int note = 1; note <= CellCollection.SUDOKU_SIZE; note++) {

            for (int baseSetIndicesMask = TechniqueHelpers.getFirstSubsetMask(cardinality);
                 baseSetIndicesMask <= TechniqueHelpers.getMaximumSubsetMask(CellCollection.SUDOKU_SIZE);
                 baseSetIndicesMask = TechniqueHelpers.getNextSubsetMask(baseSetIndicesMask, cardinality)) {

                for (int coverSetIndicesMask = TechniqueHelpers.getFirstSubsetMask(cardinality);
                     coverSetIndicesMask <= TechniqueHelpers.getMaximumSubsetMask(CellCollection.SUDOKU_SIZE);
                     coverSetIndicesMask = TechniqueHelpers.getNextSubsetMask(coverSetIndicesMask, cardinality)) {

                    // Look for a fish with with a base set of rows, cover set of columns.
                    fillCellGroupArrayFromSubsetMask(game.getCells(), GroupType.Row, baseSetIndicesMask, baseSets);
                    fillCellGroupArrayFromSubsetMask(game.getCells(), GroupType.Column, coverSetIndicesMask, coverSets);
                    rowColumnValuesToRemove = getNotesToRemoveFromFish(note, baseSets, coverSets);
                    if (!rowColumnValuesToRemove.isEmpty()) {
                        techniques.add(new FishTechnique(
                                context,
                                note,
                                rowColumnValuesToRemove,
                                GroupType.Row,
                                TechniqueHelpers.getIndicesFromSubsetMask(baseSetIndicesMask),
                                GroupType.Column,
                                TechniqueHelpers.getIndicesFromSubsetMask(coverSetIndicesMask)));
                    }

                    // Look for a fish with with a base set of columns, cover set of rows.
                    fillCellGroupArrayFromSubsetMask(game.getCells(), GroupType.Column, baseSetIndicesMask, baseSets);
                    fillCellGroupArrayFromSubsetMask(game.getCells(), GroupType.Row, coverSetIndicesMask, coverSets);
                    rowColumnValuesToRemove = getNotesToRemoveFromFish(note, baseSets, coverSets);
                    if (!rowColumnValuesToRemove.isEmpty()) {
                        techniques.add(new FishTechnique(
                                context,
                                note,
                                rowColumnValuesToRemove,
                                GroupType.Column,
                                TechniqueHelpers.getIndicesFromSubsetMask(baseSetIndicesMask),
                                GroupType.Row,
                                TechniqueHelpers.getIndicesFromSubsetMask(coverSetIndicesMask)));
                    }
                }
            }
        }

        return techniques.toArray(new FishTechnique[0]);
    }

    public static FishTechnique createXWing(Context context, SudokuGame game) {
        FishTechnique[] techniques = createAllForCardinality(context, game, 2);
        if (techniques.length > 0) {
            return techniques[0];
        } else {
            return null;
        }
    }

    public static FishTechnique createSwordfish(Context context, SudokuGame game) {
        FishTechnique[] techniques = createAllForCardinality(context, game, 3);
        if (techniques.length > 0) {
            return techniques[0];
        } else {
            return null;
        }
    }

    public static FishTechnique createJellyfish(Context context, SudokuGame game) {
        FishTechnique[] techniques = createAllForCardinality(context, game, 4);
        if (techniques.length > 0) {
            return techniques[0];
        } else {
            return null;
        }
    }

    ArrayList<int[]> mRowColumnValuesToRemove;
    int mNote;
    GroupType mBaseGroupType;
    int[] mBaseGroupIndices;
    GroupType mCoverGroupType;
    int[] mCoverGroupIndices;
    int mCardinality;

    FishTechnique(Context context, int note, ArrayList<int[]> rowColumnValuesToRemove, GroupType baseGroupType, int[] baseGroupIndices, GroupType coverGroupType, int[] coverGroupIndices) {
        super(context);

        mRowColumnValuesToRemove = rowColumnValuesToRemove;
        mNote = note;
        mBaseGroupType = baseGroupType;
        mBaseGroupIndices = baseGroupIndices;
        mCoverGroupType = coverGroupType;
        mCoverGroupIndices = coverGroupIndices;
        mCardinality = baseGroupIndices.length;

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_fish_step_1),
                (board) -> {
                    for (int b = 0; b < mBaseGroupIndices.length; b++) {
                        TechniqueHelpers.highlightGroup(TechniqueHelpers.getGroupFromIndex(board.getCells(), mBaseGroupType, mBaseGroupIndices[b]), mHighlightOverrides);
                    }

                    for (int c = 0; c < mCoverGroupIndices.length; c++) {
                        TechniqueHelpers.highlightGroup(TechniqueHelpers.getGroupFromIndex(board.getCells(), mCoverGroupType, mCoverGroupIndices[c]), mHighlightOverrides);
                    }

                    for (int[] rowColumnValue : mRowColumnValuesToRemove) {
                        Cell cell = board.getCells().getCell(rowColumnValue[0], rowColumnValue[1]);
                        HighlightOptions options = new HighlightOptions();
                        if (mHighlightOverrides.containsKey(cell)) {
                            options = mHighlightOverrides.get(cell);
                        }

                        options.setNoteHighlightMode(mNote - 1, HighlightMode.EMPHASIZE);
                        mHighlightOverrides.put(cell, options);
                    }
                }));

        /*mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_fish_step_2,
                        TechniqueHelpers.getGroupString(context, mGroupType),
                        mGroupIndex + 1),
                (board) -> {
                    TechniqueHelpers.highlightGroup(TechniqueHelpers.getGroupFromIndex(board.getCells(), mGroupType, mGroupIndex), mHighlightOverrides);
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_fish_step_3,
                        TechniqueHelpers.getGroupString(context, mGroupType),
                        mGroupIndex + 1,
                        TechniqueHelpers.noteArrayToString(mNotesInSubset)),
                (board) -> {
                    CellGroup group = TechniqueHelpers.getGroupFromIndex(board.getCells(), mGroupType, mGroupIndex);
                    for (Cell cell : group.getCells()) {
                        if (TechniqueHelpers.hasAnyNotesFromSet(cell, mNotesInSubset)) {
                            mHighlightOverrides.put(cell, TechniqueHelpers.createHighlightOptionsForNotes(cell, mNotesInSubset));
                        } else {
                            mHighlightOverrides.put(cell, new HighlightOptions(HighlightMode.NONE));
                        }
                    }
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_fish_step_4,
                        TechniqueHelpers.noteArrayToString(mNotesInSubset)),
                (board) -> {
                    CellGroup group = TechniqueHelpers.getGroupFromIndex(board.getCells(), mGroupType, mGroupIndex);
                    for (Cell cell : group.getCells()) {
                        mHighlightOverrides.put(cell, new HighlightOptions(HighlightMode.NONE));
                    }

                    for (int[] rowColumnValue : mRowColumnValuesToRemove) {
                        Cell cell = board.getCells().getCell(rowColumnValue[0], rowColumnValue[1]);
                        mHighlightOverrides.put(cell, TechniqueHelpers.createHighlightOptionsForInverseNotes(cell, mNotesInSubset));
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
            default:
            case 2:
                return mContext.getString(R.string.technique_fish_xwing_title);
            case 3:
                return mContext.getString(R.string.technique_fish_swordfish_title);
            case 4:
                return mContext.getString(R.string.technique_fish_jellyfish_title);
        }
    }
}
