package com.breakingbad.app;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
This class implements the feedback tracking system. 
This is used to save feedback from different users about whether a particular 
revision builds successfully or not.
**/
public class FeedBackTracker {
    private String user = Constants.USERNAME;
    private String pass = Constants.PASSWORD;
	public JSONArray saveFeedback(String[] directoryNames, String[] revisions,
			String[] urls, String success, String comments)
					throws JSONException {
		for (int i = 0; i < directoryNames.length; i++) {
			UserHistoryTracker tracker = new UserHistoryTracker();
			tracker.updateUserHistoryTracker(urls[i], user,
					Integer.parseInt(revisions[i]), success, comments);
		}
		JSONObject obj = new JSONObject();
		obj.put("feedback", "successful");
		obj.put("user", user);
		JSONArray arr = new JSONArray();
		arr.put(obj);
		return arr;
	}

}
