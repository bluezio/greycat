package org.kevoree.modeling.memory.cache;

import org.kevoree.modeling.KObject;
import org.kevoree.modeling.memory.KChunk;
import org.kevoree.modeling.memory.chunk.KObjectChunk;
import org.kevoree.modeling.meta.KMetaModel;

public interface KChunkSpaceManager {

    KChunk getAndMark(long universe, long time, long obj);

    void unmark(long universe, long time, long obj);

    KChunk unsafeGet(long universe, long time, long obj);

    KChunk createAndMark(long universe, long time, long obj, short type);

    void unmarkMemoryElement(KChunk element);

    void unmarkAllMemoryElements(KChunk[] elements);

    KObjectChunk cloneMarkAndUnmark(KObjectChunk previous, long newUniverse, long newTime, long obj, KMetaModel metaModel);

    void clear();

    void register(KObject object);

    void registerAll(KObject[] objects);

}
