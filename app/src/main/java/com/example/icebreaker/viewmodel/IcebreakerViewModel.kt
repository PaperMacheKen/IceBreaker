package com.example.icebreaker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.icebreaker.data.DatabaseHelper
import com.example.icebreaker.data.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IcebreakerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DatabaseHelper(application)

    // ── Database lists (shown in Input mode) ──────────────────────────────
    private val _tops    = MutableStateFlow<List<Person>>(emptyList())
    val tops: StateFlow<List<Person>> = _tops.asStateFlow()

    private val _bottoms = MutableStateFlow<List<Person>>(emptyList())
    val bottoms: StateFlow<List<Person>> = _bottoms.asStateFlow()

    // ── Game mode selections ───────────────────────────────────────────────
    private val _selectedTop    = MutableStateFlow<Person?>(null)
    val selectedTop: StateFlow<Person?> = _selectedTop.asStateFlow()

    private val _selectedBottom = MutableStateFlow<Person?>(null)
    val selectedBottom: StateFlow<Person?> = _selectedBottom.asStateFlow()

    init { refresh() }

    // ─────────────────────── Input Mode ──────────────────────────────────
    fun addTop(name: String) = viewModelScope.launch(Dispatchers.IO) {
        if (name.isBlank()) return@launch
        db.insertTop(name.trim())
        _tops.value = db.getAllTops()
    }

    fun addBottom(name: String) = viewModelScope.launch(Dispatchers.IO) {
        if (name.isBlank()) return@launch
        db.insertBottom(name.trim())
        _bottoms.value = db.getAllBottoms()
    }

    fun updateTop(id: Long, name: String) = viewModelScope.launch(Dispatchers.IO) {
        if (name.isBlank()) return@launch
        db.updateTopName(id, name.trim())
        _tops.value = db.getAllTops()
    }

    fun updateBottom(id: Long, name: String) = viewModelScope.launch(Dispatchers.IO) {
        if (name.isBlank()) return@launch
        db.updateBottomName(id, name.trim())
        _bottoms.value = db.getAllBottoms()
    }

    fun deleteTop(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        db.deleteTop(id)
        _tops.value = db.getAllTops()
    }

    fun deleteBottom(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        db.deleteBottom(id)
        _bottoms.value = db.getAllBottoms()
    }

    // ─────────────────────── Game Mode ───────────────────────────────────

    /**
     * Picks a random available Top. Returns true on success, false if exhausted.
     */
    suspend fun randomTop(): Boolean = withContext(Dispatchers.IO) {
        val available = db.getAvailableTops()
        if (available.isEmpty()) {
            false
        } else {
            _selectedTop.value = available.random()
            true
        }
    }

    /**
     * Picks a random available Bottom. Returns true on success, false if exhausted.
     */
    suspend fun randomBottom(): Boolean = withContext(Dispatchers.IO) {
        val available = db.getAvailableBottoms()
        if (available.isEmpty()) {
            false
        } else {
            _selectedBottom.value = available.random()
            true
        }
    }

    suspend fun rerollTop(): Boolean = randomTop()
    suspend fun rerollBottom(): Boolean = randomBottom()

    /** Marks both selected names as used, then resets selections. */
    fun acceptPair() = viewModelScope.launch(Dispatchers.IO) {
        _selectedTop.value?.let    { db.markTopUsed(it.id) }
        _selectedBottom.value?.let { db.markBottomUsed(it.id) }
        _selectedTop.value    = null
        _selectedBottom.value = null
        _tops.value    = db.getAllTops()
        _bottoms.value = db.getAllBottoms()
    }

    fun resetGameSelections() {
        _selectedTop.value    = null
        _selectedBottom.value = null
    }

    // ─────────────────────── Persistent Toolbar ──────────────────────────
    fun clearTopUsed() = viewModelScope.launch(Dispatchers.IO) {
        db.clearTopUsed()
        _tops.value = db.getAllTops()
    }

    fun clearBottomUsed() = viewModelScope.launch(Dispatchers.IO) {
        db.clearBottomUsed()
        _bottoms.value = db.getAllBottoms()
    }

    fun clearAllData() = viewModelScope.launch(Dispatchers.IO) {
        db.clearAllData()
        _tops.value           = emptyList()
        _bottoms.value        = emptyList()
        _selectedTop.value    = null
        _selectedBottom.value = null
    }

    // ─────────────────────── Helpers ─────────────────────────────────────
    private fun refresh() = viewModelScope.launch(Dispatchers.IO) {
        _tops.value    = db.getAllTops()
        _bottoms.value = db.getAllBottoms()
    }
}
