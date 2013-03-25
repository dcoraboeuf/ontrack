package net.ontrack.tx;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DefaultTransactionService implements TransactionService {

    private final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final ThreadLocal<Stack<ITransaction>> transaction = new ThreadLocal<>();
    private final Collection<TransactionResourceProvider<?>> transactionResourceProviders;

    @Autowired
    public DefaultTransactionService(Collection<TransactionResourceProvider<?>> transactionResourceProviders) {
        this.transactionResourceProviders = transactionResourceProviders;
    }

    @Override
    public Transaction start() {
        return start(false);
    }

    @Override
    public Transaction start(boolean nested) {
        Stack<ITransaction> currents = transaction.get();
        if (currents == null || currents.isEmpty()) {
            // Creates a new transaction
            ITransaction current = createTransaction();
            // Registers it
            currents = new Stack<>();
            currents.push(current);
            transaction.set(currents);
            // OK
            return current;
        } else if (nested) {
            // Creates a new transaction
            ITransaction current = createTransaction();
            // Registers it
            currents.push(current);
            // OK
            return current;
        } else {
            // Reuses the same transaction
            currents.peek().reuse();
            return currents.peek();
        }
    }

    @Override
    public Transaction get() {
        Stack<ITransaction> stack = transaction.get();
        if (stack != null) {
            return stack.peek();
        } else {
            return null;
        }
    }

    protected ITransaction createTransaction() {
        logger.debug("Creating transaction");

        TransactionCallback txCallback = new TransactionCallback() {

            @SuppressWarnings("unchecked")
            @Override
            public <T extends TransactionResource> T createResource(final Class<T> resourceType) {
                TransactionResourceProvider<?> provider = Iterables.find(
                        transactionResourceProviders,
                        new Predicate<TransactionResourceProvider<?>>() {
                            @Override
                            public boolean apply(TransactionResourceProvider<?> candicate) {
                                return candicate.supports(resourceType);
                            }
                        },
                        null
                );
                if (provider == null) {
                    throw new IllegalArgumentException("Cannot create a transaction resource for type " + resourceType);
                } else {
                    return (T) provider.createTxResource();
                }
            }

            @Override
            public void remove(ITransaction tx) {
                Stack<ITransaction> stack = transaction.get();
                stack.pop();
                if (stack.isEmpty()) {
                    transaction.set(null);
                }
            }
        };

        // Creates the transaction
        return new TransactionImpl(txCallback);
    }

    private static interface ITransaction extends Transaction {

        void reuse();

    }

    private static interface TransactionCallback {

        <T extends TransactionResource> T createResource(Class<T> resourceType);

        void remove(ITransaction tx);

    }

    private static class TransactionImpl implements ITransaction {

        private final TransactionCallback transactionCallback;
        private final AtomicInteger count = new AtomicInteger(1);
        private final Map<Class<? extends TransactionResource>, TransactionResource> resources = new HashMap<>();

        public TransactionImpl(TransactionCallback transactionCallback) {
            this.transactionCallback = transactionCallback;
        }

        @Override
        public void end() {
            int value = count.decrementAndGet();
            if (value == 0) {
                // Removes the transaction
                transactionCallback.remove(this);
                // Disposal
                for (TransactionResource resource : resources.values()) {
                    resource.close();
                }
            }
        }

        @Override
        public <T extends TransactionResource> T getResource(Class<T> resourceType) {
            @SuppressWarnings("unchecked")
            T resource = (T) resources.get(resourceType);
            if (resource == null) {
                resource = transactionCallback.createResource(resourceType);
                resources.put(resourceType, resource);
            }
            return resource;
        }

        @Override
        public void reuse() {
            count.incrementAndGet();
        }

    }
}
