package com.shamu.aws.tunnel;

import com.shamu.aws.sqs.Queue;

public class RestTunnelReceiver extends RestTunnelClient {

	RestTunnelReceiver(Queue queue_requests, Queue queue_responses) {
		super(queue_requests, queue_responses);
	}

	public void listenToRequests(final RestTunnelCallHandler handler)
			throws Exception {
		listen(queue_requests, new QueueListener() {
			public void onMessageReceived(String jsonMsg) {
				try {
					RestTunnelMessage request = json.fromJson(jsonMsg,
							RestTunnelMessage.class);
					String methodName = request.getMethod();
					Object response = handler.doRequest(methodName,
							request.getBody());
					sendResponse(request, response);
				} catch (Exception exp) {
					exp.printStackTrace();
				}
			}
		});
	}

	private void sendResponse(RestTunnelMessage request, Object response)
			throws Exception {
		RestTunnelMessage jsonResponse = new RestTunnelMessage(
				request.getMethod(), json.toJson(response),
				request.getReqUUid());
		queue_responses.sendMessage(json.toJson(jsonResponse));
	}

}
