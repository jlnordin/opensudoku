package org.moire.opensudoku.game.solver;

import android.content.Context;
import android.util.ArraySet;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.CellGroup;
import org.moire.opensudoku.game.CellNote;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.EditCellNoteCommand;
import org.moire.opensudoku.game.command.SetCellValueCommand;
import org.moire.opensudoku.game.solver.TechniqueHelpers.GroupType;
import org.moire.opensudoku.gui.HighlightOptions;
import org.moire.opensudoku.gui.HighlightOptions.HighlightMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LockedCandidateTechnique extends AbstractTechnique {

    public static LockedCandidateTechnique create(Context context, SudokuGame game) {

        int value = 0;
        GroupType primaryType = GroupType.Box;
        GroupType secondaryType = GroupType.Column;
        ArrayList<Cell> cellsToRemoveNoteFrom = new ArrayList<Cell>();

        // Locked Candidate Type 1 (Pointing)
        OuterLoop:
        for (CellGroup box : game.getCells().getSectors()) {
            for (int i = 1; i <= CellCollection.SUDOKU_SIZE; i++) {
                ArrayList<Cell> baseCandidates = TechniqueHelpers.getCellsWithCandidateValueFromNotes(box, i);

                for (Cell candidateCell : baseCandidates) {
                    ArrayList<Cell> secondaryCandidates = TechniqueHelpers.getCellsWithCandidateValueFromNotes(candidateCell.getColumn(), i);
                    if (secondaryCandidates.containsAll(baseCandidates)) {
                        cellsToRemoveNoteFrom.addAll(secondaryCandidates);
                        cellsToRemoveNoteFrom.removeAll(baseCandidates);

                        if (!cellsToRemoveNoteFrom.isEmpty()) {
                            value = i;
                            primaryType = GroupType.Box;
                            secondaryType = GroupType.Column;
                            break OuterLoop;
                        }
                    }

                    secondaryCandidates = TechniqueHelpers.getCellsWithCandidateValueFromNotes(candidateCell.getRow(), i);
                    if (secondaryCandidates.containsAll(baseCandidates)) {
                        cellsToRemoveNoteFrom.addAll(secondaryCandidates);
                        cellsToRemoveNoteFrom.removeAll(baseCandidates);

                        if (!cellsToRemoveNoteFrom.isEmpty()) {
                            value = i;
                            primaryType = GroupType.Box;
                            secondaryType = GroupType.Row;
                            break OuterLoop;
                        }
                    }
                }
            }
        }

        // Locked Candidate Type 2 (Claiming, row)
        if (cellsToRemoveNoteFrom.isEmpty()) {
            OuterLoop:
            for (CellGroup row : game.getCells().getRows()) {
                for (int i = 1; i <= CellCollection.SUDOKU_SIZE; i++) {
                    ArrayList<Cell> baseCandidates = TechniqueHelpers.getCellsWithCandidateValueFromNotes(row, i);

                    for (Cell candidateCell : baseCandidates) {
                        ArrayList<Cell> secondaryCandidates = TechniqueHelpers.getCellsWithCandidateValueFromNotes(candidateCell.getSector(), i);
                        if (secondaryCandidates.containsAll(baseCandidates)) {
                            cellsToRemoveNoteFrom.addAll(secondaryCandidates);
                            cellsToRemoveNoteFrom.removeAll(baseCandidates);

                            if (!cellsToRemoveNoteFrom.isEmpty()) {
                                value = i;
                                primaryType = GroupType.Row;
                                secondaryType = GroupType.Box;
                                break OuterLoop;
                            }
                        }
                    }
                }
            }
        }

        // Locked Candidate Type 2 (Claiming, column)
        if (cellsToRemoveNoteFrom.isEmpty()) {
            OuterLoop:
            for (CellGroup column : game.getCells().getColumns()) {
                for (int i = 1; i <= CellCollection.SUDOKU_SIZE; i++) {
                    ArrayList<Cell> baseCandidates = TechniqueHelpers.getCellsWithCandidateValueFromNotes(column, i);

                    for (Cell candidateCell : baseCandidates) {
                        ArrayList<Cell> secondaryCandidates = TechniqueHelpers.getCellsWithCandidateValueFromNotes(candidateCell.getSector(), i);
                        if (secondaryCandidates.containsAll(baseCandidates)) {
                            cellsToRemoveNoteFrom.addAll(secondaryCandidates);
                            cellsToRemoveNoteFrom.removeAll(baseCandidates);

                            if (!cellsToRemoveNoteFrom.isEmpty()) {
                                value = i;
                                primaryType = GroupType.Column;
                                secondaryType = GroupType.Box;
                                break OuterLoop;
                            }
                        }
                    }
                }
            }
        }

        if (!cellsToRemoveNoteFrom.isEmpty()) {
            return new LockedCandidateTechnique(context, primaryType, secondaryType, cellsToRemoveNoteFrom, value);
        } else {
            return null;
        }
    }

    GroupType mPrimaryGroup;
    GroupType mSecondaryGroup;
    int[] mRows;
    int[] mColumns;
    int mValue = 0;

    LockedCandidateTechnique(Context context, GroupType primaryType, GroupType secondaryType, ArrayList<Cell> cellsToRemoveNoteFrom, int value) {
        super(context);

        mPrimaryGroup = primaryType;
        mSecondaryGroup = secondaryType;
        mRows = new int[cellsToRemoveNoteFrom.size()];
        mColumns = new int[cellsToRemoveNoteFrom.size()];
        mValue = value;

        for (int c = 0; c < cellsToRemoveNoteFrom.size(); c++) {
            mRows[c] = cellsToRemoveNoteFrom.get(c).getRowIndex();
            mColumns[c] = cellsToRemoveNoteFrom.get(c).getColumnIndex();
        }

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_locked_candidate_step_1, mValue),
                (board) -> {}));
/*
        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_locked_candidate_step_2, TechniqueHelpers.getGroupString(mContext, mGroup), TechniqueHelpers.getGroupIndex(mGroup, mRow, mColumn) + 1),
                (board) -> {
                    TechniqueHelpers.highlightGroup(TechniqueHelpers.getGroup(board.getCells().getCell(mRow, mColumn), mGroup), mHighlightOverrides);
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_locked_candidate_step_3, mValue, TechniqueHelpers.getGroupString(mContext, mGroup), TechniqueHelpers.getGroupIndex(mGroup, mRow, mColumn) + 1),
                (board) -> {
                    CellGroup highlightedGroup = TechniqueHelpers.getGroup(board.getCells().getCell(mRow, mColumn), mGroup);
                    // highlight each group (row, column, box) and number within that group that
                    // contributes to eliminating "mValue" as a possibility.
                    for (Cell cell : highlightedGroup.getCells()) {
                        if (cell.getValue() != 0) {
                            continue;
                        }

                        if (cell.getRow().containsValue(mValue)) {
                            for (Cell cellInRow : cell.getRow().getCells()) {
                                if (cellInRow.getValue() == mValue) {
                                    mHighlightOverrides.put(cellInRow, new HighlightOptions(HighlightMode.HIGHLIGHT));
                                } else {
                                    mHighlightOverrides.put(cellInRow, new HighlightOptions(HighlightMode.EMPHASIZE));
                                }
                            }
                        }

                        if (cell.getColumn().containsValue(mValue)) {
                            for (Cell cellInColumn : cell.getColumn().getCells()) {
                                if (cellInColumn.getValue() == mValue) {
                                    mHighlightOverrides.put(cellInColumn, new HighlightOptions(HighlightMode.HIGHLIGHT));
                                } else {
                                    mHighlightOverrides.put(cellInColumn, new HighlightOptions(HighlightMode.EMPHASIZE));
                                }
                            }
                        }

                        if (cell.getSector().containsValue(mValue)) {
                            for (Cell cellInBox : cell.getSector().getCells()) {
                                if (cellInBox.getValue() == mValue) {
                                    mHighlightOverrides.put(cellInBox, new HighlightOptions(HighlightMode.HIGHLIGHT));
                                } else {
                                    mHighlightOverrides.put(cellInBox, new HighlightOptions(HighlightMode.EMPHASIZE));
                                }
                            }
                        }
                    }

                    // in a second pass, make sure all the non-0 cells in the group are highlighted
                    // since they contribute to eliminating possible locations
                    for (Cell cell : highlightedGroup.getCells()) {
                        if (cell.getValue() != 0) {
                            mHighlightOverrides.put(cell, new HighlightOptions(HighlightMode.HIGHLIGHT));
                        }
                    }

                    // finally remove any highlighting from the cell that contains the hidden single
                    mHighlightOverrides.remove(board.getCells().getCell(mRow, mColumn));
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_locked_candidate_step_4, mValue),
                (board) -> {
                    mHighlightOverrides.put(board.getCells().getCell(mRow, mColumn), new HighlightOptions());
                }));

 */
    }

    @Override
    public void applyTechnique(SudokuGame game) {
        for (int c = 0; c < mRows.length; c++) {
            Cell cell = game.getCells().getCell(mRows[c], mColumns[c]);
            CellNote newNote = cell.getNote().removeNumber(mValue);
            game.getCommandStack().execute(new EditCellNoteCommand(cell, newNote));
        }
    }

    @Override
    public String getName() {
        return mContext.getString(R.string.technique_locked_candidate_title);
    }
}
