package config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfig {

	private static Logger logger = LoggerFactory.getLogger(AppConfig.class.getSimpleName());

	public static final String ACCOUNT_SID = "ACCOUNT_SID";
	public static final String AUTH_TOKEN = "AUTH_TOKEN";
	public static final String FROM_PHONE = "FROM_PHONE";
	public static final String VIXEN_URL = "VIXEN_URL";
	public static final String PLAY_REQUEST = "PLAY_REQUEST";
	public static final String PLAY_MENU = "PLAY_MENU";
	public static final String OFF_HOURS = "OFF_HOURS";
	public static final String PLAY_1_REQUEST = "PLAY_1_REQUEST";
	public static final String PLAY_1_REPLY = "PLAY_1_REPLY";
	public static final String PLAY_1_NAME = "PLAY_1_NAME";
	public static final String PLAY_1_FILE = "PLAY_1_FILE";
	public static final String PLAY_2_REQUEST = "PLAY_2_REQUEST";
	public static final String PLAY_2_REPLY = "PLAY_2_REPLY";
	public static final String PLAY_2_NAME = "PLAY_2_NAME";
	public static final String PLAY_2_FILE = "PLAY_2_FILE";	
	public static final String PLAY_3_REQUEST = "PLAY_3_REQUEST";
	public static final String PLAY_3_REPLY = "PLAY_3_REPLY";
	public static final String PLAY_3_NAME = "PLAY_3_NAME";
	public static final String PLAY_3_FILE = "PLAY_3_FILE";
	public static final String PLAY_4_REQUEST = "PLAY_4_REQUEST";
	public static final String PLAY_4_REPLY = "PLAY_4_REPLY";
	public static final String PLAY_4_NAME = "PLAY_4_NAME";
	public static final String PLAY_4_FILE = "PLAY_4_FILE";	
	public static final String PLAY_5_REQUEST = "PLAY_5_REQUEST";
	public static final String PLAY_5_REPLY = "PLAY_5_REPLY";
	public static final String PLAY_5_NAME = "PLAY_5_NAME";
	public static final String PLAY_5_FILE = "PLAY_5_FILE";
	public static final String PLAY_6_REQUEST = "PLAY_6_REQUEST";
	public static final String PLAY_6_REPLY = "PLAY_6_REPLY";
	public static final String PLAY_6_NAME = "PLAY_6_NAME";
	public static final String PLAY_6_FILE = "PLAY_6_FILE";
	public static final String PLAY_7_REQUEST = "PLAY_7_REQUEST";
	public static final String PLAY_7_REPLY = "PLAY_7_REPLY";
	public static final String PLAY_7_NAME = "PLAY_7_NAME";
	public static final String PLAY_7_FILE = "PLAY_7_FILE";
	public static final String PLAY_8_REQUEST = "PLAY_8_REQUEST";
	public static final String PLAY_8_REPLY = "PLAY_8_REPLY";
	public static final String PLAY_8_NAME = "PLAY_8_NAME";
	public static final String PLAY_8_FILE = "PLAY_8_FILE";
	public static final String PLAY_9_REQUEST = "PLAY_9_REQUEST";
	public static final String PLAY_9_REPLY = "PLAY_9_REPLY";
	public static final String PLAY_9_NAME = "PLAY_9_NAME";
	public static final String PLAY_9_FILE = "PLAY_9_FILE";
	public static final String PLAY_10_REQUEST = "PLAY_10_REQUEST";
	public static final String PLAY_10_REPLY = "PLAY_10_REPLY";
	public static final String PLAY_10_NAME = "PLAY_10_NAME";
	public static final String PLAY_10_FILE = "PLAY_10_FILE";
	public static final String PAUSE_REQUEST = "PAUSE_REQUEST";
	public static final String PAUSE_REPLY = "PAUSE_REPLY";
	public static final String PLAY_GEN_REPLY = "PLAY_GEN_REPLY";
	public static final String MY_PHONE = "MY_PHONE";
	public static final String IDLE_CHECK_MS = "IDLE_CHECK_MS";
	public static final String IDLE_CHECK_START_HOUR = "IDLE_CHECK_START_HOUR";
	public static final String IDLE_CHECK_STOP_HOUR = "IDLE_CHECK_STOP_HOUR";
	public static final String PLAY_IDLE_NAME = "PLAY_IDLE_NAME";
	public static final String PLAY_IDLE_FILE = "PLAY_IDLE_FILE";
	
	private static final String PROP_FILENAME = "config.properties";
	private Properties prop = new Properties();

	// singleton
	private static AppConfig instance = new AppConfig();
	private AppConfig() {}
	public static AppConfig getInstance() {
		return instance;
	}

	public void load() throws IOException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROP_FILENAME);
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + PROP_FILENAME + "' not found in the classpath");
		}
	}
	
	public String getString(String key) {
		String val = prop.getProperty(key);
		if(val == null) {
			logger.error("key ["+key+"] not found");
			return "";
		}
		
		return val;
	}
	
	public int getInt(String key) {
		return Integer.valueOf(getString(key));
	}
	
	public long getLong(String key) {
		return Long.valueOf(getString(key));
	}
	
	// return true during "active hours"
	// return false during "off hours"
	public boolean timeCheck() {
		Calendar now = Calendar.getInstance();
		if (now.get(Calendar.HOUR_OF_DAY) < getInt(AppConfig.IDLE_CHECK_START_HOUR) ||
			now.get(Calendar.HOUR_OF_DAY) > getInt(AppConfig.IDLE_CHECK_STOP_HOUR))
			return true;
		
		return false;
	}
}
