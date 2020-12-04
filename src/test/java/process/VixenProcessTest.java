package process;

import static org.junit.Assert.*;

import org.junit.Test;

public class VixenProcessTest {

	@Test
	public void testStart() {
		VixenProcess vp = new VixenProcess();
		
		assertFalse(vp.isRunning());
		vp.start();
		safeSleep(15000);
		assertTrue(vp.isRunning());
		vp.stop();
		safeSleep(10000);
		assertFalse(vp.isRunning());
	}

	void safeSleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e1) {}
	}
}
