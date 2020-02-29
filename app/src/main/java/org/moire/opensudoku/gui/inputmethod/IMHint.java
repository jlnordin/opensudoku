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

import org.moire.opensudoku.R;
import org.moire.opensudoku.game.Cell;

public class IMHint extends InputMethod {

    private Cell mSelectedCell;

    @Override
    protected void onActivated() {
        mBoard.setAutoHideTouchedCellHint(false);
    }

    @Override
    protected void onDeactivated() {
        mBoard.setAutoHideTouchedCellHint(true);
    }

    @Override
    protected void onCellTapped(Cell cell) {
        mSelectedCell = cell;
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
        return R.string.popup;
    }

    @Override
    public int getHelpResID() {
        return R.string.im_popup_hint;
    }

    @Override
    public String getAbbrName() {
        return mContext.getString(R.string.popup_abbr);
    }

    @Override
    protected View createControlPanelView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.im_hint, null);
    }
}
