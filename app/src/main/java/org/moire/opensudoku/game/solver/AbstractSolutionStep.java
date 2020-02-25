package org.moire.opensudoku.game.solver;

import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.command.AbstractCellCommand;

public abstract class AbstractSolutionStep {

    AbstractSolutionStep() {
    }

    public abstract AbstractCellCommand getCommand(CellCollection cells);
}
