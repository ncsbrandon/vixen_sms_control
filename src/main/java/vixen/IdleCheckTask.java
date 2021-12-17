package vixen;

import java.util.Calendar;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.AppConfig;
import process.VixenProcess;
import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;

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
		logger.debug("idle check task");

		// get the settings
		AppConfig ac = AppConfig.getInstance();

		// check the time
		if (!ac.timeCheck()) {
			logger.debug("off hours");
			
			// stop (if we started) vixen, during off hours
			if (vp.isRunning())
				vp.stop();

			// stop here
			return;
		}

		// check if the ports exist
		try {
			String portName = ac.getString(AppConfig.PORT_CHECK_NAME);
			CommPortIdentifier.getPortIdentifier(portName);
		} catch (NoSuchPortException e1) {
			logger.debug("wait for the ports to appear");
			return;
		}
		
		// start vixen if it isn't running
		if (!vp.isRunning()) {
			logger.debug("Start vixen");
			// pauses to let vixen complete it's startup
			vp.start();
		}

		// if we can check status,
		if (vc.status()) {
			// and if it's not playing something,
			if (vc.numActive() == 0) {
				// used to hold off any user requests about to happen
				ac.idleCheckHoldoff = Calendar.getInstance();
				ac.idleCheckHoldoff.add(Calendar.SECOND, 10);

				// play the default
				vc.play(ac.getString(AppConfig.PLAY_IDLE_NAME), ac.getString(AppConfig.PLAY_IDLE_FILE));
				
				// wait for it to start
				try {
					logger.info("wait for {}", ac.getString(AppConfig.PLAY_IDLE_NAME));
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					logger.error("start sleep interrupted: {}", e.getMessage());
					Thread.currentThread().interrupt();
				}
			}

			// i'f it's playing more than 1 right now
			if (vc.numActive() > 1) {
				// stop the default
				vc.stop(ac.getString(AppConfig.PLAY_IDLE_NAME), ac.getString(AppConfig.PLAY_IDLE_FILE));
				
				// wait for it to start
				try {
					logger.info("wait for it to stop");
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					logger.error("start sleep interrupted: {}", e.getMessage());
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}
