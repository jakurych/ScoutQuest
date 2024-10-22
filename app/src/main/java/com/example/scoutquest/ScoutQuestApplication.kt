package com.example.scoutquest

import android.app.Application
import android.content.Intent
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ScoutQuestApplication : Application() {
    private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        checkGooglePlayServices()

        try {
            ProviderInstaller.installIfNeeded(applicationContext)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
            Log.e("ScoutQuestApplication", "Usługi Google Play wymagają naprawy")
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
            Log.e("ScoutQuestApplication", "Usługi Google Play są niedostępne")
        }

        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )

        ProviderInstaller.installIfNeededAsync(this, object : ProviderInstaller.ProviderInstallListener {
            override fun onProviderInstalled() {
                Log.i("ScoutQuestApplication", "Provider zainstalowany pomyślnie")
            }

            override fun onProviderInstallFailed(errorCode: Int, intent: Intent?) {
                if (GoogleApiAvailability.getInstance().isUserResolvableError(errorCode)) {
                    // Można tu dodać kod do obsługi błędu, np. wyświetlenie dialogu z prośbą o aktualizację Google Play Services
                }
                Log.e("ScoutQuestApplication", "Nie udało się zainstalować Provider: $errorCode")
            }
        })
    }

    private fun checkGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Log.e("ScoutQuestApplication", "Google Play Services problem can be resolved")
            } else {
                Log.e("ScoutQuestApplication", "This device is not supported for Google Play Services")
            }
        }
    }
}
