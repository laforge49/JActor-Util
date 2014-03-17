package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.FactoryLocator;

public class UnionTest extends TestCase {
    public void test() throws Exception {
        Durables.createPlant();
        try {
            final FactoryLocator factoryLocator = Durables
                    .getFactoryLocator();
            Durables.registerUnionFactory(factoryLocator, "siUnion",
                    JAString.FACTORY_NAME, "siUnion");
            final Union siu1 = (Union) Durables.newSerializable("siUnion");
            assertNull(siu1.getValue());
            final Reactor reactor = new NonBlockingReactor();
            final Union siu2 = (Union) siu1.copy(reactor);
            assertNull(siu2.getValue());
            siu2.setValue(JAString.FACTORY_NAME);
            final JAString sj2 = (JAString) siu2.getValue();
            assertNotNull(sj2);
            final Union siu3 = (Union) siu2.copy(reactor);
            final JAString sj3 = (JAString) siu3.getValue();
            assertNotNull(sj3);
        } finally {
            Plant.close();
        }
    }
}
