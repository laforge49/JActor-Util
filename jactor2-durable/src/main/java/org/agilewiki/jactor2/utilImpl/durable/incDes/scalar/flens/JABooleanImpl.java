package org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.flens;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.util.durable.incDes.JABoolean;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.ReadableBytes;

/**
 * A JID actor that holds a boolean.
 */
public class JABooleanImpl extends FLenScalar<Boolean> implements JABoolean {

    public static void registerFactory(final FactoryLocator _factoryLocator)
            throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new FactoryImpl(
                JABoolean.FACTORY_NAME) {
            @Override
            final protected JABooleanImpl instantiateBlade() {
                return new JABooleanImpl();
            }
        });
    }

    @Override
    public AsyncRequest<Boolean> getValueReq() {
        return new AsyncBladeRequest<Boolean>() {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(getValue());
            }
        };
    }

    /**
     * Create the value.
     *
     * @return The default value
     */
    @Override
    protected Boolean newValue() {
        return new Boolean(false);
    }

    /**
     * Returns the value held by this component.
     *
     * @return The value held by this component.
     */
    @Override
    public Boolean getValue() {
        if (value != null) {
            return value;
        }
        final ReadableBytes readableBytes = readable();
        value = readableBytes.readBoolean();
        return value;
    }

    @Override
    public AsyncRequest<Void> setValueReq(final Boolean v) {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                setValue(v);
                processAsyncResponse(null);
            }
        };
    }

    /**
     * Returns the number of bytes needed to serialize the persistent data.
     *
     * @return The minimum size of the byte array needed to serialize the persistent data.
     */
    @Override
    public int getSerializedLength() {
        return LENGTH;
    }

    /**
     * Serialize the persistent data.
     *
     * @param appendableBytes The wrapped byte array into which the persistent data is to be serialized.
     */
    @Override
    protected void serialize(final AppendableBytes appendableBytes) {
        appendableBytes.writeBoolean(value.booleanValue());
    }

    @Override
    public void initialize(final Reactor reactor, final Ancestor parent,
            final FactoryImpl factory) throws Exception {
        super.initialize(reactor, parent, factory);
    }
}
