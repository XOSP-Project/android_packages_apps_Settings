/*
 * Copyright (C) 2015 The VRToxin Project
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

package com.android.settings.xosp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarTickerSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

        private static final String TAG = "Ticker";

        private static final String PREF_SHOW_TICKER = "status_bar_show_ticker";
        private static final String PREF_TEXT_COLOR = "status_bar_ticker_text_color";
        private static final String PREF_ICON_COLOR = "status_bar_ticker_icon_color";
        private static final String PREF_TICKER_RESTORE_DEFAULTS = "ticker_restore_defaults";

        private SwitchPreference mShowTicker;
        private ColorPickerPreference mTextColor;
        private ColorPickerPreference mIconColor;
        private Preference mTickerDefaults;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.statusbar_ticker);

            PreferenceScreen prefSet = getPreferenceScreen();
            ContentResolver resolver = getActivity().getContentResolver();

            mShowTicker = (SwitchPreference) prefSet.findPreference(PREF_SHOW_TICKER);
            mShowTicker.setChecked(Settings.System.getInt(resolver,
                    Settings.System.STATUS_BAR_SHOW_TICKER, 0) != 0);
            mShowTicker.setOnPreferenceChangeListener(this);

            mTextColor = (ColorPickerPreference) prefSet.findPreference(PREF_TEXT_COLOR);
            mTextColor.setOnPreferenceChangeListener(this);
            int textColor = Settings.System.getInt(resolver,
                    Settings.System.STATUS_BAR_TICKER_TEXT_COLOR, 0xff4285f4);
            String textHexColor = String.format("#%08x", (0xff4285f4 & textColor));
            mTextColor.setSummary(textHexColor);
            mTextColor.setNewPreviewColor(textColor);

            mIconColor = (ColorPickerPreference) prefSet.findPreference(PREF_ICON_COLOR);
            mIconColor.setOnPreferenceChangeListener(this);
            int iconColor = Settings.System.getInt(resolver,
                    Settings.System.STATUS_BAR_TICKER_ICON_COLOR, 0xff4285f4);
            String iconHexColor = String.format("#%08x", (0xff4285f4 & iconColor));
            mIconColor.setSummary(iconHexColor);
            mIconColor.setNewPreviewColor(iconColor);

            mTickerDefaults = prefSet.findPreference(PREF_TICKER_RESTORE_DEFAULTS);
        }

        public boolean onPreferenceChange(Preference preference, Object newValue) {
            ContentResolver resolver = getActivity().getContentResolver();
            if (preference == mShowTicker) {
                int enabled = ((Boolean) newValue) ? 1 : 0;
                Settings.System.putInt(resolver,
                        Settings.System.STATUS_BAR_SHOW_TICKER, enabled);
                return true;
            } else if (preference == mTextColor) {
                String hex = ColorPickerPreference.convertToARGB(
                        Integer.valueOf(String.valueOf(newValue)));
                preference.setSummary(hex);
                int intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.STATUS_BAR_TICKER_TEXT_COLOR, intHex);
                return true;
            } else if (preference == mIconColor) {
                String hex = ColorPickerPreference.convertToARGB(
                        Integer.valueOf(String.valueOf(newValue)));
                preference.setSummary(hex);
                int intHex = ColorPickerPreference.convertToColorInt(hex);
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                        Settings.System.STATUS_BAR_TICKER_ICON_COLOR, intHex);
                return true;
            }
            return false;
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            final ContentResolver resolver = getActivity().getContentResolver();
            if (preference == mTickerDefaults) {
                int intColor;
                String hexColor;

                Settings.System.putInt(resolver,
                        Settings.System.STATUS_BAR_TICKER_TEXT_COLOR, 0xff4285f4);
                intColor = Settings.System.getInt(resolver,
                        Settings.System.STATUS_BAR_TICKER_TEXT_COLOR, 0xff4285f4);
                hexColor = String.format("#%08x", (0xff4285f4 & intColor));
                mTextColor.setSummary(hexColor);
                mTextColor.setNewPreviewColor(intColor);

                Settings.System.putInt(resolver,
                        Settings.System.STATUS_BAR_TICKER_ICON_COLOR, 0xff4285f4);
                intColor = Settings.System.getInt(resolver,
                        Settings.System.STATUS_BAR_TICKER_ICON_COLOR, 0xff4285f4);
                hexColor = String.format("#%08x", (0xff4285f4 & intColor));
                mIconColor.setSummary(hexColor);
                mIconColor.setNewPreviewColor(intColor);
            }
            return super.onPreferenceTreeClick(preference);
        }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.XOSP;
    }
}
