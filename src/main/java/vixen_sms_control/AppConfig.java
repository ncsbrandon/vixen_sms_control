package vixen_sms_control;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

	public static final String CONFIG_ACCOUNT_SID = "ACCOUNT_SID";
	public static final String CONFIG_AUTH_TOKEN = "AUTH_TOKEN";
	public static final String CONFIG_FROM_PHONE = "FROM_PHONE";
	
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
		return prop.getProperty(key);
	}
	
	public int getInt(String key) {
		return Integer.valueOf(getString(key));
	}
}
