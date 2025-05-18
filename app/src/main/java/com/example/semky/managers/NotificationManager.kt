// app/src/main/java/com/example/semky/notifications/NotificationManager.kt
package com.example.semky.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager as SysNotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import androidx.work.WorkManager
import com.example.semky.R
import com.example.semky.data.model.Deadline
import java.util.concurrent.TimeUnit

/**
 * Správa notifikácií pre termíny v aplikácii.
 * Stará sa o vytváranie notifikačných kanálov a plánovanie notifikácií pre termíny.
 * Zdroj (približne): https://developer.android.com/develop/ui/views/notifications/build-notification#kotlin
 */
object NotificationManager {
    const val CHANNEL_ID = "deadlines_channel"
    var canPostNotifications = true

    /**
     * Vytvorí notifikačný kanál pre notifikácie termínov.
     *
     * @param context Kontext aplikácie používaný pre notifikačné operácie
     */
    fun createNotificationChannel(context: Context) {
        val name = context.getString(R.string.deadlines_channel_name)
        val descriptionText = context.getString(R.string.deadlines_channel_description)
        val importance = SysNotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: SysNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as SysNotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Naplánuje notifikáciu pre termín, ktorá sa zobrazí 8 hodín pred termínom.
     * Používa WorkManager pre spoľahlivé spracovanie na pozadí.
     *
     * @param context Kontext aplikácie
     * @param deadline Termín, pre ktorý sa má naplánovať notifikácia
     */
    fun scheduleDeadlineNotification(context: Context, deadline: Deadline) {
        if (!canPostNotifications) return;

        createNotificationChannel(context)
        // Upozorní používatela 8 hodin pred začatím noveho dňa
        val delay = deadline.date.time - System.currentTimeMillis() - TimeUnit.HOURS.toMillis(8)
        if (delay <= 0) return // Neplánovať upozornenia v minulosti

        val data = workDataOf(
            "deadline_id" to deadline.id,
            "deadline_name" to deadline.name,
            "deadline_time" to deadline.date.time
        )

        val workRequest = OneTimeWorkRequestBuilder<DeadlineNotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("deadline_${deadline.id}")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "deadline_${deadline.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    /**
     * Zruší predtým naplánovanú notifikáciu pre konkrétny termín.
     *
     * @param context Kontext aplikácie
     * @param deadlineId ID termínu, ktorého notifikácia sa má zrušiť
     */
    fun cancelDeadlineNotification(context: Context, deadlineId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("deadline_$deadlineId")
    }
}

/**
 * Worker trieda, ktorá spracováva skutočné doručenie notifikácií pre termíny.
 * Rozširuje Android Worker triedu pre spracovanie na pozadí.
 *
 * @property context Kontext aplikácie
 * @property params Parametre workera obsahujúce informácie o termíne
 */
class DeadlineNotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        val deadlineName = inputData.getString("deadline_name") ?: "Deadline"
        val notificationId = inputData.getLong("deadline_id", 0L).toInt()

        val builder = NotificationCompat.Builder(applicationContext, NotificationManager.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(applicationContext.getString(R.string.deadline_approaching)) //TODO: stringy do resourcov
            .setContentText("Termín s názvom $deadlineName sa blíži!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    this@DeadlineNotificationWorker.applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return Result.failure()
            }
            notify(notificationId, builder.build())
        }
        return Result.success()
    }
}