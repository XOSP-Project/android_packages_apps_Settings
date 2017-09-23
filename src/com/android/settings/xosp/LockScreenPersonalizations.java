/*
 * Copyright (C) 2016 The Xperia Open Source Project
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

import com.android.internal.logging.MetricsProto.MetricsEvent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.CheckBoxPreference;

import android.os.UserHandle;
import android.view.Display;
import android.view.IWindowManager;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.WindowManagerImpl;
import android.widget.Toast;
import com.android.internal.view.RotationPolicy;
import com.android.internal.view.RotationPolicy.RotationPolicyListener;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import static android.provider.Settings.Secure.CAMERA_GESTURE_DISABLED;
import static android.provider.Settings.Secure.DOUBLE_TAP_TO_WAKE;
import static android.provider.Settings.Secure.DOZE_ENABLED;
import static android.provider.Settings.Secure.WAKE_GESTURE_ENABLED;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;

import android.app.WallpaperManager;
import android.content.Intent;
import android.net.Uri;
import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.Dialog;
import android.app.IActivityManager;
import android.app.ProgressDialog;
import android.app.UiModeManager;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference;
import android.provider.SearchIndexableResource;
import com.android.settings.SettingsPreferenceFragment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import com.android.settings.R;
import com.android.settings.xosp.CustomSeekBarPreference;


import java.util.ArrayList;
import java.util.List;
import com.android.settings.Utils;

import com.android.internal.util.benzo.Helpers;

public class LockScreenPersonalizations extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener{

    private static final String KEY_DTA_LOCK = "double_tap_sleep_anywhere";
    private static final String LOCKSCREEN_ROTATION = "lockscreen_rotation";

    private SwitchPreference mDT2SAnywherePreference;
    private SwitchPreference mLockScreenRotationPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.xosp_lockscreen_cat);
        final Activity activity = getActivity();
        PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = activity.getContentResolver();

        mDT2SAnywherePreference = (SwitchPreference) findPreference(KEY_DTA_LOCK);
        mDT2SAnywherePreference.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.DOUBLE_TAP_SLEEP_ANYWHERE, 0) == 1));
        mLockScreenRotationPref = (SwitchPreference) prefSet.findPreference(LOCKSCREEN_ROTATION);

        boolean configEnableLockRotation = getResources().
                        getBoolean(com.android.internal.R.bool.config_enableLockScreenRotation);
        Boolean lockScreenRotationEnabled = Settings.System.getInt(getContentResolver(),
                        Settings.System.LOCKSCREEN_ROTATION, configEnableLockRotation ? 1 : 0) != 0;
        mLockScreenRotationPref.setChecked(lockScreenRotationEnabled);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.XOSP;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue){
        return false;
    }

    private static void doSystemUIReboot() {
        Helpers.restartSystemUI();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        boolean enabled;

        if (preference == mDT2SAnywherePreference) {
            enabled = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.DOUBLE_TAP_SLEEP_ANYWHERE, enabled ? 1:0);
            return true;
        } else if(preference == mLockScreenRotationPref) {
            enabled = mLockScreenRotationPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_ROTATION, enabled ? 1 : 0);
            doSystemUIReboot();
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
