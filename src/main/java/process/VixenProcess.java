package process;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VixenProcess {

	private static Logger logger = LoggerFactory.getLogger(VixenProcess.class.getSimpleName());

	private static final String VIXEN = "C:\\Program Files\\Vixen Lights\\Vixen\\Vixen.Application.exe";
	private static final String DIR = "C:\\Program Files\\Vixen Lights\\Vixen\\";

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
			Thread.sleep(15000);
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

		// wait for it to stop
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			logger.error("start sleep interrupted: {}", e.getMessage());
			Thread.currentThread().interrupt();
		}
		
		try {
			Files.delete(Path.of("C:\\Users\\ncsbr\\OneDrive\\Documents\\Vixen 3\\.lock"));
		} catch (IOException e) {
			logger.error("unable to delete the lock: {}", e.getMessage());
		}
		
		p = null;
	}
}
