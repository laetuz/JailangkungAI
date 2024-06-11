package id.neotica.asclepius

import android.app.Application
import com.google.android.material.color.DynamicColors
import id.neotica.asclepius.koin.databaseModule
import id.neotica.asclepius.koin.networkModule
import id.neotica.asclepius.koin.viewModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        startKoin {
            androidContext(this@MyApp)
            modules(
                databaseModule,
                viewModule,
                networkModule
            )
        }
    }
}