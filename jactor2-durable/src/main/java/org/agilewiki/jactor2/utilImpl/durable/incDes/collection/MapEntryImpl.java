package org.agilewiki.jactor2.utilImpl.durable.incDes.collection;

import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.JASerializable;
import org.agilewiki.jactor2.util.durable.incDes.MapEntry;
import org.agilewiki.jactor2.utilImpl.durable.ComparableKey;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.app.DurableImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.IncDesImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.Scalar;

/**
 * A map is, in part, a list of map entries.
 */
public class MapEntryImpl<KEY_TYPE extends Comparable<KEY_TYPE>, VALUE_TYPE>
        extends DurableImpl implements MapEntry<KEY_TYPE, VALUE_TYPE>,
        ComparableKey<KEY_TYPE> {

    private static final int TUPLE_KEY = 0;
    private static final int TUPLE_VALUE = 1;

    void setFactories(final Factory keyFactory, final Factory valueFactory) {
        tupleFactories = new FactoryImpl[2];
        tupleFactories[TUPLE_KEY] = keyFactory;
        tupleFactories[TUPLE_VALUE] = valueFactory;
    }

    @Override
    public KEY_TYPE getKey() throws Exception {
        return (KEY_TYPE) ((Scalar) _iGet(TUPLE_KEY).getDurable()).getValue();
    }

    public void setKey(final KEY_TYPE key) throws Exception {
        ((Scalar) _iGet(TUPLE_KEY).getDurable()).setValue(key);
    }

    @Override
    public VALUE_TYPE getValue() throws Exception {
        return (VALUE_TYPE) _iGet(TUPLE_VALUE);
    }

    public void setValueBytes(final byte[] bytes) throws Exception {
        final JASerializable old = (JASerializable) getValue();
        ((IncDesImpl) old.getDurable()).setContainerJid(null);
        final JASerializable elementJid = createSubordinate(
                tupleFactories[TUPLE_VALUE], this, bytes);
        tuple[TUPLE_VALUE] = elementJid;
        change(elementJid.getDurable().getSerializedLength()
                - old.getDurable().getSerializedLength());
    }

    /**
     * Compares the key or value;
     *
     * @param o The comparison value.
     * @return The result of a compareTo(o).
     */
    @Override
    public int compareKeyTo(final KEY_TYPE o) throws Exception {
        return getKey().compareTo(o);
    }
}
