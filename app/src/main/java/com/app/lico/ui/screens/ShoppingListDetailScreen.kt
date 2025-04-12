package com.app.lico.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.lico.R
import com.app.lico.models.ShoppingItem
import com.app.lico.viewmodels.ShoppingViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListDetailScreen(
    listId: Long,
    onBack: () -> Unit,
    viewModel: ShoppingViewModel = hiltViewModel()
) {
    val list by viewModel.lists.collectAsState()
    val currentList = list.find { it.id == listId }

    var showAddDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQty by remember { mutableStateOf("1") }
    var itemUnit by remember { mutableStateOf("uds") }

    val purchasedItems = currentList?.items?.filter { it.isPurchased }
    val pendingItems = currentList?.items?.filterNot { it.isPurchased }
    var showPurchased by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadShoppingLists()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentList?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir producto")
            }
        }
    ) { innerPadding ->
        currentList?.let { list ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                items(pendingItems.orEmpty()) { item ->
                    ShoppingItemRow(
                        item,
                        listId,
                        viewModel = viewModel,
                        onTogglePurchased = {
                        viewModel.toggleItemPurchased(
                            item,
                            list.id
                        )
                    })
                    Spacer(modifier = Modifier.height(7.dp))
                }
                item {
                    if (!purchasedItems.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))

                        TextButton (
                            onClick = { showPurchased = !showPurchased },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("(${purchasedItems.size}) Mostrar productos comprados")
                        }

                        if (showPurchased) {
                            Spacer(modifier = Modifier.height(12.dp))

                            purchasedItems.forEach { item ->
                                ShoppingItemRow(
                                    viewModel = viewModel,
                                    listId = listId,
                                    item = item,
                                    isPurchased = true,
                                    onTogglePurchased = {
                                        viewModel.toggleItemPurchased(
                                            item,
                                            list.id
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            } // LazyColumn
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Nuevo producto") },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Producto") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = itemQty,
                        onValueChange = { itemQty = it },
                        label = { Text("Cantidad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = itemUnit,
                        onValueChange = { itemUnit = it },
                        label = { Text("Unidad") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = itemName.isNotBlank(),
                    onClick = {
                        viewModel.addItemToList(
                            name = itemName,
                            quantity = itemQty.toDoubleOrNull() ?: 1.0,
                            unit = itemUnit,
                            listId = listId
                        )
                        itemName = ""
                        itemQty = "1"
                        itemUnit = "uds"
                        showAddDialog = false
                    }
                ) {
                    Text("Añadir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingItemRow(
    item: ShoppingItem,
    listId: Long,
    onTogglePurchased: () -> Unit,
    isPurchased: Boolean = false,
    viewModel: ShoppingViewModel,
) {
    var showActionsDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var editName by remember { mutableStateOf(item.name) }
    var editQty by remember { mutableStateOf(item.quantity.toString()) }
    var editUnit by remember { mutableStateOf(item.unit) }
    var isSelected by remember { mutableStateOf(false) }

    val textColor = if (isPurchased) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
    val defaultBg = if (isPurchased) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surface
    val selectedBg = MaterialTheme.colorScheme.onTertiaryContainer
    val bgColor = if (isSelected) selectedBg else defaultBg

    val quantityColor = if (isPurchased) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface

    Surface(
        color = bgColor,
        shadowElevation = 4.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .background(color = bgColor)
                .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        isSelected = true
                        showActionsDialog = true
                    }
                )
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onTogglePurchased,
                modifier = Modifier.size(36.dp)
            ) {
                if (item.isPurchased) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Marcado",
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.baseline_radio_button_unchecked_24),
                        contentDescription = "No marcado",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${item.quantity} ${item.unit}",
                color = quantityColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }

    // DIALOGS
    if (showActionsDialog) {
        ModalBottomSheet(
            onDismissRequest = {
                showActionsDialog = false
                isSelected = false
            },
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
                        text = item.name.uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
                    )
                }

                // Contenido
                Column(modifier = Modifier.padding(16.dp)) {
                    TextButton(onClick = {
                        showActionsDialog = false
                        isSelected = false
                        showEditDialog = true
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
                        showActionsDialog = false
                        isSelected = false
                        showDeleteDialog = true
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

    // EDIT DIALOG
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar producto") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nombre") },
                        singleLine = true,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editQty,
                        onValueChange = { editQty = it },
                        label = { Text("Cantidad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editUnit,
                        onValueChange = { editUnit = it },
                        label = { Text("Unidad") },
                        singleLine = true,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = editName.isNotBlank(),
                    onClick = {
                        viewModel.updateItem(item, editName, editQty.toDoubleOrNull() ?: 1.0, editUnit, listId)
                        showEditDialog = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DELETE DIALOG
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                isSelected = false
            },
            title = { Text("Eliminar producto") },
            text = { Text("¿Seguro que quieres eliminar este producto?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteItem(item, listId)
                    showDeleteDialog = false
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    isSelected = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ActionsDialog(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Acciones",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Opciones
                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                    TextButton(
                        onClick = {
                            onDismiss()
                            onEdit()
                        }
                    ) {
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

                    TextButton(
                        onClick = {
                            onDismiss()
                            onDelete()
                        }
                    ) {
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismiss
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

