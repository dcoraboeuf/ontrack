package net.ontrack.tx;

public interface TransactionResourceProvider<T extends TransactionResource> {

    T createTxResource();

    boolean supports(Class<? extends TransactionResource> resourceType);
}
