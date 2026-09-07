package com.example.wanderlist.ui.visited

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlist.data.model.Destination
import com.example.wanderlist.viewmodel.VisitedViewModel

@Composable
fun StarRow(
    rating: Int,
    editable: Boolean = false,
    onRatingChange: (Int) -> Unit = {}
) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier
                    .size(22.dp)
                    .then(
                        if (editable) Modifier.clickable { onRatingChange(i) } else Modifier
                    )
            )
        }
    }
}

@Composable
fun VisitedCard(
    destination: Destination,
    onClick: () -> Unit,
    onReturnToWishlist: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF3E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Posjećeno",
                        tint = Color(0xFFFF9800)
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
                }

                IconButton(onClick = onReturnToWishlist) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Vrati na wishlistu"
                    )
                }
            }

            StarRow(rating = destination.rating)

            if (destination.notes.isNotBlank()) {
                Text(
                    text = destination.notes,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp, start = 10.dp)
                )
            }
        }
    }
}

@Composable
fun AddVisitedDialog(
    errorMessage: String? = null,
    onDismiss: () -> Unit,
    onConfirm: (name: String, country: String, rating: Int, review: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(0) }
    var review by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova posjećena destinacija") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Naziv destinacije") },
                    isError = errorMessage != null,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Država") },
                    isError = errorMessage != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Text(
                    text = "Ocjena",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)
                )
                StarRow(rating = rating, editable = true, onRatingChange = { rating = it })
                OutlinedTextField(
                    value = review,
                    onValueChange = { review = it },
                    label = { Text("Recenzija") },
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) onConfirm(name.trim(), country.trim(), rating, review.trim())
            }) { Text("Dodaj") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Odustani") }
        }
    )
}

@Composable
fun EditVisitedDialog(
    destination: Destination,
    onDismiss: () -> Unit,
    onConfirm: (name: String, country: String, rating: Int, review: String) -> Unit
) {
    var name by remember { mutableStateOf(destination.name) }
    var country by remember { mutableStateOf(destination.country) }
    var rating by remember { mutableIntStateOf(destination.rating) }
    var review by remember { mutableStateOf(destination.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Uredi posjećenu destinaciju") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Naziv destinacije") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Država") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
                Text(
                    text = "Ocjena",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)
                )
                StarRow(rating = rating, editable = true, onRatingChange = { rating = it })
                OutlinedTextField(
                    value = review,
                    onValueChange = { review = it },
                    label = { Text("Recenzija") },
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(name.trim(), country.trim(), rating, review.trim())
            }) { Text("Spremi") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Odustani") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitedScreen() {
    val viewModel: VisitedViewModel = viewModel()
    val destinations by viewModel.destinations.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    var showAddDialog by remember { mutableStateOf(false) }
    var editingDestination by remember { mutableStateOf<Destination?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadVisited()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (destinations.isNotEmpty()) {
                Text(
                    text = "Povuci karticu ulijevo ili udesno za brisanje",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            text = "Još nemaš posjećenih destinacija.\nOznači ih na Wishlisti, ili dodaj direktno klikom na + gumb.",
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
                                    VisitedCard(
                                        destination = destination,
                                        onClick = { editingDestination = destination },
                                        onReturnToWishlist = { viewModel.returnToWishlist(destination) }
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
            Icon(Icons.Filled.Add, contentDescription = "Dodaj posjećenu destinaciju")
        }
    }

    var addErrorMessage by remember { mutableStateOf<String?>(null) }

    if (showAddDialog) {
        AddVisitedDialog(
            errorMessage = addErrorMessage,
            onDismiss = {
                showAddDialog = false
                addErrorMessage = null
            },
            onConfirm = { name, country, rating, review ->
                viewModel.addVisited(name, country, rating, review) { success, error ->
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
        EditVisitedDialog(
            destination = destination,
            onDismiss = { editingDestination = null },
            onConfirm = { name, country, rating, review ->
                viewModel.updateVisited(destination, name, country, rating, review)
                editingDestination = null
            }
        )
    }
}