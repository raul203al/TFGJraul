import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

open class Kiosk : AppCompatActivity() {
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var compName: ComponentName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        compName = ComponentName(this, Kiosk::class.java)

        if (devicePolicyManager.isDeviceOwnerApp(packageName)) {
            setKioskPolicies()
        } else {
            Log.e("KioskActivity", "Not a device owner app")
        }
    }

    private fun setKioskPolicies() {
        devicePolicyManager.setLockTaskPackages(compName, arrayOf(packageName))
        val intentFilter = IntentFilter(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            addCategory(Intent.CATEGORY_DEFAULT)
        }
        devicePolicyManager.addPersistentPreferredActivity(
            compName,
            intentFilter,
            ComponentName(packageName, javaClass.name)
        )
        devicePolicyManager.setKeyguardDisabled(compName, true)
        devicePolicyManager.setStatusBarDisabled(compName, true)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        devicePolicyManager.setGlobalSetting(
            compName,
            Settings.Global.DEVICE_PROVISIONED,
            "1"
        )
    }

    override fun onResume() {
        super.onResume()
        startLockTask()
    }

    override fun onPause() {
        super.onPause()
        stopLockTask()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                    finish()
                })
                .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })
            val alert = dialogBuilder.create()
            alert.setTitle("Exit App")
            alert.show()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
