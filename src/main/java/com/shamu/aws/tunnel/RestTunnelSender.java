package com.shamu.aws.tunnel;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.shamu.aws.sqs.Queue;


public class RestTunnelSender extends RestTunnelClient {
	
    private static final int MAX_RESPONSE_TIME = 10000;
	
	//holds request for matching responses
    final Map<String,RestTunnelMessage> requests = new ConcurrentHashMap<String, RestTunnelMessage>();
	
	
    RestTunnelSender(Queue queue_requests, Queue queue_responses) {
		super(queue_requests, queue_responses);  
    }
    
	public <T> T post(String url,Object body,Class<T> returnType) throws Exception{
		return sendRequest(url,body,returnType);
	}
	
	public <T> T get(String url,Class<T> returnType) throws Exception{
		return sendRequest(url,null,returnType);
	}
	
	private <T> T sendRequest(String method,Object body,Class<T> returnType) throws Exception{
		//wrap the message in JSON format and send it
		String jsonBody = (body==null || "".equals(body))? "" : json.toJson(body);  
		RestTunnelMessage request = new RestTunnelMessage(method,jsonBody,UUID.randomUUID().toString());  
	    requests.put(request.getReqUUid(), request);
		 
		 synchronized (request) {
			 	writeMessage(queue_requests, json.toJson(request));
			 	
			 	//block till we receive response
	    		request.wait(MAX_RESPONSE_TIME);
		}
		//get here after we get response 
	    RestTunnelMessage response = request.getResponse();
	    if(response==null)
	    	throw new Exception("Did not receive response on time");
	    T retval = new JsonResponseExtractor<T>().extractData(response.getBody(), returnType);
	    return retval;
	 };
    
    
    
    public void listenToResponses() throws Exception{
    	//first,listen to response
    	listen(queue_responses, new QueueListener() {			
			public void onMessageReceived(String jsonMsg) {	
				try{
					RestTunnelMessage response = json.fromJson(jsonMsg, RestTunnelMessage.class);
					RestTunnelMessage request = requests.get(response.getReqUUid());	
					if(request!=null){
						request.setResponse(response);
						synchronized (request) {
							request.notifyAll();
						}
					}else
						System.out.println("No request found for " + response.getReqUUid());
				}
				catch(Exception exp){
					exp.printStackTrace();
				}
			}
		});    	
    }
    
    private class JsonResponseExtractor<T> {
    	T extractData(String response,Class<T> classOfT){
    		T ret = json.fromJson(response, classOfT);
    		return ret;
    	}
    }	

}
