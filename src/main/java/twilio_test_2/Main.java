package twilio_test_2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;

import static spark.Spark.*;

public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static final String ACCOUNT_SID = "ACf8818985bc091eb19e153d01ca4a4c31";
	public static final String AUTH_TOKEN = "c903b9edbd6df76e36522b31444e9bdb";

	public static final String PLAY_REQUEST = "play";
	public static final String PLAY_MENU = "Tune your radio to 88.3 FM and select a song:\n"
			+ "1 - Wizards in Winter\n"
			+ "2 - Red and White\n"
			+ "3 - Resonance";
	public static final String PLAY_WIZ_REPLY = "Wizards in Winter - Trans-Siberian Orchestra!";
	
	public static void main(String[] args) {
		get("/", (req, res) -> "Hello Web");

		post("/sms", (req, res) -> {
			res.type("application/xml");

			//logger.info("request: " + req.body());
			String requestBody = req.queryParamOrDefault("Body", "REQUEST_ERROR");
			logger.info("request body: " + requestBody);
			
			if (0 == requestBody.compareToIgnoreCase(PLAY_REQUEST)) {
				return createReply(PLAY_MENU);
			} else if (0 == requestBody.compareTo("1")) {
				return createReply(PLAY_WIZ_REPLY);
			} else if (0 == requestBody.compareTo("2")) {
				return createReply(PLAY_WIZ_REPLY);
			}
			
			return createReply("Merry Christmas from the Whitakers!");
		});
	}

	public static void SendAMessage() {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

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
