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

package de.nisnagel.iogo.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import de.nisnagel.iogo.ui.detail.StateFragment;
import de.nisnagel.iogo.ui.history.HistoryFragment;
import de.nisnagel.iogo.ui.info.InfoFragment;
import de.nisnagel.iogo.ui.main.EnumFragment;
import de.nisnagel.iogo.ui.main.FunctionFragment;
import de.nisnagel.iogo.ui.main.HomeFragment;
import de.nisnagel.iogo.ui.main.RoomFragment;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract EnumFragment contributeEnumFragment();

    @ContributesAndroidInjector
    abstract StateFragment contributeStateFragment();

    @ContributesAndroidInjector
    abstract HistoryFragment contributeHistoryFragment();

    @ContributesAndroidInjector
    abstract InfoFragment contributeInfoFragment();

    @ContributesAndroidInjector
    abstract HomeFragment contributeHomeFragment();

    @ContributesAndroidInjector
    abstract RoomFragment contributeRoomFragment();

    @ContributesAndroidInjector
    abstract FunctionFragment contributeFunctionFragment();
}