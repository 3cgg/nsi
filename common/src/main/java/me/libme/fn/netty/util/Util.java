package me.libme.fn.netty.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

public class Util {

	public static String getMsg(Throwable throwable){
		ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream(1000);
		PrintStream printStream=new PrintStream(byteArrayOutputStream);
		throwable.printStackTrace(printStream);
		printStream.flush();
		return new String(byteArrayOutputStream.toByteArray(), Charset.forName("UTF-8"));
	}
	
	
}
