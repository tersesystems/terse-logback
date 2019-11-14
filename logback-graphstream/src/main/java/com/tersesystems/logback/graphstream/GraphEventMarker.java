package com.tersesystems.logback.graphstream;

import static java.util.Collections.singletonList;

import java.util.List;
import java.util.Set;
import net.logstash.logback.marker.ObjectAppendingMarker;

public abstract class GraphEventMarker extends ObjectAppendingMarker {

  GraphEventMarker(GraphEvent graphEvent) {
    this(singletonList(graphEvent));
  }

  GraphEventMarker(List<GraphEvent> events) {
    super("graphstream", events);
  }

  static class AddNodeEventMarker extends GraphEventMarker {
    AddNodeEventMarker(String sourceId, String nodeId, Set<GraphAttributeEvent> attributeSet) {
      super(new GraphEvent.Node.Add(sourceId, nodeId, attributeSet));
    }
  }

  static class ChangeNodeEventMarker extends GraphEventMarker {
    ChangeNodeEventMarker(String sourceId, String nodeId, Set<GraphAttributeEvent> attributeSet) {
      super(new GraphEvent.Node.Change(sourceId, nodeId, attributeSet));
    }
  }

  static class RemoveNodeEventMarker extends GraphEventMarker {
    RemoveNodeEventMarker(String sourceId, String nodeId) {
      super(new GraphEvent.Node.Remove(sourceId, nodeId));
    }
  }

  static class AddEdgeEventMarker extends GraphEventMarker {
    AddEdgeEventMarker(
        String sourceId,
        String edgeId,
        String fromNodeId,
        String toNodeId,
        boolean directed,
        Set<GraphAttributeEvent> attributes) {
      super(new GraphEvent.Edge.Add(sourceId, edgeId, fromNodeId, toNodeId, directed, attributes));
    }
  }

  static class ChangeEdgeEventMarker extends GraphEventMarker {
    ChangeEdgeEventMarker(String sourceId, String nodeId, Set<GraphAttributeEvent> attributes) {
      super(new GraphEvent.Edge.Change(sourceId, nodeId, attributes));
    }
  }

  static class RemoveEdgeEventMarker extends GraphEventMarker {
    RemoveEdgeEventMarker(String sourceId, String nodeId) {
      super(new GraphEvent.Edge.Remove(sourceId, nodeId));
    }
  }

  static class StepEventMarker extends GraphEventMarker {
    StepEventMarker(String sourceId, double step) {
      super(new GraphEvent.Step(sourceId, step));
    }
  }

  static class ChangeGraphEventMarker extends GraphEventMarker {
    ChangeGraphEventMarker(String sourceId, Set<GraphAttributeEvent> attributes) {
      super(new GraphEvent.Change(sourceId, attributes));
    }
  }

  static class ClearGraphEventMarker extends GraphEventMarker {
    ClearGraphEventMarker(String sourceId) {
      super(new GraphEvent.Clear(sourceId));
    }
  }
}
