package com.breakingbad.app;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;

/**
 * Class which manages client communication with SVN repository.
 * @author prageshjagnani
 *
 */
public class ClientManager {
	private static SVNClientManager ourClientManager;
    
	public static SVNClientManager getClientManager(String name,String password) {
		DAVRepositoryFactory.setup();
		ourClientManager = SVNClientManager.newInstance(null, name, password);
		return ourClientManager;
	}
}
