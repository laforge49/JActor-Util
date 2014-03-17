package org.agilewiki.jactor2.utilImpl.durable.incDes.collection.bmap;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;
import org.agilewiki.jactor2.utilImpl.durable.FactoryLocatorImpl;
import org.agilewiki.jactor2.utilImpl.durable.incDes.collection.smap.IntegerSMapFactory;
import org.agilewiki.jactor2.utilImpl.durable.incDes.scalar.vlens.UnionImpl;

/**
 * Creates IntegerBMap's.
 */
public class IntegerBMapFactory extends FactoryImpl {

    private static final int NODE_CAPACITY = 28;

    public static void registerFactory(final FactoryLocator _factoryLocator,
            final String actorType, final String valueType)
            throws FactoryLocatorClosedException {
        UnionImpl.registerFactory(_factoryLocator, "U." + actorType, "LM."
                + actorType, "IM." + actorType);

        ((FactoryLocatorImpl) _factoryLocator)
                .registerFactory(new IntegerBMapFactory(actorType, valueType,
                        true, true));
        ((FactoryLocatorImpl) _factoryLocator)
                .registerFactory(new IntegerBMapFactory("IN." + actorType,
                        valueType, false, false));

        IntegerSMapFactory.registerFactory(_factoryLocator, "LM." + actorType,
                valueType, NODE_CAPACITY);
        IntegerSMapFactory.registerFactory(_factoryLocator, "IM." + actorType,
                "IN." + actorType, NODE_CAPACITY);
    }

    private final String valueType;
    private boolean isRoot = true;
    private boolean auto = true;

    /**
     * Create an FactoryImpl.
     *
     * @param jidType   The jid type.
     * @param valueType The value type.
     */
    protected IntegerBMapFactory(final String jidType, final String valueType,
            final boolean isRoot, final boolean auto) {
        super(jidType);
        this.valueType = valueType;
        this.isRoot = isRoot;
        this.auto = auto;
    }

    /**
     * Create a JLPCActor.
     *
     * @return The new actor.
     */
    @Override
    protected IntegerBMap instantiateBlade() {
        return new IntegerBMap();
    }

    /**
     * Create and configure an actor.
     *
     * @param reactor The processing of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public IntegerBMap newSerializable(final Reactor reactor,
            final Ancestor parent) throws Exception {
        final IntegerBMap imj = (IntegerBMap) super.newSerializable(reactor,
                parent);
        final FactoryLocator fl = Durables.getFactoryLocator(reactor);
        imj.valueFactory = fl.getFactory(valueType);
        imj.nodeCapacity = NODE_CAPACITY;
        imj.isRoot = isRoot;
        imj.init();
        if (auto) {
            imj.setNodeLeaf();
        }
        return imj;
    }
}
