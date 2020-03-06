package org.moire.opensudoku.game.solver;

import android.content.Context;

import org.moire.opensudoku.game.Cell;
import org.moire.opensudoku.game.CellCollection;
import org.moire.opensudoku.game.SudokuGame;
import org.moire.opensudoku.game.command.AbstractCellCommand;
import org.moire.opensudoku.gui.HighlightOptions;
import org.moire.opensudoku.gui.SudokuBoardView;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractTechnique {

    HashMap<Cell, HighlightOptions> mHighlightOverrides;
    Context mContext;

    AbstractTechnique(Context context) {
        mExplanationSteps = new ArrayList<Explanation>();
        mContext = context;
        mCurrentStep = 0;
        mHighlightOverrides = new HashMap<Cell, HighlightOptions>();
    }

    public abstract void applyTechnique(SudokuGame game);

    public abstract String getName();

    public interface ShowExplanation { void ShowExplanation(SudokuBoardView board); }

    class Explanation {
        String mExplanationText;
        ShowExplanation mShowExplanationCallback;

        Explanation(String explanationText, ShowExplanation callback) {
            mExplanationText = explanationText;
            mShowExplanationCallback = callback;
        }

        public String getExplanationText() {
            return mExplanationText;
        }

        public void show(SudokuBoardView board) {
            mShowExplanationCallback.ShowExplanation(board);
        }
    }

    protected int mCurrentStep;
    protected ArrayList<Explanation> mExplanationSteps;

    public int getTotalSteps() {
        return mExplanationSteps.size();
    }

    public int getCurrentStep() {
        return mCurrentStep;
    }

    public boolean isFirstStep() {
        return mCurrentStep == 0;
    }

    public boolean isLastStep() { return mCurrentStep == getTotalSteps() - 1; }

    Explanation getCurrentExplanation() {
        return mExplanationSteps.get(mCurrentStep);
    }

    public String getCurrentExplanationText() {
        return getCurrentExplanation().getExplanationText();
    }

    public void showCurrentExplanation(SudokuBoardView board) {
        board.setHighlightCellOverrides(mHighlightOverrides);
        getCurrentExplanation().show(board);
    }

    public void showNextExplanation(SudokuBoardView board) {
        if (mCurrentStep < getTotalSteps() - 1) {
            mCurrentStep++;
            showCurrentExplanation(board);
        }
    }

    public void showPreviousExplanation(SudokuBoardView board) {
        if (mCurrentStep > 0) {
            mCurrentStep--;
            showCurrentExplanation(board);
        }
    }
}