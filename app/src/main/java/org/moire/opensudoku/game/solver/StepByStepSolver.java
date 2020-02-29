package org.moire.opensudoku.game.solver;

import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.command.CommandStack;

import java.util.ArrayList;

public class StepByStepSolver {

    static boolean isSolved(CellCollection cells) {
        return cells.validate() && cells.isCompleted();
    }

    static public AbstractSolutionStep getNextStep(CellCollection cells) {
        return BruteForceSolutionStep.create(cells, SudokuSolver.solve(cells));
    }

    static public AbstractSolutionStep getNextStep(CellCollection cells, ArrayList<int[]> solution) {
        if (isSolved(cells)) {
            return null;
        } else {
            return BruteForceSolutionStep.create(cells, solution);
        }
    }

    static public ArrayList<AbstractSolutionStep> getAllSolutionSteps(CellCollection cells) {
        CellCollection copyOfCells = cells.clone();
        ArrayList<AbstractSolutionStep> solutionSteps = new ArrayList<AbstractSolutionStep>();
        CommandStack commands = new CommandStack(copyOfCells);

        ArrayList<int[]> solution = SudokuSolver.solve(cells);
        while (true) {
            AbstractSolutionStep nextStep = getNextStep(copyOfCells, solution);
            if (nextStep == null) {
                break;
            }

            solutionSteps.add(nextStep);
            commands.execute(nextStep.getCommand(copyOfCells));
        }

        return solutionSteps;
    }
}
