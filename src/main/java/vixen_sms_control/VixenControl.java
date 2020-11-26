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
	
	public VixenControl(String url) {
		this.url = url;
	}

	public boolean isActive() {
		if(active != null && active.sequence != null && active.sequence.name.length() > 0)
			return true;
		
		return false;
	}
	
	public String getActive() {
		if(active != null && active.sequence != null && active.sequence.name.length() > 0)
			return active.sequence.name;
		
		return "";
	}
	
	public boolean play(String name, String file) {
		// if there's a currently active song, stop it first
		stopActive();
			
		// build the request body
		StringBuilder requestBody = new StringBuilder();
		requestBody.append("Name=" + name);
		requestBody.append("&");
		requestBody.append("FileName=" + file);

		// post
		try {
			logger.info("requesting: " + name);
			post("api/play/playSequence", requestBody.toString());
		} catch (IOException | InterruptedException e) {
			logger.error("play failure", e);
			return false;
		}
		
		return true;
	}
	
	public boolean stopActive() {
		// if there's a currently active song, stop it first
		status();
		if(isActive()) {
			return stop(active.sequence.name, active.sequence.fileName);
		}
		
		return true;
	}
	
	private boolean stop(String name, String filename) {
		StringBuilder requestBody = new StringBuilder();
		requestBody.append("Name=" + name);
		requestBody.append("&");
		requestBody.append("FileName=" + filename);
		
		try {
			logger.info("stopping: " + name);
			post("api/play/stopSequence", requestBody.toString());
		} catch (IOException | InterruptedException e) {
			logger.error("stop failure", e);
			return false;
		}
		
		return true;
	}
	
	public boolean status() {
		String response;
		try {
			response = get("api/play/status");
		} catch (IOException | InterruptedException e) {
			logger.error("stop failure", e);
			response = "";
		}
		
		try {
			if(response.length() > 0) {
				// strip the square brackets
				response = response.substring(1, response.length()-1);
				
				if(response.length() > 0) {
					// parse the json
					active = om.readValue(response, Root.class);
					logger.info("active song: " + active.toString());
				}else {
					active = null;
					logger.info("no active song");
				}
			} else {
				active = null;
				logger.info("no response");
				return false;
			}
		} catch (Exception e) {
			active = null;
			logger.error("status response parse failure", e);
			return false;
		}
		
		return true;
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
		logger.debug("-> " + request.toString());
		//Map<String, List<String>> requestHeaders = request.headers().map();
		//for (Entry<String, List<String>> requestHeader : requestHeaders.entrySet()) {
		//	logger.info("-> Header Name - " + requestHeader.getKey() + ", Value - " + requestHeader.getValue().toString());
		//}
		//logger.info("-> " + requestBody);

		// send and receive
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		// logging
		logger.debug("<- " + response.toString());
		//Map<String, List<String>> responseHeaders = response.headers().map();
		//for (Entry<String, List<String>> responseHeader : responseHeaders.entrySet()) {
		//	logger.info("<- Header Name - " + responseHeader.getKey() + ", Value - " + responseHeader.getValue().toString());
		//}
		logger.debug("<- " + response.body());
	}
	
	private String get(String page) throws IOException, InterruptedException {
		// build the request
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url + page))
				.build();

		// logging
		logger.debug("-> " + request.toString());
		//Map<String, List<String>> requestHeaders = request.headers().map();
		//for (Entry<String, List<String>> requestHeader : requestHeaders.entrySet()) {
		//	logger.info("-> Header Name - " + requestHeader.getKey() + ", Value - " + requestHeader.getValue().toString());
		//}

		// send and receive
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		// logging
		logger.debug("<- " + response.toString());
		//Map<String, List<String>> responseHeaders = response.headers().map();
		//for (Entry<String, List<String>> responseHeader : responseHeaders.entrySet()) {
		//	logger.info("<- Header Name - " + responseHeader.getKey() + ", Value - " + responseHeader.getValue().toString());
		//}
		logger.debug("<- " + response.body());
		
		return response.body();
	}
}
