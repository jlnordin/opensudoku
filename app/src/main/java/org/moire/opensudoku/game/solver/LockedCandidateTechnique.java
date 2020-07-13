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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LockedCandidateTechnique extends AbstractTechnique {

    public static LockedCandidateTechnique create(Context context, SudokuGame game) {

        int value = 0;
        GroupType primaryType = GroupType.Box;
        int primaryIndex = 0;
        GroupType secondaryType = GroupType.Column;
        int secondaryIndex = 0;
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
                            primaryIndex = TechniqueHelpers.getGroupIndex(primaryType, candidateCell.getRowIndex(), candidateCell.getColumnIndex());
                            secondaryType = GroupType.Column;
                            secondaryIndex = TechniqueHelpers.getGroupIndex(secondaryType, candidateCell.getRowIndex(), candidateCell.getColumnIndex());
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
                            primaryIndex = TechniqueHelpers.getGroupIndex(primaryType, candidateCell.getRowIndex(), candidateCell.getColumnIndex());
                            secondaryType = GroupType.Row;
                            secondaryIndex = TechniqueHelpers.getGroupIndex(secondaryType, candidateCell.getRowIndex(), candidateCell.getColumnIndex());
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
                                primaryIndex = TechniqueHelpers.getGroupIndex(primaryType, candidateCell.getRowIndex(), candidateCell.getColumnIndex());
                                secondaryType = GroupType.Box;
                                secondaryIndex = TechniqueHelpers.getGroupIndex(secondaryType, candidateCell.getRowIndex(), candidateCell.getColumnIndex());
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
                                primaryIndex = TechniqueHelpers.getGroupIndex(primaryType, candidateCell.getRowIndex(), candidateCell.getColumnIndex());
                                secondaryType = GroupType.Box;
                                secondaryIndex= TechniqueHelpers.getGroupIndex(secondaryType, candidateCell.getRowIndex(), candidateCell.getColumnIndex());
                                break OuterLoop;
                            }
                        }
                    }
                }
            }
        }

        if (!cellsToRemoveNoteFrom.isEmpty()) {
            return new LockedCandidateTechnique(context, primaryType, primaryIndex, secondaryType, secondaryIndex, cellsToRemoveNoteFrom, value);
        } else {
            return null;
        }
    }

    GroupType mPrimaryGroup;
    int mPrimaryIndex = 0;
    GroupType mSecondaryGroup;
    int mSecondaryIndex = 0;
    int[] mRows;
    int[] mColumns;
    int mValue = 0;

    LockedCandidateTechnique(Context context, GroupType primaryType, int primaryIndex, GroupType secondaryType, int secondaryIndex, ArrayList<Cell> cellsToRemoveNoteFrom, int value) {
        super(context);

        mPrimaryGroup = primaryType;
        mPrimaryIndex = primaryIndex;
        mSecondaryGroup = secondaryType;
        mSecondaryIndex = secondaryIndex;
        mRows = new int[cellsToRemoveNoteFrom.size()];
        mColumns = new int[cellsToRemoveNoteFrom.size()];
        mValue = value;

        for (int c = 0; c < cellsToRemoveNoteFrom.size(); c++) {
            mRows[c] = cellsToRemoveNoteFrom.get(c).getRowIndex();
            mColumns[c] = cellsToRemoveNoteFrom.get(c).getColumnIndex();
        }

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_locked_candidate_step_1, mValue),
                (board) -> {
                    TechniqueHelpers.highlightNotes(board.getCells(), mValue, mHighlightOverrides);
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_locked_candidate_step_2,
                        TechniqueHelpers.getGroupString(mContext, mPrimaryGroup),
                        mPrimaryIndex + 1,
                        TechniqueHelpers.getGroupString(mContext, mSecondaryGroup),
                        mSecondaryIndex + 1),
                (board) -> {
                    CellGroup primaryGroup = TechniqueHelpers.getGroupFromIndex(board.getCells(), mPrimaryGroup, mPrimaryIndex);
                    CellGroup secondaryGroup = TechniqueHelpers.getGroupFromIndex(board.getCells(), mSecondaryGroup, mSecondaryIndex);
                    TechniqueHelpers.highlightGroup(primaryGroup, mHighlightOverrides);
                    TechniqueHelpers.highlightGroup(secondaryGroup, mHighlightOverrides);
                    for (Cell cell : secondaryGroup.getCells()) {
                        if (cell.getValue() == 0 && cell.getNote().hasNumber(mValue)) {
                            mHighlightOverrides.get(cell).setNoteHighlightMode(mValue - 1, HighlightMode.HIGHLIGHT);
                        }
                    }
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_locked_candidate_step_3,
                        mValue,
                        TechniqueHelpers.getGroupString(mContext, mPrimaryGroup),
                        mPrimaryIndex + 1,
                        TechniqueHelpers.getGroupString(mContext, mSecondaryGroup),
                        mSecondaryIndex + 1),
                (board) -> {
                    CellGroup primaryGroup = TechniqueHelpers.getGroupFromIndex(board.getCells(), mPrimaryGroup, mPrimaryIndex);
                    CellGroup secondaryGroup = TechniqueHelpers.getGroupFromIndex(board.getCells(), mSecondaryGroup, mSecondaryIndex);
                    TechniqueHelpers.highlightGroup(primaryGroup, mHighlightOverrides);
                    TechniqueHelpers.highlightGroup(secondaryGroup, mHighlightOverrides);
                    for (Cell cell : CellGroup.intersection(primaryGroup, secondaryGroup)) {
                        if (cell.getValue() == 0 && cell.getNote().hasNumber(mValue)) {
                            mHighlightOverrides.get(cell).setNoteHighlightMode(mValue - 1, HighlightMode.HIGHLIGHT);
                        }
                    }
                }));

        mExplanationSteps.add(new Explanation(
                mContext.getString(R.string.technique_locked_candidate_step_4,
                        mValue,
                        TechniqueHelpers.getGroupString(mContext, mPrimaryGroup),
                        mPrimaryIndex + 1,
                        TechniqueHelpers.getGroupString(mContext, mSecondaryGroup),
                        mSecondaryIndex + 1),
                (board) -> {
                    CellGroup primaryGroup = TechniqueHelpers.getGroupFromIndex(board.getCells(), mPrimaryGroup, mPrimaryIndex);
                    CellGroup secondaryGroup = TechniqueHelpers.getGroupFromIndex(board.getCells(), mSecondaryGroup, mSecondaryIndex);
                    TechniqueHelpers.highlightGroup(primaryGroup, mHighlightOverrides);
                    TechniqueHelpers.highlightGroup(secondaryGroup, mHighlightOverrides);
                    for (Cell cell : CellGroup.difference(secondaryGroup, primaryGroup)) {
                        if (cell.getValue() == 0 && cell.getNote().hasNumber(mValue)) {
                            mHighlightOverrides.get(cell).setNoteHighlightMode(mValue - 1, HighlightMode.HIGHLIGHT);
                        }
                    }
                }));
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
