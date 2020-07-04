package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.moire.opensudoku.game.CellNote;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.EditCellNoteCommand;
import org.moire.opensudoku.game.command.SetCellValueCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class CheckForNotationMistakeTechniqueTest {

    @Mock
    Context mContext;

    @Test
    void create() {
        assertNotNull(CheckForNotationMistakeTechnique.create(mContext, TechniqueTestHelpers.createGameWithNotationMistake()));
    }

    @Test
    void create_returnNullIfNoMistakes() {
        assertNull(CheckForNotationMistakeTechnique.create(mContext, TechniqueTestHelpers.createGameInProgress()));
    }

    @Test
    void highlightedMistakesAreCorrect_1Mistake() {
        SudokuGame game = TechniqueTestHelpers.createGameWithNotationMistake();
        CheckForNotationMistakeTechnique technique = CheckForNotationMistakeTechnique.create(mContext, game);

        assertEquals(1, technique.mHighlightedMistakes.size());
        assertTrue(technique.mHighlightedMistakes.containsKey(game.getCells().getCell(0, 0)));
    }

    @Test
    void highlightedMistakesAreCorrect_2Mistakes() {
        SudokuGame game = TechniqueTestHelpers.createGameWithNotationMistake();
        game.getCommandStack().execute(new EditCellNoteCommand(game.getCells().getCell(0, 1), CellNote.EMPTY.addNumber(5)));
        CheckForNotationMistakeTechnique technique = CheckForNotationMistakeTechnique.create(mContext, game);

        assertEquals(2, technique.mHighlightedMistakes.size());
        assertTrue(technique.mHighlightedMistakes.containsKey(game.getCells().getCell(0, 0)));
        assertTrue(technique.mHighlightedMistakes.containsKey(game.getCells().getCell(0, 1)));
    }

    @Test
    void applyTechnique() {
        SudokuGame game = TechniqueTestHelpers.createGameWithNotationMistake();
        CheckForNotationMistakeTechnique technique = CheckForNotationMistakeTechnique.create(mContext, game);

        technique.applyTechnique(game);

        // verify applying the technique removes the incorrect note "4" in cell (0,0).
        assertFalse(game.getCells().getCell(0, 0).getNote().hasNumber(4));

        // verify the new state of the game has no mistakes
        assertNull(CheckForNotationMistakeTechnique.create(mContext, game));
    }

    void applyTechnique_multipleMistakes() {
        SudokuGame game = TechniqueTestHelpers.createGameWithNotationMistake();
        game.getCommandStack().execute(new EditCellNoteCommand(game.getCells().getCell(0, 0), CellNote.EMPTY.addNumber(1).addNumber(4).addNumber(6)));
        game.getCommandStack().execute(new EditCellNoteCommand(game.getCells().getCell(0, 1), CellNote.EMPTY.addNumber(5)));
        CheckForNotationMistakeTechnique technique = CheckForNotationMistakeTechnique.create(mContext, game);

        technique.applyTechnique(game);

        // verify applying the technique removes the incorrect notes but leaves the potentially correct notes.
        assertTrue(game.getCells().getCell(0, 0).getNote().hasNumber(1));
        assertFalse(game.getCells().getCell(0, 0).getNote().hasNumber(4));
        assertFalse(game.getCells().getCell(0, 0).getNote().hasNumber(6));
        assertFalse(game.getCells().getCell(0, 1).getNote().hasNumber(5));

        // verify the new state of the game has no mistakes
        assertNull(CheckForNotationMistakeTechnique.create(mContext, game));
    }
}