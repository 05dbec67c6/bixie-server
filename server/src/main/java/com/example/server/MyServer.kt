package com.example.server

import androidx.compose.ui.semantics.text
import kotlin.io.readText

// Example data class for a simple message (you can move this to a common module later)
@Serializable
data class SimpleMessage(val text: String)

class MyServer {

    // You might want to pass configurations like port here
    fun start(port: Int = 8080) {
        embeddedServer(Netty, port = port, host = "0.0.0.0") {
            // Call the module function to configure the server
            module()
        }.start(wait = true)
    }

    // This is your Ktor application module
    @Suppress("unused") // Called from the start function
    fun Application.module() {
        // Install Content Negotiation for JSON serialization/deserialization
        install(ContentNegotiation) {
            json(Json { // Configure Json if needed
                prettyPrint = true
                isLenient = true
            })
        }

        // Install the WebSockets feature
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15) // Keep the connection alive
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }

        // Define the WebSocket endpoint
        routing {
            webSocket("/echo") { // Use a simple path like "/echo"
                kotlin.io.println("WebSocket connection established from ${call.request.origin.remoteAddress}!")

                // This loop handles incoming messages
                try {
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                kotlin.io.println("Received text: $text")
                                // Try to deserialize as SimpleMessage, or just echo raw text
                                try {
                                    val receivedMessage = Json.decodeFromString<SimpleMessage>(text)
                                    kotlin.io.println("Received message object: ${receivedMessage.text}")
                                    // Send a structured message back (demonstrating serialization)
                                    outgoing.send(Frame.Text(Json.encodeToString(SimpleMessage("Echo from server: ${receivedMessage.text}"))))
                                } catch (e: Exception) {
                                    kotlin.io.println("Could not deserialize text as SimpleMessage: ${e.message}")
                                    // If it's not a SimpleMessage, just echo the raw text
                                    outgoing.send(Frame.Text("Echo raw text: $text"))
                                }
                            }
                            is Frame.Binary -> {
                                kotlin.io.println("Received binary frame")
                                // Handle binary data if needed
                            }
                            is Frame.Ping -> {
                                // println("Received ping") // Pings can be noisy, maybe log less frequently
                            }
                            is Frame.Pong -> {
                                // println("Received pong") // Pongs can be noisy
                            }
                            is Frame.Close -> {
                                kotlin.io.println("Received close frame: ${frame.readReason()}")
                                close(CloseReason(CloseReason.Codes.NORMAL, "Client requested close"))
                            }
                        }
                    }
                } catch (e: kotlinx.coroutines.channels.ClosedReceiveChannelException) {
                    // This exception is thrown when the channel is closed normally
                    kotlin.io.println("WebSocket channel closed normally.")
                } catch (e: Exception) {
                    kotlin.io.println("Error in WebSocket connection: ${e.message}")
                    e.printStackTrace() // Print stack trace for debugging
                } finally {
                    kotlin.io.println("WebSocket connection closed!")
                }
            }
        }
    }
}

// Add a main function to start your server
fun main() {
    val server = MyServer()
    server.start(port = 8080) // You can specify the port here or use the default from application.conf
}