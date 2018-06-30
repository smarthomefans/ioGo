package de.nisio.iobroker.ui.info;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.AndroidSupportInjection;
import de.nisio.iobroker.R;
import de.nisio.iobroker.di.Injectable;
import de.nisio.iobroker.service.DataBus;
import de.nisio.iobroker.service.Events;

/**
 * A placeholder fragment containing a simple view.
 */
public class InfoFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.countRooms)
    TextView mCountRooms;

    @BindView(R.id.countFunctions)
    TextView mCountFunctions;

    @BindView(R.id.countStates)
    TextView mCountStates;

    @BindView(R.id.socketState)
    TextView mSocketState;

    @BindView(R.id.appVersion)
    TextView mAppVersion;

    private InfoViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(InfoViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.info, container, false);
        ButterKnife.bind(this, rootView);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(InfoViewModel.class);

        mViewModel.countRooms().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer value) {
                // update UI
                if(value != null) {
                    mCountRooms.setText(value.toString());
                }
            }
        });

        mViewModel.countFunctions().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer value) {
                // update UI
                if(value != null) {
                    mCountFunctions.setText(value.toString());
                }
            }
        });

        mViewModel.countStates().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer value) {
                // update UI
                if(value != null) {
                    mCountStates.setText(value.toString());
                }
            }
        });

        mViewModel.getSocketState().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String value) {
                // update UI
                if(value != null) {
                    mSocketState.setText(value);
                }
            }
        });

        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            mAppVersion.setText(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        return rootView;
    }

    @OnClick(R.id.syncObjects)
    public void onClickSyncObjects(){
        DataBus.getBus().post(new Events.SyncObjects());
    }

    @OnClick(R.id.clearDatabase)
    public void onClickClearDatabase(){
        mViewModel.clearDatabase();
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
