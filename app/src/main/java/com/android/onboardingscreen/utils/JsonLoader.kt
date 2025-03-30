package com.android.onboardingscreen.utils

import android.content.Context
import com.android.onboardingscreen.data.EventsData
import com.google.gson.Gson

object JsonLoader {
    fun loadEventsData(context: Context): EventsData {
        val jsonString = context.assets.open("events_data.json").bufferedReader().use { it.readText() }
        return Gson().fromJson(jsonString, EventsData::class.java)
    }
}
