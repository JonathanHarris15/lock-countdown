package com.jonathan.lockcountdown

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val info = TextView(this).apply {
            textSize = 16f
            text = "Lock screen: long-press your lock screen (or swipe to the widget hub), " +
                "tap add widget, and pick \"Countdown\".\n\n" +
                "Home screen: tap the button below."
        }
        val pin = Button(this).apply {
            text = "Add widget to home screen"
            setOnClickListener {
                val mgr = getSystemService(AppWidgetManager::class.java)
                if (mgr.isRequestPinAppWidgetSupported) {
                    mgr.requestPinAppWidget(ComponentName(this@MainActivity, CountdownWidget::class.java), null, null)
                }
            }
        }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(64, 64, 64, 64)
            addView(info)
            addView(pin)
        })
        CountdownWidget.refresh(this)
    }
}
