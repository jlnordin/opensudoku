/*
 * Copyright (C) 2009 Roman Masek
 *
 * This file is part of OpenSudoku.
 *
 * OpenSudoku is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenSudoku is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenSudoku.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.moire.opensudoku.gui.inputmethod;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.solver.AbstractTechnique;
import org.moire.opensudoku.game.solver.StepByStepSolver;
import org.moire.opensudoku.gui.SudokuBoardView;

public class IMHint extends InputMethod {

    Cell mSelectedCell;
    AbstractTechnique mTechnique;

    SudokuBoardView.HighlightMode mSavedHighlightMode;
    boolean mSavedHighlightTouchedCell;
    boolean mSavedHighlightWrongVals;
    boolean mSavedReadOnly;

    public IMHint() {
    }

    @Override
    protected void onActivated() {
        mSavedHighlightMode = mBoard.getHighlightSimilarCells();
        mSavedHighlightTouchedCell = mBoard.getHighlightTouchedCell();
        mSavedHighlightWrongVals = mBoard.getHighlightWrongVals();
        mSavedReadOnly = mBoard.isReadOnly();

        mBoard.setHighlightTouchedCell(false);
        mBoard.setHighlightWrongVals(false);
        mBoard.setReadOnly(true);
        mBoard.setDimCellsThatAreNotHighlighted(true);

        if (mGame.getSolutionValues() != null) {
            mTechnique = StepByStepSolver.getNextTechnique(mContext, mBoard.getCells(), mGame.getSolutionValues());
        } else {
            mTechnique = StepByStepSolver.getNextTechnique(mContext, mBoard.getCells());
        }
        update();
    }

    @Override
    protected void onDeactivated() {
        mBoard.setHighlightSimilarCell(mSavedHighlightMode);
        mBoard.setHighlightTouchedCell(mSavedHighlightTouchedCell);
        mBoard.setHighlightWrongVals(mSavedHighlightWrongVals);
        mBoard.setReadOnly(mSavedReadOnly);
        mBoard.setDimCellsThatAreNotHighlighted(false);
    }

    @Override
    protected void onCellTapped(Cell cell) {
        mSelectedCell = cell;
        mBoard.hideTouchedCellHint();
    }

    @Override
    protected void onCellSelected(Cell cell) {
        super.onCellSelected(cell);

        if (cell != null) {
            mBoard.setHighlightedValue(cell.getValue());
        } else {
            mBoard.setHighlightedValue(0);
        }
    }

    @Override
    protected void onPause() {
    }

    @Override
    public int getNameResID() {
        return R.string.hint;
    }

    @Override
    public int getHelpResID() {
        return R.string.im_hint_help;
    }

    @Override
    public String getAbbrName() {
        return mContext.getString(R.string.hint);
    }

    Button mPreviousStepButton;
    Button mNextStepButton;
    Button mApplyHintButton;
    Button mCloseButton;
    TextView mTitleText;
    TextView mExplanationText;
    TextView mStepsText;

    @Override
    protected View createControlPanelView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View controlPanel = inflater.inflate(R.layout.im_hint, null);
        mPreviousStepButton = controlPanel.findViewById(R.id.button_previous_step);
        mNextStepButton = controlPanel.findViewById(R.id.button_next_step);
        mApplyHintButton = controlPanel.findViewById(R.id.button_apply_hint);
        mCloseButton = controlPanel.findViewById(R.id.button_close);
        mTitleText = controlPanel.findViewById(R.id.title);
        mExplanationText = controlPanel.findViewById(R.id.explanation);
        mStepsText = controlPanel.findViewById(R.id.steps);

        mPreviousStepButton.setOnClickListener((view) -> {
            mTechnique.showPreviousExplanation(mBoard);
            update();
        });

        mNextStepButton.setOnClickListener((view) -> {
            mTechnique.showNextExplanation(mBoard);
            update();
        });

        mApplyHintButton.setOnClickListener((view) -> {
            mTechnique.applyTechnique(mGame);
            mGame.validate();
            mCloseButton.performClick();
        });

        return controlPanel;
    }

    void update() {
        mTechnique.showCurrentExplanation(mBoard);

        mPreviousStepButton.setEnabled(!mTechnique.isFirstStep());
        mNextStepButton.setEnabled(!mTechnique.isLastStep());
        mApplyHintButton.setEnabled(mTechnique.isLastStep());
        mTitleText.setText(mTechnique.getName());
        mExplanationText.setText(mTechnique.getCurrentExplanationText());
        mStepsText.setText(mContext.getString(R.string.step_n_of_total, mTechnique.getCurrentStep() + 1, mTechnique.getTotalSteps()));
    }
}
