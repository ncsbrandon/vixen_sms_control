package process;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VixenProcess {

	private static Logger logger = LoggerFactory.getLogger(VixenProcess.class.getSimpleName());

	private static String VIXEN = "C:\\Program Files\\Vixen\\VixenApplication.exe";
	private static String DIR = "C:\\Program Files\\Vixen\\";
	
	private Process p = null;
	
	public VixenProcess() {
	}
	
	public boolean isRunning() {
		if(p != null && p.isAlive())
			return true;
		
		return false;
	}
	
	public void start() {
		if(isRunning())
			return;
		
		try {
			ProcessBuilder pb = new ProcessBuilder(VIXEN);
			pb.directory(new File(DIR));
			p = pb.start();
			logger.info("vixen started");
		} catch (IOException e) {
			logger.error("start vixen failed");
			p = null;
		}
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			logger.error("start sleep interrupted: " + e.getMessage());
		}
	}
	
	public void stop() {
		if(!isRunning())
			return;
		
		p.destroy();
		logger.info("vixen stopped");
		
		p = null;
	}
}
