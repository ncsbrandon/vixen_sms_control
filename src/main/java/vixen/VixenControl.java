package vixen;

import java.io.IOException;
import java.net.ConnectException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import process.HTTPAPIControl;
import status.Root;

public class VixenControl extends HTTPAPIControl {

	private static Logger logger = LoggerFactory.getLogger(VixenControl.class.getSimpleName());

	private ObjectMapper om = new ObjectMapper();
	private Root[] active = null;
	
	public VixenControl(String url) {
		super(url);
	}

	public synchronized int numActive() {
		if(active != null)
			return active.length;
		
		return 0;
	}

	public synchronized boolean status() {
		// request active songs
		String response = null;
		try {
			response = get("api/play/status");
		} catch (ConnectException e) {
			logger.error("connection failure: {}", e.getMessage());
			active = null;
			return false;		
		} catch (IOException e1) {
			logger.error("status failure: {}", e1.getMessage());
			active = null;
			return false;
		} catch (InterruptedException e2) {
			logger.error("status interrupted: {}", e2.getMessage());
			Thread.currentThread().interrupt();
		}
		
		// parse the json
		try {
			if(response != null && response.length() > 0) {
				active = om.readValue(response, Root[].class);
				
				if(active.length != 1)
					logger.info("[{}] active songs", active.length);
			}else {
				active = null;
				logger.info("no active song");
			}
		} catch (Exception e) {
			active = null;
			logger.error("status response parse failure: {}", e.getMessage());
			return false;
		}
		
		return true;
	}

	public synchronized boolean stopActive() {
		// find out what's playing
		if(!status())
			return false;
		
		// stop them all
		if(numActive() > 0) {
			for(Root song : active) {
				stop(song.sequence.name, song.sequence.fileName);
			}
		}
		
		return true;
	}
	
	public boolean stop(String name, String filename) {
		// build the request body
		StringBuilder requestBody = new StringBuilder();
		requestBody.append("Name=" + name);
		requestBody.append("&");
		requestBody.append("FileName=" + filename);
		
		// post
		try {
			logger.info("stopping: {}", name);
			post("api/play/stopSequence", requestBody.toString());
		} catch (IOException e1) {
			logger.error("stop failure: {}", e1.getMessage());
			active = null;
			return false;
		} catch (InterruptedException e2) {
			logger.error("stop interrupted: {}", e2.getMessage());
			Thread.currentThread().interrupt();
		}
		
		return true;
	}
	
	public synchronized boolean play(String name, String file) {
		// if there's a currently active song, stop it first
		if(!stopActive())
			return false;
			
		// build the request body
		StringBuilder requestBody = new StringBuilder();
		requestBody.append("Name=" + name);
		requestBody.append("&");
		requestBody.append("FileName=" + file);

		// post
		try {
			logger.info("requesting: {}", name);
			post("api/play/playSequence", requestBody.toString());
		} catch (IOException e1) {
			logger.error("play failure: {}", e1.getMessage());
			active = null;
			return false;
		} catch (InterruptedException e2) {
			logger.error("play interrupted: {}", e2.getMessage());
			Thread.currentThread().interrupt();
		}
		
		return true;
	}
}
