package com.example.embeddedsygicid.sygic

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.database.getStringOrNull
import com.example.embeddedsygicid.R
import com.sygic.aura.ResourceManager
import com.sygic.aura.ResourceManager.OnResultListener
import com.sygic.aura.utils.PermissionsUtils

private const val KEY_ID = "id"
private val sygicProviderUri = Uri.parse("content://com.example.embeddedsygicid/id")

class SygicActivity : AppCompatActivity(), OnResultListener {

    private var sygicFragment: SygicNaviFragment = SygicNaviFragment()
    private var uiInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PermissionsUtils.requestStartupPermissions(this) == PackageManager.PERMISSION_GRANTED) {
            checkSygicResources()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf("android.permission.READ_MEDIA_DOCUMENTS"),
                1
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_EXTERNAL_STORAGE),
                2
            )
        }
    }

    private fun checkSygicResources() {
        val resourceManager = ResourceManager(this, null)
        if (resourceManager.shouldUpdateResources()) {
            showToast("Please wait while Sygic resources are being updated")
            resourceManager.updateResources(this)
        } else {
            initUI()
        }
    }

    override fun onError(code: Int, message: String) {
        showToast("Failed to update resources: $message")
        finish()
    }

    override fun onSuccess() {
        initUI()
    }

    private fun initUI() {
        if (uiInitialized)
            return
        uiInitialized = true
        setContentView(R.layout.activity_sygic)

        supportFragmentManager.beginTransaction().replace(R.id.sygicmap, sygicFragment)
            .commitAllowingStateLoss()

        refreshSygicIdView()

        findViewById<Button>(R.id.setIdButton).setOnClickListener {
            setSygicId("66512312-374e-4fe2-9e2a-4d2985d86830")
            refreshSygicIdView()
        }
    }

    private fun refreshSygicIdView() {
        findViewById<TextView>(R.id.idText).text = getSygicId() ?: "Could not set Sygic ID"
    }

    /**
     * Returns Sygic device ID or null if device ID can't be read. Supposedly there is an option to override
     * ID, but on our business calls they said it's not possible to use it.
     * See more here:
     * https://www.sygic.com/developers/professional-navigation-sdk/android/installation-of-navigation-core/activation-with-content-provider-app
     * */
    private fun getSygicId(): String? {
        contentResolver.query(sygicProviderUri, arrayOf(KEY_ID), null, null, null)?.use {
            return if(it.moveToFirst()) it.getStringOrNull(0) else null
        }
        return null
    }

    /** Pushes new alternative ID to Sygic app. Returns true if success. */
    private fun setSygicId(id: String) = try {
        val contentValues = ContentValues(1).apply {
            put(KEY_ID, id)
        }
        contentResolver.insert(sygicProviderUri, contentValues) != null
    } catch (e: Exception) {
        Log.e("SygicActivity", "Failed to tweak Sygic ID using $sygicProviderUri", e)
        false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        for (res in grantResults) {
            if (res != PackageManager.PERMISSION_GRANTED) {
                showToast("You have to allow all permissions")
                return
            }
        }
        if (allGranted) {
            checkSygicResources()
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onCreateDialog(id: Int): Dialog {
        return sygicFragment.onCreateDialog(id) ?: return super.onCreateDialog(id)
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onPrepareDialog(id: Int, dialog: Dialog) {
        super.onPrepareDialog(id, dialog)
        sygicFragment.onPrepareDialog(id, dialog)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        sygicFragment.onNewIntent(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        sygicFragment.onActivityResult(requestCode, resultCode, data)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return sygicFragment.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return sygicFragment.onKeyUp(keyCode, event)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}