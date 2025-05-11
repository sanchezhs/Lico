package com.app.lico.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.lico.data.db.dao.ListItemDao
import com.app.lico.data.db.dao.ListDao
import com.app.lico.data.db.dao.ListTypeDao
import com.app.lico.data.db.entities.toDomain
import com.app.lico.data.db.entities.toEntity
import com.app.lico.models.ListItem
import com.app.lico.models.ListType
import com.app.lico.models.Lists
import com.app.lico.models.SortOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val listDao: ListDao,
    private val itemDao: ListItemDao,
    private val listTypeDao: ListTypeDao
) : ViewModel() {

    private val _lists = MutableStateFlow<List<Lists>>(emptyList())
    val lists: StateFlow<List<Lists>> = _lists

    private val _listsTypes = MutableStateFlow<List<ListType>>(emptyList())
    val listsTypes: StateFlow<List<ListType>> = _listsTypes

    init {
        observeLists()
    }

    private fun observeLists() {
        viewModelScope.launch {
            combine(
                listDao.getAllLists(),               // Flow<List<ListEntity>>
                itemDao.getAllItemsFlow(),           // Flow<List<ItemEntity>>
                listTypeDao.getAllListsTypes()       // Flow<List<ListTypeEntity>>
            ) { listEntities, allItems, listTypeEntities ->
                // build a map of typeId -> ListType domain for quick lookup
                val typesById = listTypeEntities
                    .associateBy { it.id }
                    .mapValues { it.value.toDomain() }

                // map each list + its items + its type
                val fullLists = listEntities.map { listEnt ->
                    val items = allItems
                        .filter { it.listId == listEnt.id }
                        .map { it.toDomain() }

                    val type = typesById[listEnt.typeId]
                    listEnt.toDomain(items, type)
                }
                val allTypes = typesById.values.toList()
                Pair(fullLists, allTypes)
            }
                .collectLatest { (lists, types) ->
                    _lists.value = lists
                    _listsTypes.value = types
                }
        }
    }

    fun getListWithItems(listId: Long): Flow<Lists?> {
        return listDao.getListById(listId)
            .combine(itemDao.getItemsForListFlow(listId)) { listEntity, items ->
                listEntity?.toDomain(items.map { it.toDomain() })
            }
    }

//    fun loadListss() {
//        viewModelScope.launch {
//            val listEntities = listDao.getAllLists()
//            val fullLists = listEntities.map { list ->
//                val items = itemDao.getItemsByList(list.).map { it.toDomain() }
//                list.toDomain(items)
//            }
//            _lists.value = fullLists
//        }
//    }

    fun addLists(list: Lists) {
        viewModelScope.launch {
            val listId = listDao.insertList(list.toEntity())
            list.items.forEach { item ->
                itemDao.insertItem(item.toEntity(listId))
            }
//            loadListss()
        }
    }

    fun deleteLists(list: Lists) {
        viewModelScope.launch {
            listDao.deleteList(list.toEntity())
//            loadListss()
        }
    }

    fun renameLists(list: Lists, newName: String) {
        viewModelScope.launch {
            val updatedList = list.copy(name = newName)
            listDao.insertList(updatedList.toEntity())
//            loadListss()
        }
    }

    fun addItemToList(name: String, quantity: Double, unit: String, listId: Long) {
        viewModelScope.launch {
            val currentItems = itemDao.getItemsForList(listId)
            val nextPosition = currentItems.maxOfOrNull { it.position }?.plus(1) ?: 0

            itemDao.insertItem(
                ListItem(
                    id = 0,
                    name = name,
                    quantity = quantity,
                    unit = unit,
                    isChecked = false,
                    position = nextPosition,
                ).toEntity(listId)
            )
//            loadListss()
        }
    }

    fun toggleItemPurchased(item: ListItem, listId: Long) {
        viewModelScope.launch {
            itemDao.insertItem(
                item.copy(isChecked = !item.isChecked).toEntity(listId)
            )
//            loadListss()
        }
    }

    fun updateItem(item: ListItem, newName: String, newQty: Double, newUnit: String, listId: Long) {
        viewModelScope.launch {
            val updated = item.copy(name = newName, quantity = newQty, unit = newUnit)
            itemDao.insertItem(updated.toEntity(listId))
//            loadListss()
        }
    }

    fun deleteItem(item: ListItem, listId: Long) {
        viewModelScope.launch {
            itemDao.deleteItem(item.toEntity(listId))
            normalizePositions(listId)
//            loadListss()
        }
    }

    fun updateSortOption(listId: Long, option: SortOption) {
        viewModelScope.launch {
            listDao.updateSortOption(listId, option.name)
//            loadListss()
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
