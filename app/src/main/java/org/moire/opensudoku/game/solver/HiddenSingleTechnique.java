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
import org.moire.opensudoku.gui.HighlightOptions.HighlightMode;

import java.util.ArrayList;

public class HiddenSingleTechnique extends AbstractTechnique {

    public static HiddenSingleTechnique create(Context context, SudokuGame game) {

        GroupType type = GroupType.Box;
        int value = 0;
        Cell cell = null;

        for (CellGroup box : game.getCells().getSectors()) {
            for (int i = 1; i <= CellCollection.SUDOKU_SIZE; i++) {
                ArrayList<Cell> candidates = TechniqueHelpers.getCellCandidates(box, i);
                if (candidates.size() == 1) {
                    cell = candidates.get(0);
                    value = i;
                    type = GroupType.Box;
                    break;
                }
            }
        }

        if (cell == null) {
            for (CellGroup row : game.getCells().getRows()) {
                for (int i = 1; i <= CellCollection.SUDOKU_SIZE; i++) {
                    ArrayList<Cell> candidates = TechniqueHelpers.getCellCandidates(row, i);
                    if (candidates.size() == 1) {
                        cell = candidates.get(0);
                        value = i;
                        type = GroupType.Box;
                        break;
                    }
                }
            }
        }

        if (cell == null) {
            for (CellGroup column : game.getCells().getColumns()) {
                for (int i = 1; i <= CellCollection.SUDOKU_SIZE; i++) {
                    ArrayList<Cell> candidates = TechniqueHelpers.getCellCandidates(column, i);
                    if (candidates.size() == 1) {
                        cell = candidates.get(0);
                        value = i;
                        type = GroupType.Box;
                        break;
                    }
                }
            }
        }

        if (cell != null) {
            return new HiddenSingleTechnique(context, type, cell.getRowIndex(), cell.getColumnIndex(), value);
        } else {
            return null;
        }
    }

    GroupType mGroup;
    int mRow = 0;
    int mColumn = 0;
    int mValue = 0;

    HiddenSingleTechnique(Context context, GroupType type, int row, int column, int value) {
        super(context);

        mGroup = type;
        mRow = row;
        mColumn = column;
        mValue = value;

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_hidden_single_step_1),
                (board) -> {
                    mHighlightOverrides.clear();
                    board.invalidate();
                }));
        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_hidden_single_step_2, TechniqueHelpers.getGroupString(mContext, mGroup), TechniqueHelpers.getGroupIndex(mGroup, mRow, mColumn) + 1),
                (board) -> {
                    TechniqueHelpers.highlightGroup(TechniqueHelpers.getGroup(board.getCells().getCell(mRow, mColumn), mGroup), mHighlightOverrides);
                    board.invalidate();
                }));
        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_hidden_single_step_3, mValue, TechniqueHelpers.getGroupString(mContext, mGroup), TechniqueHelpers.getGroupIndex(mGroup, mRow, mColumn) + 1),
                (board) -> {
                    CellGroup highlightedGroup = TechniqueHelpers.getGroup(board.getCells().getCell(mRow, mColumn), mGroup);
                    // highlight each group (row, column, box) and number within that group that
                    // contributes to eliminating "mValue" as a possibility.
                    for (Cell cell : highlightedGroup.getCells()) {
                        if (cell.getRow().containsValue(mValue)) {
                            for (Cell cellInRow : cell.getRow().getCells()) {
                                if (cellInRow.getValue() == mValue) {
                                    mHighlightOverrides.put(cellInRow, new HighlightOptions(HighlightMode.SECONDARY_HIGHLIGHT));
                                } else {
                                    mHighlightOverrides.put(cellInRow, new HighlightOptions(HighlightMode.NORMAL));
                                }
                            }
                        }

                        if (cell.getColumn().containsValue(mValue)) {
                            for (Cell cellInColumn : cell.getColumn().getCells()) {
                                if (cellInColumn.getValue() == mValue) {
                                    mHighlightOverrides.put(cellInColumn, new HighlightOptions(HighlightMode.SECONDARY_HIGHLIGHT));
                                } else {
                                    mHighlightOverrides.put(cellInColumn, new HighlightOptions(HighlightMode.NORMAL));
                                }
                            }
                        }

                        if (cell.getSector().containsValue(mValue)) {
                            for (Cell cellInBox : cell.getSector().getCells()) {
                                if (cellInBox.getValue() == mValue) {
                                    mHighlightOverrides.put(cellInBox, new HighlightOptions(HighlightMode.SECONDARY_HIGHLIGHT));
                                } else {
                                    mHighlightOverrides.put(cellInBox, new HighlightOptions(HighlightMode.NORMAL));
                                }
                            }
                        }
                    }
                    TechniqueHelpers.highlightGroup(highlightedGroup, mHighlightOverrides);
                    board.invalidate();
                }));
        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_hidden_single_step_4, mValue),
                (board) -> {
                    mHighlightOverrides.clear();
                    mHighlightOverrides.put(board.getCells().getCell(mRow, mColumn), new HighlightOptions());
                    board.invalidate();
                }));
    }

    @Override
    public void applyTechnique(SudokuGame game) {
        game.getCommandStack().execute(new SetCellValueCommand(game.getCells().getCell(mRow, mColumn), mValue));
    }

    @Override
    public String getName() {
        return mContext.getString(R.string.technique_hidden_single_title);
    }
}
