package com.app.lico.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.lico.data.db.dao.ShoppingItemDao
import com.app.lico.data.db.dao.ShoppingListDao
import com.app.lico.data.db.entities.toDomain
import com.app.lico.data.db.entities.toEntity
import com.app.lico.models.ShoppingItem
import com.app.lico.models.ShoppingList
import com.app.lico.models.SortOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val listDao: ShoppingListDao,
    private val itemDao: ShoppingItemDao
) : ViewModel() {

    private val _lists = MutableStateFlow<List<ShoppingList>>(emptyList())
    val lists: StateFlow<List<ShoppingList>> = _lists

    init {
        observeLists()
    }

    private fun observeLists() {
        viewModelScope.launch {
            listDao.getAllLists()
                .combine(itemDao.getAllItemsFlow()) { listEntities, allItems ->
                    listEntities.map { list ->
                        val items = allItems.filter { it.listId == list.id }.map { it.toDomain() }
                        list.toDomain(items)
                    }
                }
                .collectLatest { fullLists ->
                    _lists.value = fullLists
                }
        }
    }

    fun getListWithItems(listId: Long): Flow<ShoppingList?> {
        return listDao.getListById(listId)
            .combine(itemDao.getItemsForListFlow(listId)) { listEntity, items ->
                listEntity?.toDomain(items.map { it.toDomain() })
            }
    }

//    fun loadShoppingLists() {
//        viewModelScope.launch {
//            val listEntities = listDao.getAllLists()
//            val fullLists = listEntities.map { list ->
//                val items = itemDao.getItemsByList(list.).map { it.toDomain() }
//                list.toDomain(items)
//            }
//            _lists.value = fullLists
//        }
//    }

    fun addShoppingList(list: ShoppingList) {
        viewModelScope.launch {
            val listId = listDao.insertList(list.toEntity())
            list.items.forEach { item ->
                itemDao.insertItem(item.toEntity(listId))
            }
//            loadShoppingLists()
        }
    }

    fun deleteShoppingList(list: ShoppingList) {
        viewModelScope.launch {
            listDao.deleteList(list.toEntity())
//            loadShoppingLists()
        }
    }

    fun renameShoppingList(list: ShoppingList, newName: String) {
        viewModelScope.launch {
            val updatedList = list.copy(name = newName)
            listDao.insertList(updatedList.toEntity())
//            loadShoppingLists()
        }
    }

    fun addItemToList(name: String, quantity: Double, unit: String, listId: Long) {
        viewModelScope.launch {
            val currentItems = itemDao.getItemsForList(listId)
            val nextPosition = currentItems.maxOfOrNull { it.position }?.plus(1) ?: 0

            itemDao.insertItem(
                ShoppingItem(
                    id = 0,
                    name = name,
                    quantity = quantity,
                    unit = unit,
                    isPurchased = false,
                    position = nextPosition,
                ).toEntity(listId)
            )
//            loadShoppingLists()
        }
    }

    fun toggleItemPurchased(item: ShoppingItem, listId: Long) {
        viewModelScope.launch {
            itemDao.insertItem(
                item.copy(isPurchased = !item.isPurchased).toEntity(listId)
            )
//            loadShoppingLists()
        }
    }

    fun updateItem(item: ShoppingItem, newName: String, newQty: Double, newUnit: String, listId: Long) {
        viewModelScope.launch {
            val updated = item.copy(name = newName, quantity = newQty, unit = newUnit)
            itemDao.insertItem(updated.toEntity(listId))
//            loadShoppingLists()
        }
    }

    fun deleteItem(item: ShoppingItem, listId: Long) {
        viewModelScope.launch {
            itemDao.deleteItem(item.toEntity(listId))
            normalizePositions(listId)
//            loadShoppingLists()
        }
    }

    fun updateSortOption(listId: Long, option: SortOption) {
        viewModelScope.launch {
            listDao.updateSortOption(listId, option.name)
//            loadShoppingLists()
        }
    }

    private fun normalizePositions(listId: Long) {
        viewModelScope.launch {
            val currentItems = itemDao.getItemsForList(listId)
                .sortedBy { it.position }

            currentItems.forEachIndexed { index, item ->
                if (item.position != index) {
                    itemDao.insertItem(item.copy(position = index))
                }
            }
        }
    }
}
