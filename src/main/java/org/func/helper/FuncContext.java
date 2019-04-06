package org.func.helper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * It will save some global information that we want to use across application
 * 
 */
public enum FuncContext {

	INSTANCE;

	Map<String, Object> cache = new LinkedHashMap<String, Object>();

	/**
	 * Loads Entries from supplied properties
	 * 
	 * @param props
	 */
	public void loadFromProperties(Properties props) {
		Set<Entry<Object, Object>> entrySet = props.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			String propName = entry.getKey().toString();
			String propValue = entry.getValue().toString();
			cache.put(propName, propValue);
		}
	}
	
	public void setEntry(String key, Object value){
		cache.put(key, value);
	}
	
	public Object getEntry(String key) {
		return cache.get(key);
	}

	public String getEntryAsString(String key) {
		Object o = cache.get(key);
		if (o == null)
			return null;

		return o.toString();
	}
	
	/**
	 * Gets all the keys mapped in the cache
	 * 
	 * @return
	 */
	public Set<String> getEntryKeys () {
		return cache.keySet();
	}
}
