package com.hienthai.autoclickapp


import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.hienthai.autoclickapp.databinding.ActivityFirstBinding

class FirstActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirstBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkDrawOverOtherApps()

    }

    private fun checkDrawOverOtherApps() {
        if (Settings.canDrawOverlays(this)) {
            checkAccessibilitySetting()
            return
        }
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        ).apply {
            //putExtra(KEY_REQUEST_CODE, REQUEST_DRAW_OVER_OTHER_APP)
//            setResult(REQUEST_DRAW_OVER_OTHER_APP, intent)
        }
        resultDrawOverLayLauncher.launch(intent)
    }

    private var resultDrawOverLayLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(
                    this,
                    "Cần cấp quyền hiển thị trên app khác",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@registerForActivityResult
            }
            checkAccessibilitySetting()
        }
    private var resultAccessibilityServiceLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (!isAccessibilityEnabled()) {
                Toast.makeText(
                    this,
                    "Cần cấp quyền Accessibility Service",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@registerForActivityResult
            }
            goToMainActivity()
        }

    private fun checkAccessibilitySetting() {
        if (isAccessibilityEnabled()) {
            goToMainActivity()
        } else {
            openAccessibilityServiceSettings()
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun openAccessibilityServiceSettings() {
        resultAccessibilityServiceLauncher.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            //putExtra(KEY_REQUEST_CODE, REQUEST_ACCESSIBILITY_SERVICE)
//            setResult(REQUEST_ACCESSIBILITY_SERVICE, intent)
        })
    }

    private fun isAccessibilityEnabled(): Boolean {
        val s = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return s.contains(getString(R.string.app_name))
    }

    companion object {
        const val KEY_REQUEST_CODE = "KEY_REQUEST_CODE"
        const val REQUEST_DRAW_OVER_OTHER_APP = 99
        const val REQUEST_ACCESSIBILITY_SERVICE = 100
    }
}