/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.process.traversal.strategy.decoration;

import org.apache.tinkerpop.gremlin.LoadGraphWith;
import org.apache.tinkerpop.gremlin.process.AbstractGremlinProcessTest;
import org.apache.tinkerpop.gremlin.process.GremlinProcessRunner;
import org.apache.tinkerpop.gremlin.process.IgnoreEngine;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalEngine;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.NoSuchElementException;

import static org.apache.tinkerpop.gremlin.LoadGraphWith.GraphData.MODERN;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.bothE;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.outE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
@RunWith(GremlinProcessRunner.class)
public class SubgraphStrategyProcessTest extends AbstractGremlinProcessTest {

    @Test
    @LoadGraphWith(MODERN)
    @IgnoreEngine(TraversalEngine.Type.COMPUTER)
    public void shouldFilterVertexCriterion() throws Exception {
        final Traversal<Vertex,?> vertexCriterion = __.has("name", P.within("josh", "lop", "ripple"));

        final SubgraphStrategy strategy = SubgraphStrategy.build().vertexCriterion(vertexCriterion).create();
        final GraphTraversalSource sg = create(strategy);

        // three vertices are included in the subgraph
        assertEquals(6, g.V().count().next().longValue());
        assertEquals(3, sg.V().count().next().longValue());

        // only two edges are present, even though edges are not explicitly excluded
        // (edges require their incident vertices)
        assertEquals(6, g.E().count().next().longValue());
        assertEquals(2, sg.E().count().next().longValue());

        // from vertex

        assertEquals(2, g.V(convertToVertexId("josh")).outE().count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).outE().count().next().longValue());
        assertEquals(2, g.V(convertToVertexId("josh")).out().count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).out().count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).inE().count().next().longValue());
        assertEquals(0, sg.V(convertToVertexId("josh")).inE().count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).in().count().next().longValue());
        assertEquals(0, sg.V(convertToVertexId("josh")).in().count().next().longValue());
        assertEquals(3, g.V(convertToVertexId("josh")).bothE().count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).bothE().count().next().longValue());
        assertEquals(3, g.V(convertToVertexId("josh")).both().count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).both().count().next().longValue());

        // with label

        assertEquals(2, g.V(convertToVertexId("josh")).outE("created").count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).outE("created").count().next().longValue());
        assertEquals(2, g.V(convertToVertexId("josh")).out("created").count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).out("created").count().next().longValue());
        assertEquals(2, g.V(convertToVertexId("josh")).bothE("created").count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).bothE("created").count().next().longValue());
        assertEquals(2, g.V(convertToVertexId("josh")).both("created").count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).both("created").count().next().longValue());

        assertEquals(1, g.V(convertToVertexId("josh")).inE("knows").count().next().longValue());
        assertEquals(0, sg.V(convertToVertexId("josh")).inE("knows").count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).in("knows").count().next().longValue());
        assertEquals(0, sg.V(convertToVertexId("josh")).in("knows").count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).bothE("knows").count().next().longValue());
        assertEquals(0, sg.V(convertToVertexId("josh")).bothE("knows").count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).both("knows").count().next().longValue());
        assertEquals(0, sg.V(convertToVertexId("josh")).both("knows").count().next().longValue());

        // with label and branch factor

        assertEquals(1, g.V(convertToVertexId("josh")).local(outE("created").limit(1)).count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).local(outE("created").limit(1)).count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).local(outE("created").limit(1)).count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).local(outE("created").limit(1)).count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).local(bothE("created").limit(1)).count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).local(bothE("created").limit(1)).count().next().longValue());

        // from edge

        assertEquals(2, g.E(convertToEdgeId("josh", "created", "lop")).bothV().count().next().longValue());
        assertEquals(2, sg.E(convertToEdgeId("josh", "created", "lop")).bothV().count().next().longValue());

        assertEquals(2, g.E(convertToEdgeId("peter", "created", "lop")).bothV().count().next().longValue());
        try {
            sg.E(convertToEdgeId("peter", "created", "lop")).next();
            fail("Edge 12 should not be in the graph because peter is not a vertex");
        } catch (Exception ex) {
            assertTrue(ex instanceof NoSuchElementException);
        }

        assertEquals(2, g.E(convertToEdgeId("marko", "knows", "vadas")).bothV().count().next().longValue());
        try {
            sg.E(convertToEdgeId("marko", "knows", "vadas")).next();
            fail("Edge 7 should not be in the graph because marko is not a vertex");
        } catch (Exception ex) {
            assertTrue(ex instanceof NoSuchElementException);
        }
    }

    @Test
    @LoadGraphWith(MODERN)
    public void shouldFilterEdgeCriterion() throws Exception {
        final Traversal<Edge,?> edgeCriterion = __.or(
            __.has("weight", 1.0d).hasLabel("knows"), // 8
            __.has("weight", 0.4d).hasLabel("created").outV().has("name", "marko"), // 9
            __.has("weight", 1.0d).hasLabel("created") // 10
        );

        final SubgraphStrategy strategy = SubgraphStrategy.build().edgeCriterion(edgeCriterion).create();
        final GraphTraversalSource sg = create(strategy);

        // all vertices are here
        assertEquals(6, g.V().count().next().longValue());
        final Traversal t = sg.V();
        t.hasNext();
        printTraversalForm(t);
        assertEquals(6, sg.V().count().next().longValue());

        // only the given edges are included
        assertEquals(6, g.E().count().next().longValue());
        assertEquals(3, sg.E().count().next().longValue());

        assertEquals(2, g.V(convertToVertexId("marko")).outE("knows").count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("marko")).outE("knows").count().next().longValue());

        // wrapped Traversal<Vertex, Vertex> takes into account the edges it must pass through
        assertEquals(2, g.V(convertToVertexId("marko")).out("knows").count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("marko")).out("knows").count().next().longValue());
        assertEquals(2, g.V(convertToVertexId("josh")).out("created").count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).out("created").count().next().longValue());

        // from vertex

        assertEquals(2, g.V(convertToVertexId("josh")).outE().count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).outE().count().next().longValue());
        assertEquals(2, g.V(convertToVertexId("josh")).out().count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).out().count().next().longValue());

        assertEquals(1, g.V(convertToVertexId("josh")).inE().count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).inE().count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).in().count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).in().count().next().longValue());

        assertEquals(3, g.V(convertToVertexId("josh")).bothE().count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).bothE().count().next().longValue());
        assertEquals(3, g.V(convertToVertexId("josh")).both().count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).both().count().next().longValue());

        // with label

        assertEquals(2, g.V(convertToVertexId("josh")).outE("created").count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).outE("created").count().next().longValue());
        assertEquals(2, g.V(convertToVertexId("josh")).out("created").count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).out("created").count().next().longValue());
        assertEquals(2, g.V(convertToVertexId("josh")).bothE("created").count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).bothE("created").count().next().longValue());
        assertEquals(2, g.V(convertToVertexId("josh")).both("created").count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).both("created").count().next().longValue());

        assertEquals(1, g.V(convertToVertexId("josh")).inE("knows").count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).inE("knows").count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).in("knows").count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).in("knows").count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).bothE("knows").count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).bothE("knows").count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).both("knows").count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).both("knows").count().next().longValue());

        // with branch factor

        assertEquals(1, g.V(convertToVertexId("josh")).limit(1).local(bothE().limit(1)).count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).limit(1).local(bothE().limit(1)).count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).limit(1).local(bothE().limit(1)).inV().count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).limit(1).local(bothE().limit(1)).inV().count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).local(bothE("knows", "created").limit(1)).count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).local(bothE("knows", "created").limit(1)).count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).local(bothE("knows", "created").limit(1)).inV().count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).local(bothE("knows", "created").limit(1)).inV().count().next().longValue());

        // from edge

        assertEquals(2, g.E(convertToEdgeId("marko", "knows", "josh")).bothV().count().next().longValue());
        assertEquals(2, sg.E(convertToEdgeId("marko", "knows", "josh")).bothV().count().next().longValue());

        assertEquals(3, g.E(convertToEdgeId("marko", "knows", "josh")).outV().outE().count().next().longValue());
        assertEquals(2, sg.E(convertToEdgeId("marko", "knows", "josh")).outV().outE().count().next().longValue());
    }

    @Test
    @LoadGraphWith(MODERN)
    @IgnoreEngine(TraversalEngine.Type.COMPUTER)
    public void shouldFilterMixedCriteria() throws Exception {
        final Traversal<Vertex,?> vertexCriterion = __.has("name", P.within("josh", "lop", "ripple"));

        // 9 isn't present because marko is not in the vertex list
        final Traversal<Edge, ?> edgeCriterion = __.or(
                __.has("weight", 0.4d).hasLabel("created"), // 11
                __.has("weight", 1.0d).hasLabel("created") // 10
        );

        final SubgraphStrategy strategy = SubgraphStrategy.build().edgeCriterion(edgeCriterion).vertexCriterion(vertexCriterion).create();
        final GraphTraversalSource sg = create(strategy);

        // three vertices are included in the subgraph
        assertEquals(6, g.V().count().next().longValue());
        assertEquals(3, sg.V().count().next().longValue());

        // three edges are explicitly included, but one is missing its out-vertex due to the vertex criterion
        assertEquals(6, g.E().count().next().longValue());
        assertEquals(2, sg.E().count().next().longValue());

        // from vertex

        assertEquals(2, g.V(convertToVertexId("josh")).outE().count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).outE().count().next().longValue());
        assertEquals(2, g.V(convertToVertexId("josh")).out().count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).out().count().next().longValue());

        assertEquals(1, g.V(convertToVertexId("josh")).inE().count().next().longValue());
        assertEquals(0, sg.V(convertToVertexId("josh")).inE().count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).in().count().next().longValue());
        assertEquals(0, sg.V(convertToVertexId("josh")).in().count().next().longValue());

        assertEquals(3, g.V(convertToVertexId("josh")).bothE().count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).bothE().count().next().longValue());
        assertEquals(3, g.V(convertToVertexId("josh")).both().count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).both().count().next().longValue());

        // with label

        assertEquals(2, g.V(convertToVertexId("josh")).outE("created").count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).outE("created").count().next().longValue());
        assertEquals(2, g.V(convertToVertexId("josh")).out("created").count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).out("created").count().next().longValue());
        assertEquals(2, g.V(convertToVertexId("josh")).bothE("created").count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).bothE("created").count().next().longValue());
        assertEquals(2, g.V(convertToVertexId("josh")).both("created").count().next().longValue());
        assertEquals(2, sg.V(convertToVertexId("josh")).both("created").count().next().longValue());

        assertEquals(1, g.V(convertToVertexId("josh")).inE("knows").count().next().longValue());
        assertEquals(0, sg.V(convertToVertexId("josh")).inE("knows").count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).in("knows").count().next().longValue());
        assertEquals(0, sg.V(convertToVertexId("josh")).in("knows").count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).bothE("knows").count().next().longValue());
        assertEquals(0, sg.V(convertToVertexId("josh")).bothE("knows").count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).both("knows").count().next().longValue());
        assertEquals(0, sg.V(convertToVertexId("josh")).both("knows").count().next().longValue());

        // with branch factor

        assertEquals(1, g.V(convertToVertexId("josh")).local(bothE().limit(1)).count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).local(bothE().limit(1)).count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).local(bothE().limit(1)).inV().count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).local(bothE().limit(1)).inV().count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).local(bothE("knows", "created").limit(1)).count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).local(bothE("knows", "created").limit(1)).count().next().longValue());
        assertEquals(1, g.V(convertToVertexId("josh")).local(bothE("knows", "created").limit(1)).inV().count().next().longValue());
        assertEquals(1, sg.V(convertToVertexId("josh")).local(bothE("knows", "created").limit(1)).inV().count().next().longValue());

        // from edge

        assertEquals(2, g.E(convertToEdgeId("marko", "created", "lop")).bothV().count().next().longValue());
        try {
            sg.E(convertToEdgeId("marko", "created", "lop")).next();
            fail("Edge 9 should not be in the graph because marko is not a vertex");
        } catch (Exception ex) {
            assertTrue(ex instanceof NoSuchElementException);
        }
    }

    @Test
    @LoadGraphWith(MODERN)
    @IgnoreEngine(TraversalEngine.Type.COMPUTER)
    public void shouldFilterVertexCriterionAndKeepLabels() throws Exception {
        // this will exclude "peter"
        final Traversal<Vertex, ?> vertexCriterion = __.has("name", P.within("ripple", "josh", "marko"));

        final SubgraphStrategy strategy = SubgraphStrategy.build().vertexCriterion(vertexCriterion).create();
        final GraphTraversalSource sg = create(strategy);

        assertEquals(9, g.V().as("a").out().in().as("b").dedup("a", "b").count().next().intValue());
        assertEquals(2, sg.V().as("a").out().in().as("b").dedup("a", "b").count().next().intValue());

        final List<Object> list = sg.V().as("a").out().in().as("b").dedup("a", "b").values("name").toList();
        assertThat(list, hasItems("marko", "josh"));
    }

    @Test(expected = NoSuchElementException.class)
    @LoadGraphWith(MODERN)
    public void shouldGetExcludedVertex() throws Exception {
        final Traversal<Vertex,?> vertexCriterion = __.has("name", P.within("josh", "lop", "ripple"));

        final SubgraphStrategy strategy = SubgraphStrategy.build().vertexCriterion(vertexCriterion).create();
        final GraphTraversalSource sg = create(strategy);

        sg.V(convertToVertexId("marko")).next();
    }

    @Test(expected = NoSuchElementException.class)
    @LoadGraphWith(MODERN)
    public void shouldGetExcludedEdge() throws Exception {
        final Traversal<Edge,?> edgeCriterion = __.or(
                __.has("weight", 1.0d).hasLabel("knows"), // 8
                __.has("weight", 0.4d).hasLabel("created").outV().has("name", "marko"), // 9
                __.has("weight", 1.0d).hasLabel("created") // 10
        );

        final SubgraphStrategy strategy = SubgraphStrategy.build().edgeCriterion(edgeCriterion).create();
        final GraphTraversalSource sg = create(strategy);

        sg.E(sg.E(convertToEdgeId("marko", "knows", "vadas")).next()).next();
    }
    
    private GraphTraversalSource create(final SubgraphStrategy strategy) {
        return graphProvider.traversal(graph, strategy);
    }
}
