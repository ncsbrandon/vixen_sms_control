package process;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPAPIControl {
	
	private static Logger logger = LoggerFactory.getLogger(HTTPAPIControl.class.getSimpleName());

	private HttpClient client = HttpClient.newHttpClient();
	
	private String url = "";
	
	public HTTPAPIControl() {}
	
	public HTTPAPIControl(String url) {
		this.url = url;
	}
	
	protected void post(String page, String requestBody) throws IOException, InterruptedException {
		// build the request
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
	
	protected String get(String page) throws IOException, InterruptedException {
		// build the request
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
