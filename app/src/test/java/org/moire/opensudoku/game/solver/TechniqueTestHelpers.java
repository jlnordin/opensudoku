package org.moire.opensudoku.game.solver;

import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.SetCellValueCommand;

public class TechniqueTestHelpers {

    public static SudokuGame createGameInProgress() {
        SudokuGame game = new SudokuGame();
        game.setCells(CellCollection.createDebugGame());
        return game;
    }

    public static SudokuGame createSolvedGame() {
        SudokuGame game = new SudokuGame();
        CellCollection cells = CellCollection.fromString(
                "123"+"456"+"789"+
                "456"+"789"+"123"+
                "789"+"123"+"456"+

                "234"+"567"+"891"+
                "567"+"891"+"234"+
                "891"+"234"+"567"+

                "345"+"678"+"912"+
                "678"+"912"+"345"+
                "912"+"345"+"678"
        );
        game.setCells(cells);
        return game;
    }

    public static SudokuGame createUnsolvableGame() {
        SudokuGame game = new SudokuGame();
        CellCollection cells = CellCollection.fromString(
                "000"+"999"+"789"+
                "000"+"999"+"123"+
                "000"+"999"+"456"+

                "234"+"567"+"891"+
                "567"+"891"+"234"+
                "891"+"234"+"567"+

                "345"+"678"+"912"+
                "678"+"912"+"345"+
                "912"+"345"+"678"
        );
        game.setCells(cells);
        return game;
    }

    public static SudokuGame createGameWithMistake() {
        SudokuGame game = new SudokuGame();
        CellCollection cells = CellCollection.createDebugGame();
        game.setCells(cells);
        game.getCommandStack().execute(new SetCellValueCommand(cells.getCell(0, 0), 4));
        return game;
    }

    public static SudokuGame createGameWithFullHouses() {
        SudokuGame game = new SudokuGame();
        CellCollection cells = CellCollection.fromString(
                "352"+"006"+"180"+
                "168"+"900"+"734"+
                "049"+"803"+"625"+

                "400"+"000"+"800"+
                "083"+"201"+"590"+
                "001"+"000"+"402"+

                "097"+"305"+"240"+
                "200"+"009"+"056"+
                "000"+"100"+"970"
        );
        game.setCells(cells);
        return game;
    }
}
