package org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.flens;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.util.durable.incDes.JADouble;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.ReadableBytes;

/**
 * A JID actor that holds a double.
 */
public class JADoubleImpl extends FLenScalar<Double> implements JADouble {

    public static void registerFactory(final FactoryLocator _factoryLocator)
            throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new FactoryImpl(
                JADouble.FACTORY_NAME) {
            @Override
            final protected JADoubleImpl instantiateBlade() {
                return new JADoubleImpl();
            }
        });
    }

    @Override
    public AsyncRequest<Double> getValueReq() {
        return new AsyncBladeRequest<Double>() {
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
    protected Double newValue() {
        return new Double(0.D);
    }

    /**
     * Returns the value held by this component.
     *
     * @return The value held by this component.
     */
    @Override
    public Double getValue() {
        if (value != null) {
            return value;
        }
        final ReadableBytes readableBytes = readable();
        value = readableBytes.readDouble();
        return value;
    }

    @Override
    public AsyncRequest<Void> setValueReq(final Double v) {
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
        appendableBytes.writeDouble(value);
    }

    @Override
    public void initialize(final Reactor reactor, final Ancestor parent,
            final FactoryImpl factory) throws Exception {
        super.initialize(reactor, parent, factory);
    }
}
