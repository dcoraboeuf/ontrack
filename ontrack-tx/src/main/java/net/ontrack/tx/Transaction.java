package net.ontrack.tx;

public interface Transaction extends AutoCloseable {

    void close();

    @Deprecated
    <T extends TransactionResource> T getResource(Class<T> resourceType);

    <T extends TransactionResource> T getResource(Class<T> resourceType, Object resourceId, TransactionResourceProvider<T> provider);

}
