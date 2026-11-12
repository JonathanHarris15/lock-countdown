package com.jonathan.lockcountdown

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.view.View
import android.widget.RemoteViews

/**
 * How the seconds tick without draining battery:
 * the widget shows a Chronometer counting down to the next whole hour.
 * The launcher/lock screen animates it on its own, so the app never has to wake up every second.
 * The days and hours are baked into the Chronometer's text and refreshed by one alarm per hour.
 */
class CountdownWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        refresh(context)
    }

    override fun onEnabled(context: Context) = refresh(context)

    override fun onDisabled(context: Context) = cancelAlarm(context)

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_TICK,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> refresh(context)
            else -> super.onReceive(context, intent)
        }
    }

    companion object {
        private const val ACTION_TICK = "com.jonathan.lockcountdown.TICK"
        private const val HOUR = 3_600_000L

        fun refresh(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, CountdownWidget::class.java))
            if (ids.isEmpty()) {
                cancelAlarm(context)
                return
            }

            val now = System.currentTimeMillis()
            val target = Countdown.targetMillis
            val remaining = target - now
            val views = RemoteViews(context.packageName, R.layout.widget_countdown)
            views.setTextViewText(R.id.title, Countdown.TITLE)

            if (remaining <= 0) {
                views.setViewVisibility(R.id.counter, View.GONE)
                views.setViewVisibility(R.id.done, View.VISIBLE)
                views.setChronometer(R.id.chrono, SystemClock.elapsedRealtime(), null, false)
                cancelAlarm(context)
            } else {
                val totalHours = remaining / HOUR
                val days = totalHours / 24
                val hours = totalHours % 24
                // Wall-clock moment when the hour count drops by one.
                val nextBoundary = target - totalHours * HOUR
                val base = SystemClock.elapsedRealtime() + (nextBoundary - now)

                views.setViewVisibility(R.id.counter, View.VISIBLE)
                views.setViewVisibility(R.id.done, View.GONE)
                views.setTextViewText(R.id.days, days.toString())
                views.setTextViewText(R.id.days_label, if (days == 1L) "DAY" else "DAYS")
                // Chronometer fills %s with MM:SS; hours are prefixed in front.
                views.setChronometer(R.id.chrono, base, "%02d:%%s".format(hours), true)
                views.setChronometerCountDown(R.id.chrono, true)

                scheduleAlarm(context, nextBoundary + 250)
            }

            val open = PendingIntent.getActivity(
                context, 0, Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.root, open)
            manager.updateAppWidget(ids, views)
        }

        private fun tickIntent(context: Context): PendingIntent =
            PendingIntent.getBroadcast(
                context, 1,
                Intent(context, CountdownWidget::class.java).setAction(ACTION_TICK),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        private fun scheduleAlarm(context: Context, atMillis: Long) {
            val am = context.getSystemService(AlarmManager::class.java)
            val pi = tickIntent(context)
            val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || am.canScheduleExactAlarms()
            if (canExact) am.setExact(AlarmManager.RTC, atMillis, pi)
            else am.set(AlarmManager.RTC, atMillis, pi)
        }

        private fun cancelAlarm(context: Context) {
            context.getSystemService(AlarmManager::class.java).cancel(tickIntent(context))
        }
    }
}
