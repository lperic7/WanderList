package com.example.wanderlist.ui.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wanderlist.data.model.Destination
import com.example.wanderlist.databinding.DialogAddDestinationBinding
import com.example.wanderlist.databinding.FragmentWishlistBinding
import com.example.wanderlist.viewmodel.WishlistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WishlistViewModel by viewModels()
    private lateinit var adapter: DestinationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeToDelete()
        observeViewModel()

        binding.fabAdd.setOnClickListener {
            showAddDestinationDialog()
        }

        viewModel.loadDestinations()
    }

    private fun setupRecyclerView() {
        adapter = DestinationAdapter(
            items = emptyList(),
            lifecycleScope = viewLifecycleOwner.lifecycleScope,
            onItemClick = { destination -> showEditDestinationDialog(destination) },
            onMarkVisitedClick = { destination ->
                viewModel.markAsVisited(destination)
                Toast.makeText(
                    requireContext(),
                    "${destination.name} dodano u posjećeno",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        binding.rvDestinations.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDestinations.adapter = adapter
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

        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvDestinations)
    }

    private fun observeViewModel() {
        viewModel.destinations.observe(viewLifecycleOwner) { destinations ->
            adapter.updateData(destinations)
            binding.tvEmptyState.visibility = if (destinations.isEmpty()) View.VISIBLE else View.GONE
            binding.rvDestinations.visibility = if (destinations.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showAddDestinationDialog() {
        val dialogBinding = DialogAddDestinationBinding.inflate(layoutInflater)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Nova destinacija")
            .setView(dialogBinding.root)
            .setPositiveButton("Dodaj") { _, _ ->
                val name = dialogBinding.etName.text.toString().trim()
                val country = dialogBinding.etCountry.text.toString().trim()

                if (name.isNotEmpty()) {
                    viewModel.addDestination(name, country)
                }
            }
            .setNegativeButton("Odustani", null)
            .show()
    }

    private fun showEditDestinationDialog(destination: Destination) {
        val dialogBinding = DialogAddDestinationBinding.inflate(layoutInflater)
        dialogBinding.etName.setText(destination.name)
        dialogBinding.etCountry.setText(destination.country)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Uredi destinaciju")
            .setView(dialogBinding.root)
            .setPositiveButton("Spremi") { _, _ ->
                val name = dialogBinding.etName.text.toString().trim()
                val country = dialogBinding.etCountry.text.toString().trim()
                viewModel.updateDestination(destination.id, name, country)
            }
            .setNegativeButton("Odustani", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}