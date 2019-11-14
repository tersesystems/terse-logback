package com.tersesystems.logback.graphstream;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

interface BuilderAttributes<H> {
  H addAttribute(String name, Object attribute);

  H addAttributes(Map<String, Object> attributes);

  H removeAttribute(String name);

  H removeAttributes(Set<String> names);

  H changeAttribute(String name, Object attribute);

  H changeAttributes(Map<String, Object> attributes);
}

interface BuilderEdges<H> {
  H from(String nodeId);

  H to(String nodeId);

  H directed(boolean isDirected);
}

// http://egalluzzo.blogspot.com/2010/06/using-inheritance-with-fluent.html
// https://stackoverflow.com/questions/17164375/subclassing-a-java-builder-class
// https://onelostlogician.wordpress.com/2016/10/10/inheritance-generics-and-builders/

public abstract class Builder<SELF extends Builder<SELF>> implements BuilderAttributes<SELF> {

  Set<GraphAttributeEvent> attributes;

  public abstract GraphEventMarker build();

  @SuppressWarnings("unchecked")
  final SELF self() {
    return (SELF) this;
  }

  @Override
  public SELF addAttribute(String name, Object attribute) {
    attributes.add(new GraphAttributeEvent.Add(name, attribute));
    return self();
  }

  @Override
  public SELF addAttributes(Map<String, Object> attributesMap) {
    Set<GraphAttributeEvent> newSet =
        attributesMap.entrySet().stream()
            .map(entry -> new GraphAttributeEvent.Add(entry.getKey(), entry.getValue()))
            .collect(Collectors.toSet());
    attributes.addAll(newSet);
    return self();
  }

  @Override
  public SELF removeAttribute(String name) {
    attributes.add(new GraphAttributeEvent.Remove(name));
    return self();
  }

  @Override
  public SELF removeAttributes(Set<String> names) {
    Set<GraphAttributeEvent.Remove> newSet =
        names.stream().map(GraphAttributeEvent.Remove::new).collect(Collectors.toSet());
    attributes.addAll(newSet);
    return self();
  }

  @Override
  public SELF changeAttribute(String name, Object value) {
    attributes.add(new GraphAttributeEvent.Change(name, value));
    return self();
  }

  @Override
  public SELF changeAttributes(Map<String, Object> attributeMap) {
    Set<GraphAttributeEvent.Change> newSet =
        attributeMap.entrySet().stream()
            .map(entry -> new GraphAttributeEvent.Change(entry.getKey(), entry.getValue()))
            .collect(Collectors.toSet());
    attributes.addAll(newSet);
    return self();
  }

  static class AddNode extends Builder<AddNode> {
    private final String nodeId;
    private final String graphId;

    AddNode(String graphId, String nodeId) {
      this.graphId = graphId;
      this.nodeId = nodeId;
      this.attributes = new LinkedHashSet<>();
    }

    public GraphEventMarker build() {
      return new GraphEventMarker.AddNodeEventMarker(graphId, nodeId, attributes);
    }
  }

  static class ChangeNode extends Builder<ChangeNode> {
    private final String nodeId;
    private final String graphId;

    ChangeNode(String graphId, String nodeId) {
      this.graphId = graphId;
      this.nodeId = nodeId;
      this.attributes = new LinkedHashSet<>();
    }

    @Override
    public GraphEventMarker build() {
      return new GraphEventMarker.ChangeNodeEventMarker(graphId, nodeId, attributes);
    }
  }

  static class AddEdge extends Builder<AddEdge> implements BuilderEdges<AddEdge> {

    private final String sourceId;
    private final String edgeId;
    private boolean directed;
    private String fromNodeId;
    private String toNodeId;

    AddEdge(String sourceId, String edgeId) {
      this.sourceId = sourceId;
      this.edgeId = edgeId;
      this.attributes = new LinkedHashSet<>();
    }

    @Override
    public AddEdge from(String fromNodeId) {
      this.fromNodeId = fromNodeId;
      return self();
    }

    @Override
    public AddEdge to(String toNodeId) {
      this.toNodeId = toNodeId;
      return self();
    }

    @Override
    public AddEdge directed(boolean isDirected) {
      this.directed = isDirected;
      return self();
    }

    @Override
    public GraphEventMarker build() {
      return new GraphEventMarker.AddEdgeEventMarker(
          sourceId, edgeId, fromNodeId, toNodeId, directed, attributes);
    }
  }

  static class ChangeEdge extends Builder<ChangeEdge> {
    private final String edgeId;
    private final String graphId;

    ChangeEdge(String graphId, String edgeId) {
      this.graphId = graphId;
      this.edgeId = edgeId;
      this.attributes = new LinkedHashSet<>();
    }

    @Override
    public GraphEventMarker build() {
      return new GraphEventMarker.ChangeEdgeEventMarker(graphId, edgeId, attributes);
    }
  }

  static class ChangeGraph extends Builder<ChangeGraph> {
    private final String graphId;

    ChangeGraph(String graphId) {
      this.graphId = graphId;
      this.attributes = new LinkedHashSet<>();
    }

    @Override
    public GraphEventMarker build() {
      return new GraphEventMarker.ChangeGraphEventMarker(graphId, attributes);
    }
  }
}
