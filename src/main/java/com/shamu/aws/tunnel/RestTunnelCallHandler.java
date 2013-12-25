package com.shamu.aws.tunnel;

public interface RestTunnelCallHandler {

	/**
	 * performe the logic for the RPC call
	 * 
	 * @param method
	 * @param body
	 */
	public Object doRequest(String method, String body);

}
