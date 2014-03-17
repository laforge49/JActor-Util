package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.slist;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.JASerializable;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.util.durable.incDes.JAList;
import org.agilewiki.jactor2.utilImpl.durable.AppendableBytes;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.ReadableBytes;
import org.agilewiki.jactor2.utilImpl.durable.incDes.IncDesImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.CollectionImpl;

import java.util.ArrayList;

/**
 * Holds an ArrayList of JID actors, all of the same type.
 */
public class SList<ENTRY_TYPE extends JASerializable> extends
        CollectionImpl<ENTRY_TYPE> implements JAList<ENTRY_TYPE> {

    public int initialCapacity = 10;

    /**
     * IncDesImpl factory of the elements.
     */
    protected Factory entryFactory;

    /**
     * A list of JID actors.
     */
    protected ArrayList<ENTRY_TYPE> list;

    @Override
    public AsyncRequest<Void> emptyReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                empty();
                processAsyncResponse(null);
            }
        };
    }

    /**
     * Returns the size of the collection.
     *
     * @return The size of the collection.
     */
    @Override
    public int size() throws Exception {
        initializeList();
        return list.size();
    }

    /**
     * Returns the ith JID component.
     *
     * @param i The index of the element of interest.
     *          If negative, the index used is increased by the size of the collection,
     *          so that -1 returns the last element.
     * @return The ith JID component, or null if the index is out of range.
     */
    @Override
    public ENTRY_TYPE iGet(int i) throws Exception {
        initializeList();
        if (i < 0) {
            i += list.size();
        }
        if ((i < 0) || (i >= list.size())) {
            return null;
        }
        return list.get(i);
    }

    /**
     * Returns the number of bytes needed to serialize the persistent data.
     *
     * @return The minimum size of the byte array needed to serialize the persistent data.
     */
    @Override
    public int getSerializedLength() {
        return (JAInteger.LENGTH * 2) + len;
    }

    /**
     * Load the serialized data into the JID.
     *
     * @param readableBytes Holds the serialized data.
     */
    @Override
    public void load(final ReadableBytes readableBytes) throws Exception {
        super.load(readableBytes);
        len = loadLen(readableBytes);
        list = null;
        readableBytes.skip(JAInteger.LENGTH + len);
    }

    /**
     * Returns the IncDesFactory for all the elements in the list.
     *
     * @return The IncDesFactory for of all the elements in the list.
     */
    protected Factory getEntryFactory() throws Exception {
        if (entryFactory == null) {
            throw new IllegalStateException("entryFactory uninitialized");
        }
        return entryFactory;
    }

    /**
     * Perform lazy initialization.
     */
    protected void initializeList() throws Exception {
        if (list != null) {
            return;
        }
        entryFactory = getEntryFactory();
        if (!isSerialized()) {
            list = new ArrayList<ENTRY_TYPE>();
            return;
        }
        final ReadableBytes readableBytes = readable();
        skipLen(readableBytes);
        final int count = readableBytes.readInt();
        list = new ArrayList<ENTRY_TYPE>(count);
        int i = 0;
        while (i < count) {
            final ENTRY_TYPE elementJid = (ENTRY_TYPE) createSubordinate(
                    entryFactory, this, readableBytes);
            list.add(elementJid);
            i += 1;
        }
    }

    /**
     * Serialize the persistent data.
     *
     * @param appendableBytes The wrapped byte array into which the persistent data is to be serialized.
     */
    @Override
    protected void serialize(final AppendableBytes appendableBytes)
            throws Exception {
        saveLen(appendableBytes);
        appendableBytes.writeInt(size());
        int i = 0;
        while (i < size()) {
            ((IncDesImpl) iGet(i).getDurable()).save(appendableBytes);
            i += 1;
        }
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
        initializeList();
        return super.resolvePathname(pathname);
    }

    /**
     * Creates a JID actor and loads its serialized data.
     *
     * @param i     The index of the desired element.
     * @param bytes Holds the serialized data.
     */
    @Override
    public void iSet(int i, final byte[] bytes) throws Exception {
        initializeList();
        if (i < 0) {
            i += list.size();
        }
        if ((i < 0) || (i >= list.size())) {
            throw new IllegalArgumentException();
        }
        final JASerializable elementJid = createSubordinate(entryFactory, this,
                bytes);
        final JASerializable oldElementJid = iGet(i);
        ((IncDesImpl) oldElementJid.getDurable()).setContainerJid(null);
        list.set(i, (ENTRY_TYPE) elementJid);
        change(elementJid.getDurable().getSerializedLength()
                - oldElementJid.getDurable().getSerializedLength());
    }

    @Override
    public AsyncRequest<Void> iAddReq(final int _i, final byte[] _bytes) {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                iAdd(_i, _bytes);
                processAsyncResponse(null);
            }
        };
    }

    @Override
    public void iAdd(int i, final byte[] bytes) throws Exception {
        initializeList();
        if (i < 0) {
            i = size() + 1 + i;
        }
        final JASerializable jid = createSubordinate(entryFactory, this, bytes);
        final int c = jid.getDurable().getSerializedLength();
        list.add(i, (ENTRY_TYPE) jid);
        change(c);
    }

    @Override
    public AsyncRequest<Void> iAddReq(final int _i) {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                iAdd(_i);
                processAsyncResponse(null);
            }
        };
    }

    @Override
    public void iAdd(int i) throws Exception {
        initializeList();
        if (i < 0) {
            i = size() + 1 + i;
        }
        final JASerializable jid = createSubordinate(entryFactory, this);
        final int c = jid.getDurable().getSerializedLength();
        list.add(i, (ENTRY_TYPE) jid);
        change(c);
    }

    @Override
    public void empty() throws Exception {
        int c = 0;
        int i = 0;
        final int s = size();
        while (i < s) {
            final JASerializable jid = iGet(i);
            ((IncDesImpl) jid.getDurable()).setContainerJid(null);
            c -= jid.getDurable().getSerializedLength();
            i += 1;
        }
        list.clear();
        change(c);
    }

    @Override
    public AsyncRequest<Void> iRemoveReq(final int _i) {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                iRemove(_i);
                processAsyncResponse(null);
            }
        };
    }

    @Override
    public void iRemove(int i) throws Exception {
        final int s = size();
        if (i < 0) {
            i += s;
        }
        if ((i < 0) || (i >= s)) {
            throw new IllegalArgumentException();
        }
        final JASerializable jid = iGet(i);
        ((IncDesImpl) jid.getDurable()).setContainerJid(null);
        final int c = -jid.getDurable().getSerializedLength();
        list.remove(i);
        change(c);
    }

    @Override
    public void initialize(final Reactor reactor, final Ancestor parent,
            final FactoryImpl factory) throws Exception {
        super.initialize(reactor, parent, factory);
    }
}
