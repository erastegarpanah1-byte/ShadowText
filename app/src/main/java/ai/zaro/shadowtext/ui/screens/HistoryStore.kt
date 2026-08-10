package ai.zaro.shadowtext.ui.screens

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class HistoryEntry(
    val id: Long = System.currentTimeMillis(),
    val type: String,
    val inputPreview: String,
    val outputPreview: String,
    val timestamp: Long = System.currentTimeMillis()
)

object HistoryStore {
    private const val PREFS_NAME = "shadowtext_prefs"
    private const val KEY_HISTORY = "history_entries"
    private const val MAX_ENTRIES = 100

    fun add(context: Context, entry: HistoryEntry) {
        val entries = getAll(context).toMutableList()
        entries.add(0, entry)
        if (entries.size > MAX_ENTRIES) entries.removeAt(entries.size - 1)
        save(context, entries)
    }

    fun getAll(context: Context): List<HistoryEntry> {
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_HISTORY, "[]") ?: "[]"
        val arr = JSONArray(json)
        val list = mutableListOf<HistoryEntry>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(HistoryEntry(
                id = obj.optLong("id", 0),
                type = obj.optString("type", ""),
                inputPreview = obj.optString("inputPreview", ""),
                outputPreview = obj.optString("outputPreview", ""),
                timestamp = obj.optLong("timestamp", 0)
            ))
        }
        return list
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_HISTORY, "[]").apply()
    }

    private fun save(context: Context, entries: List<HistoryEntry>) {
        val arr = JSONArray()
        for (e in entries) {
            arr.put(JSONObject().apply {
                put("id", e.id)
                put("type", e.type)
                put("inputPreview", e.inputPreview)
                put("outputPreview", e.outputPreview)
                put("timestamp", e.timestamp)
            })
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_HISTORY, arr.toString()).apply()
    }
}
