package com.jsontextfield.core.entities

import androidx.annotation.StringRes
import com.jsontextfield.core.R

enum class City(
    val cityName: String,
    @StringRes val stringRes: Int,
) {
    ALBERTA(cityName = "alberta", stringRes = R.string.alberta),
    BRITISH_COLUMBIA(cityName = "britishColumbia", stringRes = R.string.british_columbia),
    CALGARY(cityName = "calgary", stringRes = R.string.calgary),
    MANITOBA(cityName = "manitoba", stringRes = R.string.manitoba),
    NEWFOUNDLAND(cityName = "newfoundland", stringRes = R.string.newfoundland),
    NEW_BRUNSWICK(cityName = "newBrunswick", stringRes = R.string.new_brunswick),
    NOVA_SCOTIA(cityName = "novaScotia", stringRes = R.string.nova_scotia),
    ONTARIO(cityName = "ontario", stringRes = R.string.ontario),
    OTTAWA(cityName = "ottawa", stringRes = R.string.ottawa),
    PRINCE_EDWARD_ISLAND(cityName = "princeEdwardIsland", stringRes = R.string.prince_edward_island),
    QUEBEC(cityName = "quebec", stringRes = R.string.quebec),
    SASKATCHEWAN(cityName = "saskatchewan", stringRes = R.string.saskatchewan),
    SURREY(cityName = "surrey", stringRes = R.string.surrey),
    TORONTO(cityName = "toronto", stringRes = R.string.toronto),
    VANCOUVER(cityName = "vancouver", stringRes = R.string.vancouver),
    YUKON(cityName = "yukon", stringRes = R.string.yukon),
}