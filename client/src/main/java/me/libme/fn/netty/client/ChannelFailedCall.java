package me.libme.fn.netty.client;

public interface ChannelFailedCall {

	ChannelFailedCall NOTHING=new ChannelFailedCall() {
		@Override
		public void run(SimpleRequest request, Throwable cause) {
		}
	};
	
	void run(SimpleRequest request, Throwable cause);
	
}
