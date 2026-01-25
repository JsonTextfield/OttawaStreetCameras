package com.jsontextfield.shared.entities

import org.jetbrains.compose.resources.StringResource
import streetcams.shared.generated.resources.Res
import streetcams.shared.generated.resources.alberta
import streetcams.shared.generated.resources.british_columbia
import streetcams.shared.generated.resources.calgary
import streetcams.shared.generated.resources.manitoba
import streetcams.shared.generated.resources.new_brunswick
import streetcams.shared.generated.resources.newfoundland
import streetcams.shared.generated.resources.nova_scotia
import streetcams.shared.generated.resources.ontario
import streetcams.shared.generated.resources.ottawa
import streetcams.shared.generated.resources.prince_edward_island
import streetcams.shared.generated.resources.quebec
import streetcams.shared.generated.resources.saskatchewan
import streetcams.shared.generated.resources.surrey
import streetcams.shared.generated.resources.toronto
import streetcams.shared.generated.resources.vancouver
import streetcams.shared.generated.resources.yukon

enum class City(
    val cityName: String,
    val stringRes: StringResource,
) {
    ALBERTA(cityName = "alberta", stringRes = Res.string.alberta),
    BRITISH_COLUMBIA(cityName = "britishColumbia", stringRes = Res.string.british_columbia),
    CALGARY(cityName = "calgary", stringRes = Res.string.calgary),
    MANITOBA(cityName = "manitoba", stringRes = Res.string.manitoba),
    NEWFOUNDLAND(cityName = "newfoundland", stringRes = Res.string.newfoundland),
    NEW_BRUNSWICK(cityName = "newBrunswick", stringRes = Res.string.new_brunswick),
    NOVA_SCOTIA(cityName = "novaScotia", stringRes = Res.string.nova_scotia),
    ONTARIO(cityName = "ontario", stringRes = Res.string.ontario),
    OTTAWA(cityName = "ottawa", stringRes = Res.string.ottawa),
    PRINCE_EDWARD_ISLAND(cityName = "princeEdwardIsland", stringRes = Res.string.prince_edward_island),
    QUEBEC(cityName = "quebec", stringRes = Res.string.quebec),
    SASKATCHEWAN(cityName = "saskatchewan", stringRes = Res.string.saskatchewan),
    SURREY(cityName = "surrey", stringRes = Res.string.surrey),
    TORONTO(cityName = "toronto", stringRes = Res.string.toronto),
    VANCOUVER(cityName = "vancouver", stringRes = Res.string.vancouver),
    YUKON(cityName = "yukon", stringRes = Res.string.yukon),
}