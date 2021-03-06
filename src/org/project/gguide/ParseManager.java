package org.project.gguide;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;

public class ParseManager {
    Context mContext;
    private static final String me = "Jackie";
    private static final String you = "Ryan";
	
    private String senderID = me;
    private String receiverID = you;
	
    //sent message
    public ParseObject sMsg;
    //received message
    public ParseObject rMsg;
	
    public ParseManager(Context context) {
        mContext = context;
        Parse.initialize(context, "KHRJsZAovM2sss7SZdPF1aubcomrXMYL5NUgRDdz", "fvjBB3kjhxi84dPmK3g1rRZSIX8DKje3qmit7ulG");
        initMsg();
    }
	
    private void initMsg() {
        sMsg = new ParseObject(senderID);
        sMsg.put("name",me);
        sMsg.put("isGuide",false);
        //[food, hotel, park, music, fun]
        sMsg.put("specialty", Arrays.asList(0,0,0,0,0));
        sMsg.put("markers", (new ArrayList<Location>()));
        sMsg.put("isChat", true);
        sMsg.put("chatText", "");
        sMsg.saveInBackground();
		
        rMsg = new ParseObject(receiverID);
        rMsg.put("name",you);
        rMsg.put("isGuide",false);
        //[food, hotel, park, music, fun]
        rMsg.put("specialty", Arrays.asList(0,0,0,0,0));
        rMsg.put("markers", (new ArrayList<Location>()));
        rMsg.put("isChat", true);
        rMsg.put("chatText", "");
        rMsg.saveInBackground();
        Toast.makeText(mContext, "msg sent", Toast.LENGTH_SHORT).show();
		
        rMsg.fetchInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        // Success!
                    	Toast.makeText(mContext, "new message received", Toast.LENGTH_SHORT).show();
                    } else {
                        // Failure!
                    }
                }
            });
    }
	
	
    public ParseObject senderMsg() {
        return sMsg;
    }
	
    public ParseObject receiverMsg() {
        return rMsg;
    }
	
}
