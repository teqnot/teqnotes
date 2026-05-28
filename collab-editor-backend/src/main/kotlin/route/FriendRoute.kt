package com.example.route

import com.example.dto.FriendRequest
import com.example.service.FriendshipService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.friendRouting(friendshipService: FriendshipService) {
    authenticate("auth-jwt") {
        route("/friends") {

            // GET /friends - Список друзей
            get {
                call.getUserId().let { userId ->
                    friendshipService.getFriends(userId).respond(call)
                }
            }

            // GET /friends/requests - Входящие запросы
            get("/requests") {
                call.getUserId().let { userId ->
                    // Примечание: Убедитесь, что в FriendshipService реализован getIncomingRequests
                    friendshipService.getIncomingRequests(userId).respond(call)
                }
            }

            // POST /friends/request - Отправить запрос
            post("/request") {
                val userId = call.getUserId()
                val request = call.receive<FriendRequest>()
                friendshipService.sendRequest(userId, request.friendEmail)
                    .respond(call, HttpStatusCode.Created)
            }

            // POST /friends/accept/{id} - Принять запрос
            post("/accept/{id}") {
                val userId = call.getUserId()
                val requestId = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request ID"))

                friendshipService.acceptRequest(userId, requestId).respond(call, HttpStatusCode.OK)
            }

            // POST /friends/reject/{id} - Отклонить запрос / Удалить из друзей
            post("/reject/{id}") {
                val userId = call.getUserId()
                val requestId = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request ID"))

                friendshipService.removeFriendOrReject(userId, requestId).respond(call, HttpStatusCode.OK)
            }
        }
    }
}