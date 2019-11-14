package com.tersesystems.logback.graphstream.markers;

import static com.tersesystems.logback.graphstream.markers.GraphEventMarker.*;

/** This is the public API for building graphs. */
public class GraphMarkers {

  private final String sourceId;

  public GraphMarkers(String sourceId) {
    this.sourceId = sourceId;
  }

  String sourceId() {
    return this.sourceId;
  }

  public Builder.AddNode addNode(String nodeId) {
    return new Builder.AddNode(sourceId(), nodeId);
  }

  public Builder.ChangeNode changeNode(String nodeId) {
    return new Builder.ChangeNode(sourceId(), nodeId);
  }

  public GraphEventMarker removeNode(String nodeId) {
    return new RemoveNodeEventMarker(sourceId(), nodeId);
  }

  public Builder.AddEdge addEdge(String edgeId) {
    return new Builder.AddEdge(sourceId(), edgeId);
  }

  public Builder changeEdge(String edgeId) {
    return new Builder.ChangeEdge(sourceId(), edgeId);
  }

  public GraphEventMarker removeEdge(String edgeId) {
    return new RemoveEdgeEventMarker(sourceId(), edgeId);
  }

  public Builder changeGraph() {
    return new Builder.ChangeGraph(sourceId());
  }

  public GraphEventMarker clearGraph() {
    return new ClearGraphEventMarker(sourceId());
  }

  public GraphEventMarker addStep(double step) {
    return new StepEventMarker(sourceId(), step);
  }
}
