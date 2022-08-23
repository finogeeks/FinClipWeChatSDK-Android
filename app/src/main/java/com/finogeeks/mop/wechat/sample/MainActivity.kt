package com.finogeeks.mop.wechat.sample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.finogeeks.lib.applet.client.FinAppClient
import com.finogeeks.mop.wechat.sample.manager.FrameworkSyncManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_unzip_assets_framework.setOnClickListener {
            FrameworkSyncManager.unzipAssetsFramework(applicationContext) { result ->
                if (result) {
                    Toast.makeText(this, R.string.operate_success, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, R.string.operate_failure, Toast.LENGTH_SHORT).show()
                }
            }
        }

        btn_clear_framework.setOnClickListener {
            FrameworkSyncManager.clearFramework(applicationContext)
            Toast.makeText(this, R.string.operate_success, Toast.LENGTH_SHORT).show()
        }

        btn_finish_applets.setOnClickListener {
            FinAppClient.appletApiManager.finishAllRunningApplets()
        }

        btn_clear_applets.setOnClickListener {
            FinAppClient.appletApiManager.clearApplets()
        }

        btn_scan_start_applet.setOnClickListener {
            startActivity(Intent(this, ScanStartAppletActivity::class.java))
        }

        btn_test_applets.setOnClickListener {
            FinAppClient.appletApiManager.startApplet(
                this,
                "https://finchat-mop-b.finogeeks.club",
                "619715d71bcd700001146eca"
            )
        }
    }
}