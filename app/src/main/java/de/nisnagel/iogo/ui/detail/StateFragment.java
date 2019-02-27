/*
 * ioGo - android app to control ioBroker home automation server.
 *
 * Copyright (C) 2018  Nis Nagel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nisnagel.iogo.ui.detail;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.di.Injectable;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.service.util.ImageUtils;
import de.nisnagel.iogo.ui.base.StateItem;

/**
 * A placeholder fragment containing a simple view.
 */
public class StateFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private Toolbar toolbar;

    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.btnStatistic)
    Button mStatistics;
    @BindView(R.id.txtName)
    TextView mName;
    @BindView(R.id.txtEnum)
    TextView mEnum;
    @BindView(R.id.stateItems)
    RecyclerView mRecyclerView;

    private StateItemAdapter mAdapter;
    private List<StateItem> mListStates = new ArrayList<>();

    private StateViewModel mViewModel;
    private String stateId;
    private String enumId;
    private State state;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(StateViewModel.class);
        stateId = getArguments().getString(Constants.ARG_STATE_ID);
        enumId = getArguments().getString(Constants.ARG_ENUM_ID);
        state = null;
        setHasOptionsMenu(true);
        toolbar = getActivity().findViewById(R.id.toolbar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_state, container, false);
        ButterKnife.bind(this, rootView);

        mAdapter = new StateItemAdapter(mListStates, mViewModel);

        mName.setText(R.string.main_loading_date);
        mEnum.setText("");

        getActivity().runOnUiThread(() -> {
            mRecyclerView.setAdapter(mAdapter);
            RecyclerView.LayoutManager layoutManager =
                    new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setHasFixedSize(true);
        });

        mViewModel.getState(stateId).observe(this, elem -> {
            if (elem != null) {
                state = elem;
                mName.setText(elem.getName());
                mIcon.setImageResource(ImageUtils.getRoleImage(state.getRole()));
                mViewModel.setValue(elem.getVal());
                if (state.getStates() != null) {
                    mAdapter.clearList();

                    ArrayList<StateItem> stateItems = new ArrayList<>();
                    for (Map.Entry<String, String> entry : state.getStates().entrySet()) {
                        stateItems.add(new StateItem(entry.getKey(), entry.getValue()));
                    }
                    mAdapter.addAll(stateItems);
                    mAdapter.notifyDataSetChanged();
                }
                if(state.hasHistory() && "number".equals(state.getType())){
                    mStatistics.setVisibility(View.VISIBLE);
                }else{
                    mStatistics.setVisibility(View.GONE);
                }
                if (toolbar.getMenu().size() > 0) {
                    setFavoriteIcon(toolbar.getMenu().getItem(0), "true".equals(state.getFavorite()));
                }
            }
        });

        mViewModel.getEnum(enumId).observe(this, elem -> {
            if (elem != null) {
                mEnum.setText(elem.getName());
            }
        });

        return rootView;
    }

    private void setFavoriteIcon(MenuItem item, boolean starred) {
        if (starred) {
            item.setIcon(R.drawable.starred);
        } else {
            item.setIcon(R.drawable.unstarred);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_state, menu);
        if (state != null) {
            setFavoriteIcon(menu.getItem(0), "true".equals(state.getFavorite()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                if ("true".equals(state.getFavorite())) {
                    state.setFavorite("false");
                    setFavoriteIcon(item, true);
                    Toast.makeText(getContext(), R.string.main_unstarred, Toast.LENGTH_SHORT).show();
                } else {
                    state.setFavorite("true");
                    setFavoriteIcon(item, false);
                    Toast.makeText(getContext(), R.string.main_starred, Toast.LENGTH_SHORT).show();
                }

                mViewModel.saveState(state);
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
