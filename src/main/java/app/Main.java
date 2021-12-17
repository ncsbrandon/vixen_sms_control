package app;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.type.PhoneNumber;

import config.AppConfig;
import process.VixenProcess;
import vixen.IdleCheckTask;
import vixen.VixenControl;

public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class.getSimpleName());

	private static VixenControl vc;
	private static VixenProcess vp;
	private static Timer idleCheck = null;
		
	public static void main(String[] args) {
		logger.info("starting application");
		System.out.println("classpath: " + System.getProperty("java.class.path"));
		AppConfig ac = AppConfig.getInstance();
		try {
			ac.load();
			logger.info("config loaded");
		} catch (IOException e) {
			logger.error("config loading error: {}", e.getMessage());
			return;
		}
		
		logger.debug("debug logging");
		
		logger.info("creating vixen objects");
		vc = new VixenControl(ac.getString(AppConfig.VIXEN_URL));
		vp = new VixenProcess();
		
		logger.info("setting up endpoints");
		get("/", (req, res) -> "404");

		post("/sms", (req, res) -> {
			res.type("application/xml");
			
			if(!ac.timeCheck())
				return createReply(ac.getString(AppConfig.OFF_HOURS));

			String requestBody = req.queryParamOrDefault("Body", "REQUEST_ERROR").trim();
			logger.info("request body: {}",  requestBody);
			
			if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_REQUEST))) {
				return createReply(ac.getString(AppConfig.PLAY_MENU));
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_1_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_1_NAME), ac.getString(AppConfig.PLAY_1_FILE), ac.getString(AppConfig.PLAY_1_REPLY), ac);
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_2_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_2_NAME), ac.getString(AppConfig.PLAY_2_FILE), ac.getString(AppConfig.PLAY_2_REPLY), ac);
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_3_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_3_NAME), ac.getString(AppConfig.PLAY_3_FILE), ac.getString(AppConfig.PLAY_3_REPLY), ac);
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_4_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_4_NAME), ac.getString(AppConfig.PLAY_4_FILE), ac.getString(AppConfig.PLAY_4_REPLY), ac);
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_5_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_5_NAME), ac.getString(AppConfig.PLAY_5_FILE), ac.getString(AppConfig.PLAY_5_REPLY), ac);
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_6_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_6_NAME), ac.getString(AppConfig.PLAY_6_FILE), ac.getString(AppConfig.PLAY_6_REPLY), ac);
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_7_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_7_NAME), ac.getString(AppConfig.PLAY_7_FILE), ac.getString(AppConfig.PLAY_7_REPLY), ac);
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_8_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_8_NAME), ac.getString(AppConfig.PLAY_8_FILE), ac.getString(AppConfig.PLAY_8_REPLY), ac);
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_9_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_9_NAME), ac.getString(AppConfig.PLAY_9_FILE), ac.getString(AppConfig.PLAY_9_REPLY), ac);
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_10_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_10_NAME), ac.getString(AppConfig.PLAY_10_FILE), ac.getString(AppConfig.PLAY_10_REPLY), ac);
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PAUSE_REQUEST))) {
				return pause(ac.getString(AppConfig.PAUSE_REPLY), ac);
			}

			// not sure what they've requested
			return createReply(ac.getString(AppConfig.PLAY_GEN_REPLY));
		});
		
		logger.info("finished endoints");
		
		// run an idle check every so often
		long checkms = ac.getLong(AppConfig.IDLE_CHECK_MS);
		logger.info("creating idle check [{}]", checkms);
		createIdleCheck(checkms);
	}
	
	private static void createIdleCheck(long period) {
		if(idleCheck != null) {
			idleCheck.cancel();
			idleCheck = null;
		}
		
		idleCheck = new Timer();
		idleCheck.schedule(new IdleCheckTask(vc, vp), period, period);
	}
	
	private static void holdOffFromIdleCheck(Calendar holdOff) {
		if(holdOff == null)
			return;
				
		while(holdOff.after(Calendar.getInstance())) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.error("holdOffFromIdleCheck interrupted");
				Thread.currentThread().interrupt();
			}
		}
	}
	
	// create a twilio reply with the contents
 	private static String createReply(String contents) {
		Body body = new Body.Builder(contents)
				.build();
		com.twilio.twiml.messaging.Message sms = new com.twilio.twiml.messaging.Message.Builder()
				.body(body)
				.build();
		MessagingResponse twiml = new MessagingResponse.Builder()
				.message(sms)
				.build();
		return twiml.toXml();
	}
	
	// launch a thread to play a song
	private static String play(String name, String file, String reply, AppConfig ac) {
		// reschedule the idle check
		createIdleCheck(ac.getLong(AppConfig.IDLE_CHECK_MS));
		
		holdOffFromIdleCheck(ac.idleCheckHoldoff);
		
		(new Thread() {
			@Override
			public void run() {
				if(!vc.play(name, file)) {
					logger.error("play error");
				} else {
					sendMessage(AppConfig.getInstance().getString(AppConfig.MY_PHONE), "Playing " + name);
				}
			}
		}).start();
		
		return createReply(reply);
	}
	
	// launch a thread to stop songs
	private static String pause(String reply, AppConfig ac) {
		// reschedule the idle check
		createIdleCheck(ac.getLong(AppConfig.IDLE_CHECK_MS));
				
		holdOffFromIdleCheck(ac.idleCheckHoldoff);
		
		(new Thread() {
			@Override
			public void run() {
				if(!vc.stopActive()) {
					logger.error("stop error");
				}
			}
		}).start();
		
		return createReply(reply);
	}
	
	// send an SMS
 	private static void sendMessage(String toPhone, String messageText) {
		String sid = AppConfig.getInstance().getString(AppConfig.ACCOUNT_SID);
		String auth = AppConfig.getInstance().getString(AppConfig.AUTH_TOKEN);
		String fromPhone = AppConfig.getInstance().getString(AppConfig.FROM_PHONE);
		
		Twilio.init(sid, auth);

		Message message = Message
				.creator(new PhoneNumber(toPhone), new PhoneNumber(fromPhone), messageText)
				.create();

		logger.info("sent [{}]", message.getSid());
	}
}
