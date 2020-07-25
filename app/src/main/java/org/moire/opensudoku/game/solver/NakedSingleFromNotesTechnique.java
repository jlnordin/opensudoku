package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellGroup;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.SetCellValueCommand;
import org.moire.opensudoku.gui.HighlightOptions;
import org.moire.opensudoku.gui.HighlightOptions.HighlightMode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class NakedSingleFromNotesTechnique extends AbstractTechnique {

    public static NakedSingleFromNotesTechnique create(Context context, SudokuGame game) {

        for (CellGroup box : game.getCells().getSectors()) {
            for (Cell cellToCheck : box.getCells()) {
                if (cellToCheck.getValue() == 0) {
                    List<Integer> notes = cellToCheck.getNote().getNotedNumbers();
                    if (notes.size() == 1) {
                        return new NakedSingleFromNotesTechnique(context, cellToCheck.getRowIndex(), cellToCheck.getColumnIndex(), notes.get(0));
                    }
                }
            }
        }

        return null;
    }

    public static NakedSingleFromNotesTechnique[] createAll(Context context, SudokuGame game) {
        ArrayList<NakedSingleFromNotesTechnique> techniques = new ArrayList<NakedSingleFromNotesTechnique>();
        for (CellGroup box : game.getCells().getSectors()) {
            for (Cell cellToCheck : box.getCells()) {
                if (cellToCheck.getValue() == 0) {
                    List<Integer> notes = cellToCheck.getNote().getNotedNumbers();
                    if (notes.size() == 1) {
                        techniques.add(new NakedSingleFromNotesTechnique(context, cellToCheck.getRowIndex(), cellToCheck.getColumnIndex(), notes.get(0)));
                    }
                }
            }
        }
        return techniques.toArray(new NakedSingleFromNotesTechnique[0]);
    }

    int mRow = 0;
    int mColumn = 0;
    int mValue = 0;

    NakedSingleFromNotesTechnique(Context context, int row, int column, int value) {
        super(context);

        mRow = row;
        mColumn = column;
        mValue = value;

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_naked_single_from_notes_step_1),
                (board) -> {}));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_naked_single_from_notes_step_2, mValue),
                (board) -> {
                    HighlightOptions options = new HighlightOptions(HighlightMode.EMPHASIZE);
                    options.setNoteHighlightMode(mValue - 1, HighlightMode.HIGHLIGHT);
                    mHighlightOverrides.put(board.getCells().getCell(mRow, mColumn), options);
                }));
    }

    @Override
    public void applyTechnique(SudokuGame game) {
        game.getCommandStack().execute(new SetCellValueCommand(game.getCells().getCell(mRow, mColumn), mValue));
    }

    @Override
    public String getName() {
        return mContext.getString(R.string.technique_naked_single_from_notes_title);
    }
}
