package com.radiohack.tloyalty.data.local

import android.content.Context
import com.radiohack.tloyalty.domain.model.DemoProfile

data class SelectedUser(
    val userId: Int?,
    val label: String?,
    val segment: String?,
)

class UserPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("t_loyalty_prefs", Context.MODE_PRIVATE)

    fun saveSelectedUser(profile: DemoProfile) {
        prefs.edit()
            .putInt(KEY_USER_ID, profile.userId)
            .putString(KEY_LABEL, profile.label)
            .putString(KEY_SEGMENT, profile.financialSegment)
            .apply()
    }

    fun selectedUser(): SelectedUser {
        val userId = if (prefs.contains(KEY_USER_ID)) prefs.getInt(KEY_USER_ID, 0) else null
        return SelectedUser(
            userId = userId?.takeIf { it > 0 },
            label = prefs.getString(KEY_LABEL, null),
            segment = prefs.getString(KEY_SEGMENT, null),
        )
    }

    private companion object {
        const val KEY_USER_ID = "selected_user_id"
        const val KEY_LABEL = "selected_user_label"
        const val KEY_SEGMENT = "selected_user_segment"
    }
}
