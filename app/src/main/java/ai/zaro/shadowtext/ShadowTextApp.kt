package ai.zaro.shadowtext

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point for ShadowText.
 * Hilt-managed, no networking, no analytics, fully offline.
 */
@HiltAndroidApp
class ShadowTextApp : Application()
