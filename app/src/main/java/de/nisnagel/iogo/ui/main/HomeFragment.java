package de.nisnagel.iogo.ui.main;

import android.arch.lifecycle.Observer;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.Enum;
import de.nisnagel.iogo.di.Injectable;


public class HomeFragment extends Fragment implements Injectable {

    private EnumListAdapter mAdapter;

    @BindView(R.id.favorite_list)
    RecyclerView recyclerView;

    @BindView(R.id.adView)
    AdView mAdView;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    public ArrayList<Enum> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        EnumViewModel mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(EnumViewModel.class);

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mAdapter = new EnumListAdapter(list);
                recyclerView.setAdapter(mAdapter);
            }
        });

        mViewModel.getFavoriteEnums()
                .observe(this, new Observer<List<Enum>>() {
                    @Override
                    public void onChanged(@Nullable List<Enum> newList) {
                        // update UI
                        mAdapter = new EnumListAdapter(newList);
                        recyclerView.setAdapter(mAdapter);
                    }
                });

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(getActivity(), getString(R.string.ad_unit_id));

        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(adRequest);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
