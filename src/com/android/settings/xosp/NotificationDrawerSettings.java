/*
* Copyright (C) 2014 The CyanogenMod Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.android.settings.xosp;


import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;
import android.provider.Settings;
import com.android.internal.logging.MetricsLogger;
import cyanogenmod.providers.CMSettings;
import com.android.settings.util.Helpers;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NotificationDrawerSettings extends SettingsPreferenceFragment {
 
 private static final String PREF_ENABLE_TASK_MANAGER = "enable_task_manager";
 private static final String PREF_CUSTOM_HEADER_DEFAULT = "status_bar_custom_header_default";

    private SwitchPreference mEnableTaskManager;
    private SwitchPreference mCustomHeader;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.notification_drawer_settings);
        PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();


        // Task manager
        mEnableTaskManager = (SwitchPreference) prefSet.findPreference(PREF_ENABLE_TASK_MANAGER);
        mEnableTaskManager.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.ENABLE_TASK_MANAGER, 0) == 1));

        //Custom Headers
        mCustomHeader = (SwitchPreference) prefSet.findPreference(PREF_CUSTOM_HEADER_DEFAULT);
        mCustomHeader.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_CUSTOM_HEADER_DEFAULT, 0) == 1));

    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.APPLICATION;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if  (preference == mEnableTaskManager) {
            boolean enabled = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.ENABLE_TASK_MANAGER, enabled ? 1:0);  
	    }
        if (preference == mCustomHeader) {
           boolean customHeader = ((SwitchPreference)preference).isChecked();
           Settings.System.putInt(getActivity().getContentResolver(),
                   Settings.System.STATUS_BAR_CUSTOM_HEADER_DEFAULT, customHeader ? 1:0);
           Helpers.restartSystemUI();
        }    
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


}

