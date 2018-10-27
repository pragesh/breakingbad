package com.breakingbad.app;
import java.util.HashMap;
import java.util.Map;

import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.ISVNInfoHandler;
import org.tmatesoft.svn.core.wc.SVNInfo;

public class InfoHandler implements ISVNInfoHandler {
    
	public Map<String,String> revMap = new HashMap<String,String>();
	public Map<String,String> directoryURLMap = new HashMap<String,String>();
	public Map<String,String> localRevisionDateMap = new HashMap<String,String>();
	String dirName = "";

	public InfoHandler(Map<String, String> revisionMap,
			Map<String, String> directoryURLMap,
			Map<String, String> localRevisionDateMap, String directoryName) {
	this.revMap = revisionMap;
		this.directoryURLMap = directoryURLMap;
		this.dirName = directoryName;
		this.localRevisionDateMap = localRevisionDateMap;
    }
	public void handleInfo(SVNInfo info) {
		System.out.println("-----------------INFO-----------------");
		System.out.println("Local Path: " + info.getFile().getPath());
		System.out.println("URL: " + info.getURL());
		revMap.put(info.getURL().toString(), info.getRevision().toString());
		directoryURLMap.put(dirName, info.getURL().toString());
		if (info.isRemote() && info.getRepositoryRootURL() != null) {
			System.out.println("Repository Root URL: "
					+ info.getRepositoryRootURL());
		}

		if (info.getRepositoryUUID() != null) {
			System.out.println("Repository UUID: " + info.getRepositoryUUID());
		}

		System.out.println("Revision: " + info.getRevision().getNumber());
		System.out.println("Node Kind: " + info.getKind().toString());

		if (!info.isRemote()) {
			System.out.println("Schedule: "
					+ (info.getSchedule() != null ? info.getSchedule()
							: "normal"));
		}

		System.out.println("Last Changed Author: " + info.getAuthor());
		System.out.println("Last Changed Revision: "
				+ info.getCommittedRevision().getNumber());
		System.out.println("Last Changed Date: " + info.getCommittedDate());
		localRevisionDateMap.put(info.getURL().toString(), info.getCommittedDate().toString());
		if (info.getPropTime() != null) {
			System.out
					.println("Properties Last Updated: " + info.getPropTime());
		}

		if (info.getKind() == SVNNodeKind.FILE && info.getChecksum() != null) {
			if (info.getTextTime() != null) {
				System.out.println("Text Last Updated: " + info.getTextTime());
			}
			System.out.println("Checksum: " + info.getChecksum());
		}

		if (info.getLock() != null) {
			if (info.getLock().getID() != null) {
				System.out.println("Lock Token: " + info.getLock().getID());
			}

			System.out.println("Lock Owner: " + info.getLock().getOwner());
			System.out.println("Lock Created: "
					+ info.getLock().getCreationDate());

			if (info.getLock().getExpirationDate() != null) {
				System.out.println("Lock Expires: "
						+ info.getLock().getExpirationDate());
			}

			if (info.getLock().getComment() != null) {
				System.out.println("Lock Comment: "
						+ info.getLock().getComment());
			}
		}
	}
}