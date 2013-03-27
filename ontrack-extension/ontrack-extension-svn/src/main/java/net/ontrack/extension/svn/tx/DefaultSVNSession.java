package net.ontrack.extension.svn.tx;

import org.tmatesoft.svn.core.wc.SVNClientManager;

public class DefaultSVNSession implements SVNSession {

	private final SVNClientManager clientManager;

	public DefaultSVNSession(SVNClientManager clientManager) {
		this.clientManager = clientManager;
	}

	@Override
	public void close() {
		clientManager.dispose();
	}

	@Override
	public SVNClientManager getClientManager() {
		return clientManager;
	}

}
