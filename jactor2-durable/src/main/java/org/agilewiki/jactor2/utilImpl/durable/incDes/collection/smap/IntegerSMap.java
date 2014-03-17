package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.smap;

import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.utilImpl.durable.incDes.IncDesImpl;

/**
 * Holds a map with Integer keys.
 */
public class IntegerSMap<VALUE_TYPE extends IncDesImpl> extends
        SMap<Integer, VALUE_TYPE> {
    /**
     * Returns the IncDesFactory for the key.
     *
     * @return The IncDesFactory for the key.
     */
    @Override
    final protected Factory getKeyFactory() throws Exception {
        return Durables.getFactoryLocator(getReactor()).getFactory(
                JAInteger.FACTORY_NAME);
    }

    /**
     * Converts a string to a key.
     *
     * @param skey The integer to be converted.
     * @return The key.
     */
    @Override
    final protected Integer stringToKey(final String skey) {
        return new Integer(skey);
    }
}
