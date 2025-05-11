package com.app.lico.ui.screens.lists

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.app.lico.R
import com.app.lico.models.ListType
import com.app.lico.models.Lists
import com.app.lico.models.toIconRes
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsCard(
    list: Lists,
    listType: ListType?,
    onClick: () -> Unit = {},
    onRename: (String) -> Unit = {},
    onDelete: () -> Unit = {},
    onCopy: (Lists) -> Unit = {},
) {
    if (listType == null) {
        throw Exception("ListsScreen: listType is null")
    }
    val totalItems = list.items.size
    val checkedItems = list.items.count { it.isChecked }

    var showBottomSheet by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showCopyDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var renameInput by remember { mutableStateOf(list.name) }

    val context = LocalContext.current

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

                // OPTIONS
                Column(modifier = Modifier.padding(16.dp)) {

                    // EDIT
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

                    // SHARE
                    TextButton(onClick = {
                        val shareText = buildString {
                            appendLine("Lista: ${list.name}")
                            list.items.forEach { item ->
                                appendLine("- ${item.name} (${item.quantity} ${item.unit})")
                            }
                        }
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Compartir lista")
                        context.startActivity(shareIntent)
                    }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share Icon",
                            modifier = Modifier.size(25.dp),
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Compartir",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }

                    // COPY
                    TextButton(onClick = {
                        showBottomSheet = false
                        showCopyDialog = true
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_file_copy_24),
                            contentDescription = "Copy Icon",
                            modifier = Modifier.size(25.dp),
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Copiar",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }

                    // DELETE
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
        RenameListDialog(
            renameInput = renameInput,
            onRenameInputChange = { renameInput = it },
            onConfirm = {
                onRename(renameInput)
                showRenameDialog = false
            },
            onDismiss = {
                showRenameDialog = false
                renameInput = list.name
            }
        )
    }

    // COPY DIALOG
    if (showCopyDialog) {
        CopyListDialog(
            renameInput = renameInput,
            onRenameInputChange = { renameInput = it },
            list = list,
            onCopy = {
                val copiedList = list.copy(
                    id = 0L,
                    name = renameInput,
                    items = list.items.map {
                        it.copy(id = 0L, isChecked = false)
                        }.toMutableList()
                    )
                onCopy(copiedList)
                showCopyDialog = false
            },
            onDismiss = {
                showCopyDialog = false
                renameInput = list.name
            }
        )
    }

    // DELETE CONFIRMATION
    if (showDeleteConfirm) {
        DeleteListDialog(
            onConfirm = {
                onDelete()
                showDeleteConfirm = false
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 8.dp, horizontal = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color = Color(listType.colorHex.toColorInt()).copy(alpha = 0.5f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = listType.toIconRes()),
                    contentDescription = null,
                    tint = Color(listType.colorHex.toColorInt()),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title and subtitle
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = list.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = listType.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Item count
            Text(
                text = "${checkedItems}/${totalItems}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            // More icon
            IconButton(onClick = { showBottomSheet = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Más opciones"
                )
            }
        }
        // Divider
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
    }
}