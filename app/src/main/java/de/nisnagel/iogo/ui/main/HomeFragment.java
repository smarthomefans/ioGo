package de.nisnagel.iogo.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.di.Injectable;


public class HomeFragment extends Fragment implements Injectable {

    private EnumHomeListAdapter mEnumAdapter;
    private StateListAdapter mStateAdapter;

    @BindView(R.id.favorite_enums)
    RecyclerView rvEnums;

    @BindView(R.id.favorite_states)
    RecyclerView rvStates;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    public ArrayList<Enum> enumList = new ArrayList<>();
    public ArrayList<State> stateList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        EnumViewModel mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(EnumViewModel.class);

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mEnumAdapter = new EnumHomeListAdapter(enumList, mViewModel);
                rvEnums.setAdapter(mEnumAdapter);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
                rvEnums.setLayoutManager(mLayoutManager);

                mStateAdapter = new StateListAdapter(stateList, mViewModel, getActivity());
                rvStates.setAdapter(mStateAdapter);
            }
        });

        mViewModel.getFavoriteEnums()
                .observe(this, new Observer<List<Enum>>() {
                    @Override
                    public void onChanged(@Nullable List<Enum> newList) {
                        // update UI
                        mEnumAdapter = new EnumHomeListAdapter(newList, mViewModel);
                        rvEnums.setAdapter(mEnumAdapter);
                    }
                });

        mViewModel.getFavoriteStates()
                .observe(this, new Observer<List<State>>() {
                    @Override
                    public void onChanged(@Nullable List<State> newList) {
                        // update UI
                        mStateAdapter = new StateListAdapter(newList, mViewModel, getActivity());
                        rvStates.setAdapter(mStateAdapter);
                    }
                });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
