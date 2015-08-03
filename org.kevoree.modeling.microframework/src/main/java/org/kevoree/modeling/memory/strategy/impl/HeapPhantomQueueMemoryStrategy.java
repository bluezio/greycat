package org.kevoree.modeling.memory.strategy.impl;

import org.kevoree.modeling.memory.cache.KChunkSpaceManager;
import org.kevoree.modeling.memory.cache.impl.PhantomQueueChunkSpaceManager;
import org.kevoree.modeling.memory.space.KChunkSpace;
import org.kevoree.modeling.memory.space.impl.HeapChunkSpace;
import org.kevoree.modeling.memory.strategy.KMemoryStrategy;

public class HeapPhantomQueueMemoryStrategy implements KMemoryStrategy {

    @Override
    public KChunkSpace newStorage() {
        return new HeapChunkSpace();
    }

    @Override
    public KChunkSpaceManager newCache(KChunkSpace p_storage) {
        return new PhantomQueueChunkSpaceManager(p_storage);
    }

}
