package com.letter.schedule.fragment

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.letter.schedule.R

/**
 * 设置Fragment
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class SettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_preferences, rootKey)
    }
}