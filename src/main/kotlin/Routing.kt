package com

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlin.text.equals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun Application.configureRouting() {

    install(WebSockets) {
        pingPeriod = 15.seconds // Optional: Period for sending pings
        timeout = 30.seconds    // Optional: Timeout for receiving pongs
        maxFrameSize = Long.MAX_VALUE       // Optional: Maximum frame size allowed
        masking = false                     // Optional: Whether to mask outgoing frames
    }

    routing {
        webSocket("/echo") { // Define the WebSocket endpoint path, e.g., ws://localhost:8080/echo
            // WebSocket logic will go here in the next step
            for (frame in incoming) { // Loop to process incoming frames
                if (frame is Frame.Text) { // Check if the frame is a text frame
                    val receivedText = frame.readText()
                    println("Received from client: $receivedText") // Log received message

                    // Echo the received text back to the client
                    outgoing.send(Frame.Text("Server echoes: $receivedText"))
                    println("Sent to client: Server echoes: $receivedText") // Log sent message

                    // Example: Close connection if client sends "bye"
                    if (receivedText.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said bye"))
                        println("Connection closed by server.")
                    }
                } else if (frame is Frame.Binary) {
                    // Handle binary frames if needed, for this echo server we'll ignore them
                    // or send an error back.
                    outgoing.send(Frame.Text("Server received binary data, but only echoes text."))
                }
                // Handle other frame types (Close, Ping, Pong) if necessary
                // Ktor handles Ping/Pong automatically if pingPeriod and timeout are set.
            }
            // This block is reached when the WebSocket connection is closing or closed.
            // You can perform cleanup here.
            println("WebSocket connection to /echo is closing. Reason: ${closeReason.await()}")
        }

        // You can have other HTTP routes here as well
        get("/") {
            call.respondText("Hello, Ktor Echo Server is running! Connect to /echo via WebSocket.")
        }
    }
}
