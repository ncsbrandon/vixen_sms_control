package vixen;

import java.util.Calendar;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.AppConfig;
import process.VixenProcess;

public class IdleCheckTask extends TimerTask {

	private static Logger logger = LoggerFactory.getLogger(IdleCheckTask.class.getSimpleName());

	private VixenControl vc = null;
	private VixenProcess vp = null;

	public IdleCheckTask(VixenControl vc, VixenProcess vp) {
		this.vc = vc;
		this.vp = vp;
	}

	@Override
	public void run() {
		AppConfig ac = AppConfig.getInstance();
		
		// check the time
		Calendar now = Calendar.getInstance();
		if (now.get(Calendar.HOUR_OF_DAY) < ac.getInt(AppConfig.IDLE_CHECK_START_HOUR) ||
			now.get(Calendar.HOUR_OF_DAY) > ac.getInt(AppConfig.IDLE_CHECK_STOP_HOUR)) {
			
			// stop vixen during off hours
			if(vp.isRunning())
				vp.stop();
			
			return;
		}
		
		// make sure it's running
		if(!vp.isRunning())
			vp.start();

		try {
			// get the active song
			vc.status();

			// if it's not playing something,
			if (!vc.isActive()) {
				// play the default
				vc.play(ac.getString(AppConfig.PLAY_IDLE_NAME), ac.getString(AppConfig.PLAY_IDLE_FILE));
			}
		} catch (Exception e) {
			logger.error("idle check failure: " + e.getMessage());
		}
	}
}
