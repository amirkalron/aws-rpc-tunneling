package com.shamu.aws.tunnel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.amazonaws.services.sqs.model.Message;
import com.google.gson.Gson;
import com.shamu.aws.sqs.Queue;

/**
 * base class for both tunnel Endpoints,Sender and Receiver
 * 
 * @author kalron
 */
public abstract class RestTunnelClient {

	ExecutorService executer = Executors.newFixedThreadPool(5);

	// SQS queue for requests
	Queue queue_requests;

	// SQS queue for responses
	Queue queue_responses;

	private boolean listenning;

	Gson json = new Gson();

	RestTunnelClient(Queue queue_requests, Queue queue_responses) {
		super();
		this.queue_requests = queue_requests;
		this.queue_responses = queue_responses;
	}

	protected void listen(final Queue queue, final QueueListener listener)
			throws Exception {
		listenning = true;
		// because SQS is a distributed system, we need to poll until we get the
		// message
		Thread reader = new Thread(queue.getQueueEndpoint() + " reader") {
			public void run() {
				try {
					while (listenning) {
						try {
							List<Message> msgs = queue.receiveMessage(1, 20);
							if (msgs.size() > 0) {
								Message message = msgs.get(0);
								String msg = message.getBody();
								deleteMessage(queue, message);
								notifyListener(msg, listener);
							} else {
								System.out.println("no messages received");
							}
						} catch (Exception exp) {
							exp.printStackTrace();
						}

					}
				} catch (Exception exp) {
					exp.printStackTrace();
				}
			}
		};
		reader.setDaemon(true);
		reader.start();
	}

	public void stopListenning() {
		listenning = false;
	}

	private void notifyListener(final String message,
			final QueueListener listener) {
		Runnable r = new Runnable() {
			public void run() {
				listener.onMessageReceived(message);
			}
		};
		executer.execute(r);
	}

	private void deleteMessage(final Queue queue, final Message msg) {
		Runnable r = new Runnable() {
			public void run() {
				try {
					queue.deleteMessage(msg.getReceiptHandle());
				} catch (Exception exp) {
					exp.printStackTrace();
				}
			}
		};
		executer.execute(r);
	}

	protected void writeMessage(final Queue queue, final String json)
			throws Exception {
		Runnable r = new Runnable() {
			public void run() {
				try {
					queue.sendMessage(json);
				} catch (Exception exp) {
					exp.printStackTrace();
				}
			}
		};
		executer.execute(r);
	};

	interface QueueListener {

		public void onMessageReceived(String message);

	}

}
