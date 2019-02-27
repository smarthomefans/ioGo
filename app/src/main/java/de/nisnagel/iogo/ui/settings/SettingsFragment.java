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

package de.nisnagel.iogo.ui.settings;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.nisnagel.iogo.R;

public class SettingsFragment extends Fragment {

    @BindView(R.id.list)
    RecyclerView recyclerView;
    private SettingsAdapter mAdapter;

    private List<SettingsAdapter.SettingItem> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, rootView);

        list.add(new SettingsAdapter.SettingItem(R.drawable.account,getString(R.string.settings_account_title),getString(R.string.settings_server_account_desc), SettingsMainActivity.intentAccount));
        list.add(new SettingsAdapter.SettingItem(R.drawable.server_network,getString(R.string.settings_server_title),getString(R.string.settings_server_server_desc), SettingsMainActivity.intentServer));
        list.add(new SettingsAdapter.SettingItem(R.drawable.television_guide,getString(R.string.settings_server_layout),getString(R.string.settings_server_layout_desc), SettingsMainActivity.intentLayout));
        list.add(new SettingsAdapter.SettingItem(R.drawable.cellphone_android,getString(R.string.settings_server_device),getString(R.string.settings_server_device_desc), SettingsMainActivity.intentDevice));
        list.add(new SettingsAdapter.SettingItem(R.drawable.bug,getString(R.string.settings_server_error),getString(R.string.settings_server_error_desc), SettingsMainActivity.intentError));
        list.add(new SettingsAdapter.SettingItem(R.drawable.trophy_variant_outline,"Pro Features","Buy extra features", SettingsMainActivity.intentBilling));
        mAdapter = new SettingsAdapter(list);
        getActivity().runOnUiThread(() -> recyclerView.setAdapter(mAdapter));

        return rootView;
    }

}
