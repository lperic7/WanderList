package com.example.wanderlist.ui.wishlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlist.BuildConfig
import com.example.wanderlist.data.model.Destination
import com.example.wanderlist.data.remote.RetrofitInstance
import com.example.wanderlist.ui.theme.AppColors
import com.example.wanderlist.viewmodel.WishlistViewModel
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle

@Composable
fun DestinationCard(
    destination: Destination,
    onClick: () -> Unit,
    onMarkVisited: () -> Unit
) {
    var weatherText by remember(destination.id) { mutableStateOf("Učitavanje vremena...") }

    LaunchedEffect(destination.id) {
        try {
            val response = RetrofitInstance.weatherApi.getCurrentWeather(
                lat = destination.latitude,
                lon = destination.longitude,
                apiKey = BuildConfig.OPEN_WEATHER_API_KEY
            )
            val temp = response.main.temp.toInt()
            val description = response.weather.firstOrNull()?.description ?: ""
            weatherText = "$temp°C, $description"
        } catch (e: Exception) {
            weatherText = "Vrijeme nedostupno"
        }
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = "Destinacija",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = destination.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = destination.country,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = weatherText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            IconButton(onClick = onMarkVisited) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Označi kao posjećeno",
                    tint = AppColors.Success
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen() {
    val viewModel: WishlistViewModel = viewModel()
    val destinations by viewModel.destinations.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    var showAddDialog by remember { mutableStateOf(false) }
    var editingDestination by remember { mutableStateOf<Destination?>(null) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadDestinations()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (destinations.isNotEmpty()) {
                Text(
                    text = "Povuci karticu ulijevo ili udesno za brisanje",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 12.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Top,
                            trim = LineHeightStyle.Trim.Both
                        )
                    ),
                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    destinations.isEmpty() -> {
                        Text(
                            text = "Još nemaš destinacija na listi želja.\nDodaj prvu klikom na + gumb dolje desno.",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 88.dp)
                        ) {
                            items(destinations, key = { it.id }) { destination ->
                                val dismissState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = { value ->
                                        if (value == SwipeToDismissBoxValue.StartToEnd || value == SwipeToDismissBoxValue.EndToStart) {
                                            viewModel.deleteDestination(destination.id)
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                )

                                SwipeToDismissBox(
                                    state = dismissState,
                                    backgroundContent = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(MaterialTheme.colorScheme.error),
                                            contentAlignment = Alignment.CenterEnd
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = "Obriši",
                                                tint = Color.White,
                                                modifier = Modifier.padding(end = 24.dp)
                                            )
                                        }
                                    }
                                ) {
                                    DestinationCard(
                                        destination = destination,
                                        onClick = { editingDestination = destination },
                                        onMarkVisited = { viewModel.markAsVisited(destination) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Dodaj destinaciju")
        }
    }

    var addErrorMessage by remember { mutableStateOf<String?>(null) }
    var editErrorMessage by remember { mutableStateOf<String?>(null) }

    if (showAddDialog) {
        AddEditDestinationDialog(
            title = "Nova destinacija",
            confirmLabel = "Dodaj",
            errorMessage = addErrorMessage,
            onDismiss = {
                showAddDialog = false
                addErrorMessage = null
            },
            onConfirm = { name, country ->
                viewModel.addDestination(name, country) { success, error ->
                    if (success) {
                        showAddDialog = false
                        addErrorMessage = null
                    } else {
                        addErrorMessage = error
                    }
                }
            }
        )
    }

    editingDestination?.let { destination ->
        AddEditDestinationDialog(
            initialName = destination.name,
            initialCountry = destination.country,
            title = "Uredi destinaciju",
            confirmLabel = "Spremi",
            errorMessage = editErrorMessage,
            onDismiss = {
                editingDestination = null
                editErrorMessage = null
            },
            onConfirm = { name, country ->
                viewModel.updateDestination(destination.id, name, country) { success, error ->
                    if (success) {
                        editingDestination = null
                        editErrorMessage = null
                    } else {
                        editErrorMessage = error
                    }
                }
            }
        )
    }
}