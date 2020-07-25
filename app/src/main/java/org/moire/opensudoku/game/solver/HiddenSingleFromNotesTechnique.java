package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.CellGroup;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.SetCellValueCommand;
import org.moire.opensudoku.game.solver.TechniqueHelpers.GroupType;
import org.moire.opensudoku.gui.HighlightOptions;
import org.moire.opensudoku.gui.HighlightOptions.HighlightMode;

import java.util.ArrayList;

public class HiddenSingleFromNotesTechnique extends AbstractTechnique {

    static HiddenSingleFromNotesTechnique createTechniqueFromGroup(Context context, SudokuGame game, GroupType groupType, CellGroup group) {
        for (int i = 1; i <= CellCollection.SUDOKU_SIZE; i++) {
            ArrayList<Cell> candidates = TechniqueHelpers.getCellsWithCandidateValueFromNotes(group, i);
            if (candidates.size() == 1) {
                return new HiddenSingleFromNotesTechnique(
                        context,
                        groupType,
                        candidates.get(0).getRowIndex(),
                        candidates.get(0).getColumnIndex(),
                        i);
            }
        }
        return null;
    }

    public static HiddenSingleFromNotesTechnique create(Context context, SudokuGame game) {

        HiddenSingleFromNotesTechnique technique = null;

        for (CellGroup box : game.getCells().getSectors()) {
            for (int i = 1; i <= CellCollection.SUDOKU_SIZE; i++) {
                technique = createTechniqueFromGroup(context, game, GroupType.Box, box);
                if (technique != null) {
                    return technique;
                }
            }
        }

        for (CellGroup row : game.getCells().getRows()) {
            for (int i = 1; i <= CellCollection.SUDOKU_SIZE; i++) {
                technique = createTechniqueFromGroup(context, game, GroupType.Row, row);
                if (technique != null) {
                    return technique;
                }
            }
        }

        for (CellGroup column : game.getCells().getColumns()) {
            for (int i = 1; i <= CellCollection.SUDOKU_SIZE; i++) {
                technique = createTechniqueFromGroup(context, game, GroupType.Column, column);
                if (technique != null) {
                    return technique;
                }
            }
        }

        return null;
    }

    public static HiddenSingleFromNotesTechnique[] createAll(Context context, SudokuGame game) {

        HiddenSingleFromNotesTechnique technique = null;
        ArrayList<HiddenSingleFromNotesTechnique> techniques = new ArrayList<HiddenSingleFromNotesTechnique>();

        for (CellGroup box : game.getCells().getSectors()) {
            for (int i = 1; i <= CellCollection.SUDOKU_SIZE; i++) {
                technique = createTechniqueFromGroup(context, game, GroupType.Box, box);
                if (technique != null) {
                    techniques.add(technique);
                }
            }
        }

        for (CellGroup row : game.getCells().getRows()) {
            for (int i = 1; i <= CellCollection.SUDOKU_SIZE; i++) {
                technique = createTechniqueFromGroup(context, game, GroupType.Row, row);
                if (technique != null) {
                    techniques.add(technique);
                }
            }
        }

        for (CellGroup column : game.getCells().getColumns()) {
            for (int i = 1; i <= CellCollection.SUDOKU_SIZE; i++) {
                technique = createTechniqueFromGroup(context, game, GroupType.Column, column);
                if (technique != null) {
                    techniques.add(technique);
                }
            }
        }

        return techniques.toArray(new HiddenSingleFromNotesTechnique[0]);
    }

    GroupType mGroup;
    int mRow = 0;
    int mColumn = 0;
    int mValue = 0;

    HiddenSingleFromNotesTechnique(Context context, GroupType type, int row, int column, int value) {
        super(context);

        mGroup = type;
        mRow = row;
        mColumn = column;
        mValue = value;

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_hidden_single_from_notes_step_1),
                (board) -> {}));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_hidden_single_from_notes_step_2, mValue),
                (board) -> {
                    TechniqueHelpers.highlightNotes(board.getCells(), mValue, mHighlightOverrides);
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_hidden_single_from_notes_step_3, TechniqueHelpers.getGroupString(mContext, mGroup), TechniqueHelpers.getGroupIndex(mGroup, mRow, mColumn) + 1),
                (board) -> {
                    CellGroup group = TechniqueHelpers.getGroup(board.getCells().getCell(mRow, mColumn), mGroup);
                    TechniqueHelpers.highlightGroup(group, mHighlightOverrides);
                    TechniqueHelpers.highlightNotesInGroup(group, mValue, mHighlightOverrides);
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_hidden_single_from_notes_step_4, mValue, TechniqueHelpers.getGroupString(mContext, mGroup), TechniqueHelpers.getGroupIndex(mGroup, mRow, mColumn) + 1, mValue),
                (board) -> {
                    HighlightOptions options = new HighlightOptions();
                    options.setNoteHighlightMode(mValue, HighlightMode.HIGHLIGHT);
                    mHighlightOverrides.put(board.getCells().getCell(mRow, mColumn), options);
                }));
    }

    @Override
    public void applyTechnique(SudokuGame game) {
        game.getCommandStack().execute(new SetCellValueCommand(game.getCells().getCell(mRow, mColumn), mValue));
    }

    @Override
    public String getName() {
        return mContext.getString(R.string.technique_hidden_single_from_notes_title);
    }
}
