package vixen;

import java.util.Calendar;
import java.util.TimerTask;

import config.AppConfig;
import process.VixenProcess;

public class IdleCheckTask extends TimerTask {

	//private static Logger logger = LoggerFactory.getLogger(IdleCheckTask.class.getSimpleName());

	private VixenControl vc = null;
	private VixenProcess vp = null;

	public IdleCheckTask(VixenControl vc, VixenProcess vp) {
		this.vc = vc;
		this.vp = vp;
	}

	@Override
	public void run() {
		// get the settings
		AppConfig ac = AppConfig.getInstance();
		
		// check the time
		Calendar now = Calendar.getInstance();
		if (now.get(Calendar.HOUR_OF_DAY) < ac.getInt(AppConfig.IDLE_CHECK_START_HOUR) ||
			now.get(Calendar.HOUR_OF_DAY) > ac.getInt(AppConfig.IDLE_CHECK_STOP_HOUR)) {
			
			// stop (if we started) vixen, during off hours
			if(vp.isRunning())
				vp.stop();
			
			// stop here
			return;
		}
		
		// start vixen if it isn't running
		if(!vp.isRunning())
			vp.start();

		// if we can check status,
		if(vc.status()) {	
			// and if it's not playing something,
			if (!vc.isActive()) {
				// play the default
				vc.play(ac.getString(AppConfig.PLAY_IDLE_NAME), ac.getString(AppConfig.PLAY_IDLE_FILE));
			}
		}
	}
}
