package com.shamu.aws.tunnel;

public class RestTunnelMessage {
	
	private String reqUUid;
	private String method;
	private String body;
	private RestTunnelMessage response;
	
	public RestTunnelMessage(){};
	
	public RestTunnelMessage(String method, String body,String reqUUid) {
		super();
		this.method = method;
		this.body = body;
		this.reqUUid = reqUUid;
	}
	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getReqUUid() {
		return reqUUid;
	}
	public void setReqUUid(String reqUUid) {
		this.reqUUid = reqUUid;
	}
	public RestTunnelMessage getResponse() {
		return response;
	}
	public void setResponse(RestTunnelMessage response) {
		this.response = response;
	}	

}
