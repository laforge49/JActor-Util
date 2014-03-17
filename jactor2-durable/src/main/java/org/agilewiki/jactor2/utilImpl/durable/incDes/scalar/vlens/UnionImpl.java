package org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.vlens;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.*;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.util.durable.incDes.Union;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.ReadableBytes;
import org.agilewiki.jactor2.utilImpl.durable.incDes.IncDesImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.Scalar;

public class UnionImpl extends Scalar<String, JASerializable> implements Union {

    public static void registerFactory(final FactoryLocator _factoryLocator,
            final String _subActorType, final String... _actorTypes)
            throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new FactoryImpl(
                _subActorType) {

            @Override
            protected UnionImpl instantiateBlade() {
                return new UnionImpl();
            }

            @Override
            public UnionImpl newSerializable(final Reactor reactor,
                    final Ancestor parent) throws Exception {
                final UnionImpl uj = (UnionImpl) super.newSerializable(reactor,
                        parent);
                final Factory[] afs = new FactoryImpl[_actorTypes.length];
                int i = 0;
                while (i < _actorTypes.length) {
                    afs[i] = _factoryLocator.getFactory(_actorTypes[i]);
                    i += 1;
                }
                uj.unionFactories = afs;
                return uj;
            }
        });
    }

    protected Factory[] unionFactories;
    protected int factoryIndex = -1;
    protected JASerializable value;

    @Override
    public AsyncRequest<Void> clearReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                clear();
                processAsyncResponse(null);
            }
        };
    }

    @Override
    public AsyncRequest<JASerializable> getValueReq() {
        return new AsyncBladeRequest<JASerializable>() {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(getValue());
            }
        };
    }

    protected Factory[] getUnionFactories() {
        if (unionFactories != null) {
            return unionFactories;
        }
        throw new IllegalStateException("unionFactories is null");
    }

    protected int getFactoryIndex(final String actorType) throws Exception {
        final FactoryLocator factoryLocator = Durables
                .getFactoryLocator(getReactor());
        final Factory actorFactory = factoryLocator.getFactory(actorType);
        return getFactoryIndex(actorFactory);
    }

    protected int getFactoryIndex(final Factory actorFactory) {
        final String factoryKey = ((FactoryImpl) actorFactory).getFactoryKey();
        final Factory[] uf = getUnionFactories();
        int i = 0;
        while (i < uf.length) {
            if (((FactoryImpl) uf[i]).getFactoryKey().equals(factoryKey)) {
                return i;
            }
            i += 1;
        }
        throw new IllegalArgumentException("Not a valid union type: "
                + factoryKey);
    }

    /**
     * Load the serialized data into the JID.
     *
     * @param readableBytes Holds the serialized data.
     */
    @Override
    public void load(final ReadableBytes readableBytes) throws Exception {
        super.load(readableBytes);
        factoryIndex = readableBytes.readInt();
        if (factoryIndex == -1) {
            return;
        }
        final Factory factory = getUnionFactories()[factoryIndex];
        value = factory.newSerializable(getReactor(), getParent());
        ((IncDesImpl) value.getDurable()).load(readableBytes);
        ((IncDesImpl) value.getDurable()).setContainerJid(this);
    }

    /**
     * Returns the number of bytes needed to serialize the persistent data.
     *
     * @return The minimum size of the byte array needed to serialize the persistent data.
     */
    @Override
    public int getSerializedLength() throws Exception {
        if (factoryIndex == -1) {
            return JAInteger.LENGTH;
        }
        return JAInteger.LENGTH + value.getDurable().getSerializedLength();
    }

    /**
     * Clear the content.
     */
    @Override
    public void clear() throws Exception {
        setValue(-1);
    }

    @Override
    public void setValue(final String actorType) throws Exception {
        setValue(getFactoryIndex(actorType));
    }

    @Override
    public AsyncRequest<Void> setValueReq(final String actorType) {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                setValue(actorType);
                processAsyncResponse(null);
            }
        };
    }

    public void setValue(final FactoryImpl factoryImpl) throws Exception {
        setValue(getFactoryIndex(factoryImpl));
    }

    public void setValue(final Integer ndx) throws Exception {
        final int oldLength = getSerializedLength();
        if (value != null) {
            ((IncDesImpl) value.getDurable()).setContainerJid(null);
        }
        if (ndx == -1) {
            factoryIndex = -1;
            value = null;
        } else {
            final Factory factory = getUnionFactories()[ndx];
            factoryIndex = ndx;
            value = factory.newSerializable(getReactor(), getParent());
            ((IncDesImpl) value.getDurable()).setContainerJid(this);
        }
        change(getSerializedLength() - oldLength);
    }

    /**
     * Creates a JID actor and loads its serialized data.
     *
     * @param jidType A jid type name.
     * @param bytes   The serialized data.
     */
    @Override
    public void setValue(final String jidType, final byte[] bytes)
            throws Exception {
        setUnionBytes(getFactoryIndex(jidType), bytes);
    }

    @Override
    public AsyncRequest<Void> setValueReq(final String jidType,
            final byte[] bytes) {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                setValue(jidType, bytes);
                processAsyncResponse(null);
            }
        };
    }

    /**
     * Creates a JID actor and loads its serialized data.
     *
     * @param ndx   The factory index.
     * @param bytes The serialized data.
     */
    public void setUnionBytes(final Integer ndx, final byte[] bytes)
            throws Exception {
        final int oldLength = getSerializedLength();
        if (value != null) {
            ((IncDesImpl) value.getDurable()).setContainerJid(null);
        }
        final Factory factory = getUnionFactories()[ndx];
        factoryIndex = ndx;
        value = factory.newSerializable(getReactor(), getParent());
        ((IncDesImpl) value.getDurable()).setContainerJid(this);
        ((IncDesImpl) value.getDurable()).load(new ReadableBytes(bytes, 0));
        change(getSerializedLength() - oldLength);
    }

    /**
     * Assign a value unless one is already present.
     *
     * @param jidType The MakeValue request.
     * @return True if a new value is created.
     */
    @Override
    public Boolean makeValue(final String jidType) throws Exception {
        return makeUnionValue(getFactoryIndex(jidType));
    }

    @Override
    public AsyncRequest<Boolean> makeValueReq(final String jidType) {
        return new AsyncBladeRequest<Boolean>() {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(makeValue(jidType));
            }
        };
    }

    /**
     * Assign a value unless one is already present.
     *
     * @param ndx The Make request.
     * @return True if a new value is created.
     */
    public Boolean makeUnionValue(final Integer ndx) throws Exception {
        if (factoryIndex > -1) {
            return false;
        }
        setValue(ndx);
        return true;
    }

    /**
     * Creates a JID actor and loads its serialized data, unless a JID actor is already present.
     *
     * @param jidType A jid type name.
     * @param bytes   The serialized data.
     * @return True if a new value is created.
     */
    @Override
    public Boolean makeValue(final String jidType, final byte[] bytes)
            throws Exception {
        return makeUnionBytes(getFactoryIndex(jidType), bytes);
    }

    @Override
    public AsyncRequest<Boolean> makeValueReq(final String jidType,
            final byte[] bytes) {
        return new AsyncBladeRequest<Boolean>() {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse(makeValue(jidType, bytes));
            }
        };
    }

    public Boolean makeUnionBytes(final Integer ndx, final byte[] bytes)
            throws Exception {
        if (factoryIndex > -1) {
            return false;
        }
        setUnionBytes(ndx, bytes);
        return true;
    }

    @Override
    public JASerializable getValue() {
        return value;
    }

    /**
     * Serialize the persistent data.
     *
     * @param appendableBytes The wrapped byte array into which the persistent data is to be serialized.
     */
    @Override
    protected void serialize(final AppendableBytes appendableBytes)
            throws Exception {
        appendableBytes.writeInt(factoryIndex);
        if (factoryIndex == -1) {
            return;
        }
        ((IncDesImpl) value.getDurable()).save(appendableBytes);
    }

    /**
     * Resolves a JID pathname, returning a JID actor or null.
     *
     * @param pathname A JID pathname.
     * @return A JID actor or null.
     */
    @Override
    public JASerializable resolvePathname(final String pathname)
            throws Exception {
        if (pathname.length() == 0) {
            throw new IllegalArgumentException("empty string");
        }
        if (pathname.equals("0")) {
            return getValue();
        }
        if (pathname.startsWith("0/")) {
            final JASerializable v = getValue();
            if (v == null) {
                return null;
            }
            return v.getDurable().resolvePathname(pathname.substring(2));
        }
        throw new IllegalArgumentException("pathname " + pathname);
    }

    @Override
    public void initialize(final Reactor reactor, final Ancestor parent,
            final FactoryImpl factory) throws Exception {
        super.initialize(reactor, parent, factory);
    }
}
