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
class NakedSingleFromNotesTechniqueTest {

    @Mock
    Context mContext;

    @Test
    void create() {
        SudokuGame game = TechniqueTestHelpers.createGameWithNakedSingle();
        game.getCommandStack().execute(new FillInNotesCommand());
        assertNotNull(NakedSingleFromNotesTechnique.create(mContext, game));
    }

    @Test
    void create_returnNullIfNoNakedSingle() {
        assertNull(NakedSingleFromNotesTechnique.create(mContext, TechniqueTestHelpers.createSolvedGame()));
    }
}