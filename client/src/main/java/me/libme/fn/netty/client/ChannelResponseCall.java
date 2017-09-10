package me.libme.fn.netty.client;

public interface ChannelResponseCall {

	ChannelResponseCall NOTHING=new ChannelResponseCall() {
		@Override
		public void run(SimpleRequest request, Object object) {
		}
	};
	
	void run(SimpleRequest request, Object object);
	
}
