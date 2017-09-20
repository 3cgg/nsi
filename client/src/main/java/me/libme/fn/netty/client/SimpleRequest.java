package me.libme.fn.netty.client;

import io.netty.handler.codec.http.HttpMethod;
import me.libme.fn.netty.client.msg.IMessageBody;
import me.libme.fn.netty.client.msg.MsgHeader;

import java.util.Map;

public abstract class SimpleRequest {

	private String url;

	protected final HttpMethod httpMethod;

	private MsgHeader msgHeader=new MsgHeader();

	private IMessageBody messageBody;

	public SimpleRequest(HttpMethod httpMethod) {
		this.httpMethod=httpMethod;
	}


	public HttpMethod getHttpMethod() {
		return httpMethod;
	}


	public void setMessageBody(IMessageBody messageBody) {
		this.messageBody = messageBody;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public static PostRequest post(){
		PostRequest request=new PostRequest();
		return request;
	}
	
	public static GetRequest get(){
		GetRequest request=new GetRequest();
		return request;
	}
	
	public static class PostRequest extends SimpleRequest {

		public PostRequest() {
			super(HttpMethod.POST);
		}

	}
	
	public static class GetRequest extends SimpleRequest {
		public GetRequest() {
			super(HttpMethod.GET);
		}
	}

	public SimpleRequest  addHeader(String name,String value){
		msgHeader.addEntry(name,value);
		return this;
	}

	public Map<String, String> getHeaders(){
		return msgHeader.getHeaders();
	}

	public byte[] getContent(){
		return messageBody.body();
	}

	public String getHeader(String name){
		return msgHeader.get(name);
	}

}
