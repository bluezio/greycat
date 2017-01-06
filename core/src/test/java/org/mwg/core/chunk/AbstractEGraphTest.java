package org.mwg.core.chunk;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;
import org.mwg.chunk.ChunkSpace;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.StateChunk;
import org.mwg.core.MockStorage;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.plugin.MemoryFactory;
import org.mwg.plugin.Storage;
import org.mwg.struct.*;

public abstract class AbstractEGraphTest {

    private MemoryFactory factory;

    public AbstractEGraphTest(MemoryFactory factory) {
        this.factory = factory;
    }

    @Test
    public void simpleUsageTest() {
        Graph g = GraphBuilder.newBuilder().withScheduler(new NoopScheduler()).build();
        g.connect(null);
        ChunkSpace space = factory.newSpace(100, g);
        StateChunk chunk = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);
        //test embedded graph attribute
        EGraph egraph = (EGraph) chunk.getOrCreate(0, Type.EGRAPH);
        //test primitive attribute
        ENode eNode = egraph.newNode();
        egraph.setRoot(eNode);
        eNode.set("name", Type.STRING, "hello");
        Assert.assertEquals("{\"name\":\"hello\"}", eNode.toString());
        //test single eRelation
        ENode secondENode = egraph.newNode();
        secondENode.set("name", Type.STRING, "secondNode");
        eNode.set("children", Type.ENODE, secondENode);
        ENode retrieved = (ENode) eNode.get("children");
        Assert.assertEquals(retrieved.toString(), retrieved.toString());
        //test eRelation
        ERelation eRelation = (ERelation) eNode.getOrCreate("testRel", Type.ERELATION);
        for (int i = 0; i < 3; i++) {
            ENode loopNode = egraph.newNode();
            secondENode.set("name", Type.STRING, "node_" + i);
            eRelation.add(loopNode);
        }
        ERelation resolvedERelation = (ERelation) eNode.get("testRel");
        Assert.assertEquals(3, resolvedERelation.size());
        Assert.assertEquals("[2,3,4]", resolvedERelation.toString());
    }

    @Test
    public void setCostTest() {
        Graph g = GraphBuilder.newBuilder().withScheduler(new NoopScheduler()).build();
        g.connect(null);
        ChunkSpace space = factory.newSpace(100, g);
        StateChunk chunk = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);
        EGraph egraph = (EGraph) chunk.getOrCreate(0, Type.EGRAPH);
        ENode eNode = egraph.newNode();
        for (int i = 0; i < 1000000; i++) {
            eNode.setAt(i, Type.INT, i);
        }
        for (int i = 0; i < 1000000; i++) {
            Assert.assertEquals(i, (int) eNode.getAt(i));
        }
    }

    @Test
    public void loadSaveTest() {

        Storage mock = new MockStorage();

        Graph g = GraphBuilder.newBuilder()
                .withScheduler(new NoopScheduler())
                .withStorage(mock)
                .build();

        g.connect(null);

        StateChunk chunk = (StateChunk) g.space().createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);
        chunk.setFromKey("name", Type.STRING, "ParentChunk");

        //test embedded graph attribute
        EGraph egraph = (EGraph) chunk.getOrCreate(0, Type.EGRAPH);
        //test primitive attribute
        ENode eNode = egraph.newNode();
        egraph.setRoot(eNode);
        eNode.set("self", Type.ENODE, eNode);
        eNode.set("name", Type.STRING, "root");
        ERelation eRelation = (ERelation) eNode.getOrCreate("children", Type.ERELATION);
        for (int i = 0; i < 9999; i++) {
            ENode loopNode = egraph.newNode();
            loopNode.set("name", Type.STRING, "node_" + i);
            eRelation.add(loopNode);
        }
        long before = System.currentTimeMillis();
        g.save(null);
        long after = System.currentTimeMillis();
        System.out.println("save time:" + (after - before));
        g.disconnect(null);

        g = GraphBuilder.newBuilder()
                .withScheduler(new NoopScheduler())
                .withStorage(mock)
                .build();
        g.connect(null);
        final long before2 = System.currentTimeMillis();
        g.space().getOrLoadAndMark(ChunkType.STATE_CHUNK, 0, 0, 0, res -> {
            StateChunk loaded = (StateChunk) res;
            EGraph egraphLoaded = (EGraph) loaded.get(0);
            long after2 = System.currentTimeMillis();
            System.out.println("loading time:" + (after2 - before2));
            Assert.assertEquals(egraph.toString(), egraphLoaded.toString());
            ENode rootLoaded = egraphLoaded.root();
            Assert.assertTrue(rootLoaded != null);
            Assert.assertEquals(rootLoaded.toString(), rootLoaded.get("self").toString());
        });
    }

    @Test
    public void volatildeTest() {
        Graph g = GraphBuilder.newBuilder().withScheduler(new NoopScheduler()).build();
        g.connect(null);

        ChunkSpace space = factory.newSpace(100, g);

        EGraph eGraph = space.newVolatileGraph();
        ENode eNode = eGraph.newNode();

        Assert.assertNotNull(eGraph);
        Assert.assertNotNull(eNode);

        LMatrix lmat = (LMatrix) eNode.getOrCreate("lmat", Type.LMATRIX);
        lmat.appendColumn(new long[]{1, 2, 3});
        lmat.set(1, 0, 5L);

        DMatrix mat = (DMatrix) eNode.getOrCreate("mat", Type.DMATRIX);
        mat.appendColumn(new double[]{1.0, 2.0, 3.0});
        mat.set(1, 0, 0.7);

        ERelation eRel = (ERelation) eNode.getOrCreate("erel", Type.ERELATION);
        eRel.add(eNode);


    }


}