package org.project.gguide;

import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

public class SQSManager {

	private AmazonSQSClient sqsClient;
	public static final String me = "Jacky";
	public static final String other = "Ryan";
	private String myQueue = new String();
	private String otherQueue;
	private JSONObject sMsg;
	private Context mContext;
	
	public SQSManager (Context context) {
		BasicAWSCredentials creds = new BasicAWSCredentials("AKIAIRQFMA3ASX7ORJMA", "9M0ZkeuxisNg/LQ2KbT50jP5PbaLy9ONmAFZBXoe");
		sqsClient = new AmazonSQSClient(creds);
		Region region = Region.getRegion(Regions.US_WEST_2); 
		sqsClient.setRegion(region);
		
		try {
		//registration queues
			CreateQueueRequest cqrMe = new CreateQueueRequest(me);
			CreateQueueResult result = sqsClient.createQueue(cqrMe);
			myQueue = result.getQueueUrl();
		
			CreateQueueRequest cqrOther = new CreateQueueRequest(other);
			CreateQueueResult result2 = sqsClient.createQueue(cqrOther);
			otherQueue = result2.getQueueUrl();
		} catch (Exception exception) {
			System.out.println("Exception  = " + exception);
		}
		
		//Toast.makeText(mContext, "SQS "+myQueue, Toast.LENGTH_SHORT).show();
		mContext = context;
		try {
			sqsClient.sendMessage(new SendMessageRequest(otherQueue, "Hell"));
		} catch (Exception exception) {
			System.out.println("Exception  = " + exception);
		}
		Toast.makeText(mContext, "SQS "+myQueue, Toast.LENGTH_SHORT).show();
	}
	
	public ReceiveMessageResult receiveMsg () {
		return sqsClient.receiveMessage(new ReceiveMessageRequest(myQueue));
	}
	
	public void setMsg (JSONObject msg) {
		sMsg = msg;
	}
	
	public SendMessageResult sendMsg() {
		return sqsClient.sendMessage(new SendMessageRequest(otherQueue, sMsg.toString()));
	}
	
	/**
	 * checks to see if the sqs topic for this user exists. 
	 * creates a new topic if it does not exist
	 * @param sqsClient 
	 * @param userName 
	 * @return queue url
	 */
	private String initializeQueue(AmazonSQSClient sqsClient, final String userName) {
		try {
			CreateQueueRequest cqr = new CreateQueueRequest(userName);
			CreateQueueResult result = this.sqsClient.createQueue(cqr);
			Toast.makeText(mContext, "SQS created!", Toast.LENGTH_SHORT).show();
			return result.getQueueUrl();
		} catch (Exception exception) {
			//Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
			System.out.println("Exception = " + exception);
			return "";
		}
	}

}
