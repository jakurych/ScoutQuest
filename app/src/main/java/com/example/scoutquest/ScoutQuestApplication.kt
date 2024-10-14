package com.example.scoutquest

import android.app.Application
import android.content.Intent
import android.util.Log
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ScoutQuestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        try {
            ProviderInstaller.installIfNeeded(applicationContext)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
            Log.e("ScoutQuestApplication", "Usługi Google Play wymagają naprawy")
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
            Log.e("ScoutQuestApplication", "Usługi Google Play są niedostępne")

        }


        //Wybierz odpowiedni dostawca App Check
        val firebaseAppCheck = FirebaseAppCheck.getInstance()

        // Dla środowiska produkcyjnego
        /*firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )*/

        // Dla środowiska deweloperskiego
        firebaseAppCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )


        // Instalacja ProviderInstaller
        ProviderInstaller.installIfNeededAsync(this, object : ProviderInstaller.ProviderInstallListener {
            override fun onProviderInstalled() {
                Log.i("ScoutQuestApplication", "Provider zainstalowany pomyślnie")
            }

            override fun onProviderInstallFailed(errorCode: Int, intent: Intent?) {
                Log.e("ScoutQuestApplication", "Nie udało się zainstalować Provider: $errorCode")
            }
        })
    }
}
