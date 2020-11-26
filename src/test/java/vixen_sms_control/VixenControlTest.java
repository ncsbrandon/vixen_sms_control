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
		
		poster.play("wizards", "C:\\Users\\ncsbr\\Documents\\Vixen 3\\Sequence\\wizards.tim");
		safeSleep(5000);

		poster.play("tree", "C:\\Users\\ncsbr\\Documents\\Vixen 3\\Sequence\\tree.tim");
		safeSleep(5000);
		
		poster.stopActive();
		safeSleep(5000);
		
		poster.stopActive();
		safeSleep(5000);
		
		poster.status();
	}
	
	void safeSleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e1) {}
	}
}
