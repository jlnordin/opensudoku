package org.moire.opensudoku.game.solver;

import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.command.AbstractCellCommand;
import org.moire.opensudoku.game.command.SetCellValueCommand;

import java.util.ArrayList;

public class BruteForceSolutionStep extends AbstractSolutionStep {

    public static BruteForceSolutionStep create(CellCollection cells, ArrayList<int[]> solution) {
        return new BruteForceSolutionStep(cells, solution);
    }

    int mRow = 0;
    int mColumn = 0;
    int mValue = 0;

    BruteForceSolutionStep(CellCollection cells, ArrayList<int[]> solution) {
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
    }

    @Override
    public AbstractCellCommand getCommand(CellCollection cells) {
        return new SetCellValueCommand(cells.getCell(mRow, mColumn), mValue);
    }
}
