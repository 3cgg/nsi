package me.libme.fn.netty.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.handler.codec.http.*;
import me.libme.fn.netty.msg.HeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class ChannelRunnable {

	private final Logger LOGGER= LoggerFactory.getLogger(this.getClass());
	
	private final SimpleRequest request;
	
	private final ChannelResponseCall responseCall;
	
	private ChannelFailedCall failedCall=ChannelFailedCall.NOTHING;
	
	private ChannelCancelledCall cancelledCall=ChannelCancelledCall.NOTHING;
	
	
	public ChannelRunnable(SimpleRequest request, ChannelResponseCall responseCall) {
		this.request = request;
		this.responseCall=responseCall;
	}
	
	public final void cancelled(Object cause){
		try{
			cancelledCall.run(request, cause);
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public final void fail(Throwable cause){
		try{
			failedCall.run(request, cause);
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public final void response(Object object){
		try{
			responseCall.run(request, object);
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public final ChannelFuture request(Channel channel){
		try{
			DefaultFullHttpRequest fullHttpRequest=prepare();
			ChannelFuture channelFuture= doRequest(channel,fullHttpRequest);
			SimpleChannelPool channelPool = channel.attr(NioChannelExecutor.NIO_CHANNEL_EXECUTOR_ATTRIBUTE_KEY).get().getChannelPool();
			channelPool.release(channel);
			LOGGER.info("completely send message ["+request.getHeader(HeaderNames.SEQUENCE_IDENTITY)+" ; " +
					request.getUrl()+" ], release channel to pool : " +channel);
			return channelFuture;
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	abstract protected ChannelFuture doRequest(Channel channel,DefaultFullHttpRequest fullHttpRequest)throws Exception;
	
	/**
	 * 
	 * @return
	 */
	protected DefaultFullHttpRequest prepare(){
		checkHeader();
		URI uri=uri();
		// Prepare the HTTP request.
    	DefaultFullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, httpMethod(), uri.getRawPath(),
                Unpooled.wrappedBuffer(content()));
    	HttpHeaders httpHeaders=fullHttpRequest.headers();


    	Map<String, String> headers=request.getHeaders();
		for(Entry<String, String> entry:headers.entrySet()){
			httpHeaders.set(entry.getKey(),entry.getValue());
		}

		httpHeaders.set(HttpHeaderNames.HOST, uri.getHost());
		httpHeaders.set(HttpHeaderNames.CONTENT_LENGTH, content().length);

    	return fullHttpRequest;
	}
	
	private static List<String> usedHeaders=new ArrayList<String>(){
		
		private String _key="";
		
		{
			add(HttpHeaderNames.HOST.toString());
//			add(HttpHeaderNames.CONNECTION.toString());
//			add(HttpHeaderNames.ACCEPT_ENCODING.toString());
			add(HttpHeaderNames.CONTENT_LENGTH.toString());
			_key=_key.substring(1);
		}

		public boolean add(String e) {
			boolean flag=super.add(e);
			if(flag){
				_key=_key+","+e;
			}
			return flag;
		};
		
		
		@Override
		public String toString() {
			return "["+_key+"]";
		}
		
	};
	
	
	private void checkHeader(){
		Map<String, String> headers=request.getHeaders();
		for(String key:headers.keySet()){
			if(usedHeaders.contains(key)){
				throw new RuntimeException("invalid header name : ["+key+"], custome header mustnot be in "
			+usedHeaders.toString());
			}
		}
	}
	
	protected HttpMethod httpMethod(){
		return request.httpMethod;
	}
	
	protected URI uri(){
		return URI.create(request.getUrl());
	}

	protected byte[] content(){
		return request.getContent();
	}

	public ChannelRunnable cancelledCall(ChannelCancelledCall cancelledCall) {
		this.cancelledCall = cancelledCall;
		return this;
	}
	
	public ChannelRunnable failedCall(ChannelFailedCall failedCall) {
		this.failedCall = failedCall;
		return this;
	}

	void addHeader(String name,String value){
		request.addHeader(name,value);
	}

}
