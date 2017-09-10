package me.libme.fn.netty.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public abstract class JStringUtils {

	private static final Logger LOGGER= LoggerFactory.getLogger(JStringUtils.class);
	
	/**
	 * extract all bytes from the {@link InputStream}. 	
	 * @param input
	 * @return
	 * @throws IOException	
	 */
	public static byte[] getBytes(InputStream input) {
		return getBytes(input, false);
	}
	
	
	public static byte[] getBytes(InputStream input,boolean close) {
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    byte[] buffer = new byte[4096];
	    int n = 0;
	    try {
			while (-1 != (n = input.read(buffer))) {
			    output.write(buffer, 0, n);
			}
			output.flush();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}finally{
			if(close){
				if(input!=null){
					try {
						input.close();
					} catch (IOException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
		}
	    return output.toByteArray();
	}
	
	
	
	
	/**
	 * check whether the length of {@param string} exceeds the {@param size}.
	 * @param string
	 * @param size
	 * @return 
	 */
	public static boolean exceedLength(String string, int size) {
		if (string == null)
			return false;
		else
			return string.length() > size;
	}


	/**
	 * return true if the {@param before} is larger or equal than {@param after}
	 * <p>compare mechanism on {@link Comparable} 
	 * @param before
	 * @param after
	 * @return		
	 */
	public static boolean gtEqualString(Object before,Object after){

		if(before==null&&after==null) return true;

		if(before==null) return false;

		if(after==null) return true;

		return String.valueOf(before).compareTo(String.valueOf(after))>=0;
	}

	/**
	 * return true if the {@param before} is smaller or equal than {@param after}
	 * <p>compare mechanism on {@link Comparable} 
	 * @param before
	 * @param after
	 * @return		
	 */
	public static boolean ltEqualString(Object before,Object after){

		if(before==null&&after==null) return true;

		if(before==null) return false;

		if(after==null) return true;

		return String.valueOf(before).compareTo(String.valueOf(after))<=0;
	}
	
	/**
	 * check the {@param string} is null or empty . 
	 * @param string
	 * @return
	 */
	public static boolean isNullOrEmpty(String string){
		return string==null||"".equals(string.trim());
	}
	
	/**
	 * check the {@param string} is not null or empty . 
	 * @param string
	 * @return
	 */
	public static boolean isNotNullOrEmpty(String string){
		return string!=null&&!"".equals(string.trim());
	}
	
	/**
	 * convert {@param object} to string, empty returned if the object is null . 
	 * @param object
	 * @return
	 */
	public static String toString(Object object){
		if(object==null){
			return "";
		}
		else{
			return String.valueOf(object);
		}
	}
	
	
	/**
	 * Check that the given CharSequence is neither {@code null} nor of length 0.
	 * Note: Will return {@code true} for a CharSequence that purely consists of whitespace.
	 * <p><pre class="code">
	 * StringUtils.hasLength(null) = false
	 * StringUtils.hasLength("") = false
	 * StringUtils.hasLength(" ") = true
	 * StringUtils.hasLength("Hello") = true
	 * </pre>
	 * @param str the CharSequence to check (may be {@code null})
	 * @return {@code true} if the CharSequence is not null and has length
	 * @see #hasText(String)
	 */
	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * Check that the given String is neither {@code null} nor of length 0.
	 * Note: Will return {@code true} for a String that purely consists of whitespace.
	 * @param str the String to check (may be {@code null})
	 * @return {@code true} if the String is not null and has length
	 * @see #hasLength(CharSequence)
	 */
	public static boolean hasLength(String str) {
		return hasLength((CharSequence) str);
	}

	/**
	 * Check whether the given CharSequence has actual text.
	 * More specifically, returns {@code true} if the string not {@code null},
	 * its length is greater than 0, and it contains at least one non-whitespace character.
	 * <p><pre class="code">
	 * StringUtils.hasText(null) = false
	 * StringUtils.hasText("") = false
	 * StringUtils.hasText(" ") = false
	 * StringUtils.hasText("12345") = true
	 * StringUtils.hasText(" 12345 ") = true
	 * </pre>
	 * @param str the CharSequence to check (may be {@code null})
	 * @return {@code true} if the CharSequence is not {@code null},
	 * its length is greater than 0, and it does not contain whitespace only
	 * @see Character#isWhitespace
	 */
	public static boolean hasText(CharSequence str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given String has actual text.
	 * More specifically, returns {@code true} if the string not {@code null},
	 * its length is greater than 0, and it contains at least one non-whitespace character.
	 * @param str the String to check (may be {@code null})
	 * @return {@code true} if the String is not {@code null}, its length is
	 * greater than 0, and it does not contain whitespace only
	 * @see #hasText(CharSequence)
	 */
	public static boolean hasText(String str) {
		return hasText((CharSequence) str);
	}
	
	/**
	 * contact 
	 * @param strings
	 * @param delimit
	 * @return
	 */
	public static String toString(String[] strings,String delimit){
		String result="";
		for(String string:strings){
			result=result.concat(delimit).concat(string);
		}
		return result.replaceFirst(delimit, "");
	}
	
	public static String[] toStringArray(String strings,String delimit){
		return toStringList(strings, delimit).toArray(new String[0]);
	}
	
	public static List<String> toStringList(String strings,String delimit){
		List<String> list=new ArrayList<String>();
		String[] result=strings.split(delimit);
		for(String string:result){
			if(JStringUtils.isNotNullOrEmpty(string)){
				list.add(string);
			}
		}
		return list;
	}

	public static byte[] utf8(String string){
		return string.getBytes(Charset.forName("utf-8"));
	}

	public static String utf8(byte[] bytes){
		return new String(bytes,Charset.forName("utf-8"));
	}

}
