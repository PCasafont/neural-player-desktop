package neuralplayer.desktop.controller.rest

import com.fasterxml.jackson.databind.ObjectMapper
import neuralplayer.desktop.controller.rest.dto.UserDto
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import tornadofx.*

/**
 * @author Pere
 * @since 2018/04/22
 */
class UserRestController : Controller() {

	private val api: Rest by inject()

	fun getAll()
			= api.get("users").list().toModel<UserDto>()

	// Workaround to skip auth
	fun create(userDto: UserDto) {
		val client = HttpClientBuilder.create().build()
		val request = HttpPost("${api.baseURI}/users")
		request.setHeader("Content-type", "application/json");
		val objectMapper = ObjectMapper()
		request.entity = StringEntity(objectMapper.writeValueAsString(userDto))
		val response = client.execute(request)
		//println(String(response.entity.content.readBytes()))
	}
}
