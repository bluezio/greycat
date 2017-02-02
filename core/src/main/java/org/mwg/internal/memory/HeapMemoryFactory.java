/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.internal.memory;

import org.mwg.Graph;
import org.mwg.chunk.ChunkSpace;
import org.mwg.internal.chunk.heap.HeapChunkSpace;
import org.mwg.plugin.MemoryFactory;
import org.mwg.struct.Buffer;

public class HeapMemoryFactory implements MemoryFactory {

    @Override
    public ChunkSpace newSpace(long memorySize, Graph graph, boolean deepWorld) {
        return new HeapChunkSpace((int) memorySize, graph, deepWorld);
    }

    @Override
    public Buffer newBuffer() {
        return new HeapBuffer();
    }
}