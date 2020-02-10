package com.letter.schedule.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.letter.schedule.R

class SettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_preferences, rootKey)
    }
}