package id.neotica.imageclassificationdemo

import android.app.Application
import com.google.android.material.color.DynamicColors
import id.neotica.imageclassificationdemo.di.networkModule
import id.neotica.imageclassificationdemo.di.repoModules
import id.neotica.imageclassificationdemo.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        GlobalContext.startKoin {
            androidContext(this@MyApp)
            modules(
                appModules
            )
        }
    }
}

val appModules = listOf(
    viewModelModules,
    networkModule,
    repoModules
)