package com.shamu.aws.tunnel;

import com.shamu.aws.sqs.Queue;

/**
 * Enable to call RPC calls behind any firewall\Nat
 * 
 * how?
 * both sides,Sender and Receiver, are polling the messages from amazon SQS - 
 * Sender from responses "Qname_responses"
 * Receiver from requests "Qname_responses"
 * 
 * This is a better approach then creating an SSH tunnel since it works on a distributed environment,
 * where multiple server IP's and credentials are not needed * 
 * 
 * @author kalron
 */
public class RestTunnel {
	
	public static String AWS_PROP_FILE = "aws.properties";
	
    //sending side
	RestTunnelSender sender;
	
	//receiving side
	RestTunnelReceiver receiver;
	
	private String name;
    
	public RestTunnel(String name){
    	this.name = name;
	}
	
	/**
     * @return tunnel ID
     */
    public void init() throws Exception{
    	
    	//create or reuse existing queues
    	Queue queue_requests = Queue.createQueue(name + "_requests");
    	Queue queue_responses = Queue.createQueue(name + "_responses");
    	
    	sender = new RestTunnelSender(queue_requests, queue_responses);    	
    	receiver = new RestTunnelReceiver(queue_requests, queue_responses);
    }
    
    public RestTunnelSender getSender() {
		return sender;
	}

	public RestTunnelReceiver getReceiver() {
		return receiver;
	}
	

}
