@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.jsontextfield.streetcamstv.ui.theme

import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.darkColorScheme
import androidx.tv.material3.lightColorScheme
import com.jsontextfield.core.ui.theme.backgroundDark
import com.jsontextfield.core.ui.theme.backgroundLight
import com.jsontextfield.core.ui.theme.errorContainerDark
import com.jsontextfield.core.ui.theme.errorContainerLight
import com.jsontextfield.core.ui.theme.errorDark
import com.jsontextfield.core.ui.theme.errorLight
import com.jsontextfield.core.ui.theme.inverseOnSurfaceDark
import com.jsontextfield.core.ui.theme.inverseOnSurfaceLight
import com.jsontextfield.core.ui.theme.inversePrimaryDark
import com.jsontextfield.core.ui.theme.inversePrimaryLight
import com.jsontextfield.core.ui.theme.inverseSurfaceDark
import com.jsontextfield.core.ui.theme.inverseSurfaceLight
import com.jsontextfield.core.ui.theme.onBackgroundDark
import com.jsontextfield.core.ui.theme.onBackgroundLight
import com.jsontextfield.core.ui.theme.onErrorContainerDark
import com.jsontextfield.core.ui.theme.onErrorContainerLight
import com.jsontextfield.core.ui.theme.onErrorDark
import com.jsontextfield.core.ui.theme.onErrorLight
import com.jsontextfield.core.ui.theme.onPrimaryContainerDark
import com.jsontextfield.core.ui.theme.onPrimaryContainerLight
import com.jsontextfield.core.ui.theme.onPrimaryDark
import com.jsontextfield.core.ui.theme.onPrimaryLight
import com.jsontextfield.core.ui.theme.onSecondaryContainerDark
import com.jsontextfield.core.ui.theme.onSecondaryContainerLight
import com.jsontextfield.core.ui.theme.onSecondaryDark
import com.jsontextfield.core.ui.theme.onSecondaryLight
import com.jsontextfield.core.ui.theme.onSurfaceDark
import com.jsontextfield.core.ui.theme.onSurfaceLight
import com.jsontextfield.core.ui.theme.onSurfaceVariantDark
import com.jsontextfield.core.ui.theme.onSurfaceVariantLight
import com.jsontextfield.core.ui.theme.onTertiaryContainerDark
import com.jsontextfield.core.ui.theme.onTertiaryContainerLight
import com.jsontextfield.core.ui.theme.onTertiaryDark
import com.jsontextfield.core.ui.theme.onTertiaryLight
import com.jsontextfield.core.ui.theme.primaryContainerDark
import com.jsontextfield.core.ui.theme.primaryContainerLight
import com.jsontextfield.core.ui.theme.primaryDark
import com.jsontextfield.core.ui.theme.primaryLight
import com.jsontextfield.core.ui.theme.scrimDark
import com.jsontextfield.core.ui.theme.scrimLight
import com.jsontextfield.core.ui.theme.secondaryContainerDark
import com.jsontextfield.core.ui.theme.secondaryContainerLight
import com.jsontextfield.core.ui.theme.secondaryDark
import com.jsontextfield.core.ui.theme.secondaryLight
import com.jsontextfield.core.ui.theme.surfaceDark
import com.jsontextfield.core.ui.theme.surfaceLight
import com.jsontextfield.core.ui.theme.surfaceVariantDark
import com.jsontextfield.core.ui.theme.surfaceVariantLight
import com.jsontextfield.core.ui.theme.tertiaryContainerDark
import com.jsontextfield.core.ui.theme.tertiaryContainerLight
import com.jsontextfield.core.ui.theme.tertiaryDark
import com.jsontextfield.core.ui.theme.tertiaryLight

val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
)

val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
)