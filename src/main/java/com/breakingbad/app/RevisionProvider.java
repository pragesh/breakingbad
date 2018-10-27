package com.breakingbad.app;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNRevision;


public class RevisionProvider {
	private  Map<String, String> qaRevisionMap = new HashMap<String, String>();
	private  Map<String, String> qaRevisionDateMap = new HashMap<String, String>();
	private  Map<String, String> jenkinsRevisionMap = new HashMap<String, String>();
	private  Map<String, String> jenkinsRevisionDateMap = new HashMap<String, String>();
	private static Map<String,String> localRevisionMap = new HashMap<String,String>();
	private static Map<String,String> directoryURLMap = new HashMap<String,String>();
	private static Map<String,String> localRevisionDateMap = new HashMap<String,String>();
	private static List<String> localDirectories = new ArrayList<String>();
	private static Map<Integer, Health> revisionHealthMap = new HashMap<Integer,Health>();
    private String user = Constants.USERNAME;
    private String pass = Constants.PASSWORD;
	private String workspace;
	public JSONArray initiateProcess(String workspaceURL) throws IOException, SVNException, JSONException{
		processScrappingRequest(Constants.QA_URL_REVISIONS);
		workspace = workspaceURL;
		getLocalRevisionMap(workspaceURL);
		return createJSON();
	}
	
	public void processScrappingRequest(String uri) throws IOException {
		Document doc = Jsoup.connect(uri).timeout(0).ignoreContentType(true)
				.get();
		Element link = doc.select("a").first();
		String relHref = link.attr("href");
		processRequest(relHref, qaRevisionMap,qaRevisionDateMap);
	}

	public void processRequest(String uri,
			Map<String, String> revisionMap,Map<String,String> dateMap) throws IOException {

		URL url = new URL(uri);
		String line = null;
		StringBuffer tmp = new StringBuffer();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				url.openStream()));
		String key = null;
		String localDate = "";
		while ((line = in.readLine()) != null) {
			tmp.append(line);
			if (line.contains("http://")) {
				key = line.split("=")[1].replaceAll("http://", "https://");
				if (!revisionMap.containsKey(key)) {
					revisionMap.put(key, "");
				}
				if(!dateMap.containsKey(key)){
					dateMap.put(key, localDate);
				}
			} else if (line.contains("scm.version")) {
				String value = line.split("=")[1];
				if (revisionMap.containsKey(key)
						&& (revisionMap.get(key).isEmpty())) {
					revisionMap.put(key, value);
				}
			} else if (line.contains("scm.lastrevision.date")) {
				String value = line.split("=")[1];
				DateFormat utcFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
				utcFormat.setTimeZone(TimeZone.getTimeZone("PST"));

				Date date = new Date();
				try {
					date = utcFormat.parse(value);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				DateFormat localeFormat = new SimpleDateFormat("EEE' 'MMM' 'dd' 'HH:mm:ss' 'z' 'yyyy");
				localeFormat.setTimeZone(TimeZone.getDefault());
				localDate = localeFormat.format(date);
			}

		}
		Iterator<Map.Entry<String, String>> entries = revisionMap.entrySet()
				.iterator();
		while (entries.hasNext()) {
			Map.Entry<String, String> entry = entries.next();
			System.out.println("Key = " + entry.getKey() + ", Value = "
					+ entry.getValue());
		}
	}
	
	public void getLocalRevisionMap(String workspaceURL) throws SVNException{
		getDirectoryList(workspaceURL);
		for(String subDirectory : localDirectories) {
		 String subDirectoryPath = workspaceURL + "\\" + subDirectory;
		 showInfo(new File(subDirectoryPath),
		  SVNRevision.WORKING, false,subDirectory);
		}
	}
	
	public void getDirectoryList(String uri) throws SVNException{
    	RecursiveFileDisplay.display(uri,localDirectories);
    }
	

	private void showInfo(File wcPath, SVNRevision revision,
			boolean isRecursive,String directoryName) throws SVNException {
		InfoHandler info = new InfoHandler(localRevisionMap,directoryURLMap, localRevisionDateMap,directoryName);
		ClientManager.getClientManager(user,pass).getWCClient().doInfo(wcPath, revision, isRecursive,
				info);
		Iterator<Map.Entry<String, String>> entries = localRevisionMap.entrySet()
				.iterator();
		while (entries.hasNext()) {
			Map.Entry<String, String> entry = entries.next();
			System.out.println("Key = " + entry.getKey() + ", Value = "
					+ entry.getValue());
		}

	}

	private void getRevisionsHealth(JSONObject obj,String directory) throws JSONException {
		String url = directoryURLMap.get(directory);
		UserHistoryTracker tracker = new UserHistoryTracker();
		revisionHealthMap = tracker.getRevisionHealthMap(url);
		Health health = revisionHealthMap.get(Integer.parseInt(qaRevisionMap
				.get(url)));
		if (health != null) {
			obj.put("qarevisionpositivecount", health.getPositiveCount());
			obj.put("qarevisiontotalcount", health.getTotalCount());
			if(Double.isNaN(health.getPercentage())){
				obj.put("qarevisionpercentage","NA");
			} else {
				obj.put("qarevisionpercentage", health.getPercentage());
			}
			obj.put("qarevisioninprogress", health.getInProgressUsers());
			obj.put("qarevisionnegative", health.getNegativeUsers());
			obj.put("qarevisionpositive", health.getPositiveUsers());
		}
		Health jenkinsHealth = revisionHealthMap.get(Integer
				.parseInt(jenkinsRevisionMap.get(url)));
		if (jenkinsHealth != null) {
			obj.put("jrevisionpositivecount", jenkinsHealth.getPositiveCount());
			obj.put("jrevisiontotalcount", jenkinsHealth.getTotalCount());
			if(Double.isNaN(health.getPercentage())){
				obj.put("jrevisionpercentage","NA");
			} else {
				obj.put("jrevisionpercentage", health.getPercentage());
			}			
			obj.put("jrevisioninprogress", jenkinsHealth.getInProgressUsers());
			obj.put("jrevisionnegative", jenkinsHealth.getNegativeUsers());
			obj.put("jrevisionpositive", jenkinsHealth.getPositiveUsers());
		}
	}
	private JSONArray createJSON() throws JSONException{
		JSONArray jsonArray = new JSONArray();
		for(String directory : localDirectories){
			JSONObject obj = new JSONObject();
			String url = directoryURLMap.get(directory);
			obj.put("name", directory);
			obj.put("url", url);
			obj.put("qarevision",qaRevisionMap.get(url));
			obj.put("jrevision", jenkinsRevisionMap.get(url));
			obj.put("user", user);
			obj.put("workspace",workspace);
			obj.put("localrevision", localRevisionMap.get(url));
			obj.put("qarevisiondate", qaRevisionDateMap.get(url));
			obj.put("jrevisiondate", jenkinsRevisionDateMap.get(url));
			obj.put("localrevisiondate", localRevisionDateMap.get(url));
			getRevisionsHealth(obj, directory);
			jsonArray.put(obj);
		}
		return jsonArray;
	}
}
