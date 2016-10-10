// Dono Android - Password Derivation Tool
// Copyright (C) 2016  Dono - Password Derivation Tool
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.dono.psakkos.dono;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.List;

public class LabelAdapter extends BaseSwipeAdapter
{

    private Context mContext;
    private List<String> labels;
    private OnLabelRemoveClickListener onLabelRemoveClickListener;

    public LabelAdapter(Context mContext, List<String> labels, OnLabelRemoveClickListener onLabelRemoveClickListener)
    {
        this.mContext = mContext;
        this.labels = labels;
        this.onLabelRemoveClickListener = onLabelRemoveClickListener;
    }

    @Override
    public int getSwipeLayoutResourceId(int position)
    {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent)
    {
        View v = LayoutInflater.from(mContext).inflate(R.layout.label_row, null);

        v.findViewById(R.id.trash).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (onLabelRemoveClickListener != null){
                    onLabelRemoveClickListener.onLabelRemoveClick(position);
                }
            }
        });

        v.setBackgroundColor(Color.WHITE);

        return v;
    }

    @Override
    public void fillValues(int position, View convertView)
    {
        TextView t = (TextView)convertView.findViewById(R.id.labelText);
        t.setText(this.labels.get(position));
    }

    @Override
    public int getCount()
    {
        return labels.size();
    }

    @Override
    public Object getItem(int position)
    {
        return labels.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public interface OnLabelRemoveClickListener{
        void onLabelRemoveClick(int position);
    }
}
