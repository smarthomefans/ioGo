package de.nisnagel.iogo.ui.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.ui.main.HomeFragment;

public class SettingsFragment extends Fragment {

    @BindView(R.id.list)
    RecyclerView recyclerView;
    private SettingsAdapter mAdapter;

    private List<SettingsAdapter.SettingItem> list = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, rootView);

        list.add(new SettingsAdapter.SettingItem(R.drawable.satellite_uplink,"Connection","Connect to your server", "Connection"));
        list.add(new SettingsAdapter.SettingItem(R.drawable.television_guide,"Design","Adjust app behaviour", "Design"));
        list.add(new SettingsAdapter.SettingItem(R.drawable.bug,"Troubleshooting","What to do with bugs", "Error"));
        mAdapter = new SettingsAdapter(list);
        getActivity().runOnUiThread(() -> recyclerView.setAdapter(mAdapter));

        return rootView;
    }

}
