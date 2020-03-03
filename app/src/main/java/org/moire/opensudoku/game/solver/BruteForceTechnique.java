package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.command.AbstractCellCommand;
import org.moire.opensudoku.game.command.SetCellValueCommand;
import org.moire.opensudoku.gui.HighlightOptions;

import java.util.ArrayList;

public class BruteForceTechnique extends AbstractTechnique {

    public static BruteForceTechnique create(Context context, CellCollection cells, ArrayList<int[]> solution) {
        return new BruteForceTechnique(context, cells, solution);
    }

    int mRow = 0;
    int mColumn = 0;
    int mValue = 0;

    BruteForceTechnique(Context context, CellCollection cells, ArrayList<int[]> solution) {
        super(context);

        for (int[] rowColVal : solution) {
            int row = rowColVal[0];
            int col = rowColVal[1];
            int val = rowColVal[2];
            Cell cell = cells.getCell(row, col);

            if (cell.getValue() != val) {
                mRow = row;
                mColumn = col;
                mValue = val;
                break;
            }
        }

        mExplanationSteps.add(new Explanation(
                context.getString(R.string.technique_brute_force_step_1),
                (board) -> {
                    mHighlightOverrides.clear();
                    board.invalidate();
                }));
        mExplanationSteps.add(new Explanation(
                context.getString(R.string.technique_brute_force_step_2, mRow + 1, mColumn + 1, mValue),
                (board) -> {
                    mHighlightOverrides.put(board.getCells().getCell(mRow, mColumn), new HighlightOptions());
                    board.invalidate();
                }));
    }

    @Override
    public AbstractCellCommand getCommand(CellCollection cells) {
        return new SetCellValueCommand(cells.getCell(mRow, mColumn), mValue);
    }

    @Override
    public String getName() {
        return  mContext.getString(R.string.technique_brute_force_title);
    }
}
