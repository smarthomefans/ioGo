package de.nisnagel.iogo.ui.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.di.Injectable;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.ui.main.EnumViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class StateFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.txtName)
    TextView mName;
    @BindView(R.id.txtRoom)
    TextView mRoom;
    @BindView(R.id.txtFunction)
    TextView mFunction;
    Toolbar toolbar;

    private StateViewModel mViewModel;
    private String stateId;
    private State state;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(StateViewModel.class);
        stateId = getArguments().getString(Constants.ARG_STATE_ID);
        state = null;
        setHasOptionsMenu(true);
        toolbar = getActivity().findViewById(R.id.toolbar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_state, container, false);
        ButterKnife.bind(this, rootView);

        mName.setText("loading");
        mRoom.setText("");
        mFunction.setText("");

        mViewModel.getState(stateId).observe(this, new Observer<State>() {
            @Override
            public void onChanged(@Nullable State elem) {
                // update UI
                if (elem != null) {
                    mName.setText(elem.getName());
                    state = elem;
                    if(toolbar.getMenu().size() > 0) {
                        setFavoriteIcon(toolbar.getMenu().getItem(0), "true".equals(state.getFavorite()));
                    }
                }
            }
        });

        return rootView;
    }

    private void setFavoriteIcon(MenuItem item, boolean starred){
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
        if(state != null){
            setFavoriteIcon(menu.getItem(0), "true".equals(state.getFavorite()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_favorite:
                if ("true".equals(state.getFavorite())) {
                    state.setFavorite("false");
                    setFavoriteIcon(item, true);
                    Toast.makeText(getContext(), "unstarred", Toast.LENGTH_SHORT).show();
                } else {
                    state.setFavorite("true");
                    setFavoriteIcon(item, false);
                    Toast.makeText(getContext(), "starred", Toast.LENGTH_SHORT).show();
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
