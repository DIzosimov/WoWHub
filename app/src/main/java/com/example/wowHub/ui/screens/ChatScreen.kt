package com.example.wowHub.ui.screens

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket

data class Storage(
    val text: String? = null
) : Serializable

sealed class ChatItem
data class ChatText(val text: String) : ChatItem()

@Composable
fun ChatScreen() {
    val context = LocalContext.current
    var message by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf<List<ChatItem>>(emptyList()) }
    var connected by remember { mutableStateOf(false) }
    val socketState = remember { mutableStateOf<Socket?>(null) }
    val outputState = remember { mutableStateOf<ObjectOutputStream?>(null) }
    val inputState = remember { mutableStateOf<ObjectInputStream?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        createNotificationChannel(context)
        checkNotificationPermission(context)
    }

    fun connectToServer() {
        if (connected) return
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val s = Socket()
                s.connect(InetSocketAddress("atlas.dsv.su.se", 4848), 5000)
                socketState.value = s

                val output = ObjectOutputStream(s.getOutputStream())
                output.flush()
                outputState.value = output

                // send dummy message to avoid deadlock
                output.writeObject(Storage(text = "CONNECTED"))
                output.flush()
                output.reset()

                val input = ObjectInputStream(s.getInputStream())
                inputState.value = input

                withContext(Dispatchers.Main) {
                    connected = true
                }

                while (s.isConnected) {
                    val obj = input.readObject() as? Storage ?: continue
                    val vibrator = getVibrator(context)
                    withContext(Dispatchers.Main) {
                        obj.text?.let { messages = messages + ChatText(it) }
                        vibrator.vibrate(500)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    connected = false
                }
            }
        }
    }

    fun disconnectFromServer() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                outputState.value?.close()
                inputState.value?.close()
                socketState.value?.close()
            } catch (_: Exception) {}
            withContext(Dispatchers.Main) {
                outputState.value = null
                inputState.value = null
                socketState.value = null
                connected = false
                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (connected) "Connected" else "Disconnected",
                style = MaterialTheme.typography.titleMedium,
                color = if (connected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Row {
                Button(onClick = { connectToServer() }, enabled = !connected) { Text("Connect") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { disconnectFromServer() }, enabled = connected) { Text("Disconnect") }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 8.dp)
        ) {
            items(messages) { msg ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    when (msg) {
                        is ChatText -> Text(msg.text, Modifier.padding(8.dp))
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Enter message") },
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                enabled = connected
            )

            Button(
                onClick = {
                    if (connected && message.isNotBlank()) {
                        coroutineScope.launch(Dispatchers.IO) {
                            outputState.value?.writeObject(Storage(text = message))
                            outputState.value?.flush()
                            outputState.value?.reset()
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Message Sent!", Toast.LENGTH_SHORT).show()
                                message = ""
                            }
                        }
                    }
                },
                enabled = connected && message.isNotBlank()
            ) { Text("Send") }
        }
    }
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "chat_channel",
            "Chat Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for chat messages"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 200, 500)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

private fun checkNotificationPermission(context: Context): Boolean {
    val requiredPermissions = arrayOf(android.Manifest.permission.POST_NOTIFICATIONS)
    val hasPermissions = requiredPermissions.all { permission ->
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    if (!hasPermissions) {
        val activity = context as? Activity
        activity?.let {
            ActivityCompat.requestPermissions(it, requiredPermissions, 101)
        }
    }
    return hasPermissions
}

private fun sendNotification(context: Context, title: String, message: String) {
    if (!checkNotificationPermission(context)) {
        Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        return
    }

    val notificationBuilder = NotificationCompat.Builder(context, "chat_channel")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}

fun getVibrator(context: Context): Vibrator {
    return context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
}
