package de.nisnagel.iogo.ui.main;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.data.repository.EnumRepository;
import de.nisnagel.iogo.di.Injectable;

public class FunctionFragment extends Fragment implements Injectable {

    private EnumListAdapter mAdapter;
    private EnumViewModel mViewModel;

    @BindView(R.id.enum_list)
    RecyclerView recyclerView;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private ArrayList<Enum> list = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(EnumViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_enum_list, container, false);
        ButterKnife.bind(this, rootView);

        mAdapter = new EnumListAdapter(list, mViewModel);
        getActivity().runOnUiThread(() -> recyclerView.setAdapter(mAdapter));

        mViewModel.getEnums(EnumRepository.TYPE_FUNCTION)
                .observe(this, newList -> {
                    // update UI
                    mAdapter.clearList();
                    mAdapter.addAll(newList);
                    mAdapter.notifyDataSetChanged();
                });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
