package net.ontrack.client.support;

import net.ontrack.client.AdminUIClient;
import net.ontrack.client.ControlUIClient;
import net.ontrack.client.ManageUIClient;
import net.ontrack.client.PropertyUIClient;

public class ClientSupport {

    private final ControlUIClient controlClient;
    private final ManageUIClient manageClient;
    private final PropertyUIClient propertyClient;
    private final AdminUIClient adminClient;

    public ClientSupport(String url) {
        ClientFactory clientFactory = ClientFactory.create(url);
        controlClient = clientFactory.control();
        manageClient = clientFactory.manage();
        propertyClient = clientFactory.property();
        adminClient = clientFactory.admin();
    }

    public ControlUIClient getControlClient() {
        return controlClient;
    }

    public ManageUIClient getManageClient() {
        return manageClient;
    }

    public PropertyUIClient getPropertyClient() {
        return propertyClient;
    }

    public AdminUIClient getAdminClient() {
        return adminClient;
    }

    public <T> T anonymous(ManageClientCall<T> call) {
        try {
            return call.onCall(manageClient);
        } catch (ClientException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T asUser(String user, String password, ManageClientCall<T> call) {
        manageClient.login(user, password);
        try {
            try {
                return call.onCall(manageClient);
            } catch (ClientException ex) {
                throw ex;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } finally {
            manageClient.logout();
        }
    }

    public <T> T asUser(String user, String password, ControlClientCall<T> call) {
        controlClient.login(user, password);
        try {
            try {
                return call.onCall(controlClient);
            } catch (ClientException ex) {
                throw ex;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } finally {
            controlClient.logout();
        }
    }

    public <T> T asUser(String user, String password, PropertyClientCall<T> call) {
        propertyClient.login(user, password);
        try {
            try {
                return call.onCall(propertyClient);
            } catch (ClientException ex) {
                throw ex;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } finally {
            propertyClient.logout();
        }
    }

    public <T> T asUser(String user, String password, AdminClientCall<T> call) {
        adminClient.login(user, password);
        try {
            try {
                return call.onCall(adminClient);
            } catch (ClientException ex) {
                throw ex;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } finally {
            adminClient.logout();
        }
    }
}
