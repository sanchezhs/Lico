package com.app.lico.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.lico.data.mercadona.ProductEntity
import com.app.lico.ui.shared.myTopAppBarColors
import com.app.lico.viewmodels.ShoppingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    listId: Long,
    onBack: () -> Unit,
    viewModel: ShoppingViewModel = hiltViewModel(),
) {
    var itemName by remember { mutableStateOf("") }
    val selectedEntities = remember { mutableStateListOf<ProductEntity>() }
    val selectedNames = remember { mutableStateListOf<String>() }

    var isRenameDialogOpen by remember { mutableStateOf(false) }
    var renameIndex by remember { mutableIntStateOf(-1) }
    var renameText by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = { Text("Añadir ítem") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = myTopAppBarColors()
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Nuevo ítem") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = ButtonDefaults.buttonColors().contentColor,
                    disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor,
                    disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor
                ),
                onClick = {
                    selectedNames.add(itemName)
                    itemName = ""
                },
                enabled = itemName.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Añadir")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedNames.isNotEmpty() || selectedEntities.isNotEmpty()) {
                Text("Ítems existentes", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    selectedNames.forEachIndexed { index, name ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(name, modifier = Modifier.weight(1f))

                            IconButton(onClick = {
                                renameIndex = index
                                renameText = name
                                isRenameDialogOpen = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Renombrar")
                            }

                            IconButton(onClick = { selectedNames.removeAt(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Borrar")
                            }
                        }
                    }

                    selectedEntities.forEach { entity ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(entity.name ?: "Ítem", modifier = Modifier.weight(1f))
                                IconButton(onClick = { selectedEntities.remove(entity) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Borrar")
                                }
                            }
                            HorizontalDivider(thickness = 5.dp, color = Color(0xFFE0E0E0))
                        }
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                OutlinedButton(
                    onClick = {
                    selectedNames.clear()
                    selectedEntities.clear()
                },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Limpiar")
                }
                Button(
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = ButtonDefaults.buttonColors().contentColor,
                        disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor,
                        disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor
                    ),
                    onClick = {
                    selectedNames.forEach { name ->
                        viewModel.addItemToList(
                            name = name,
                            quantity = 1.0,
                            unit = "uds",
                            listId = listId
                        )
                    }
                    onBack()
                },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar todo")
                }
            }
        }

        // Rename Dialog
        if (isRenameDialogOpen) {
            AlertDialog(
                onDismissRequest = { isRenameDialogOpen = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (renameIndex in selectedNames.indices && renameText.isNotBlank()) {
                                selectedNames[renameIndex] = renameText
                            }
                            isRenameDialogOpen = false
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isRenameDialogOpen = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Renombrar ítem") },
                text = {
                    OutlinedTextField(
                        value = renameText,
                        onValueChange = { renameText = it },
                        label = { Text("Nuevo nombre") },
                        singleLine = true
                    )
                }
            )
        }
    }
}