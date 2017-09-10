package me.libme.fn.netty.client;

public interface ChannelCancelledCall {

	ChannelCancelledCall NOTHING=new ChannelCancelledCall() {
		@Override
		public void run(SimpleRequest request, Object cause) {
		}
	};
	
	void run(SimpleRequest request, Object cause);
	
}
