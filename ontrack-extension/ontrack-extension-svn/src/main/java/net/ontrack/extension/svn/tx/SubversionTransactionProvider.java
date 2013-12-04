package net.ontrack.extension.svn.tx;

import net.ontrack.extension.svn.SubversionConfigurationExtension;
import net.ontrack.tx.TransactionResource;
import net.ontrack.tx.TransactionResourceProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNClientManager;

@Component
public class SubversionTransactionProvider implements TransactionResourceProvider<SVNSession> {

    private final SubversionConfigurationExtension configurationExtension;

    @Autowired
    public SubversionTransactionProvider(SubversionConfigurationExtension configurationExtension) {
        this.configurationExtension = configurationExtension;
    }

    @Override
    public SVNSession createTxResource() {
        // Creates the client manager for SVN
        SVNClientManager clientManager = SVNClientManager.newInstance();
        // Authentication (if needed)
        String svnUser = configurationExtension.getUser();
        String svnPassword = configurationExtension.getPassword();
        if (StringUtils.isNotBlank(svnUser) && StringUtils.isNotBlank(svnPassword)) {
            clientManager.setAuthenticationManager(new BasicAuthenticationManager(svnUser, svnPassword));
        }
        // OK
        return new DefaultSVNSession(clientManager);
    }

    @Override
    public boolean supports(Class<? extends TransactionResource> resourceType) {
        return SVNSession.class.isAssignableFrom(resourceType);
    }
}
