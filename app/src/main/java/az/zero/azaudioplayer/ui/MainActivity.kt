package az.zero.azaudioplayer.ui

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.AZApplication
import az.zero.azaudioplayer.R
import az.zero.db.helpers.AudioDbHelper
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@ExperimentalPagerApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    @Inject
    lateinit var audioDbHelper: AudioDbHelper

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
                audioDbHelper.compareWithLocalList()
                Log.d("activityResultLauncher", "entered")
            }

        }

    override fun onStart() {
        super.onStart()

        activityResultLauncher.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        activityResultLauncher.unregister()
    }
}
