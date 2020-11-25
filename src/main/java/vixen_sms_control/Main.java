package vixen_sms_control;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twilio.Twilio;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.type.PhoneNumber;

public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);

	public static final String PLAY_REQUEST = "play";
	public static final String PLAY_MENU = "Tune your radio to 88.3 FM and then select a song:\n"
										 + "1 - Wizards in Winter\n"
										 + "2 - Red and White From State\n"
										 + "3 - Christmas tree";
	public static final String PLAY_1_REQUEST = "1";
	public static final String PLAY_1_REPLY = "Loading Wizards in Winter by Trans-Siberian Orchestra...";
	public static final String PLAY_1_NAME = "wizards";
	public static final String PLAY_1_FILE = "C:\\Users\\ncsbr\\Documents\\Vixen 3\\Sequence\\wizards.tim";
	
	public static final String PLAY_2_REQUEST = "2";
	public static final String PLAY_2_REPLY = "Loading Red and White From State by NC State Marching Band...";
	public static final String PLAY_2_NAME = "red_and_white";
	public static final String PLAY_2_FILE = "C:\\Users\\ncsbr\\Documents\\Vixen 3\\Sequence\\red_and_white.tim";
	
	public static final String PLAY_3_REQUEST = "3";
	public static final String PLAY_3_REPLY = "Loading Christmas tree...";
	public static final String PLAY_3_NAME = "tree";
	public static final String PLAY_3_FILE = "C:\\Users\\ncsbr\\Documents\\Vixen 3\\Sequence\\tree.tim";
	
	public static final String PLAY_4_REQUEST = "4";
	public static final String PLAY_4_REPLY = "Loading Test pattern...";
	public static final String PLAY_4_NAME = "test";
	public static final String PLAY_4_FILE = "C:\\Users\\ncsbr\\Documents\\Vixen 3\\Sequence\\test.tim";
	
	public static final String PAUSE_REQUEST = "pause";
	public static final String PAUSE_REPLY = "Pausing...";
	
	public static final String PLAY_GEN_REPLY = "Merry Christmas from the Whitaker family!";

	private static VixenControl vc = new VixenControl("http://192.168.14.2:8888/");
	private static Timer idleCheck;
	
	public static void main(String[] args) {
		get("/", (req, res) -> "Hello Web");

		post("/sms", (req, res) -> {
			res.type("application/xml");

			String requestBody = req.queryParamOrDefault("Body", "REQUEST_ERROR").trim();
			logger.info("request body: " + requestBody);

			if (0 == requestBody.compareToIgnoreCase(PLAY_REQUEST)) {
				return createReply(PLAY_MENU);
			} else if (0 == requestBody.compareToIgnoreCase(PLAY_1_REQUEST)) {
				return play(PLAY_1_NAME, PLAY_1_FILE, PLAY_1_REPLY);
			} else if (0 == requestBody.compareToIgnoreCase(PLAY_2_REQUEST)) {
				return play(PLAY_2_NAME, PLAY_2_FILE, PLAY_2_REPLY);
			} else if (0 == requestBody.compareToIgnoreCase(PLAY_3_REQUEST)) {
				return play(PLAY_3_NAME, PLAY_3_FILE, PLAY_3_REPLY);
			} else if (0 == requestBody.compareToIgnoreCase(PLAY_4_REQUEST)) {
				return play(PLAY_4_NAME, PLAY_4_FILE, PLAY_4_REPLY);
			} else if (0 == requestBody.compareToIgnoreCase(PAUSE_REQUEST)) {
				return pause(PAUSE_REPLY);
			}

			return createReply(PLAY_GEN_REPLY);
		});
		
		// run an idle check every minute
		idleCheck = new Timer();
		idleCheck.schedule(new IdleCheckTask(vc), 4000, 60000);
	}
	
	public static String play(String name, String file, String reply) {
		(new Thread() {
			public void run() {
			    vc.play(name, file);
			}
		}).start();
		return createReply(reply);
	}
	
	public static String pause(String reply) {
		(new Thread() {
			public void run() {
			    vc.stopActive();
			}
		}).start();
		return createReply(reply);
	}
	
 	public static void sendMessage(String toPhone, String messageText) {
		String sid = AppConfig.getInstance().get(AppConfig.CONFIG_ACCOUNT_SID);
		String auth = AppConfig.getInstance().get(AppConfig.CONFIG_AUTH_TOKEN);
		String fromPhone = AppConfig.getInstance().get(AppConfig.CONFIG_FROM_PHONE);
		
		Twilio.init(sid, auth);

		com.twilio.rest.api.v2010.account.Message message = com.twilio.rest.api.v2010.account.Message
				.creator(new PhoneNumber(toPhone),
						new PhoneNumber(fromPhone),
						messageText)
				.create();

		System.out.println(message.getSid());
	}

	public static String createReply(String contents) {
		Body body = new Body.Builder(contents).build();
		com.twilio.twiml.messaging.Message sms = new com.twilio.twiml.messaging.Message.Builder().body(body).build();
		MessagingResponse twiml = new MessagingResponse.Builder().message(sms).build();
		return twiml.toXml();
	}
}
