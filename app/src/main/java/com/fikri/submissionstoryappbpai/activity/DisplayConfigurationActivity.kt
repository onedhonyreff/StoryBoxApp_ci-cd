package com.fikri.submissionstoryappbpai.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.databinding.ActivityDisplayConfigurationBinding
import com.fikri.submissionstoryappbpai.data.DataStorePreferences
import com.fikri.submissionstoryappbpai.view_model.DisplayConfigurationViewModel
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelFactory

class DisplayConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayConfigurationBinding

    private lateinit var viewModel: DisplayConfigurationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayConfigurationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        setupAction()
    }

    private fun setupData() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[DisplayConfigurationViewModel::class.java]
    }

    private fun setupAction() {
        viewModel.apply {
            binding.apply {

                llLanguageOptions.setOnClickListener {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                }

                swThemeSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                    saveThemeSetting(isChecked)
                }

                rgMapMode.setOnCheckedChangeListener { _, item ->
                    when (item) {
                        R.id.rb_mode_hybrid -> saveMapMode(DataStorePreferences.MODE_HYBRID)
                        R.id.rb_mode_satellite -> saveMapMode(DataStorePreferences.MODE_NIGHT)
                        R.id.rb_mode_traffic -> saveMapMode(DataStorePreferences.MODE_NORMAL)
                    }
                }

                getThemeSettings().observe(this@DisplayConfigurationActivity) { isDarkModeActive: Boolean ->
                    if (isDarkModeActive) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        ivThemeMode.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@DisplayConfigurationActivity,
                                R.drawable.ic_night_mode
                            )
                        )
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        ivThemeMode.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@DisplayConfigurationActivity,
                                R.drawable.ic_light_mode
                            )
                        )
                    }
                    swThemeSwitch.isChecked = isDarkModeActive
                }

                getMapMode().observe(this@DisplayConfigurationActivity) { mapMode ->
                    when (mapMode) {
                        DataStorePreferences.MODE_HYBRID -> rbModeHybrid.isChecked = true
                        DataStorePreferences.MODE_NIGHT -> rbModeSatellite.isChecked = true
                        DataStorePreferences.MODE_NORMAL -> rbModeTraffic.isChecked = true
                    }
                }
            }
        }
    }
}