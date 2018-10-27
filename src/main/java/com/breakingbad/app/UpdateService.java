package com.breakingbad.app;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNRevision;

public class UpdateService {
	private static boolean isRecursive = true;
	private JSONArray jArray = new JSONArray();
    private String user = Constants.USERNAME;
    private String pass = Constants.PASSWORD;
	public JSONArray update(String baseURL, String[] directoryNames,
			String[] revisions, String[] urls)
					throws SVNException, JSONException {
		for (int i = 0; i < directoryNames.length; i++) {
			List<String> conflictList = new ArrayList<String>();
			File wcPath = new File(baseURL + "\\" + directoryNames[i]);
			if (revisions[i] != null) {
				ClientManager
				.getClientManager(user, pass)
				.getUpdateClient()
				.doUpdate(
						wcPath,
						SVNRevision.create(Long.parseLong(revisions[i])),
						SVNDepth.INFINITY, true, true);
				System.out.println("Successful");
				status(wcPath, SVNRevision.WORKING, user, pass, conflictList);
				JSONObject obj = new JSONObject();
				obj.put("name", directoryNames[i]);
				obj.put("workspace", baseURL);
				obj.put("revision", revisions[i]);
				obj.put("url", urls[i]);
				obj.put("username", user);
				obj.put("password", pass);

				if (conflictList != null && conflictList.size() > 0) {
					obj.put("conflictFiles", conflictList);
				}
				jArray.put(obj);
			}
			UserHistoryTracker tracker = new UserHistoryTracker();
			tracker.updateUserHistoryTracker(urls[i], user,
					Integer.parseInt(revisions[i]), "NA", "");
		}
		return jArray;
	}
	public JSONArray revert(String baseURL, String[] directoryNames,
			String[] revisions, String[] urls)
					throws SVNException, JSONException {
		for (int i = 0; i < directoryNames.length; i++) {
			List<String> conflictList = new ArrayList<String>();
			File wcPath = new File(baseURL + "\\" + directoryNames[i]);
			if (revisions[i] != null) {
				ClientManager
				.getClientManager(user, pass)
				.getUpdateClient()
				.doUpdate(
						wcPath,
						SVNRevision.create(Long.parseLong(revisions[i])),
						SVNDepth.INFINITY, true, true);
				System.out.println("Successful");
				status(wcPath, SVNRevision.WORKING, user, pass, conflictList);
				JSONObject obj = new JSONObject();
				obj.put("name", directoryNames[i]);
				obj.put("workspace", baseURL);
				obj.put("revision", revisions[i]);
				obj.put("url", urls[i]);
				obj.put("username", user);
				obj.put("password", pass);

				if (conflictList != null && conflictList.size() > 0) {
					obj.put("conflictFiles", conflictList);
				}
				jArray.put(obj);
			}
		}
		return jArray;
	}
	private void status(File wcPath, SVNRevision revision, String user,
			String pass, List<String> conflictList) throws SVNException {

		ClientManager
		.getClientManager(user, pass)
		.getStatusClient()
		.doStatus(wcPath, SVNRevision.WORKING, SVNDepth.INFINITY,
				false, false, false, false,
				new StatusHandler(true, conflictList), null);
		System.out.println("Successful");

	}

}
