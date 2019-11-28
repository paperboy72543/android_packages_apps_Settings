/*
 * Copyright (C) 2021 FusionOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.fusion.fragments;

import android.os.Bundle;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;
import com.android.internal.util.fusion.FuseUtils;

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;

import com.fusion.support.preferences.SecureSettingMasterSwitchPreference;
import com.fusion.support.preferences.SecureSettingSwitchPreference;
import com.fusion.support.preferences.SystemSettingMasterSwitchPreference;
import com.fusion.support.preferences.SystemSettingSeekBarPreference;
import com.fusion.support.preferences.SystemSettingSwitchPreference;

public class DisplayCustomizations extends SettingsPreferenceFragment {

    private static final String TAG = "Display Customizations";
    private static final String KEY_NETWORK_TRAFFIC = "network_traffic_location";
    private static final String KEY_NETWORK_TRAFFIC_ARROW = "network_traffic_arrow";
    private static final String KEY_NETWORK_TRAFFIC_AUTOHIDE = "network_traffic_autohide_threshold";

    private ListPreference mNetworkTraffic;
    private SystemSettingSwitchPreference mNetworkTrafficArrow;
    private SystemSettingSeekBarPreference mNetworkTrafficAutohide;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.display_customizations);
        
        mNetworkTraffic = (ListPreference) findPreference(KEY_NETWORK_TRAFFIC);
        int networkTraffic = Settings.System.getInt(resolver,
        Settings.System.NETWORK_TRAFFIC_LOCATION, 0);
        CharSequence[] NonNotchEntries = { getResources().getString(R.string.network_traffic_disabled),
                getResources().getString(R.string.network_traffic_statusbar),
                getResources().getString(R.string.network_traffic_qs_header) };
        CharSequence[] NotchEntries = { getResources().getString(R.string.network_traffic_disabled),
                getResources().getString(R.string.network_traffic_qs_header) };
        CharSequence[] NonNotchValues = {"0", "1" , "2"};
        CharSequence[] NotchValues = {"0", "2"};
        mNetworkTraffic.setEntries(FuseUtils.hasNotch(getActivity()) ? NotchEntries : NonNotchEntries);
        mNetworkTraffic.setEntryValues(FuseUtils.hasNotch(getActivity()) ? NotchValues : NonNotchValues);
        mNetworkTraffic.setValue(String.valueOf(networkTraffic));
        mNetworkTraffic.setSummary(mNetworkTraffic.getEntry());
        mNetworkTraffic.setOnPreferenceChangeListener(this);

        mNetworkTrafficArrow = (SystemSettingSwitchPreference) findPreference(KEY_NETWORK_TRAFFIC_ARROW);
        mNetworkTrafficAutohide = (SystemSettingSeekBarPreference) findPreference(KEY_NETWORK_TRAFFIC_AUTOHIDE);
        updateNetworkTrafficPrefs(networkTraffic);
    }

  @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNetworkTraffic) {
            int networkTraffic = Integer.valueOf((String) newValue);
            int index = mNetworkTraffic.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_LOCATION, networkTraffic);
            mNetworkTraffic.setSummary(mNetworkTraffic.getEntries()[index]);
            updateNetworkTrafficPrefs(networkTraffic);
            return true;
        }
        return false;
    }

    private void updateNetworkTrafficPrefs(int networkTraffic) {
        if (mNetworkTraffic != null) {
            if (networkTraffic == 0) {
                mNetworkTrafficArrow.setEnabled(false);
                mNetworkTrafficAutohide.setEnabled(false);
            } else {
                mNetworkTrafficArrow.setEnabled(true);
                mNetworkTrafficAutohide.setEnabled(true);
            }
        }
    }

    @Override
    public int getMetricsCategory() {
       return MetricsProto.MetricsEvent.FUSION;
    }
}
