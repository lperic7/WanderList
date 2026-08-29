package com.example.wanderlist.ui.visited

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlist.data.model.Destination
import com.example.wanderlist.databinding.DialogAddVisitedBinding
import com.example.wanderlist.databinding.FragmentVisitedBinding
import com.example.wanderlist.viewmodel.VisitedViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class VisitedFragment : Fragment() {

    private var _binding: FragmentVisitedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VisitedViewModel by viewModels()
    private lateinit var adapter: VisitedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisitedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeToDelete()
        observeViewModel()

        binding.fabAdd.setOnClickListener {
            showAddVisitedDialog()
        }

        viewModel.loadVisited()
    }
    private fun setupRecyclerView() {
        adapter = VisitedAdapter(
            items = emptyList(),
            onItemClick = { destination -> showEditRatingDialog(destination) },
            onReturnToWishlistClick = { destination -> viewModel.returnToWishlist(destination) }
        )
        binding.rvVisited.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVisited.adapter = adapter
    }

    private fun setupSwipeToDelete() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val destination = adapter.getDestinationAt(position)
                viewModel.deleteDestination(destination.id)
            }

            override fun onChildDraw(
                c: android.graphics.Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val background = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#E53935")
                }

                val rect = if (dX > 0) {
                    android.graphics.RectF(
                        itemView.left.toFloat(), itemView.top.toFloat(),
                        dX, itemView.bottom.toFloat()
                    )
                } else {
                    android.graphics.RectF(
                        itemView.right.toFloat() + dX, itemView.top.toFloat(),
                        itemView.right.toFloat(), itemView.bottom.toFloat()
                    )
                }
                c.drawRect(rect, background)

                val icon = androidx.core.content.ContextCompat.getDrawable(
                    requireContext(), android.R.drawable.ic_menu_delete
                )
                icon?.let {
                    val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                    val iconTop = itemView.top + iconMargin
                    val iconBottom = iconTop + it.intrinsicHeight

                    if (dX > 0) {
                        it.setBounds(
                            itemView.left + iconMargin, iconTop,
                            itemView.left + iconMargin + it.intrinsicWidth, iconBottom
                        )
                    } else {
                        it.setBounds(
                            itemView.right - iconMargin - it.intrinsicWidth, iconTop,
                            itemView.right - iconMargin, iconBottom
                        )
                    }
                    it.draw(c)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvVisited)
    }

    private fun observeViewModel() {
        viewModel.destinations.observe(viewLifecycleOwner) { destinations ->
            adapter.updateData(destinations)
            binding.tvEmptyState.visibility = if (destinations.isEmpty()) View.VISIBLE else View.GONE
            binding.rvVisited.visibility = if (destinations.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showAddVisitedDialog() {
        val dialogBinding = DialogAddVisitedBinding.inflate(layoutInflater)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Nova posjećena destinacija")
            .setView(dialogBinding.root)
            .setPositiveButton("Dodaj") { _, _ ->
                val name = dialogBinding.etName.text.toString().trim()
                val country = dialogBinding.etCountry.text.toString().trim()
                val rating = dialogBinding.ratingBar.rating.toInt()
                val review = dialogBinding.etReview.text.toString().trim()

                if (name.isNotEmpty()) {
                    viewModel.addVisited(name, country, rating, review)
                }
            }
            .setNegativeButton("Odustani", null)
            .show()
    }

    private fun showEditRatingDialog(destination: Destination) {
        val dialogBinding = DialogAddVisitedBinding.inflate(layoutInflater)
        dialogBinding.etName.setText(destination.name)
        dialogBinding.etCountry.setText(destination.country)
        dialogBinding.ratingBar.rating = destination.rating.toFloat()
        dialogBinding.etReview.setText(destination.notes)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Uredi posjećenu destinaciju")
            .setView(dialogBinding.root)
            .setPositiveButton("Spremi") { _, _ ->
                val newRating = dialogBinding.ratingBar.rating.toInt()
                val newReview = dialogBinding.etReview.text.toString().trim()
                viewModel.updateVisited(destination, newRating, newReview)
            }
            .setNegativeButton("Odustani", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}