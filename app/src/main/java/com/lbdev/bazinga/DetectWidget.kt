package com.lbdev.bazinga

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.Toast
import com.bumptech.glide.Glide
import com.lbdev.bazinga.db.RecentBazingasDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Implementation of App Widget functionality.
 */
class DetectWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val remoteViews = RemoteViews(context.packageName, R.layout.detect_widget)

    val intent1 = Intent(context, MainScreen::class.java)
    intent1.putExtra("functionType", "function1")
    val pendingIntent1 = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_IMMUTABLE)

    remoteViews.setOnClickPendingIntent(
        R.id.logo_bg_widget,
        pendingIntent1
    )

    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
}