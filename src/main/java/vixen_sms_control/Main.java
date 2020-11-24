package vixen_sms_control;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.Properties;

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
										 + "3 - Resonance";
	public static final String PLAY_1_REPLY = "Wizards in Winter - Trans-Siberian Orchestra";
	public static final String PLAY_2_REPLY = "Red and White From State - NC State Marching Band";
	public static final String PLAY_3_REPLY = "Resonance - Home";
	public static final String PLAY_4_REPLY = "Test";
	public static final String PLAY_GEN_REPLY = "Merry Christmas from the Whitakers!";

	public static void main(String[] args) {
		get("/", (req, res) -> "Hello Web");

		post("/sms", (req, res) -> {
			res.type("application/xml");

			// logger.info("request: " + req.body());
			String requestBody = req.queryParamOrDefault("Body", "REQUEST_ERROR");
			logger.info("request body: " + requestBody);

			if (0 == requestBody.compareToIgnoreCase(PLAY_REQUEST)) {
				return createReply(PLAY_MENU);
			} else if (0 == requestBody.compareTo("1")) {
				return createReply(PLAY_1_REPLY);
			} else if (0 == requestBody.compareTo("2")) {
				return createReply(PLAY_2_REPLY);
			} else if (0 == requestBody.compareTo("3")) {
				return createReply(PLAY_3_REPLY);
			} else if (0 == requestBody.compareTo("4")) {
				return createReply(PLAY_4_REPLY);
			}

			return createReply(PLAY_GEN_REPLY);
		});
	}
	
	

	public static void sendMessage(Properties prop) {
		Twilio.init(prop.getProperty("ACCOUNT_SID"), prop.getProperty("AUTH_TOKEN"));

		com.twilio.rest.api.v2010.account.Message message = com.twilio.rest.api.v2010.account.Message
				.creator(new PhoneNumber("+14159352345"), // to
						new PhoneNumber("+12569523125"), // from
						"Where's Wallace?")
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
