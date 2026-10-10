package com.poultrypathsprint.arcade.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.poultrypathsprint.arcade.R
import com.poultrypathsprint.arcade.core.di.ServiceLocator
import com.poultrypathsprint.arcade.databinding.DialogOutfitCoopBinding
import com.poultrypathsprint.arcade.domain.model.Outfit

class OutfitCoopDialog : DialogFragment() {

    private var _binding: DialogOutfitCoopBinding? = null
    private var adapter: OutfitAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_PoultryPathSprint_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val created = DialogOutfitCoopBinding.inflate(inflater, container, false)
        _binding = created
        return created.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val local = _binding ?: return
        val created = OutfitAdapter { outfit, owned -> handleAction(outfit, owned) }
        adapter = created
        local.coopList.layoutManager = LinearLayoutManager(requireContext())
        local.coopList.adapter = created
        local.coopClose.setOnClickListener { dismissAllowingStateLoss() }
        refresh()
    }

    private fun handleAction(outfit: Outfit, owned: Boolean) {
        val context = context ?: return
        val outfits = ServiceLocator.outfitRepository(context)
        if (owned) {
            outfits.equip(outfit.id)
        } else {
            val runs = ServiceLocator.runRepository(context)
            if (runs.totalFeathers() >= outfit.cost) {
                outfits.unlock(outfit.id)
                outfits.equip(outfit.id)
            }
        }
        refresh()
    }

    private fun refresh() {
        val local = _binding ?: return
        val context = context ?: return
        val outfits = ServiceLocator.outfitRepository(context)
        val runs = ServiceLocator.runRepository(context)
        val balance = runs.totalFeathers()
        val items = outfits.outfits()
        local.coopBalanceCount.text = balance.toString()
        adapter?.submit(items, outfits.unlockedIds(), outfits.equippedId(), balance)
        local.coopEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        local.coopList.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        _binding?.coopList?.adapter = null
        adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "outfit_coop_dialog"
    }
}
