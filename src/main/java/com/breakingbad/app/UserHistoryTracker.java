package com.breakingbad.app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHistoryTracker {
	/**
	 * revision history read and write urls for feedback system.
	 */
	private String revisionHistoryReadURI = Constants.FTP_SERVER_URL_FOR_USER_HISTORY_READ;
	private String revisionHistoryWriteURI = Constants.FTP_SERVER_URL_FOR_USER_HISTORY_WRITE;

	private Map<String, List<UserHistoryBean>> userHistoryBeanMap = new HashMap<>();

	public UserHistoryTracker() {
		initUserHistoryBeanMap();
	}

	public void initUserHistoryBeanMap() {
		try {
			URL url = new URL(revisionHistoryReadURI);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String str = "";

			String currentKey = "";
			List<UserHistoryBean> currentUserHistoryBeans = new ArrayList<UserHistoryBean>();
			while ((str = br.readLine()) != null) {
				if (str.startsWith("key=")) {
					currentKey = str.split("=")[1];
				} else if (str.equals("end")) {
					userHistoryBeanMap.put(currentKey, currentUserHistoryBeans);
					currentKey = "";
					currentUserHistoryBeans = new ArrayList<>();
				} else {
					currentUserHistoryBeans.add(getUserHistoryBean(str));
				}
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public Map<String, List<UserHistoryBean>> getUserHistoryMap() {
		return userHistoryBeanMap;
	}

	public Map<Integer, Health> getRevisionHealthMap(String projectKey) {
		Map<Integer, Health> revisionHealthMap = new HashMap<>();

		List<UserHistoryBean> userHistoryBeans = userHistoryBeanMap
				.get(projectKey);
		if (userHistoryBeans != null) {
			for (UserHistoryBean bean : userHistoryBeans) {
				Health health = revisionHealthMap.get(bean.getRevisionNumber());
				if (health == null) {
					health = new Health();
					revisionHealthMap.put(bean.getRevisionNumber(), health);
				}
				String username = bean.getUsername()
						+ (!bean.getComments().isEmpty() ? " # "
								+ bean.getComments() + "" : "");
				if (!bean.isInProgress()) {
					health.updateCount(bean.isWorking(), username);
				} else {
					health.addInProgressUser(username);
				}
			}
		}
		return revisionHealthMap;
	}

	private UserHistoryBean getUserHistoryBean(String str) {
		String[] tokens = str.split(",");
		UserHistoryBean userHistoryBean = new UserHistoryBean();
		userHistoryBean.setUsername(tokens[0]);
		userHistoryBean.setRevisionNumber(Integer.parseInt(tokens[1]));
		if (!tokens[2].equals("NA")) {
			userHistoryBean.setWorking(Boolean.parseBoolean(tokens[2]));
			userHistoryBean.setInProgress(false);
		} else {
			userHistoryBean.setInProgress(true);
		}
		if (tokens.length >= 4) {
			userHistoryBean.setComments(tokens[3]);
		}
		return userHistoryBean;
	}

	public void updateUserHistoryTracker(String projectKey, String username,
			int revisionNumber, String isWorking, String comments) {
		initUserHistoryBeanMap();
		List<UserHistoryBean> userHistoryBeanList = userHistoryBeanMap
				.get(projectKey);
		if (userHistoryBeanList == null) {
			userHistoryBeanList = new ArrayList<>();
			userHistoryBeanMap.put(projectKey, userHistoryBeanList);
		}
		UserHistoryBean foundBean = null;
		for (UserHistoryBean bean : userHistoryBeanList) {
			if (bean.getUsername().equals(username)) {
				foundBean = bean;
				break;
			}
		}
		if (foundBean == null) {
			foundBean = new UserHistoryBean();
			foundBean.setUsername(username);
			userHistoryBeanList.add(foundBean);
		}
		foundBean.setInProgress(false);
		foundBean.setRevisionNumber(revisionNumber);
		if (!isWorking.equals("NA")) {
			foundBean.setWorking(Boolean.parseBoolean(isWorking));
			foundBean.setInProgress(false);
		} else {
			foundBean.setInProgress(true);
		}
		foundBean.setComments(comments);

		writeToServer();
	}

	private void writeToServer() {
		try {
			URL url = new URL(revisionHistoryWriteURI);
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty(
					"Content-Type",
					"multipart/form-data; boundary=" + "==="
							+ System.currentTimeMillis() + "===");

			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
					connection.getOutputStream()), true);
			for (String projectKey : userHistoryBeanMap.keySet()) {
				List<UserHistoryBean> beanList = userHistoryBeanMap
						.get(projectKey);
				writer.write("key=" + projectKey + "\n");
				for (UserHistoryBean bean : beanList) {
					String str = bean.getUsername() + ","
							+ bean.getRevisionNumber() + ","
							+ (bean.isInProgress() ? "NA" : bean.isWorking())
							+ "," + bean.getComments();
					writer.write(str + "\n");
				}
				writer.write("end" + "\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
