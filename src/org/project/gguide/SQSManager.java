package org.project.gguide;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;

import android.os.AsyncTask;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQSManager {

	private AmazonSQSClient sqsClient;
	private final String[]  users = new String[] {"JackieJ", "RyanC"};
	private String queueURL;
	
	public SQSManager () {
		BasicAWSCredentials creds = new BasicAWSCredentials("AKIAI5JU3AHJTVGQODJQ", "Y7eKeWnxUlyjMhJGQRaJthEw2WNULu9W/xg+Vh0o");
		sqsClient = new AmazonSQSClient(creds);
		String myQueue = initializeQueue(sqsClient, users[0]);
		String otherQueue = initializeQueue(sqsClient, users[1]);
		
		//sending and receiving messages
		runAsync();
		
		//do in an infinite loop in another thread
		sqsClient.receiveMessage(new ReceiveMessageRequest(myQueue));
		
		sqsClient.sendMessage(new SendMessageRequest(otherQueue, "helloWorld"));
		
	}

	private void runAsync() {
		new AsyncTask <Void, Integer ,String>() {

			@Override
			protected String doInBackground(Void... params) {
				return queueURL;
				// TODO Auto-generated method stub
				
			}
			
		}.execute(null,null,null);
	}
	
	/**
	 * checks to see if the sqs topic for this user exists. 
	 * creates a new topic if it does not exist
	 * @param sqsClient 
	 * @param userName 
	 * @return queue url
	 */
	private String initializeQueue(AmazonSQSClient sqsClient, String userName) {
		ListQueuesResult queues = sqsClient.listQueues(new ListQueuesRequest(userName));
		List<String> queueURLs = queues.getQueueUrls();
		for (String queueUrl : queueURLs) {
			System.err.println(queueUrl);
			if (queueUrl.endsWith(userName)) {
				return queueUrl;
			}
		}
		return sqsClient.createQueue(new CreateQueueRequest(userName)).getQueueUrl();

	}


}
