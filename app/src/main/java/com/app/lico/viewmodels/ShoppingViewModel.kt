package com.app.lico.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.lico.data.db.dao.ShoppingItemDao
import com.app.lico.data.db.dao.ShoppingListDao
import com.app.lico.data.db.entities.toDomain
import com.app.lico.data.db.entities.toEntity
import com.app.lico.models.ShoppingItem
import com.app.lico.models.ShoppingList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val listDao: ShoppingListDao,
    private val itemDao: ShoppingItemDao
) : ViewModel() {

    private val _lists = MutableStateFlow<List<ShoppingList>>(emptyList())
    val lists: StateFlow<List<ShoppingList>> = _lists

    fun loadShoppingLists() {
        viewModelScope.launch {
            val listEntities = listDao.getAllLists()
            val fullLists = listEntities.map { list ->
                val items = itemDao.getItemsByList(list.id).map { it.toDomain() }
                list.toDomain(items)
            }
            _lists.value = fullLists
        }
    }

    fun addShoppingList(list: ShoppingList) {
        viewModelScope.launch {
            val listId = listDao.insertList(list.toEntity())
            list.items.forEach { item ->
                itemDao.insertItem(item.toEntity(listId))
            }
            loadShoppingLists()
        }
    }

    fun deleteShoppingList(list: ShoppingList) {
        viewModelScope.launch {
            listDao.deleteList(list.toEntity())
            loadShoppingLists()
        }
    }

    fun renameShoppingList(list: ShoppingList, newName: String) {
        viewModelScope.launch {
            val updatedList = list.copy(name = newName)
            listDao.insertList(updatedList.toEntity())
            loadShoppingLists()
        }
    }

    fun addItemToList(name: String, quantity: Double, unit: String, listId: Long) {
        viewModelScope.launch {
            itemDao.insertItem(
                ShoppingItem(
                    id = 0,
                    name = name,
                    quantity = quantity,
                    unit = unit,
                    isPurchased = false
                ).toEntity(listId)
            )
            loadShoppingLists()
        }
    }

    fun toggleItemPurchased(item: ShoppingItem, listId: Long) {
        viewModelScope.launch {
            itemDao.insertItem(
                item.copy(isPurchased = !item.isPurchased).toEntity(listId)
            )
            loadShoppingLists()
        }
    }

}
