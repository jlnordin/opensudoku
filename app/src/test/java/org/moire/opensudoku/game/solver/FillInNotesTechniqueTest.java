package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.FillInNotesCommand;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(MockitoExtension.class)
class FillInNotesTechniqueTest {

    @Mock
    Context mContext;

    @Test
    void create() {
        assertNotNull(FillInNotesTechnique.create(mContext, TechniqueTestHelpers.createGameInProgress()));
    }

    @Test
    void create_returnNullIfNotesAreComplete() {
        SudokuGame game = TechniqueTestHelpers.createGameInProgress();
        game.getCommandStack().execute(new FillInNotesCommand());
        assertNull(FillInNotesTechnique.create(mContext, game));
    }
}