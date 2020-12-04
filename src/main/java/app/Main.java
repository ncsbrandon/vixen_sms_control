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
	private static Timer idleCheck;
	
	public static void main(String[] args) {
		logger.info("starting application");
		
		AppConfig ac = AppConfig.getInstance();
		try {
			ac.load();
			logger.info("config loaded");
		} catch (IOException e) {
			logger.error("config loading error: " + e.getMessage());
			return;
		}
		
		vc = new VixenControl(ac.getString(AppConfig.VIXEN_URL));
		vp = new VixenProcess();
		
		get("/", (req, res) -> "404");

		post("/sms", (req, res) -> {
			res.type("application/xml");
			
			if(timeCheck(ac))
				return createReply(ac.getString(AppConfig.OFF_HOURS));

			String requestBody = req.queryParamOrDefault("Body", "REQUEST_ERROR").trim();
			logger.info("request body: " + requestBody);
			
			if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_REQUEST))) {
				return createReply(ac.getString(AppConfig.PLAY_MENU));
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_1_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_1_NAME), ac.getString(AppConfig.PLAY_1_FILE), ac.getString(AppConfig.PLAY_1_REPLY));
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_2_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_2_NAME), ac.getString(AppConfig.PLAY_2_FILE), ac.getString(AppConfig.PLAY_2_REPLY));
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_3_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_3_NAME), ac.getString(AppConfig.PLAY_3_FILE), ac.getString(AppConfig.PLAY_3_REPLY));
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_4_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_4_NAME), ac.getString(AppConfig.PLAY_4_FILE), ac.getString(AppConfig.PLAY_4_REPLY));
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_5_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_5_NAME), ac.getString(AppConfig.PLAY_5_FILE), ac.getString(AppConfig.PLAY_5_REPLY));
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_6_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_6_NAME), ac.getString(AppConfig.PLAY_6_FILE), ac.getString(AppConfig.PLAY_6_REPLY));
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_7_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_7_NAME), ac.getString(AppConfig.PLAY_7_FILE), ac.getString(AppConfig.PLAY_7_REPLY));
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_8_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_8_NAME), ac.getString(AppConfig.PLAY_8_FILE), ac.getString(AppConfig.PLAY_8_REPLY));
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_9_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_9_NAME), ac.getString(AppConfig.PLAY_9_FILE), ac.getString(AppConfig.PLAY_9_REPLY));
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PLAY_10_REQUEST))) {
				return play(ac.getString(AppConfig.PLAY_10_NAME), ac.getString(AppConfig.PLAY_10_FILE), ac.getString(AppConfig.PLAY_10_REPLY));
			} else if (0 == requestBody.compareToIgnoreCase(ac.getString(AppConfig.PAUSE_REQUEST))) {
				return pause(ac.getString(AppConfig.PAUSE_REPLY));
			}

			// not sure what they've requested
			return createReply(ac.getString(AppConfig.PLAY_GEN_REPLY));
		});
		
		// run an idle check every minute
		idleCheck = new Timer();
		idleCheck.schedule(new IdleCheckTask(vc, vp), 1000, ac.getLong(AppConfig.IDLE_CHECK_MS));
	}
	
	public static boolean timeCheck(AppConfig ac) {
		Calendar now = Calendar.getInstance();
		if (now.get(Calendar.HOUR_OF_DAY) < ac.getInt(AppConfig.IDLE_CHECK_START_HOUR) ||
			now.get(Calendar.HOUR_OF_DAY) > ac.getInt(AppConfig.IDLE_CHECK_STOP_HOUR))
			return true;
		
		return false;
	}
	
	public static String createReply(String contents) {
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
	
	public static String play(String name, String file, String reply) {
		(new Thread() {
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
	
	public static String pause(String reply) {
		(new Thread() {
			public void run() {
				if(!vc.stopActive()) {
					logger.error("pause error");
				}
			}
		}).start();
		
		return createReply(reply);
	}
	
 	public static void sendMessage(String toPhone, String messageText) {
		String sid = AppConfig.getInstance().getString(AppConfig.ACCOUNT_SID);
		String auth = AppConfig.getInstance().getString(AppConfig.AUTH_TOKEN);
		String fromPhone = AppConfig.getInstance().getString(AppConfig.FROM_PHONE);
		
		Twilio.init(sid, auth);

		Message message = Message
				.creator(new PhoneNumber(toPhone), new PhoneNumber(fromPhone), messageText)
				.create();

		logger.debug(message.getSid());
	}
}
