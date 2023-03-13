package com.xiaor.androidbugly

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xiaor.xrbugly.XRBugly

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        XRBugly.autoUpgrade(applicationContext, "http://bugly.xiao-r.com/api/checkUpdate")
    }
}