package az.zero.azaudioplayer.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.AZApplication
import az.zero.azaudioplayer.R
import az.zero.base.di.ApplicationScope
import az.zero.datastore.DataStoreManager
import az.zero.db.helpers.AudioDbHelper
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagerApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    @Inject
    lateinit var audioDbHelper: AudioDbHelper

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    @ApplicationScope
    lateinit var appScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val azApp = (application as AZApplication)
            val failedToGrant = permissions.entries.any { !it.value }
            if (failedToGrant) {
                azApp.checkedForPermission = false
                finish()
                return@registerForActivityResult
            }
            if (!azApp.checkedForPermission) {
                azApp.checkedForPermission = true
                appScope.launch {
                    val skipRecordings = dataStoreManager.read(DataStoreManager.SKIP_RECORDINGS_FILES, true)
                    val skipAndroidFiles = dataStoreManager.read(DataStoreManager.SKIP_ANDROID_FILES, true)
                    audioDbHelper.searchForNewAudios(
                        skipRecordings = skipRecordings,
                        skipAndroidFiles = skipAndroidFiles
                    )
                }

            }

        }

    override fun onStart() {
        super.onStart()

        activityResultLauncher.launch(
            if(Build.VERSION.SDK_INT < 32){
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            }else{
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        activityResultLauncher.unregister()
    }
}
