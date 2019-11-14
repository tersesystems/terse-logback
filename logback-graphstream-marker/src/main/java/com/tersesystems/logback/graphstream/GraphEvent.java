package com.tersesystems.logback.graphstream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Collections;
import java.util.Set;

/** */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
  @JsonSubTypes.Type(value = GraphEvent.Node.Add.class),
  @JsonSubTypes.Type(value = GraphEvent.Node.Change.class),
  @JsonSubTypes.Type(value = GraphEvent.Node.Remove.class),
  @JsonSubTypes.Type(value = GraphEvent.Edge.Add.class),
  @JsonSubTypes.Type(value = GraphEvent.Edge.Change.class),
  @JsonSubTypes.Type(value = GraphEvent.Edge.Remove.class),
  @JsonSubTypes.Type(value = GraphEvent.Change.class),
  @JsonSubTypes.Type(value = GraphEvent.Clear.class),
  @JsonSubTypes.Type(value = GraphEvent.Step.class),
})
public abstract class GraphEvent {

  private long timeId;
  private String graphId;

  @JsonProperty("op")
  public abstract String op();

  public String getGraphId() {
    return graphId;
  }

  public void setGraphId(String graphId) {
    this.graphId = graphId;
  }

  // ------------------------------------------------------------
  // Node events
  // ------------------------------------------------------------

  public abstract static class Node extends GraphEvent {
    private String nodeId;

    public String getNodeId() {
      return nodeId;
    }

    public void setNodeId(String nodeId) {
      this.nodeId = nodeId;
    }

    public static class Add extends Node {
      private Set<GraphAttributeEvent> attributes = Collections.emptySet();

      public Add() {}

      public Add(String sourceId, String nodeId, Set<GraphAttributeEvent> attributes) {
        setGraphId(sourceId);
        setNodeId(nodeId);
        this.attributes = attributes;
      }

      @Override
      public String op() {
        return "an";
      }

      public Set<GraphAttributeEvent> getAttributes() {
        return attributes;
      }

      public void setAttributes(Set<GraphAttributeEvent> attributes) {
        this.attributes = attributes;
      }
    }

    public static class Change extends Node {
      private Set<GraphAttributeEvent> attributes;

      public Change() {}

      @Override
      public String op() {
        return "cn";
      }

      public Change(String sourceId, String nodeId, Set<GraphAttributeEvent> attrs) {
        setGraphId(sourceId);
        setNodeId(nodeId);
        setAttributes(attrs);
      }

      public Set<GraphAttributeEvent> getAttributes() {
        return attributes;
      }

      public void setAttributes(Set<GraphAttributeEvent> attributes) {
        this.attributes = attributes;
      }
    }

    public static class Remove extends Node {

      public Remove() {}

      @Override
      public String op() {
        return "dn";
      }

      public Remove(String sourceId, String nodeId) {
        setGraphId(sourceId);
        setNodeId(nodeId);
      }
    }
  }

  // ------------------------------------------------------------
  // Edge events
  // ------------------------------------------------------------

  public abstract static class Edge extends GraphEvent {
    private String edgeId;

    public String getEdgeId() {
      return edgeId;
    }

    public void setEdgeId(String edgeId) {
      this.edgeId = edgeId;
    }

    public static class Add extends Edge {
      private String fromNodeId;
      private String toNodeId;
      private boolean directed;
      private Set<GraphAttributeEvent> attributes = Collections.emptySet();

      public Add() {}

      public Add(
          String sourceId,
          String edgeId,
          String fromNodeId,
          String toNodeId,
          boolean directed,
          Set<GraphAttributeEvent> attributes) {
        setGraphId(sourceId);
        setEdgeId(edgeId);
        setFromNodeId(fromNodeId);
        setToNodeId(toNodeId);
        setDirected(directed);
        this.attributes = attributes;
      }

      @Override
      public String op() {
        return "ae";
      }

      public Set<GraphAttributeEvent> getAttributes() {
        return attributes;
      }

      public void setAttributes(Set<GraphAttributeEvent> attributes) {
        this.attributes = attributes;
      }

      public String getFromNodeId() {
        return fromNodeId;
      }

      public void setFromNodeId(String fromNodeId) {
        this.fromNodeId = fromNodeId;
      }

      public String getToNodeId() {
        return toNodeId;
      }

      public void setToNodeId(String toNodeId) {
        this.toNodeId = toNodeId;
      }

      public boolean isDirected() {
        return directed;
      }

      public void setDirected(boolean directed) {
        this.directed = directed;
      }
    }

    public static class Change extends Edge {
      private Set<GraphAttributeEvent> attributes;

      public Change() {}

      public Change(String sourceId, String edgeId, Set<GraphAttributeEvent> attrs) {
        setGraphId(sourceId);
        setEdgeId(edgeId);
        setAttributes(attrs);
      }

      @Override
      public String op() {
        return "ce";
      }

      public Set<GraphAttributeEvent> getAttributes() {
        return attributes;
      }

      public void setAttributes(Set<GraphAttributeEvent> attributes) {
        this.attributes = attributes;
      }
    }

    public static class Remove extends Edge {

      public Remove() {}

      public Remove(String sourceId, String edgeId) {
        setGraphId(sourceId);
        setEdgeId(edgeId);
      }

      @Override
      public String op() {
        return "de";
      }
    }
  }

  // ------------------------------------------------------------
  // Graph events
  // ------------------------------------------------------------

  public static class Change extends GraphEvent {
    private Set<GraphAttributeEvent> attributes;

    public Change() {}

    public Change(String graphId, Set<GraphAttributeEvent> attributes) {
      setGraphId(graphId);
      this.attributes = attributes;
    }

    @Override
    public String op() {
      return "cg";
    }

    public Set<GraphAttributeEvent> getAttributes() {
      return attributes;
    }

    public void setAttributes(Set<GraphAttributeEvent> attributes) {
      this.attributes = attributes;
    }
  }

  public static class Clear extends GraphEvent {

    public Clear() {}

    public Clear(String sourceId) {
      setGraphId(sourceId);
    }

    @Override
    public String op() {
      return "cl";
    }
  }

  public static class Step extends GraphEvent {

    public Step() {}

    public Step(String sourceId, double step) {
      setGraphId(sourceId);
      setStep(step);
    }

    private double step;

    @Override
    public String op() {
      return "st";
    }

    public double getStep() {
      return step;
    }

    public void setStep(double step) {
      this.step = step;
    }
  }
}
