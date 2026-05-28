package com.example.route

import com.example.dto.CreateProjectRequest
import com.example.dto.UpdateProjectRequest
import com.example.service.ProjectService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
fun Route.projectRouting(projectService: ProjectService) {
    authenticate("auth-jwt") {
        route("/projects") {

            // GET /projects - Список проектов пользователя
            get {
                call.getUserId().let { userId ->
                    projectService.getUserProjects(userId).respond(call)
                }
            }

            // POST /projects - Создать проект
            post {
                val userId = call.getUserId()
                val request = call.receive<CreateProjectRequest>()
                projectService.createProject(userId, request).respond(call, HttpStatusCode.Created)
            }

            // GET /projects/{id} - Получить проект
            get("{id}") {
                val userId = call.getUserId()
                val projectId = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid project ID"))

                projectService.getProject(userId, projectId).respond(call)
            }

            // PATCH /projects/{id} - Обновить проект
            patch("{id}") {
                val userId = call.getUserId()
                val projectId = call.parameters["id"]?.toIntOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid project ID"))

                val request = call.receive<UpdateProjectRequest>()
                projectService.updateProject(userId, projectId, request).respond(call)
            }

            // DELETE /projects/{id} - Удалить проект
            delete("{id}") {
                val userId = call.getUserId()
                val projectId = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid project ID"))

                projectService.deleteProject(userId, projectId).respond(call, HttpStatusCode.NoContent)
            }

            // POST /projects/{id}/members - Добавить участника
            post("{id}/members") {
                val userId = call.getUserId()
                val projectId = call.parameters["id"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid project ID"))

                val request = call.receive<AddMemberRequest>()
                projectService.addMemberToProject(userId, projectId, request.email, request.role)
                    .respond(call, HttpStatusCode.OK)
            }
        }
    }
}