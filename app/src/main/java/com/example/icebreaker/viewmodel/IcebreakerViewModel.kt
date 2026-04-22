package com.example.icebreaker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.icebreaker.BuildConfig
import com.example.icebreaker.data.DatabaseHelper
import com.example.icebreaker.data.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * ViewModel responsible for managing the state and logic of the Icebreaker app.
 * It interacts with the DatabaseHelper to persist participant names and their "used" status.
 */
class IcebreakerViewModel(application: Application) : AndroidViewModel(application) {

    // Internal reference to the local SQLite database helper
    private val db = DatabaseHelper(application)

    // ── Database State (Input Mode) ───────────────────────────────────────
    
    // Internal mutable state flow for the list of Tops
    private val _tops    = MutableStateFlow<List<Person>>(emptyList())
    // Public read-only state flow observed by the UI
    val tops: StateFlow<List<Person>> = _tops.asStateFlow()

    // Internal mutable state flow for the list of Bottoms
    private val _bottoms = MutableStateFlow<List<Person>>(emptyList())
    // Public read-only state flow observed by the UI
    val bottoms: StateFlow<List<Person>> = _bottoms.asStateFlow()

    // ── Update Check ─────────────────────────────────────────────────────
    private val _isUpdateAvailable = MutableStateFlow(false)
    val isUpdateAvailable: StateFlow<Boolean> = _isUpdateAvailable.asStateFlow()

    val currentVersion: String = BuildConfig.VERSION_NAME

    // ── Active Game Selections ───────────────────────────────────────────
    
    // The currently selected participant from the Tops list
    private val _selectedTop    = MutableStateFlow<Person?>(null)
    val selectedTop: StateFlow<Person?> = _selectedTop.asStateFlow()

    // The currently selected participant from the Bottoms list
    private val _selectedBottom = MutableStateFlow<Person?>(null)
    val selectedBottom: StateFlow<Person?> = _selectedBottom.asStateFlow()

    // Load initial data from database on initialization
    init {
        refresh()
        checkForUpdates()
    }

    private fun checkForUpdates() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val url = URL("https://api.github.com/repos/PaperMacheKen/IceBreaker/releases/latest")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json")

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                val latestTag = json.getString("tag_name").removePrefix("v")

                // Simple version comparison
                if (latestTag != currentVersion) {
                    _isUpdateAvailable.value = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ─────────────────────── Input Mode Operations ───────────────────────

    /** Adds a new Top to the database and refreshes the local list. */
    fun addTop(name: String) = viewModelScope.launch(Dispatchers.IO) {
        if (name.isBlank()) return@launch
        db.insertTop(name.trim())
        _tops.value = db.getAllTops()
    }

    /** Adds a new Bottom to the database and refreshes the local list. */
    fun addBottom(name: String) = viewModelScope.launch(Dispatchers.IO) {
        if (name.isBlank()) return@launch
        db.insertBottom(name.trim())
        _bottoms.value = db.getAllBottoms()
    }

    /** Updates the name of an existing Top record. */
    fun updateTop(id: Long, name: String) = viewModelScope.launch(Dispatchers.IO) {
        if (name.isBlank()) return@launch
        db.updateTopName(id, name.trim())
        _tops.value = db.getAllTops()
    }

    /** Updates the name of an existing Bottom record. */
    fun updateBottom(id: Long, name: String) = viewModelScope.launch(Dispatchers.IO) {
        if (name.isBlank()) return@launch
        db.updateBottomName(id, name.trim())
        _bottoms.value = db.getAllBottoms()
    }

    /** Removes a Top from the database. */
    fun deleteTop(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        db.deleteTop(id)
        _tops.value = db.getAllTops()
    }

    /** Removes a Bottom from the database. */
    fun deleteBottom(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        db.deleteBottom(id)
        _bottoms.value = db.getAllBottoms()
    }

    // ─────────────────────── Game Mode Logic ────────────────────────────

    /**
     * Randomly selects a Top who has not yet been used in this session.
     * @return true if a name was found, false if all available names are exhausted.
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
     * Randomly selects a Bottom who has not yet been used in this session.
     * @return true if a name was found, false if all available names are exhausted.
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

    /** Re-selects a new random Top (synonym for randomTop). */
    suspend fun rerollTop(): Boolean = randomTop()
    
    /** Re-selects a new random Bottom (synonym for randomBottom). */
    suspend fun rerollBottom(): Boolean = randomBottom()

    /** 
     * Confirms the current pairing, marks both participants as "used" in the database,
     * and clears the current selection state.
     */
    fun acceptPair() = viewModelScope.launch(Dispatchers.IO) {
        _selectedTop.value?.let    { db.markTopUsed(it.id) }
        _selectedBottom.value?.let { db.markBottomUsed(it.id) }
        _selectedTop.value    = null
        _selectedBottom.value = null
        _tops.value    = db.getAllTops()
        _bottoms.value = db.getAllBottoms()
    }

    /** Clears only the temporary visual selection in the game UI without marking as used. */
    fun resetGameSelections() {
        _selectedTop.value    = null
        _selectedBottom.value = null
    }

    // ─────────────────────── Maintenance / Global Actions ──────────────────

    /** Resets the "used" status for all Tops so they can be picked again. */
    fun clearTopUsed() = viewModelScope.launch(Dispatchers.IO) {
        db.clearTopUsed()
        _tops.value = db.getAllTops()
    }

    /** Resets the "used" status for all Bottoms so they can be picked again. */
    fun clearBottomUsed() = viewModelScope.launch(Dispatchers.IO) {
        db.clearBottomUsed()
        _bottoms.value = db.getAllBottoms()
    }

    /** Wipes all data (Tops, Bottoms, and session status) from the application database. */
    fun clearAllData() = viewModelScope.launch(Dispatchers.IO) {
        db.clearAllData()
        _tops.value           = emptyList()
        _bottoms.value        = emptyList()
        _selectedTop.value    = null
        _selectedBottom.value = null
    }

    // ─────────────────────── Private Helpers ─────────────────────────────
    
    /** Fetches the latest lists of participants from the database. */
    private fun refresh() = viewModelScope.launch(Dispatchers.IO) {
        _tops.value    = db.getAllTops()
        _bottoms.value = db.getAllBottoms()
    }
}
