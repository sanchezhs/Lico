package com.app.lico.ui.screens.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.lico.R
import com.app.lico.ui.shared.myTopAppBarColors
import com.app.lico.viewmodels.ShoppingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    modifier: Modifier = Modifier,
    viewModel: ShoppingViewModel = hiltViewModel(),
    onNavigateNewList: () -> Unit,
    onNavigateListDetail: (listId: Long) -> Unit,
) {
    val shoppingLists by viewModel.lists.collectAsState()
    val listsTypes by viewModel.listsTypes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis listas") },
                colors = myTopAppBarColors(),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateNewList() }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Lista")
            }
        }
    ) {
        innerPadding ->
            Box(modifier = modifier.padding(innerPadding).fillMaxSize()) {
                if (shoppingLists.isEmpty()) {
                    EmptyListPlaceholder(
                        drawableId = R.drawable.list,
                        title = "No tienes listas todavía",
                        subtitle = "Pulsa el botón para crear tu primera lista",
                    )
                } else {
                    LazyColumn(
                        modifier = modifier
                            .padding(12.dp)
                            .fillMaxSize()
                    ) {
                        items(shoppingLists) { list ->
                            ListsCard(
                                list = list,
                                listType = listsTypes.find { it.id == list.typeId },
                                onClick = { onNavigateListDetail(list.id) },
                                onRename = { newName -> viewModel.renameLists(list, newName) },
                                onDelete = { viewModel.deleteLists(list) },
                                onCopy = { newList -> viewModel.addLists(newList) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
    }
}