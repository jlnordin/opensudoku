package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.SetCellValueCommand;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class CheckForMistakeTechniqueTest {

    @Mock
    Context mContext;

    @Test
    void create() {
        assertNotNull(CheckForMistakeTechnique.create(mContext, TechniqueTestHelpers.createGameWithMistake()));
    }

    @Test
    void create_returnNullIfNoMistakes() {
        assertNull(CheckForMistakeTechnique.create(mContext, TechniqueTestHelpers.createGameInProgress()));
    }

    @Test
    void highlightedMistakesAreCorrect_1Mistake() {
        SudokuGame game = TechniqueTestHelpers.createGameWithMistake();
        CheckForMistakeTechnique technique = CheckForMistakeTechnique.create(mContext, game);

        assertEquals(1, technique.mHighlightedMistakes.size());
        assertTrue(technique.mHighlightedMistakes.containsKey(game.getCells().getCell(0, 0)));
    }

    @Test
    void highlightedMistakesAreCorrect_2Mistakes() {
        SudokuGame game = TechniqueTestHelpers.createGameWithMistake();
        game.getCommandStack().execute(new SetCellValueCommand(game.getCells().getCell(0, 1), 5));
        CheckForMistakeTechnique technique = CheckForMistakeTechnique.create(mContext, game);

        assertEquals(2, technique.mHighlightedMistakes.size());
        assertTrue(technique.mHighlightedMistakes.containsKey(game.getCells().getCell(0, 0)));
        assertTrue(technique.mHighlightedMistakes.containsKey(game.getCells().getCell(0, 1)));
    }

    @Test
    void applyTechnique() {
        SudokuGame game = TechniqueTestHelpers.createGameWithMistake();
        game.getCommandStack().execute(new SetCellValueCommand(game.getCells().getCell(0, 1), 5));
        CheckForMistakeTechnique technique = CheckForMistakeTechnique.create(mContext, game);

        technique.applyTechnique(game);

        // verify applying the technique resets cells (0,0) and (0,1) to 0 (no value).
        assertEquals(0, game.getCells().getCell(0, 0).getValue());
        assertEquals(0, game.getCells().getCell(0, 1).getValue());

        // verify the new state of the game has no mistakes
        assertNull(CheckForMistakeTechnique.create(mContext, game));
    }
}