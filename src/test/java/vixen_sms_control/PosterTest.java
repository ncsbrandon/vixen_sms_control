package vixen_sms_control;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PosterTest {

	private static Logger logger = LoggerFactory.getLogger(PosterTest.class.getSimpleName());

	@Test
	public void testPost() {
		Poster poster = new Poster();
		try {
			poster.post();
			logger.info("post success");
		} catch (IOException e) {
			logger.error("post failure", e);
		} catch (InterruptedException e) {
			logger.error("post failure", e);
		}
	}

}
