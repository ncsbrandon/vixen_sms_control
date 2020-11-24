package vixen_sms_control;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Poster {

	private static Logger logger = LoggerFactory.getLogger(Poster.class.getSimpleName());

	public void post() throws IOException, InterruptedException {
		var values = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;

			{
				put("name", "John Doe");
				put("occupation", "gardener");
			}
		};

		ObjectMapper objectMapper = new ObjectMapper();
		String requestBody = objectMapper.writeValueAsString(values);

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://httpbin.org/post"))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		logger.info(response.body());
	}
}
