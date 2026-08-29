package com.example.wanderlist.ui.wishlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlist.BuildConfig
import com.example.wanderlist.data.model.Destination
import com.example.wanderlist.data.remote.RetrofitInstance
import com.example.wanderlist.databinding.ItemDestinationBinding
import kotlinx.coroutines.launch

class DestinationAdapter(
    private var items: List<Destination>,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onItemClick: (Destination) -> Unit,
    private val onMarkVisitedClick: (Destination) -> Unit
) : RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder>() {
    inner class DestinationViewHolder(val binding: ItemDestinationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val binding = ItemDestinationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DestinationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        val destination = items[position]
        holder.binding.tvDestinationName.text = destination.name
        holder.binding.tvDestinationCountry.text = destination.country
        holder.binding.tvWeather.text = "Učitavanje vremena..."
        holder.binding.root.setOnClickListener { onItemClick(destination) }
        holder.binding.btnMarkVisited.setOnClickListener { onMarkVisitedClick(destination) }

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.weatherApi.getCurrentWeather(
                    lat = destination.latitude,
                    lon = destination.longitude,
                    apiKey = BuildConfig.OPEN_WEATHER_API_KEY
                )
                val temp = response.main.temp.toInt()
                val description = response.weather.firstOrNull()?.description ?: ""
                holder.binding.tvWeather.text = "$temp°C, $description"
            } catch (e: Exception) {
                android.util.Log.e("WeatherFetch", "Greška pri dohvatu vremena", e)
                holder.binding.tvWeather.text = "Vrijeme nedostupno"
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Destination>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun getDestinationAt(position: Int): Destination = items[position]
}