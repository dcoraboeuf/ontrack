package net.ontrack.tx;

public interface Transaction {

	void end();

	<T extends TransactionResource> T getResource(Class<T> resourceType);

}
