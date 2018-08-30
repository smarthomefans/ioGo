package de.nisnagel.iogo.ui.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import de.nisnagel.iogo.data.model.Enum;
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

    Toolbar toolbar;

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

        mName.setText(R.string.loading_date);
        mEnum.setText("");

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mRecyclerView.setAdapter(mAdapter);
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setHasFixedSize(true);
            }
        });

        mViewModel.getState(stateId).observe(this, new Observer<State>() {
            @Override
            public void onChanged(@Nullable State elem) {
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
            }
        });

        mViewModel.getEnum(enumId).observe(this, new Observer<Enum>() {
            @Override
            public void onChanged(@Nullable Enum elem) {
                if (elem != null) {
                    mEnum.setText(elem.getName());
                }
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
                    Toast.makeText(getContext(), R.string.unstarred, Toast.LENGTH_SHORT).show();
                } else {
                    state.setFavorite("true");
                    setFavoriteIcon(item, false);
                    Toast.makeText(getContext(), R.string.starred, Toast.LENGTH_SHORT).show();
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
