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
import java.util.Collection;
import java.util.HashSet;

public class FishTechnique extends AbstractTechnique {

    static FishTechnique[] createAllForCardinality(Context context, SudokuGame game, int cardinality) {

        ArrayList<FishTechnique> techniques = new ArrayList<FishTechnique>();
        ArrayList<int[]> rowColumnValuesToRemove = null;

        /*for (CellGroup box : game.getCells().getSectors()) {
            techniques.addAll(getFishTechniquesFromGroup(context, GroupType.Box, box, cardinality));
        }

        for (CellGroup row : game.getCells().getRows()) {
            techniques.addAll(getFishTechniquesFromGroup(context, GroupType.Row, row, cardinality));
        }

        for (CellGroup column : game.getCells().getColumns()) {
            techniques.addAll(getFishTechniquesFromGroup(context, GroupType.Column, column, cardinality));
        }*/

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
                (board) -> {}));

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
