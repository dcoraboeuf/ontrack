package net.ontrack.tx;

// FIXME Removes implementations
public interface TransactionResourceProvider<T extends TransactionResource> {

    T createTxResource();

    @Deprecated
    boolean supports(Class<? extends TransactionResource> resourceType);
}
