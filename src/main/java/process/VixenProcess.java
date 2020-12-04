package process;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VixenProcess {

	private static Logger logger = LoggerFactory.getLogger(VixenProcess.class.getSimpleName());

	private static String VIXEN = "C:\\Program Files\\Vixen\\VixenApplication.exe";
	private Process p = null;
	
	public VixenProcess() {
	}
	
	public boolean isRunning() {
		if(p != null && p.isAlive())
			return true;
		
		return false;
	}
	
	public void start() {
		try {
			p = new ProcessBuilder(VIXEN).start();
			logger.info("vixen started");
		} catch (IOException e) {
			logger.error("start vixen failed");
			p = null;
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
