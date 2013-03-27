package net.ontrack.extension.svn.tx;

import net.ontrack.tx.TransactionResource;
import org.tmatesoft.svn.core.wc.SVNClientManager;

public interface SVNSession extends TransactionResource {

	SVNClientManager getClientManager();

}
