package me.libme.fn.netty.client;

public enum TransportStatus {
	
	SUCCESS("SUCCESS"),
	FAILED("FAILED"),
	UNCANCELLABLE("UNCANCELLABLE"),
	CANCELLED("CANCELLED"),
	RESPONSE("RESPONSE");
	
	private String name;
	
	private TransportStatus(String name) {
		this.name=name;
	}


}
