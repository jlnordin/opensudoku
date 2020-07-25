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

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NakedSingleTechnique extends AbstractTechnique {

    public static NakedSingleTechnique create(Context context, SudokuGame game) {

        int value = 0;
        Cell cell = null;

        OuterLoop:
        for (CellGroup box : game.getCells().getSectors()) {
            for (Cell cellToCheck : box.getCells()) {
                ArrayList<Integer> candidates = TechniqueHelpers.getCandidatesForCell(cellToCheck);
                if (candidates.size() == 1) {
                    cell = cellToCheck;
                    value = candidates.get(0);
                    break OuterLoop;
                }
            }
        }

        if (cell != null) {
            return new NakedSingleTechnique(context, cell.getRowIndex(), cell.getColumnIndex(), value);
        } else {
            return null;
        }
    }

    public static NakedSingleTechnique[] createAll(Context context, SudokuGame game) {

        ArrayList<NakedSingleTechnique> techniques = new ArrayList<NakedSingleTechnique>();
        for (CellGroup box : game.getCells().getSectors()) {
            for (Cell cellToCheck : box.getCells()) {
                ArrayList<Integer> candidates = TechniqueHelpers.getCandidatesForCell(cellToCheck);
                if (candidates.size() == 1) {
                    techniques.add(new NakedSingleTechnique(context, cellToCheck.getRowIndex(), cellToCheck.getColumnIndex(), candidates.get(0)));
                }
            }
        }

        return techniques.toArray(new NakedSingleTechnique[0]);
    }

    int mRow = 0;
    int mColumn = 0;
    int mValue = 0;

    NakedSingleTechnique(Context context, int row, int column, int value) {
        super(context);

        mRow = row;
        mColumn = column;
        mValue = value;

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_naked_single_step_1),
                (board) -> {}));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_naked_single_step_2),
                (board) -> {
                    Cell cell = board.getCells().getCell(mRow, mColumn);
                    TechniqueHelpers.highlightGroup(cell.getRow(), mHighlightOverrides);
                    TechniqueHelpers.highlightGroup(cell.getColumn(), mHighlightOverrides);
                    TechniqueHelpers.highlightGroup(cell.getSector(), mHighlightOverrides);
                    mHighlightOverrides.put(cell, new HighlightOptions(HighlightMode.HIGHLIGHT));
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_naked_single_step_3),
                (board) -> {
                    Cell cell = board.getCells().getCell(mRow, mColumn);
                    for (Cell cellToHighlight : cell.getRow().getCells()) {
                        if (cellToHighlight.getValue() != 0) {
                            mHighlightOverrides.put(cellToHighlight, new HighlightOptions());
                        } else {
                            mHighlightOverrides.put(cellToHighlight, new HighlightOptions(HighlightMode.EMPHASIZE));
                        }
                    }

                    for (Cell cellToHighlight : cell.getColumn().getCells()) {
                        if (cellToHighlight.getValue() != 0) {
                            mHighlightOverrides.put(cellToHighlight, new HighlightOptions());
                        } else {
                            mHighlightOverrides.put(cellToHighlight, new HighlightOptions(HighlightMode.EMPHASIZE));
                        }
                    }

                    for (Cell cellToHighlight : cell.getSector().getCells()) {
                        if (cellToHighlight.getValue() != 0) {
                            mHighlightOverrides.put(cellToHighlight, new HighlightOptions());
                        } else {
                            mHighlightOverrides.put(cellToHighlight, new HighlightOptions(HighlightMode.EMPHASIZE));
                        }
                    }

                    mHighlightOverrides.remove(board.getCells().getCell(mRow, mColumn));
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_naked_single_step_4, mValue),
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
        return mContext.getString(R.string.technique_naked_single_title);
    }
}
