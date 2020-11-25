package vixen_sms_control;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class VixenControl {

	private static Logger logger = LoggerFactory.getLogger(VixenControl.class.getSimpleName());

	private String url = "";
	private ObjectMapper om = new ObjectMapper();
	private Root active;
	
	public boolean isActive() {
		if(active != null && active.sequence != null && active.sequence.name.length() > 0)
			return true;
		
		return false;
	}

	public VixenControl(String url) {
		this.url = url;
	}

	public void play(String name, String file) {
		// if there's a currently active song, stop it first
		status();
		if(isActive()) {
			stopActive();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {}
		}
			
		// build the request body
		StringBuilder requestBody = new StringBuilder();
		requestBody.append("Name=" + name);
		requestBody.append("&");
		requestBody.append("FileName=" + file);

		// post
		try {
			post("api/play/playSequence", requestBody.toString());
		} catch (IOException | InterruptedException e) {
			logger.error("play failure", e);
		}
	}
	
	public void stopActive() {
		// if there's a currently active song, stop it first
		status();
		if(isActive()) {
			stop(active.sequence.name, active.sequence.fileName);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {}
		}
	}
	
	private void stop(String name, String filename) {
		StringBuilder requestBody = new StringBuilder();
		requestBody.append("Name=" + name);
		requestBody.append("&");
		requestBody.append("FileName=" + filename);
		
		try {
			post("api/play/stopSequence", requestBody.toString());
		} catch (IOException | InterruptedException e) {
			logger.error("stop failure", e);
		}
	}
	
	public void status() {
		String response;
		try {
			response = get("api/play/status");
		} catch (IOException | InterruptedException e) {
			logger.error("stop failure", e);
			response = "";
		}
		
		// strip the square brackets
		response = response.substring(1, response.length()-1);
		
		// parse the json
		try {
			if(response.length() > 0) {
				active = om.readValue(response, Root.class);
				logger.info("active song: " + active.toString());
			} else {
				active = null;
				logger.info("no active song");
			}
		} catch (Exception e) {
			active = null;
			logger.error("status response parse failure", e);
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
	
	private String get(String page) throws IOException, InterruptedException {
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

		// logging
		logger.info("<- " + response.toString());
		//Map<String, List<String>> responseHeaders = response.headers().map();
		//for (Entry<String, List<String>> responseHeader : responseHeaders.entrySet()) {
		//	logger.info("<- Header Name - " + responseHeader.getKey() + ", Value - " + responseHeader.getValue().toString());
		//}
		logger.info("<- " + response.body());
		
		return response.body();
	}
}
