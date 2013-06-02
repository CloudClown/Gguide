package org.project.gguide;

import java.util.List;
import java.util.Random;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;

public class SNSManager {
	
	private final AmazonSNSClient client;
	//TODO add real username here
	private final String userName = "username2";
	
	private SNSManager () {
		Random rand = new Random();
		String randStr = rand.nextInt()+"";
		BasicAWSCredentials creds = new BasicAWSCredentials("AKIAI5JU3AHJTVGQODJQ", "Y7eKeWnxUlyjMhJGQRaJthEw2WNULu9W/xg+Vh0o");
		client = new AmazonSNSClient(creds);
		
		client.createTopic(new CreateTopicRequest(randStr));
		AmazonSQSClient sqsClient = new AmazonSQSClient(creds);
		String queueARN = initializeQueue(sqsClient);
		
		client.subscribe(new SubscribeRequest(randStr, "sqs", queueARN));
	}

	/**
	 * checks to see if the sqs topic for this user exists. 
	 * creates a new topic if it does not exist
	 * @param sqsClient 
	 * @return queue arn
	 */
	private String initializeQueue(AmazonSQSClient sqsClient) {
		String realUrl = null;
		ListQueuesResult queues = sqsClient.listQueues(new ListQueuesRequest(userName));
		List<String> queueURLs = queues.getQueueUrls();
		for (String queueUrl : queueURLs) {
			System.err.println(queueUrl);
			if (queueUrl.endsWith(userName)) {
				realUrl = queueUrl;
				break;
			}
		}
		if (realUrl == null) {
			realUrl = sqsClient.createQueue(new CreateQueueRequest(userName)).getQueueUrl();
		}
		
		
		return sqsClient.getQueueAttributes(new GetQueueAttributesRequest(realUrl)).getAttributes().get("QueueArn");
	}
	
	private void publish(String topic, String message) {
		PublishRequest pr = new PublishRequest( topic, message );
		this.client.publish( pr );
	}
	
	private void subscribeToQueue(String queue_name, String topic) {
		SubscribeRequest request = new SubscribeRequest();
		request.withEndpoint( queue_name ).withProtocol( "sqs" ).withTopicArn( topic );
				
		this.client.subscribe( request );
	}
	
	
}
