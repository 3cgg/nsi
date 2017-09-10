package me.libme.fn.netty.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.libme.fn.netty.util.JStringUtils;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Map;


/**
 * JJSON class contains a single self.  
 * All operation related JSON can be processed through the class. 
 * @author Administrator
 *
 */
public class JJSON {
	
	ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
	{
		//always default
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private static JJSON json=new JJSON();
	
	private JJSON(){}
	
	/**
	 * a default JSON returned,the JSON use default configuration in the commons-json.properties under the class path,
	 * the method in the level of the platform scope.
	 * @return
	 */
	public static final JJSON get(){
		return json;
	}
	
	/**
	 * use the passed configuration to construct a JSON utilization,the method is only temporally to fit with your requirement,
	 * must not be used in the platform scope. 
	 * @param config
	 * @return
	 */
	public static JJSON getJSON(JJSONConfig config){
		JJSON configJson=new JJSON();
		String dateFormat=config.getDateFormat();
		if(JStringUtils.isNotNullOrEmpty(dateFormat)){
			configJson.mapper.setDateFormat(new SimpleDateFormat(dateFormat));
		}
		return configJson;
	}
	
	/**
	 * Parse a string 
	 * @param string
	 * @return
	 */
	public Map<String, Object> parse(String string){
		try {
			return mapper.readValue(string, new TypeReference<Map<String, Object>>() {
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}  
	
	/**
	 * Parse a string to Object . 
	 * @param string
	 * @param t
	 * @return
	 */
	public <T> T parse(String string, Class<T> t){
		try {
			return mapper.readValue(string, t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}  
	
	/**
	 * Parse a string to Object . 
	 * @param string
	 * @param typeReference
	 * @return
	 */
	public <T> T parse(String string, TypeReference<T> typeReference){
		try {
			return mapper.readValue(string, typeReference);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}  

	/**
	 * format object to string
	 * <strong>Replace by {@link #formatObject(Object)} due to any potential implicit invoke.</strong>
	 * @param object
	 * @return
	 */
	public String format(Object object){
		try {
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			mapper.writeValue(out, object);
			return out.toString("UTF-8");
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}


	/**
	 * format object returned by the method {@link JJSONObject#serializableJSONObject()}
	 * @param jsonObject
	 * @return
	 */
	public String formatJSONObject(JJSONObject<?> jsonObject){
		try {
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			mapper.writeValue(out, jsonObject.serializableJSONObject());
			return out.toString("UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	/**
	 * format object to string
	 * @param object
	 * @return
	 */
	public String formatObject(Object object){
		return format(object);
	}
	

	
}	

