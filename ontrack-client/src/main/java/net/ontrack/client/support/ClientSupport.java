package net.ontrack.client.support;

import net.ontrack.client.ControlUIClient;
import net.ontrack.client.ManageUIClient;
import net.ontrack.client.PropertyUIClient;

public class ClientSupport {

    private final ControlUIClient controlClient;
    private final ManageUIClient manageClient;
    private final PropertyUIClient propertyClient;

    public ClientSupport(String url) {
        ClientFactory clientFactory = ClientFactory.create(url);
        controlClient = clientFactory.control();
        manageClient = clientFactory.manage();
        propertyClient = clientFactory.property();
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

    public <T> T anonymous(ManageClientCall<T> call) {
        try {
            return call.onCall(manageClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T asUser(String user, String password, ManageClientCall<T> call) {
        manageClient.login(user, password);
        try {
            try {
                return call.onCall(manageClient);
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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } finally {
            propertyClient.logout();
        }
    }
}
