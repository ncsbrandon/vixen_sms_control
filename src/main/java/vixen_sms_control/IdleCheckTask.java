package vixen_sms_control;

import java.util.Calendar;
import java.util.TimerTask;

public class IdleCheckTask extends TimerTask {

	public static final int START_HOUR = 5; //17;
	public static final int STOP_HOUR = 23;
	
	public VixenControl vc = null;

	public IdleCheckTask(VixenControl vc) {
		this.vc = vc;
	}

	@Override
	public void run() {
		// only during active hours
		Calendar now = Calendar.getInstance();
		if (now.get(Calendar.HOUR_OF_DAY) < START_HOUR || now.get(Calendar.HOUR_OF_DAY) > STOP_HOUR)
			return;

		// get the active song
		vc.status();

		// play the default
		if (!vc.isActive()) {
			vc.play(Main.PLAY_3_NAME, Main.PLAY_3_FILE);
		}
	}

}
