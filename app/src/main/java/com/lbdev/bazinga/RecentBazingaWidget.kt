package com.lbdev.bazinga

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.AppWidgetTarget
import com.bumptech.glide.request.transition.Transition
import com.lbdev.bazinga.db.RecentBazingasDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Implementation of App Widget functionality.
 */
private lateinit var myDB: RecentBazingasDatabase

class RecentBazingaWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateRecentWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateRecentWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.recent_bazinga_widget)

    GlobalScope.launch {
        myDB = RecentBazingasDatabase.getDatabase(context)

        val bazingas = myDB.recentDao().getAllRecentBazingas()

        if (bazingas.isNotEmpty()) {
            val photoUrl = myDB.recentDao().getAllRecentBazingas()[0].photoUrl
            val photoId = myDB.recentDao().getAllRecentBazingas()[0].id
            val name = myDB.recentDao().getAllRecentBazingas()[0].name
            withContext(Dispatchers.Main) {

                views.setViewVisibility(R.id.recentActorImgWidget, View.VISIBLE)
                views.setViewVisibility(R.id.noRecentActorImgWidget, View.GONE)
                views.setViewVisibility(R.id.recentActorNameWidget, View.VISIBLE)
                views.setViewVisibility(R.id.widgetDeco, View.VISIBLE)

                views.setTextViewText(R.id.recentActorNameWidget, name)

                val awt: AppWidgetTarget = object : AppWidgetTarget(
                    context.applicationContext,
                    R.id.recentActorImgWidget,
                    views,
                    appWidgetId
                ) {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        super.onResourceReady(resource, transition)
                    }
                }

                val options = RequestOptions().override(300, 300).placeholder(R.drawable.nothing)
                    .error(R.drawable.nothing)

                Glide.with(context.applicationContext).asBitmap().load(photoUrl).apply(
                    options
                ).into(awt)

                val intent1 = Intent(context, ActorActivity::class.java)
                intent1.putExtra("id", photoId)
                intent1.putExtra("photoUrl", photoUrl)

                val uniqueRequestCode = photoId.hashCode()
                val pendingIntent1 = PendingIntent.getActivity(
                    context, uniqueRequestCode, intent1, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                views.setOnClickPendingIntent(
                    R.id.recentActorImgWidget,
                    pendingIntent1
                )
            }
        }
    }

    appWidgetManager.updateAppWidget(appWidgetId, views)
}