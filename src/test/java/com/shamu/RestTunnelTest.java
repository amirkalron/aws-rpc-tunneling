package com.shamu;

import org.testng.annotations.Test;

import com.shamu.aws.tunnel.RestTunnel;
import com.shamu.aws.tunnel.RestTunnelCallHandler;
import com.shamu.aws.tunnel.RestTunnelReceiver;
import com.shamu.aws.tunnel.RestTunnelSender;


/**
 * Example for using the RestTunnel.
 * 
 * create a sending and receiving sides and send messages between them.
 * 
 * important - 
 * first,configure aws.properties with AWS credentials and proxy settings
 * @author kalron 
 *
 */
public class RestTunnelTest {
	
	@Test
	public void testTunnel() throws Exception {
		
    	//first,create the tunnel between the client and server,and initiate it
		RestTunnel tunnel = new RestTunnel("testTunnelExample"); 
		tunnel.init();
    	
    	//create receiver and handling call handler
		RestTunnelReceiver receiver = tunnel.getReceiver();
    	receiver.listenToRequests(new RestTunnelCallHandler() {			
			public Object doRequest(String method, String body) {
				return "this is the server response for " + method + ": " + body;
			}
		});
    	
    	//create sender
    	RestTunnelSender sender = tunnel.getSender();
    	sender.listenToResponses();    	
    	
    	//now we are ready to send and handle messages    	    	
    	int i=0;
    	while(i++<10){    		
	    	String body = Integer.toString(i);	    	
	    	String postResponse = sender.post("test", body,String.class);
	    	System.out.println(postResponse);
    	}
    	
	}
	 
}
