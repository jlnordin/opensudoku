package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.CellGroup;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.SetCellValueCommand;
import org.moire.opensudoku.gui.HighlightOptions;
import org.moire.opensudoku.gui.SudokuBoardView;
import org.moire.opensudoku.game.solver.TechniqueHelpers.GroupType;

public class FullHouseTechnique extends AbstractTechnique {

    static Cell checkGroupForFullHouse(CellGroup group) {
        Cell fullHouseCandidate = null;
        for (Cell cell : group.getCells()) {
            if (cell.getValue() == 0) {
                if (fullHouseCandidate != null) {
                    // if we already have a candidate, then this is the second cell without a value
                    // in the group, meaning this group is not a full house
                    return null;
                }
                fullHouseCandidate = cell;
            }
        }

        return fullHouseCandidate;
    }

    static int getFullHouseValue(Cell cell, GroupType type) {
        CellGroup group;
        switch (type) {
            default:
            case Box:
                group = cell.getSector();
                break;

            case Row:
                group = cell.getRow();
                break;

            case Column:
                group = cell.getColumn();
                break;
        }

        for (int i = 1; i <= CellCollection.SUDOKU_SIZE; i++) {
            if (group.doesNotContain(i)) {
                return i;
            }
        }

        return 0;
    }

    public static FullHouseTechnique create(Context context, SudokuGame game) {

        GroupType type = GroupType.Box;
        Cell candidate = null;

        for (CellGroup box : game.getCells().getSectors()) {
            candidate = checkGroupForFullHouse(box);
            if (candidate != null) {
                type = GroupType.Box;
                break;
            }
        }

        if (candidate == null) {
            for (CellGroup row : game.getCells().getRows()) {
                candidate = checkGroupForFullHouse(row);
                if (candidate != null) {
                    type = GroupType.Row;
                    break;
                }
            }
        }

        if (candidate == null) {
            for (CellGroup column : game.getCells().getColumns()) {
                candidate = checkGroupForFullHouse(column);
                if (candidate != null) {
                    type = GroupType.Column;
                    break;
                }
            }
        }

        if (candidate != null) {
            return new FullHouseTechnique(context, type, candidate.getRowIndex(), candidate.getColumnIndex(), getFullHouseValue(candidate, type));
        } else {
            return null;
        }
    }

    GroupType mType;
    int mRow = 0;
    int mColumn = 0;
    int mValue = 0;

    FullHouseTechnique(Context context, GroupType type, int row, int column, int value) {
        super(context);

        mType = type;
        mRow = row;
        mColumn = column;
        mValue = value;

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_full_house_step_1),
                (board) -> {
                    mHighlightOverrides.clear();
                    board.invalidate();
                }));
        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_full_house_step_2, getGroupString(), getGroupIndex() + 1),
                (board) -> {
                    TechniqueHelpers.highlightGroup(TechniqueHelpers.getGroup(board.getCells().getCell(mRow, mColumn), mType), mHighlightOverrides);
                    board.invalidate();
                }));
        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_full_house_step_3, getGroupString(), getGroupIndex() + 1, mValue),
                (board) -> {
                    TechniqueHelpers.highlightGroup(TechniqueHelpers.getGroup(board.getCells().getCell(mRow, mColumn), mType), mHighlightOverrides);
                    board.invalidate();
                }));
    }

    String getGroupString() {
        return TechniqueHelpers.getGroupString(mContext, mType);
    }

    int getGroupIndex()
    {
        return TechniqueHelpers.getGroupIndex(mType, mRow, mColumn);
    }

    @Override
    public void applyTechnique(SudokuGame game) {
        game.getCommandStack().execute(new SetCellValueCommand(game.getCells().getCell(mRow, mColumn), mValue));
    }

    @Override
    public String getName() {
        return mContext.getString(R.string.technique_full_house_title);
    }
}
