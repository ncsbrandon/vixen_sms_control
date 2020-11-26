package vixen_sms_control;

import java.util.Calendar;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdleCheckTask extends TimerTask {

	private static Logger logger = LoggerFactory.getLogger(IdleCheckTask.class.getSimpleName());

	public static final int START_HOUR = 17;
	public static final int STOP_HOUR = 23;

	private VixenControl vc = null;

	public IdleCheckTask(VixenControl vc) {
		this.vc = vc;
	}

	@Override
	public void run() {
		// only during active hours
		Calendar now = Calendar.getInstance();
		if (now.get(Calendar.HOUR_OF_DAY) < START_HOUR || now.get(Calendar.HOUR_OF_DAY) > STOP_HOUR)
			return;

		try {
			// get the active song
			vc.status();

			// if it's not playing something,
			if (!vc.isActive()) {
				// play the default
				vc.play(Main.PLAY_3_NAME, Main.PLAY_3_FILE);
			}
		} catch (Exception e) {
			logger.error("idle check failure: " + e.getMessage());
		}
	}
}
