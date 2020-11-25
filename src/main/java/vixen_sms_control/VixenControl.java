package vixen_sms_control;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VixenControl {

	private static Logger logger = LoggerFactory.getLogger(VixenControl.class.getSimpleName());

	private String url = "";
	private String lastName = "";
	private String lastFile = "";
	private ObjectMapper om = new ObjectMapper();
	private Root root;

	public VixenControl(String url) {
		this.url = url;
	}

	public void play(String name, String file) {
		// build the request body
		StringBuilder requestBody = new StringBuilder();
		requestBody.append("Name=" + name);
		requestBody.append("&");
		requestBody.append("FileName=" + file);

		try {
			post("api/play/playSequence", requestBody.toString());
		} catch (IOException | InterruptedException e) {
			logger.error("play failure", e);
		}
		
		lastName = name;
		lastFile = file;
	}

	public void stop() {
		StringBuilder requestBody = new StringBuilder();
		requestBody.append("Name=" + lastName);
		requestBody.append("&");
		requestBody.append("FileName=" + lastFile);
		
		try {
			post("api/play/stopSequence", requestBody.toString());
		} catch (IOException | InterruptedException e) {
			logger.error("stop failure", e);
		}
	}
	
	public void status() {
		try {
			get("api/play/status");
		} catch (IOException | InterruptedException e) {
			logger.error("stop failure", e);
		}
	}

	private void post(String page, String requestBody) throws IOException, InterruptedException {
		// build the request
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url + page))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
				.header("Accept", "application/json, text/javascript, */*; q=0.01")
				.build();

		// logging
		logger.info("-> " + request.toString());
		//Map<String, List<String>> requestHeaders = request.headers().map();
		//for (Entry<String, List<String>> requestHeader : requestHeaders.entrySet()) {
		//	logger.info("-> Header Name - " + requestHeader.getKey() + ", Value - " + requestHeader.getValue().toString());
		//}
		//logger.info("-> " + requestBody);

		// send and receive
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		// logging
		logger.info("<- " + response.toString());
		//Map<String, List<String>> responseHeaders = response.headers().map();
		//for (Entry<String, List<String>> responseHeader : responseHeaders.entrySet()) {
		//	logger.info("<- Header Name - " + responseHeader.getKey() + ", Value - " + responseHeader.getValue().toString());
		//}
		logger.info("<- " + response.body());
	}
	
	private void get(String page) throws IOException, InterruptedException {
		// build the request
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url + page))
				.build();

		// logging
		logger.info("-> " + request.toString());
		//Map<String, List<String>> requestHeaders = request.headers().map();
		//for (Entry<String, List<String>> requestHeader : requestHeaders.entrySet()) {
		//	logger.info("-> Header Name - " + requestHeader.getKey() + ", Value - " + requestHeader.getValue().toString());
		//}

		// send and receive
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		logger.info("<- " + response.body());
		if(response.body().length() > 0) {
			root = om.readValue(response.body(), Root.class);
		} else {
			root = new Root();
		}
		logger.info(root.toString());
		
		// logging
		logger.info("<- " + response.toString());
		//Map<String, List<String>> responseHeaders = response.headers().map();
		//for (Entry<String, List<String>> responseHeader : responseHeaders.entrySet()) {
		//	logger.info("<- Header Name - " + responseHeader.getKey() + ", Value - " + responseHeader.getValue().toString());
		//}
		//logger.info("<- " + response.body());
	}
}
