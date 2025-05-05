package com.app.lico.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.lico.data.mercadona.ProductEntity
import com.app.lico.ui.shared.myTopAppBarColors
import com.app.lico.viewmodels.ShoppingViewModel
import com.app.lico.viewmodels.SuggestionViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddProductScreen(
    listId: Long,
    onBack: () -> Unit,
    suggestionViewModel: SuggestionViewModel = hiltViewModel(),
    viewModel: ShoppingViewModel = hiltViewModel(),
) {
    // State for the search query
    var query by remember { mutableStateOf("") }

    // Live data of product suggestions
    val suggestions by suggestionViewModel.suggestions.collectAsState()
    val quickSuggestions = listOf("Leche", "Pan", "Huevos", "Arroz", "Agua")

    // Hold selected items: entities from API and custom names
    val selectedEntities = remember { mutableStateListOf<ProductEntity>() }
    val selectedNames = remember { mutableStateListOf<String>() }

    // Fetch suggestions whenever query changes
    LaunchedEffect(query) {
        suggestionViewModel.searchProducts(query)
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = { Text("Añadir producto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = myTopAppBarColors()
            )
        },
        floatingActionButton = {
            if (selectedEntities.isNotEmpty() || selectedNames.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        selectedEntities.forEach { product ->
                            viewModel.addItemToList(
                                name = product.name ?: "Producto",
                                quantity = 1.0,
                                unit = "uds",
                                listId = listId
                            )
                        }
                        selectedNames.forEach { name ->
                            viewModel.addItemToList(
                                name = name,
                                quantity = 1.0,
                                unit = "uds",
                                listId = listId
                            )
                        }
                        selectedEntities.clear()
                        selectedNames.clear()
                        onBack()
                    }
                ) {
                    Icon(Icons.Filled.Check, contentDescription = "Add products to list.")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar producto...") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick suggestion chips
            if (query.isBlank()) {
                Text("Sugerencias rápidas", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    quickSuggestions.forEach { name ->
                        FilterChip(
                            selected = name in selectedNames,
                            onClick = {
                                if (name in selectedNames) selectedNames.remove(name)
                                else selectedNames.add(name)
                            },
                            label = { Text(name) }
                        )
                    }
                }
            }

            // Custom query chip
            if (query.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                FilterChip(
                    selected = query in selectedNames,
                    onClick = {
                        if (query in selectedNames) selectedNames.remove(query)
                        else selectedNames.add(query)
                    },
                    label = { Text("Añadir \"$query\"") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Preview of selected products
            if (selectedEntities.isNotEmpty() || selectedNames.isNotEmpty()) {
                Text("Selección", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Show custom and quick names
                    selectedNames.forEach { name ->
                        FilterChip(
                            selected = true,
                            onClick = { selectedNames.remove(name) },
                            label = { Text(name) }
                        )
                    }
                    // Show entity-based selections
                    selectedEntities.forEach { product ->
                        FilterChip(
                            selected = true,
                            onClick = { selectedEntities.remove(product) },
                            label = { Text(product.name ?: "Producto") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (suggestions.isNotEmpty()) {
                suggestions.forEach { product ->
                    val isSelected = product in selectedEntities
                    SuggestionCard(
                        product = product,
                        isSelected = isSelected,
                        onToggleSelect = {
                            if (isSelected) selectedEntities.remove(product)
                            else selectedEntities.add(product)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SuggestionCard(
    product: ProductEntity,
    isSelected: Boolean,
    onToggleSelect: () -> Unit
) {
    // Card showing a suggested product, toggles selection on click
    Card(
        onClick = onToggleSelect,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Add,
                contentDescription = if (isSelected) "Selected" else "Select",
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = product.name ?: "Producto sin nombre",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
