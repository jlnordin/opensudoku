package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.FillInNotesCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(MockitoExtension.class)
class HiddenSingleFromNotesTechniqueTest {

    @Mock
    Context mContext;

    @Test
    void create() {
        SudokuGame game = TechniqueTestHelpers.createGameWithHiddenSingleInColumn();
        game.getCommandStack().execute(new FillInNotesCommand());

        HiddenSingleFromNotesTechnique technique = HiddenSingleFromNotesTechnique.create(mContext, game);
        assertNotNull(technique);
        assertEquals(TechniqueHelpers.GroupType.Column, technique.mGroup);

        game = TechniqueTestHelpers.createGameWithHiddenSingleInBox();
        game.getCommandStack().execute(new FillInNotesCommand());
        technique = HiddenSingleFromNotesTechnique.create(mContext, game);
        assertNotNull(technique);
        assertEquals(TechniqueHelpers.GroupType.Box, technique.mGroup);
    }

    @Test
    void create_returnNullIfNoHiddenSingle() {
        assertNull(HiddenSingleFromNotesTechnique.create(mContext, TechniqueTestHelpers.createSolvedGame()));
    }
}