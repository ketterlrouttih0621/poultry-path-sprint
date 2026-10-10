package com.poultrypathsprint.arcade.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.poultrypathsprint.arcade.R
import com.poultrypathsprint.arcade.databinding.DialogPauseBinding

class PauseDialog : DialogFragment() {

    interface PauseListener {
        fun onPauseResume()
        fun onPauseMenu()
    }

    private var _binding: DialogPauseBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_PoultryPathSprint_Dialog)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = DialogPauseBinding.inflate(inflater, container, false)
        _binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val local = _binding ?: return
        val distance = arguments?.getInt(ARG_DISTANCE, 0) ?: 0
        val feathers = arguments?.getInt(ARG_FEATHERS, 0) ?: 0
        local.pauseStatMetres.bindAlways(distance, getString(R.string.label_metres))
        local.pauseStatFeathers.bind(feathers, getString(R.string.label_feathers))

        local.pauseResume.setOnClickListener {
            listener()?.onPauseResume()
            dismissAllowingStateLoss()
        }
        local.pauseMenu.setOnClickListener {
            listener()?.onPauseMenu()
            dismissAllowingStateLoss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCancel(dialog: android.content.DialogInterface) {
        super.onCancel(dialog)
        listener()?.onPauseResume()
    }

    private fun listener(): PauseListener? = parentFragment as? PauseListener

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "pause_dialog"
        private const val ARG_DISTANCE = "arg_distance"
        private const val ARG_FEATHERS = "arg_feathers"

        fun newInstance(distance: Int, feathers: Int): PauseDialog {
            val fragment = PauseDialog()
            val args = Bundle()
            args.putInt(ARG_DISTANCE, distance)
            args.putInt(ARG_FEATHERS, feathers)
            fragment.arguments = args
            return fragment
        }
    }
}
