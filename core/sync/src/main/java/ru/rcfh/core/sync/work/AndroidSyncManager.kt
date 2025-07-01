package ru.rcfh.core.sync.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import ru.rcfh.data.util.SyncManager

private const val SYNC_WORK_NAME = "syncWork"
private val SyncWorkConstraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

class AndroidSyncManager(
    context: Context
) : SyncManager {
    private val workManager = WorkManager.getInstance(context)

    override fun requestSync() {
        workManager.enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(SyncWorkConstraints)
                .build()
        )
    }
}