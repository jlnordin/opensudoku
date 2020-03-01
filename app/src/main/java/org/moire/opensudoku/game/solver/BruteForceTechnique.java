package org.moire.opensudoku.game.solver;

import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.command.AbstractCellCommand;
import org.moire.opensudoku.game.command.SetCellValueCommand;

import java.util.ArrayList;

public class BruteForceTechnique extends AbstractTechnique {

    public static BruteForceTechnique create(CellCollection cells, ArrayList<int[]> solution) {
        return new BruteForceTechnique(cells, solution);
    }

    int mRow = 0;
    int mColumn = 0;
    int mValue = 0;

    BruteForceTechnique(CellCollection cells, ArrayList<int[]> solution) {
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
                "This puzzle is really hard!\n\nThe best hint is to simply reveal the correct number for one of the cells.",
                (board) -> {}));
        mExplanationSteps.add(new Explanation(
                String.format("The correct number for row %d, column %d is %d.", mRow + 1, mColumn + 1, mValue),
                (board) -> {}));
    }

    @Override
    public AbstractCellCommand getCommand(CellCollection cells) {
        return new SetCellValueCommand(cells.getCell(mRow, mColumn), mValue);
    }

    @Override
    public String getName() {
        return "Brute Force";
    }
}
