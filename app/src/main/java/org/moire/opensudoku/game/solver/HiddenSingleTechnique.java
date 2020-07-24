package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.CellGroup;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.SetCellValueCommand;
import org.moire.opensudoku.gui.HighlightOptions;
import org.moire.opensudoku.game.solver.TechniqueHelpers.GroupType;
import org.moire.opensudoku.gui.HighlightOptions.HighlightMode;

import java.util.ArrayList;

public class HiddenSingleTechnique extends AbstractTechnique {

    static HiddenSingleTechnique createTechniqueFromGroup(Context context, SudokuGame game, GroupType groupType, CellGroup group) {
        for (int i = 1; i <= CellCollection.SUDOKU_SIZE; i++) {
            ArrayList<Cell> candidates = TechniqueHelpers.getCellsWithCandidateValue(group, i);
            if (candidates.size() == 1) {
                return new HiddenSingleTechnique(
                        context,
                        groupType,
                        candidates.get(0).getRowIndex(),
                        candidates.get(0).getColumnIndex(),
                        i);
            }
        }
        return null;
    }

    public static HiddenSingleTechnique create(Context context, SudokuGame game) {

        HiddenSingleTechnique technique = null;

        for (CellGroup box : game.getCells().getSectors()) {
            technique = createTechniqueFromGroup(context, game, GroupType.Box, box);
            if (technique != null) {
                return technique;
            }
        }

        for (CellGroup row : game.getCells().getRows()) {
            technique = createTechniqueFromGroup(context, game, GroupType.Row, row);
            if (technique != null) {
                return technique;
            }
        }

        for (CellGroup column : game.getCells().getColumns()) {
            technique = createTechniqueFromGroup(context, game, GroupType.Column, column);
            if (technique != null) {
                return technique;
            }
        }

        return null;
    }

    public static HiddenSingleTechnique[] createAll(Context context, SudokuGame game) {

        HiddenSingleTechnique technique = null;
        ArrayList<HiddenSingleTechnique> techniques = new ArrayList<HiddenSingleTechnique>();

        for (CellGroup box : game.getCells().getSectors()) {
            technique = createTechniqueFromGroup(context, game, GroupType.Box, box);
            if (technique != null) {
                techniques.add(technique);
            }
        }

        for (CellGroup row : game.getCells().getRows()) {
            technique = createTechniqueFromGroup(context, game, GroupType.Row, row);
            if (technique != null) {
                techniques.add(technique);
            }
        }

        for (CellGroup column : game.getCells().getColumns()) {
            technique = createTechniqueFromGroup(context, game, GroupType.Column, column);
            if (technique != null) {
                techniques.add(technique);
            }
        }

        return techniques.toArray(new HiddenSingleTechnique[0]);
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
                (board) -> {}));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_hidden_single_step_2, TechniqueHelpers.getGroupString(mContext, mGroup), TechniqueHelpers.getGroupIndex(mGroup, mRow, mColumn) + 1),
                (board) -> {
                    TechniqueHelpers.highlightGroup(TechniqueHelpers.getGroup(board.getCells().getCell(mRow, mColumn), mGroup), mHighlightOverrides);
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_hidden_single_step_3, mValue, TechniqueHelpers.getGroupString(mContext, mGroup), TechniqueHelpers.getGroupIndex(mGroup, mRow, mColumn) + 1),
                (board) -> {
                    CellGroup highlightedGroup = TechniqueHelpers.getGroup(board.getCells().getCell(mRow, mColumn), mGroup);
                    // highlight each group (row, column, box) and number within that group that
                    // contributes to eliminating "mValue" as a possibility.
                    for (Cell cell : highlightedGroup.getCells()) {
                        if (cell.getValue() != 0) {
                            continue;
                        }

                        if (cell.getRow().containsValue(mValue)) {
                            for (Cell cellInRow : cell.getRow().getCells()) {
                                if (cellInRow.getValue() == mValue) {
                                    mHighlightOverrides.put(cellInRow, new HighlightOptions(HighlightMode.HIGHLIGHT));
                                } else {
                                    mHighlightOverrides.put(cellInRow, new HighlightOptions(HighlightMode.EMPHASIZE));
                                }
                            }
                        }

                        if (cell.getColumn().containsValue(mValue)) {
                            for (Cell cellInColumn : cell.getColumn().getCells()) {
                                if (cellInColumn.getValue() == mValue) {
                                    mHighlightOverrides.put(cellInColumn, new HighlightOptions(HighlightMode.HIGHLIGHT));
                                } else {
                                    mHighlightOverrides.put(cellInColumn, new HighlightOptions(HighlightMode.EMPHASIZE));
                                }
                            }
                        }

                        if (cell.getSector().containsValue(mValue)) {
                            for (Cell cellInBox : cell.getSector().getCells()) {
                                if (cellInBox.getValue() == mValue) {
                                    mHighlightOverrides.put(cellInBox, new HighlightOptions(HighlightMode.HIGHLIGHT));
                                } else {
                                    mHighlightOverrides.put(cellInBox, new HighlightOptions(HighlightMode.EMPHASIZE));
                                }
                            }
                        }
                    }

                    // in a second pass, make sure all the non-0 cells in the group are highlighted
                    // since they contribute to eliminating possible locations
                    for (Cell cell : highlightedGroup.getCells()) {
                        if (cell.getValue() != 0) {
                            mHighlightOverrides.put(cell, new HighlightOptions(HighlightMode.HIGHLIGHT));
                        }
                    }

                    // finally remove any highlighting from the cell that contains the hidden single
                    mHighlightOverrides.remove(board.getCells().getCell(mRow, mColumn));
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_hidden_single_step_4, mValue),
                (board) -> {
                    mHighlightOverrides.put(board.getCells().getCell(mRow, mColumn), new HighlightOptions());
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
