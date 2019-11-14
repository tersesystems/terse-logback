package com.tersesystems.logback.graphstream;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.Instantiatable;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import java.io.IOException;
import java.util.Set;
import org.graphstream.stream.file.FileSinkBase;

public class FileSinkJson extends FileSinkBase {

  private final ObjectWriter objectWriter =
      new ObjectMapper().writer(new NewlineAddingPrettyPrinter()).withRootValueSeparator("\n");

  private SequenceWriter writer;

  @Override
  protected void outputHeader() throws IOException {
    writer = objectWriter.writeValues(output);
    writer.init(false);
  }

  @Override
  protected void outputEndOfFile() throws IOException {}

  @Override
  public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
    Set<GraphAttributeEvent> attrs = singleton(new GraphAttributeEvent.Add(attribute, value));
    try {
      writer.write(new GraphEvent.Change(sourceId, attrs));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void graphAttributeChanged(
      String sourceId, long timeId, String attribute, Object oldValue, Object newValue) {
    Set<GraphAttributeEvent> attrs = singleton(new GraphAttributeEvent.Change(attribute, newValue));
    try {
      writer.write(new GraphEvent.Change(sourceId, attrs));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void graphAttributeRemoved(String sourceId, long timeId, String attribute) {
    Set<GraphAttributeEvent> attrs = singleton(new GraphAttributeEvent.Remove(attribute));
    try {
      writer.write(new GraphEvent.Change(sourceId, attrs));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void nodeAttributeAdded(
      String sourceId, long timeId, String nodeId, String attribute, Object value) {
    Set<GraphAttributeEvent> attrs = singleton(new GraphAttributeEvent.Add(attribute, value));
    try {
      writer.write(new GraphEvent.Node.Change(sourceId, nodeId, attrs));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void nodeAttributeChanged(
      String sourceId,
      long timeId,
      String nodeId,
      String attribute,
      Object oldValue,
      Object newValue) {
    Set<GraphAttributeEvent> attrs = singleton(new GraphAttributeEvent.Change(attribute, newValue));
    try {
      writer.write(new GraphEvent.Node.Change(sourceId, nodeId, attrs));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void nodeAttributeRemoved(String sourceId, long timeId, String nodeId, String attribute) {
    Set<GraphAttributeEvent> attrs = singleton(new GraphAttributeEvent.Remove(attribute));
    try {
      writer.write(new GraphEvent.Node.Change(sourceId, nodeId, attrs));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void edgeAttributeAdded(
      String sourceId, long timeId, String edgeId, String attribute, Object value) {
    Set<GraphAttributeEvent> attrs = singleton(new GraphAttributeEvent.Add(attribute, value));
    try {
      writer.write(new GraphEvent.Edge.Change(sourceId, edgeId, attrs));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void edgeAttributeChanged(
      String sourceId,
      long timeId,
      String edgeId,
      String attribute,
      Object oldValue,
      Object newValue) {
    Set<GraphAttributeEvent> attrs = singleton(new GraphAttributeEvent.Change(attribute, newValue));
    try {
      writer.write(new GraphEvent.Edge.Change(sourceId, edgeId, attrs));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void edgeAttributeRemoved(String sourceId, long timeId, String edgeId, String attribute) {
    Set<GraphAttributeEvent> attrs = singleton(new GraphAttributeEvent.Remove(attribute));
    try {
      writer.write(new GraphEvent.Edge.Change(sourceId, edgeId, attrs));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void nodeAdded(String sourceId, long timeId, String nodeId) {
    try {
      writer.write(new GraphEvent.Node.Add(sourceId, nodeId, emptySet()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void nodeRemoved(String sourceId, long timeId, String nodeId) {
    try {
      writer.write(new GraphEvent.Node.Remove(sourceId, nodeId));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void edgeAdded(
      String sourceId,
      long timeId,
      String edgeId,
      String fromNodeId,
      String toNodeId,
      boolean directed) {
    try {
      writer.write(
          new GraphEvent.Edge.Add(sourceId, edgeId, fromNodeId, toNodeId, directed, emptySet()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void edgeRemoved(String sourceId, long timeId, String edgeId) {
    try {
      writer.write(new GraphEvent.Edge.Remove(sourceId, edgeId));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void graphCleared(String sourceId, long timeId) {
    try {
      writer.write(new GraphEvent.Clear(sourceId));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void stepBegins(String sourceId, long timeId, double step) {
    try {
      writer.write(new GraphEvent.Step(sourceId, step));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static final class NewlineAddingPrettyPrinter extends MinimalPrettyPrinter
      implements Instantiatable<PrettyPrinter> {
    private int depth = 0;

    @Override
    public void writeStartObject(JsonGenerator jg) throws IOException, JsonGenerationException {
      super.writeStartObject(jg);
      ++depth;
    }

    @Override
    public void writeEndObject(JsonGenerator jg, int nrOfEntries)
        throws IOException, JsonGenerationException {
      super.writeEndObject(jg, nrOfEntries);
      if (--depth == 0) {
        jg.writeRaw('\n');
      }
    }

    @Override
    public PrettyPrinter createInstance() {
      return new NewlineAddingPrettyPrinter();
    }
  }
}
