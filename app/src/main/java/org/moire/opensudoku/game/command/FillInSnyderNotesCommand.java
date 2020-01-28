package org.moire.opensudoku.game.command;

import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.CellNote;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FillInSnyderNotesCommand extends FillInNotesCommand {

    public FillInSnyderNotesCommand() {
    }

    @Override
    void execute() {
        saveOldNotes();
        getCells().fillInSnyderNotes();
    }
}
