package com.poultrypathsprint.arcade.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CompoundButton
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import com.poultrypathsprint.arcade.R
import com.poultrypathsprint.arcade.core.di.ServiceLocator
import com.poultrypathsprint.arcade.databinding.DialogSettingsBinding
import com.poultrypathsprint.arcade.domain.model.GameSettings

class SettingsDialog : DialogFragment() {

    private var _binding: DialogSettingsBinding? = null
    private var current = GameSettings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_PoultryPathSprint_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = DialogSettingsBinding.inflate(inflater, container, false)
        _binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val local = _binding ?: return
        val context = requireContext()
        current = ServiceLocator.settingsRepository(context).load()

        local.settingsSwitchSound.isChecked = current.soundEnabled
        local.settingsSwitchShake.isChecked = current.shakeEnabled
        local.settingsSwitchContrast.isChecked = current.highContrast
        local.settingsSensitivity.value = current.hopSensitivity.toFloat().coerceIn(1f, 3f)

        applyStateDescriptions()

        local.settingsSwitchSound.setOnCheckedChangeListener(
            object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(button: CompoundButton, checked: Boolean) {
                    current = current.copy(soundEnabled = checked)
                    persist()
                }
            }
        )
        local.settingsSwitchShake.setOnCheckedChangeListener(
            object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(button: CompoundButton, checked: Boolean) {
                    current = current.copy(shakeEnabled = checked)
                    persist()
                }
            }
        )
        local.settingsSwitchContrast.setOnCheckedChangeListener(
            object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(button: CompoundButton, checked: Boolean) {
                    current = current.copy(highContrast = checked)
                    persist()
                }
            }
        )
        local.settingsSensitivity.addOnChangeListener { _, value, _ ->
            current = current.copy(hopSensitivity = value.toInt())
            persist()
        }

        local.settingsReset.setOnClickListener { resetProgress() }
        local.settingsClose.setOnClickListener { dismissAllowingStateLoss() }
    }

    private fun persist() {
        val context = context ?: return
        ServiceLocator.settingsRepository(context).save(current)
        applyStateDescriptions()
    }

    private fun applyStateDescriptions() {
        val local = _binding ?: return
        ViewCompat.setStateDescription(local.settingsSwitchSound, stateText(current.soundEnabled))
        ViewCompat.setStateDescription(local.settingsSwitchShake, stateText(current.shakeEnabled))
        ViewCompat.setStateDescription(local.settingsSwitchContrast, stateText(current.highContrast))
    }

    private fun stateText(enabled: Boolean): String =
        getString(if (enabled) R.string.state_on else R.string.state_off)

    private fun resetProgress() {
        val context = context ?: return
        ServiceLocator.runRepository(context).resetProgress()
        dismissAllowingStateLoss()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "settings_dialog"
    }
}
