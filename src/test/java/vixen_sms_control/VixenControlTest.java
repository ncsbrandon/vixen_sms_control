package vixen_sms_control;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VixenControlTest {

	private static Logger logger = LoggerFactory.getLogger(VixenControlTest.class.getSimpleName());

	@Test
	public void testPlay() {
		VixenControl poster = new VixenControl("http://192.168.14.2:8888/");
		logger.info("testing");
		
		poster.status();
		
		poster.play("wizards", "C:\\Users\\ncsbr\\Documents\\Vixen 3\\Sequence\\wizards.tim");

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {}

		poster.status();
		
		poster.stopActive();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {}
		
		poster.status();
	}
}
