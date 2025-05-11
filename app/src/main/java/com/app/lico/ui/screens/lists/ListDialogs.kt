package com.app.lico.ui.screens.lists

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.app.lico.models.Lists

@Composable
fun RenameListDialog(
    renameInput: String,
    onRenameInputChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = { Text("Renombrar lista") },
        text = {
            OutlinedTextField(
                value = renameInput,
                onValueChange = onRenameInputChange,
                label = { Text("Nuevo nombre") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CopyListDialog(
    renameInput: String,
    onRenameInputChange: (String) -> Unit,
    list: Lists,
    onCopy: (Lists) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = { Text("Copiar lista") },
        text = {
            OutlinedTextField(
                value = renameInput,
                onValueChange = onRenameInputChange,
                label = { Text("Nuevo nombre") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = {
                val copiedList = list.copy(
                    id = 0L,
                    name = renameInput,
                    items = list.items.map {
                        it.copy(id = 0L, isChecked = false)
                    }.toMutableList()
                )
                onCopy(copiedList)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DeleteListDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = { Text("Eliminar lista") },
        text = { Text("¿Seguro que quieres eliminar esta lista? Esta acción no se puede deshacer.") },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                onClick = onConfirm
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
