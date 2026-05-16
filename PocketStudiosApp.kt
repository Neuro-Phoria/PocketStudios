package com.pocketstudios.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PocketStudiosApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        initFirebase()
        initRevenueCat()
        createNotificationChannels()
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
        FirebaseCrashlytics.getInstance().apply {
            setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        }
    }

    private fun initRevenueCat() {
        if (BuildConfig.ENABLE_LOGGING) {
            Purchases.logLevel = LogLevel.DEBUG
        }
        Purchases.configure(
            PurchasesConfiguration.Builder(this, BuildConfig.REVENUECAT_API_KEY).build()
        )
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // Export channel
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_EXPORTS,
                    "Export Progress",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Shows progress while exporting your video"
                    setShowBadge(false)
                }
            )

            // General channel
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_GENERAL,
                    "Updates & Tips",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "New effects drops, tips, and app updates"
                }
            )
        }
    }

    companion object {
        const val CHANNEL_EXPORTS = "exports"
        const val CHANNEL_GENERAL = "general"
    }
}
