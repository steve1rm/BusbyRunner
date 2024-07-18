package me.androidbox.core.connectivity.data.messaging

import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import me.androidbox.core.connectivity.data.mapper.toMessageAction
import me.androidbox.core.connectivity.data.mapper.toMessagingActionDto
import me.androidbox.core.connectivity.domain.messaging.MessagingError
import me.androidbox.core.connectivity.domain.messaging.MessagingAction
import me.androidbox.core.connectivity.domain.messaging.MessagingClient
import me.androidbox.core.domain.util.EmptyResult
import me.androidbox.core.domain.util.Result

class WearMessagingClientImp(
    context: Context
) : MessagingClient {

    private val client = Wearable.getMessageClient(context)
    private val messageQueue = mutableListOf<MessagingAction>()
    private var connectedNotedId: String? = null

    override fun connectToNode(nodeId: String): Flow<MessagingAction> {
        return callbackFlow {
            send(MessagingAction.Finish)

            val listener: (MessageEvent) -> Unit = { messageEvent ->
                if(messageEvent.path.startsWith(BASE_PATH_MESSAGING_ACTION)) {
                    val jsonString = messageEvent.data.decodeToString()

                    val messageActionDto = Json.decodeFromString<MessagingActionDto>(jsonString)
                    trySend(messageActionDto.toMessageAction())

                }
            }

            client.addListener(listener)

            messageQueue.forEach { messagingAction ->
                sendOrQueryAction(messagingAction = messagingAction)
            }
            messageQueue.clear()

            awaitClose {
                client.removeListener(listener)
            }
        }
    }

    override suspend fun sendOrQueryAction(messagingAction: MessagingAction): EmptyResult<MessagingError> {
        return  connectedNotedId?.let { id ->
            try {
                val messagingActionDto = messagingAction.toMessagingActionDto()
                val jsonMessage = Json.encodeToString(messagingActionDto)
                val messageBytes = jsonMessage.encodeToByteArray()

                client.sendMessage(id, BASE_PATH_MESSAGING_ACTION, messageBytes)
                    .await()

                Result.Success(Unit)
            }
            catch(exception: ApiException) {
                Result.Failure(
                    if(exception.status.isInterrupted) {
                        MessagingError.CONNECTION_INTERRUPTED
                    }
                    else {
                        MessagingError.UNKNOWN
                    }
                )
            }
        } ?: run  {
                Result.Failure(MessagingError.DISCONNECTED)
            }
    }

    companion object {
        private const val BASE_PATH_MESSAGING_ACTION = "busbyrunner/messaging_action"
    }
}