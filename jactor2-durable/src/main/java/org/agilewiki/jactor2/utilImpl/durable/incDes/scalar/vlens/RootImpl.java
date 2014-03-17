package org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.vlens;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.util.durable.JASerializable;
import org.agilewiki.jactor2.util.durable.incDes.JAString;
import org.agilewiki.jactor2.util.durable.incDes.Root;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.ReadableBytes;
import org.agilewiki.jactor2.utilImpl.durable.incDes.IncDesImpl;

/**
 * <p>
 * The root IncDesImpl actor of a tree of IncDesImpl actors.
 * </p><p>
 * The serialized form of RootImpl does NOT contain its length.
 * The load method simply grabs all the remaining data.
 * </p>
 */
public class RootImpl extends BoxImpl implements Root {

    public static void registerFactory(final FactoryLocator _factoryLocator)
            throws FactoryLocatorClosedException {
        ((FactoryLocatorImpl) _factoryLocator).registerFactory(new FactoryImpl(
                Root.FACTORY_NAME) {
            @Override
            final protected RootImpl instantiateBlade() {
                return new RootImpl();
            }
        });
    }

    private JAString bundleLocation;

    @Override
    public String getBundleLocation() throws Exception {
        return bundleLocation.getValue();
    }

    @Override
    public void initialize(final Reactor reactor, final Ancestor parent,
            final FactoryImpl factory) throws Exception {
        super.initialize(reactor, parent, factory);
        final FactoryLocator factoryLocator = Durables
                .getFactoryLocator(getReactor());
        bundleLocation = (JAString) Durables.newSerializable(
                JAString.FACTORY_NAME, reactor);
        bundleLocation.setValue(((FactoryLocatorImpl) factoryLocator)
                .getLocation());
    }

    /**
     * Throws an UnsupportedOperationException,
     * as a RootImpl does NOT have a container.
     *
     * @param containerJid The container, or null.
     */
    @Override
    public void setContainerJid(final IncDesImpl containerJid) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the size of the serialized data (exclusive of its length header).
     *
     * @param readableBytes Holds the serialized data.
     * @return The size of the remaining bytes of serialized data.
     */
    @Override
    protected int loadLen(final ReadableBytes readableBytes) throws Exception {
        ((JAStringImpl) bundleLocation).load(readableBytes);
        final int l = readableBytes.remaining();
        if (l == 0) {
            return -1;
        }
        return l;
    }

    /**
     * There is no length, so there is nothing to skip over.
     *
     * @param readableBytes Holds the serialized data.
     */
    @Override
    protected void skipLen(final ReadableBytes readableBytes) throws Exception {
        readableBytes.skip(bundleLocation.getSerializedLength());
    }

    /**
     * The length is not saved.
     *
     * @param appendableBytes The object written to.
     */
    @Override
    protected void saveLen(final AppendableBytes appendableBytes)
            throws Exception {
        ((IncDesImpl) bundleLocation).save(appendableBytes);
    }

    /**
     * Returns the number of bytes needed to serialize the persistent data.
     *
     * @return The minimum size of the byte array needed to serialize the persistent data.
     */
    @Override
    public int getSerializedLength() throws Exception {
        if (len == -1) {
            return bundleLocation.getSerializedLength();
        }
        return bundleLocation.getSerializedLength() + len;
    }

    @Override
    public JASerializable copy(final Reactor m) throws Exception {
        Reactor mb = m;
        if (mb == null) {
            mb = getReactor();
        }
        final JASerializable jid = getFactory()
                .newSerializable(mb, getParent());
        ((IncDesImpl) jid.getDurable()).load(new ReadableBytes(
                getSerializedBytes(), 0));
        return jid;
    }
}
