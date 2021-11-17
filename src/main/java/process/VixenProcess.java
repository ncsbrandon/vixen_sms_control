package process;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VixenProcess {

	private static Logger logger = LoggerFactory.getLogger(VixenProcess.class.getSimpleName());

	private static final String VIXEN = "C:\\Program Files\\Vixen\\VixenApplication.exe";
	private static final String DIR = "C:\\Program Files\\Vixen\\";

	private Process p = null;

	public VixenProcess() {
		// constructor
	}

	public boolean isRunning() {
		// is alive
		return (p != null && p.isAlive());
	}

	public void start() {
		// already running
		if (isRunning())
			return;

		// start the application
		try {
			ProcessBuilder pb = new ProcessBuilder(VIXEN);
			pb.directory(new File(DIR));
			p = pb.start();
			logger.info("vixen started");
		} catch (IOException e) {
			logger.error("start vixen failed: {}", e.getMessage());
			p = null;
			return;
		}

		// wait for it to start
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			logger.error("start sleep interrupted: {}", e.getMessage());
			Thread.currentThread().interrupt();
		}
	}

	public void stop() {
		// already stopped
		if (!isRunning())
			return;

		// request graceful stop
		p.destroy();
		logger.info("vixen stopped");

		p = null;
	}
}
