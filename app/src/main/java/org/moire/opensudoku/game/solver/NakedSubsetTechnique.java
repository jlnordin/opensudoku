package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.CellGroup;
import org.moire.opensudoku.game.CellNote;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.EditCellNoteCommand;
import org.moire.opensudoku.game.command.SetCellValueCommand;
import org.moire.opensudoku.gui.HighlightOptions;
import org.moire.opensudoku.gui.HighlightOptions.HighlightMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NakedSubsetTechnique extends AbstractTechnique {

    public static NakedSubsetTechnique createPair(Context context, SudokuGame game) {

        final int cardinality = 2;

        Cell[] cellsWithNakedSubset = new Cell[CellCollection.SUDOKU_SIZE];

        OuterLoop:
        for (CellGroup box : game.getCells().getSectors()) {

            List<Cell> allCellsInGroup = Arrays.asList(box.getCells());
            ArrayList<Cell> cellsToCheck = new ArrayList<Cell>();

            for (Cell cell : allCellsInGroup) {
                if (cell.getValue() == 0 && cell.getNote().getNotedNumbersCount() <= cardinality) {
                    cellsToCheck.add(cell);
                }
            }

            if (cellsToCheck.size() <= cardinality) {
                continue;
            }

            // Iterate through all subsets of the cellsToCheck by using a subset bit mask. As an
            // example, if we had 4 cells to check and we were looking for every unique pair of
            // those cells, we could iterate through all numbers from 0 to 2^4 (16), stopping for
            // values that have exactly two 1's in the binary representation. A value of 0101 would
            // mean "compare cellsToCheck[0] with cellsToCheck[2]", where a value of 1100 would mean
            // "compare cellsToCheck[2] with cellsToCheck[3]".
            for (int subSetMask = 0; subSetMask < (1 << cellsToCheck.size()); subSetMask++) {
                if (Integer.bitCount(subSetMask) == cardinality) {
                    int noteMask = 0;
                    for (int c = 0; c < cellsToCheck.size(); c++) {
                        if ((subSetMask & (1 << c)) != 0) {
                            //... use a fixed-size list helper?
                            //... maybe
                        }
                    }
                }
            }
        }

        return null;
    }

    int[] mRows;
    int[] mColumns;
    int[] mNotesToRemove;

    public enum Cardinality
    {
        Pair,
        Triple,
        Quadruple
    };
    Cardinality mCardinality;

    NakedSubsetTechnique(Context context, ArrayList<Cell> cellsToRemoveNotesFrom, int[] notesToRemove) {
        super(context);

        mRows = new int[cellsToRemoveNotesFrom.size()];
        mColumns = new int[cellsToRemoveNotesFrom.size()];
        mNotesToRemove = notesToRemove;

        if (notesToRemove.length == 2) {
            mCardinality = Cardinality.Pair;
        } else if (notesToRemove.length == 3) {
            mCardinality = Cardinality.Triple;
        } else {
            mCardinality = Cardinality.Quadruple;
        }

        for (int c = 0; c < cellsToRemoveNotesFrom.size(); c++) {
            mRows[c] = cellsToRemoveNotesFrom.get(c).getRowIndex();
            mColumns[c] = cellsToRemoveNotesFrom.get(c).getColumnIndex();
        }

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_naked_subset_step_1),
                (board) -> {}));
/*
        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_naked_subset_step_2, mValue),
                (board) -> {
                    HighlightOptions options = new HighlightOptions(HighlightMode.EMPHASIZE);
                    options.setNoteHighlightMode(mValue - 1, HighlightMode.HIGHLIGHT);
                    mHighlightOverrides.put(board.getCells().getCell(mRow, mColumn), options);
                }));
*/
    }

    @Override
    public void applyTechnique(SudokuGame game) {
        for (int c = 0; c < mRows.length; c++) {
            Cell cell = game.getCells().getCell(mRows[c], mColumns[c]);
            CellNote newNote = cell.getNote();

            for (int noteToRemove : mNotesToRemove) {
                newNote = newNote.removeNumber(noteToRemove);
            }

            game.getCommandStack().execute(new EditCellNoteCommand(cell, newNote));
        }
    }

    @Override
    public String getName() {
        switch (mCardinality) {
            case Pair:
                return mContext.getString(R.string.technique_naked_pair_title);
            case Triple:
                return mContext.getString(R.string.technique_naked_triple_title);
            default:
            case Quadruple:
                return mContext.getString(R.string.technique_naked_quadruple_title);
        }
    }
}
