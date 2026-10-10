package com.poultrypathsprint.arcade.presentation.dialog

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.poultrypathsprint.arcade.R
import com.poultrypathsprint.arcade.databinding.ItemOutfitBinding
import com.poultrypathsprint.arcade.domain.model.Outfit

class OutfitAdapter(
    private val onAction: (Outfit, Boolean) -> Unit
) : RecyclerView.Adapter<OutfitAdapter.OutfitHolder>() {

    private val items = mutableListOf<Outfit>()
    private var unlocked: Set<String> = emptySet()
    private var equipped: String = ""
    private var balance: Int = 0

    fun submit(outfits: List<Outfit>, unlockedIds: Set<String>, equippedId: String, feathers: Int) {
        items.clear()
        items.addAll(outfits)
        unlocked = unlockedIds
        equipped = equippedId
        balance = feathers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutfitHolder {
        val binding = ItemOutfitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OutfitHolder(binding)
    }

    override fun onBindViewHolder(holder: OutfitHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class OutfitHolder(private val binding: ItemOutfitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(outfit: Outfit) {
            val context = binding.root.context
            val owned = unlocked.contains(outfit.id)
            val worn = owned && outfit.id == equipped
            val affordable = owned || balance >= outfit.cost

            binding.outfitName.text = outfit.name
            binding.outfitCost.text = when {
                owned -> outfit.flavour
                else -> context.getString(R.string.outfit_cost, outfit.cost)
            }
            binding.outfitIcon.imageTintList = ColorStateList.valueOf(outfit.accentColor)
            binding.outfitAction.setText(
                when {
                    worn -> R.string.btn_worn
                    owned -> R.string.btn_wear
                    else -> R.string.btn_unlock
                }
            )
            binding.outfitAction.backgroundTintList = ColorStateList.valueOf(outfit.accentColor)
            binding.outfitAction.isEnabled = affordable && !worn
            binding.outfitAction.alpha = if (affordable && !worn) 1f else DISABLED_ALPHA
            binding.root.alpha = if (affordable) 1f else ROW_DIM_ALPHA

            val description = when {
                worn -> context.getString(R.string.state_worn)
                owned -> context.getString(R.string.state_ready)
                affordable -> context.getString(R.string.state_ready)
                else -> context.getString(R.string.state_locked, outfit.cost)
            }
            ViewCompat.setStateDescription(binding.root, description)

            binding.outfitAction.setOnClickListener(
                object : View.OnClickListener {
                    override fun onClick(view: View) {
                        onAction(outfit, owned)
                    }
                }
            )
        }
    }

    companion object {
        private const val DISABLED_ALPHA = 0.45f
        private const val ROW_DIM_ALPHA = 0.6f
    }
}
