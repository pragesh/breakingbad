package com.breakingbad.app;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.tmatesoft.svn.core.SVNException;

public class Tester {

	public static void main(String[] args) throws IOException, SVNException,
			JSONException {
		RevisionProvider provider = new RevisionProvider();
		JSONArray jArray = provider.initiateProcess("C:\\hack");
		System.out.println(jArray);
		UpdateService updateSer = new UpdateService();
		JSONArray jArray2 = updateSer
				.update("C:\\hack",
						new String[] { "{Project_Name1}" },
						new String[] { "{REVISION_NUMBER1}" },
						new String[] { "{SVN_URL}" });
		System.out.println(jArray2);
		FeedBackTracker feedback = new FeedBackTracker();
		JSONArray jArray3 = feedback.saveFeedback(
				new String[] { "{Project_Name2}" },
				new String[] { "{REVISION_NUMBER2}" },
				new String[] { "{SVN_URL}" },
				"true", "HUHU");
		System.out.println(jArray3);
	}

}
