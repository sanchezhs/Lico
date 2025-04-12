package com.app.lico.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.lico.models.ShoppingList
import com.app.lico.viewmodels.ShoppingViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListsScreen(
    modifier: Modifier = Modifier,
    viewModel: ShoppingViewModel = hiltViewModel(),
    onNavigateNewList: () -> Unit,
    onNavigateListDetail: (listId: Long) -> Unit,
) {
    val shoppingLists by viewModel.lists.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadShoppingLists()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis listas") },
            )
        },

    ) {
        innerPadding ->
            Box(modifier = modifier.padding(innerPadding).fillMaxSize()) {
                LazyColumn(
                    modifier = modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    items(shoppingLists) { list ->
                        ShoppingListCard(
                            list = list,
                            onClick = { onNavigateListDetail(list.id) },
                            onRename = { newName -> viewModel.renameShoppingList(list, newName) },
                            onDelete = { viewModel.deleteShoppingList(list) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                ExtendedFloatingActionButton(
                    onClick = { onNavigateNewList() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add List Icon",
                        modifier = Modifier.size(25.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Crear nueva lista")
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListCard(
    list: ShoppingList,
    onClick: () -> Unit = {},
    onRename: (String) -> Unit = {},
    onDelete: () -> Unit = {},
) {
    val totalItems = list.items.size
    val purchasedItems = list.items.count { it.isPurchased }
    val progress = if (totalItems > 0) purchasedItems / totalItems.toFloat() else 0f

    var showBottomSheet by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var renameInput by remember { mutableStateOf(list.name) }

    // BOTTOM SHEET
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            dragHandle = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BottomSheetDefaults.DragHandle()
                }
            }
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = "Administrar lista".uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
                    )
                }

                // Contenido
                Column(modifier = Modifier.padding(16.dp)) {
                    TextButton(onClick = {
                        showBottomSheet = false
                        showRenameDialog = true
                    }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Icon",
                            modifier = Modifier.size(25.dp),
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Renombrar",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }

                    TextButton(onClick = {
                        showBottomSheet = false
                        showDeleteConfirm = true
                    }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Icon",
                            modifier = Modifier.size(25.dp),
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Borrar",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

    }

    // RENAMING DIALOG
    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Renombrar lista") },
            text = {
                OutlinedTextField(
                    value = renameInput,
                    onValueChange = { renameInput = it },
                    label = { Text("Nuevo nombre") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onRename(renameInput)
                    showRenameDialog = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRenameDialog = false
                    renameInput = list.name
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DELETE CONFIRMATION
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar lista") },
            text = { Text("¿Seguro que quieres eliminar esta lista? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteConfirm = false
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // CARD
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = list.name.uppercase(Locale.getDefault()),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { showBottomSheet = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(MaterialTheme.shapes.small),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onPrimary,
                gapSize = 0.dp,
                drawStopIndicator = {},
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "$purchasedItems / $totalItems",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
