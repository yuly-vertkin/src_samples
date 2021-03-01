package tv.mediastage.updater.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import tv.mediastage.updater.UpdateStarter

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = "MediastageUpdater.MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate")

        UpdateStarter.checkAndStartService(this@MainActivity, true, true)
        //        finish();
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        UpdateStarter.stopService(this@MainActivity)

        super.onDestroy()
    }
}
