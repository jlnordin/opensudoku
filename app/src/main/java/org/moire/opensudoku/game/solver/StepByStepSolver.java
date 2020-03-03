package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.command.CommandStack;

import java.util.ArrayList;

public class StepByStepSolver {

    static boolean isSolved(CellCollection cells) {
        return cells.validate() && cells.isCompleted();
    }

    static public AbstractTechnique getNextTechnique(Context context, CellCollection cells) {
        return BruteForceTechnique.create(context, cells, SudokuSolver.solve(cells));
    }

    static public AbstractTechnique getNextTechnique(Context context, CellCollection cells, ArrayList<int[]> solution) {
        if (isSolved(cells)) {
            return null;
        } else {
            return BruteForceTechnique.create(context, cells, solution);
        }
    }

    static public ArrayList<AbstractTechnique> getAllSolutionTechniques(Context context, CellCollection cells) {
        CellCollection copyOfCells = cells.clone();
        ArrayList<AbstractTechnique> solutionTechniques = new ArrayList<AbstractTechnique>();
        CommandStack commands = new CommandStack(copyOfCells);

        ArrayList<int[]> solution = SudokuSolver.solve(cells);
        while (true) {
            AbstractTechnique nextTechnique = getNextTechnique(context, copyOfCells, solution);
            if (nextTechnique == null) {
                break;
            }

            solutionTechniques.add(nextTechnique);
            commands.execute(nextTechnique.getCommand(copyOfCells));
        }

        return solutionTechniques;
    }
}
