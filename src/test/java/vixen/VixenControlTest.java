package vixen;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VixenControlTest {

	private static Logger logger = LoggerFactory.getLogger(VixenControlTest.class.getSimpleName());

	@Test
	public void testPlay() {
		VixenControl poster = new VixenControl("http://192.168.14.2:8888/");
		logger.info("testing");
		
		// stop nothing		
		assertTrue(poster.stopActive());
		
		// play test
		assertTrue(poster.play("test", "C:\\Users\\ncsbr\\Documents\\Vixen 3\\Sequence\\test.tim"));
		safeSleep(5000);

		// interrupt test with tree
		assertTrue(poster.play("tree", "C:\\Users\\ncsbr\\Documents\\Vixen 3\\Sequence\\tree.tim"));
		safeSleep(5000);
		
		// stop tree
		assertTrue(poster.stopActive());
		safeSleep(5000);
		
		// nothing playing
		assertTrue(poster.status());
	}
	
	void safeSleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e1) {}
	}
}
