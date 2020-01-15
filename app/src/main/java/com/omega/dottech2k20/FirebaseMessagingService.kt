package com.omega.dottech2k20

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {
	val TAG = "Main"

	override fun onNewToken(token: String) {
		Log.d(TAG, "Refreshed token: $token")
	}

	override fun onMessageReceived(message: RemoteMessage) {
		super.onMessageReceived(message)
		val nMessage = message.notification
		if (nMessage != null) {
			val title = nMessage.title
			val message = nMessage.body
			val imageUrl = nMessage.imageUrl
			if (title != null && message != null) {
				sendNotification(title, message, imageUrl)
			}
			Log.d(TAG, "Remote Message Received, data = $title  from = $message")
		}
	}

//
//	/**
//	 * Called when message is received.
//	 *
//	 * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
//	 */
//	// [START receive_message]
//	override fun onMessageReceived(remoteMessage: RemoteMessage) {
//		// [START_EXCLUDE]
//		// There are two types of messages data messages and notification messages. Data messages are handled
//		// here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
//		// traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
//		// is in the foreground. When the app is in the background an automatically generated notification is displayed.
//		// When the user taps on the notification they are returned to the app. Messages containing both notification
//		// and data payloads are treated as notification messages. The Firebase console always sends notification
//		// messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
//		// [END_EXCLUDE]
//
//		// TODO(developer): Handle FCM messages here.
//		// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//		Log.d(TAG, "From: ${remoteMessage.from}")
//
//		// Check if message contains a data payload.
//		remoteMessage.data.isNotEmpty().let {
//			Log.d(TAG, "Message data payload: " + remoteMessage.data)
//
//			if (/* Check if data needs to be processed by long running job */ true) {
//				// For long-running tasks (10 seconds or more) use WorkManager.
//				scheduleJob()
//			} else {
//				// Handle message within 10 seconds
//				handleNow()
//			}
//		}
//
//		// Check if message contains a notification payload.
//		remoteMessage.notification?.let {
//			Log.d(TAG, "Message Notification Body: ${it.body}")
//		}
//
//		// Also if you intend on generating your own notifications as a result of a received FCM
//		// message, here is where that should be initiated. See sendNotification method below.
//	}
//	// [END receive_message]

//	/**
//	 * Create and show a simple notification containing the received FCM message.
//	 *
//	 * @param messageBody FCM message body received.
//	 */
private fun sendNotification(title: String, message: String, imageUrl: Uri?) {
	val intent = Intent(this, MainActivity::class.java)
	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
	val pendingIntent = PendingIntent.getActivity(
		this, 0 /* Request code */, intent,
		PendingIntent.FLAG_ONE_SHOT
	)

	val channelId = getString(R.string.channel_id)
	val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
	val notificationBuilder = NotificationCompat.Builder(this, channelId)
		.setSmallIcon(R.drawable.ic_notification_bell)
		.setContentTitle(title)
		.setContentText(message)
		.setAutoCancel(true)
		.setSound(defaultSoundUri)
		.setContentIntent(pendingIntent)


	val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

	// Since android Oreo notification channel is needed.
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
		val channel = NotificationChannel(
			channelId,
			"Channel human readable title",
			NotificationManager.IMPORTANCE_DEFAULT
		)
		notificationManager.createNotificationChannel(channel)
	}

	notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
}


}