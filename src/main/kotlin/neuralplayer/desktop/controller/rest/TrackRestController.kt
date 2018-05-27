package neuralplayer.desktop.controller.rest

import neuralplayer.desktop.controller.ConfigController
import neuralplayer.desktop.controller.rest.dto.TrackDto
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.impl.client.HttpClientBuilder
import tornadofx.*
import java.io.File



/**
 * @author Pere
 * @since 2018/04/22
 */
class TrackRestController : Controller() {

	private val api: Rest by inject()
	private val configController: ConfigController by inject()

	fun getAll()
			= api.get("tracks").list().toModel<TrackDto>()
	fun update(id: Long, trackDto: TrackDto)
			= api.put("tracks/$id", trackDto).one().toModel<TrackDto>()
	fun delete(id: Long)
			= api.post("tracks/$id")
	//fun upload(id: Long, file: File)
	//		= api.post("tracks/$id/upload", multipartStream(file))
	fun download(id: Long)
			= api.get("tracks/$id/download").content()

	// Workaround until tornado supports multipart or I decide to make my own concise http api
	fun create(trackDto: TrackDto, file: File): Long {
		val client = HttpClientBuilder.create().build()
		val request = HttpPost("${api.baseURI}/tracks")
		val multipart = MultipartEntityBuilder.create()
				.addTextBody("track", JsonBuilder().apply { trackDto.toJSON(this) }.build().toString(), ContentType.APPLICATION_JSON)
				.addBinaryBody("file", file)
				.build()
		val credentials = UsernamePasswordCredentials(configController.username.value, configController.password.value)
		request.addHeader(BasicScheme().authenticate(credentials, request, HttpClientContext()))
		request.entity = multipart
		val ret = client.execute(request)
		return ret.entity.content.toJSON().toModel<TrackDto>().id!!
	}
}
