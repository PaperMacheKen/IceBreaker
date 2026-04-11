package com.example.icebreaker.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Person(
    val id: Long = 0,
    val name: String,
    val used: Boolean = false
)

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "icebreaker.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_TOPS    = "tops"
        const val TABLE_BOTTOMS = "bottoms"
        private const val COL_ID   = "id"
        private const val COL_NAME = "name"
        private const val COL_USED = "used"
    }

    override fun onCreate(db: SQLiteDatabase) {
        fun createSql(table: String) = """
            CREATE TABLE $table (
                $COL_ID   INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME TEXT    NOT NULL,
                $COL_USED INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createSql(TABLE_TOPS))
        db.execSQL(createSql(TABLE_BOTTOMS))
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TOPS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOTTOMS")
        onCreate(db)
    }

    // ─────────────────────── TOPS ────────────────────────
    fun getAllTops(): List<Person>      = getAll(TABLE_TOPS)
    fun getAvailableTops(): List<Person> = getAvailable(TABLE_TOPS)
    fun insertTop(name: String): Long  = insert(TABLE_TOPS, name)
    fun updateTopName(id: Long, name: String) = updateName(TABLE_TOPS, id, name)
    fun deleteTop(id: Long)            = deleteById(TABLE_TOPS, id)
    fun markTopUsed(id: Long)          = markUsed(TABLE_TOPS, id)
    fun clearTopUsed()                 = clearUsed(TABLE_TOPS)
    fun clearAllTops()                 = clearTable(TABLE_TOPS)

    // ─────────────────────── BOTTOMS ─────────────────────
    fun getAllBottoms(): List<Person>       = getAll(TABLE_BOTTOMS)
    fun getAvailableBottoms(): List<Person> = getAvailable(TABLE_BOTTOMS)
    fun insertBottom(name: String): Long   = insert(TABLE_BOTTOMS, name)
    fun updateBottomName(id: Long, name: String) = updateName(TABLE_BOTTOMS, id, name)
    fun deleteBottom(id: Long)             = deleteById(TABLE_BOTTOMS, id)
    fun markBottomUsed(id: Long)           = markUsed(TABLE_BOTTOMS, id)
    fun clearBottomUsed()                  = clearUsed(TABLE_BOTTOMS)
    fun clearAllBottoms()                  = clearTable(TABLE_BOTTOMS)

    fun clearAllData() {
        clearTable(TABLE_TOPS)
        clearTable(TABLE_BOTTOMS)
    }

    // ─────────────────────── GENERICS ────────────────────
    private fun getAll(table: String): List<Person> {
        val list = mutableListOf<Person>()
        readableDatabase
            .query(table, null, null, null, null, null, "$COL_NAME COLLATE NOCASE ASC")
            .use { c ->
                while (c.moveToNext()) {
                    list += Person(
                        id   = c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                        name = c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                        used = c.getInt(c.getColumnIndexOrThrow(COL_USED)) == 1
                    )
                }
            }
        return list
    }

    private fun getAvailable(table: String): List<Person> {
        val list = mutableListOf<Person>()
        readableDatabase
            .query(table, null, "$COL_USED = 0", null, null, null, null)
            .use { c ->
                while (c.moveToNext()) {
                    list += Person(
                        id   = c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                        name = c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                        used = false
                    )
                }
            }
        return list
    }

    private fun insert(table: String, name: String): Long {
        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_USED, 0)
        }
        return writableDatabase.insert(table, null, values)
    }

    private fun updateName(table: String, id: Long, name: String) {
        val values = ContentValues().apply { put(COL_NAME, name) }
        writableDatabase.update(table, values, "$COL_ID = ?", arrayOf(id.toString()))
    }

    private fun deleteById(table: String, id: Long) {
        writableDatabase.delete(table, "$COL_ID = ?", arrayOf(id.toString()))
    }

    private fun markUsed(table: String, id: Long) {
        val values = ContentValues().apply { put(COL_USED, 1) }
        writableDatabase.update(table, values, "$COL_ID = ?", arrayOf(id.toString()))
    }

    private fun clearUsed(table: String) {
        val values = ContentValues().apply { put(COL_USED, 0) }
        writableDatabase.update(table, values, null, null)
    }

    private fun clearTable(table: String) {
        writableDatabase.delete(table, null, null)
    }
}
