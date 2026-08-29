package com.example.wanderlist.ui.visited

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlist.data.model.Destination
import com.example.wanderlist.databinding.ItemVisitedDestinationBinding

class VisitedAdapter(
    private var items: List<Destination>,
    private val onItemClick: (Destination) -> Unit,
    private val onReturnToWishlistClick: (Destination) -> Unit
) : RecyclerView.Adapter<VisitedAdapter.VisitedViewHolder>() {
    inner class VisitedViewHolder(val binding: ItemVisitedDestinationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitedViewHolder {
        val binding = ItemVisitedDestinationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VisitedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VisitedViewHolder, position: Int) {
        val destination = items[position]
        holder.binding.tvDestinationName.text = destination.name
        holder.binding.tvDestinationCountry.text = destination.country
        holder.binding.ratingBar.rating = destination.rating.toFloat()

        if (destination.notes.isNotBlank()) {
            holder.binding.tvReview.text = destination.notes
            holder.binding.tvReview.visibility = android.view.View.VISIBLE
        } else {
            holder.binding.tvReview.visibility = android.view.View.GONE
        }

        holder.binding.root.setOnClickListener { onItemClick(destination) }
        holder.binding.btnReturnToWishlist.setOnClickListener { onReturnToWishlistClick(destination) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Destination>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun getDestinationAt(position: Int): Destination = items[position]
}