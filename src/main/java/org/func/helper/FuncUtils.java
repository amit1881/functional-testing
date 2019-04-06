package org.func.helper;

import java.io.InputStream;
import java.util.Properties;

public class FuncUtils {

	public static Properties loadProperties(Class<?> classType, Properties testProperties, String testConfigFileName) {

		try {
			InputStream stream = classType.getClassLoader().getResourceAsStream(testConfigFileName);
			testProperties.load(stream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return testProperties;
	}

	public static Properties loadProperties(Object obj, Properties prop, String configs) {
		return loadProperties(obj.getClass(), prop, configs);
	}

	public static String getTestProperty(Properties properties, String key) throws Throwable {
        try {
            return properties.getProperty(key);
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }
	
}
