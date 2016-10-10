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

package com.dono.psakkos.dono.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.dono.psakkos.dono.LabelAdapter;
import com.dono.psakkos.dono.R;
import com.dono.psakkos.dono.core.Dono;
import com.dono.psakkos.dono.core.PersistableKey;
import com.dono.psakkos.dono.core.PersistableLabels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dono.psakkos.dono.R.id.labelsListView;

public class LabelsFragment extends DonoFragment implements LabelAdapter.OnLabelRemoveClickListener {
    // Milliseconds after which the row's background color will be restored
    public static int ROW_REFRESH_MILLISECONDS = 100;

    private PersistableLabels mPersistableLabels;
    private List<String> mAdapterItems;
    private BaseSwipeAdapter mLabelListAdapter;
    private OnLabelRemovedListener mListener;
    private ListView mLabelsListView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachListener(context);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        attachListener(activity);
    }

    private void attachListener(Context activity) {
        try {
            mListener = (OnLabelRemovedListener)activity;
        }catch (ClassCastException ex){
            Log.d("LabelsFragment", "Your activity should implements OnLabelRemovedListener");
            ex.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPersistableLabels = new PersistableLabels(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.labels_fragment, container, false);

        mLabelsListView = (ListView)view.findViewById(labelsListView);

        mLabelsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        view.setBackgroundColor(getResources().getColor(R.color.secondary));

                        restoreCellColors();

                        PersistableKey persistableKey = new PersistableKey(view.getContext());
                        String key = persistableKey.getKey();

                        if (key == null)
                        {
                            donoToastFactory.makeErrorToast("You need to set your Key in order to derive passwords for your Labels!").show();
                            return;
                        }

                        String label = String.valueOf(parent.getItemAtPosition(position));

                        String password;

                        try
                        {
                            password = new Dono().computePassword(key, label);
                        }
                        catch (Exception e)
                        {
                            donoToastFactory.makeErrorToast("Oops! Failed to derive the password for Label " + label).show();
                            return;
                        }

                        ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setPrimaryClip(ClipData.newPlainText(label, password));

                        donoToastFactory.makeInfoToast("Your password for " + label + " is ready to be pasted!").show();
                    }
                }
        );

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapterItems = new ArrayList<>(Arrays.asList(new PersistableLabels(getActivity()).getAll()));
        mLabelListAdapter = new LabelAdapter(getActivity(), mAdapterItems, this);
        mLabelsListView.setAdapter(mLabelListAdapter);
    }

    public void restoreCellColors() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                ListView list = (ListView) getView().findViewById(labelsListView);

                for (int i = 0; i < list.getChildCount(); i++)
                {
                    View child = list.getChildAt(i);
                    child.setBackgroundColor(Color.WHITE);
                }
            }
        }, ROW_REFRESH_MILLISECONDS);
    }

    @Override
    public void onLabelRemoveClick(int position) {
        mPersistableLabels.deleteAt(position);

        mAdapterItems.clear();
        mAdapterItems.addAll(Arrays.asList(mPersistableLabels.getAll()));
        mLabelListAdapter.notifyDataSetChanged();
        mLabelListAdapter.closeAllItems();

        if (mListener != null){
            mListener.onLabelDatasourceChanged();
        }
    }

    public interface OnLabelRemovedListener{
        void onLabelDatasourceChanged();
    }
}
