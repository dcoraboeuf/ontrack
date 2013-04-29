package net.ontrack.client;

public interface ControlCall<T> {

    T call(ControlUIClient client);

}
