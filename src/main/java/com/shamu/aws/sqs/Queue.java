package com.shamu.aws.sqs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;
import com.shamu.aws.tunnel.RestTunnel;

public class Queue {

	private AmazonSQSClient sqs;
	private String queueEndpoint;

	public Queue(String queueEndpoint, AmazonSQSClient sqs) {
		this.queueEndpoint = queueEndpoint;
		this.sqs = sqs;
	}

	public String getQueueEndpoint() {
		return queueEndpoint;
	}

	public static Queue createQueue(String name) throws Exception {

		Properties prop = new Properties();
		prop.load(Queue.class.getClassLoader().getResourceAsStream(
				RestTunnel.AWS_PROP_FILE));

		String accessKey = prop.getProperty("accessKey");
		String secretKey = prop.getProperty("secretKey");

		AWSCredentials cridentials = new BasicAWSCredentials(accessKey,
				secretKey);

		ClientConfiguration config = new ClientConfiguration();
		String proxyHost = prop.getProperty("http.proxyHost");
		String proxyPort = prop.getProperty("http.proxyPort");
		if (proxyHost != null && proxyPort != null) {
			config.setProxyHost(proxyHost);
			config.setProxyPort(Integer.parseInt(proxyPort));
		}

		AmazonSQSClient sqs = new AmazonSQSClient(cridentials, config);

		CreateQueueRequest createQueueRequest = new CreateQueueRequest(name);
		String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
		Queue q = new Queue(myQueueUrl, sqs);

		// I want a default polling time of 20 seconds by default
		Map<String, String> atrributes = new HashMap<String, String>();
		atrributes.put("ReceiveMessageWaitTimeSeconds", "20");
		q.setQueueAttributes(atrributes);
		return q;
	}

	public void setQueueAttributes(Map<String, String> attributes)
			throws Exception {
		sqs.setQueueAttributes(new SetQueueAttributesRequest(queueEndpoint,
				attributes));
	}

	public void sendMessage(String messageBody) throws Exception {
		sqs.sendMessage(new SendMessageRequest(queueEndpoint, messageBody));
	}

	public List<Message> receiveMessage(int numMessages, long pollTimeSeconds)
			throws Exception {
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(
				queueEndpoint);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest)
				.getMessages();
		return messages;
	}

	public void deleteMessage(String receiptHandle) throws Exception {
		sqs.deleteMessage(new DeleteMessageRequest(queueEndpoint, receiptHandle));
	}

}
