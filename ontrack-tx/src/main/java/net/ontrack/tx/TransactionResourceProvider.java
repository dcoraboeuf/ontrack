package net.ontrack.tx;

public interface TransactionResourceProvider<T extends TransactionResource> {

    T createTxResource();

}
