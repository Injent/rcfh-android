package ru.rcfh.core.sync.work

import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.rcfh.common.AppDispatchers
import ru.rcfh.core.sync.R
import ru.rcfh.data.handbook.HandbookManager

class SyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {
    private val handbookRepository by inject<HandbookManager>()
    private val dispatchers by inject<AppDispatchers>()

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return foregroundInfo()
    }

    override suspend fun doWork(): Result = withContext(dispatchers.ioDispatcher) {
        if (handbookRepository.sync()) {
            Result.success()
        } else if (runAttemptCount < 2) {
            Result.retry()
        } else Result.failure()
    }
}

private fun SyncWorker.foregroundInfo(): ForegroundInfo {
    if (SDK_INT >= 24) {
        val channel = NotificationChannelCompat.Builder(
            SYNC_NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_LOW)
            .setName(applicationContext.getString(R.string.android_syncChannelName))
            .setShowBadge(false)
            .setLightsEnabled(false)
            .setVibrationEnabled(false)
            .build()

        NotificationManagerCompat.from(applicationContext).createNotificationChannel(channel)
    }
    val notification = NotificationCompat.Builder(applicationContext, SYNC_NOTIFICATION_CHANNEL)
        .setSmallIcon(R.drawable.ic_app_notification)
        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
        .setPriority(NotificationCompat.PRIORITY_MIN)
        .setSilent(true)
        .setShowWhen(false)
        .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_DEFERRED)
        .setAllowSystemGeneratedContextualActions(false)
        .setLocalOnly(true)
        .build()
    return ForegroundInfo(SYNC_NOTIFICATION_ID, notification)
}

private const val SYNC_NOTIFICATION_ID = 2
private const val SYNC_NOTIFICATION_CHANNEL = "sync-channel"