package com.app.lico.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.lico.data.mercadona.ProductEntity
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
    var query by remember { mutableStateOf("") }

    val suggestions by suggestionViewModel.suggestions.collectAsState()
    val quickSuggestions = listOf("Leche", "Pan", "Huevos", "Arroz", "Agua")

    LaunchedEffect(query) {
        suggestionViewModel.searchProducts(query)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir producto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar producto...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (suggestions.isNotEmpty()) {
                Text("Resultados", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                suggestions.forEach { product ->
                    SuggestionCard(product = product, onClick = {
                        viewModel.addItemToList(
                            name = product.name ?: "Producto sin nombre",
                            quantity = 1.0,
                            unit = product.referenceUnit ?: "uds",
                            listId = listId
                        )
                        onBack()
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Sugerencias rápidas", style = MaterialTheme.typography.titleMedium)

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                quickSuggestions.forEach { name ->
                    AssistChip(onClick = {
                        viewModel.addItemToList(
                            name,
                            quantity = 1.0,
                            unit = "uds",
                            listId = listId
                        )
                        onBack()
                    }, label = { Text(name) })
                }
            }
        }
    }
}

@Composable
fun SuggestionCard(product: ProductEntity, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = product.name ?: "malo", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${product.price} € / ${product.referenceUnit}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
