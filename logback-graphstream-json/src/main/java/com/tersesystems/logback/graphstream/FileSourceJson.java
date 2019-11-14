package com.tersesystems.logback.graphstream;

import static com.tersesystems.logback.graphstream.GraphEvent.*;
import static org.graphstream.graph.implementations.AbstractElement.AttributeChangeEvent.ADD;
import static org.graphstream.graph.implementations.AbstractElement.AttributeChangeEvent.CHANGE;
import static org.graphstream.graph.implementations.AbstractElement.AttributeChangeEvent.REMOVE;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;
import org.graphstream.stream.file.FileSourceParser;
import org.graphstream.util.parser.ParseException;
import org.graphstream.util.parser.Parser;
import org.graphstream.util.parser.ParserFactory;

public class FileSourceJson extends FileSourceParser {
  @Override
  public ParserFactory getNewParserFactory() {
    return reader -> {
      try {
        return new GraphJsonParser(FileSourceJson.this, reader);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };
  }

  @Override
  public boolean nextStep() throws IOException {
    return ((GraphJsonParser) parser).nextStep();
  }

  public class GraphJsonParser implements Parser {

    private final Reader reader;
    private final ObjectMapper mapper = new ObjectMapper();
    private final MappingIterator<GraphEvent> iterator;
    private final FileSourceJson source;

    public GraphJsonParser(FileSourceJson source, Reader reader) throws IOException {
      this.source = source;
      this.reader = reader;
      this.iterator = mapper.readerFor(GraphEvent.class).readValues(reader);
    }

    @Override
    public void all() throws IOException, ParseException {
      while (next()) {}
    }

    @Override
    public void open() throws IOException, ParseException {}

    @Override
    public boolean next() throws IOException, ParseException {
      if (iterator.hasNext()) {
        GraphEvent event = iterator.next();
        if (event instanceof Node.Add) {
          Node.Add an = (Node.Add) event;
          source.sendNodeAdded(sourceId, an.getNodeId());
          Set<GraphAttributeEvent> attributes = an.getAttributes();
          if (notEmpty(attributes)) {
            for (GraphAttributeEvent attr : attributes) {
              sendAttribute(sourceId, ElementType.NODE, an.getNodeId(), attr);
            }
          }
        } else if (event instanceof Node.Change) {
          Node.Change cn = (Node.Change) event;
          Set<GraphAttributeEvent> attributes = cn.getAttributes();
          if (notEmpty(attributes)) {
            ElementType elementType = ElementType.NODE;
            String nodeId = cn.getNodeId();
            for (GraphAttributeEvent attr : attributes) {
              sendAttribute(sourceId, elementType, nodeId, attr);
            }
          }
        } else if (event instanceof Node.Remove) {
          Node.Remove dn = (Node.Remove) event;
          source.sendNodeRemoved(sourceId, dn.getNodeId());
        } else if (event instanceof Edge.Add) {
          Edge.Add ae = (Edge.Add) event;
          source.sendEdgeAdded(
              sourceId, ae.getEdgeId(), ae.getFromNodeId(), ae.getToNodeId(), ae.isDirected());
          Set<GraphAttributeEvent> attributes = ae.getAttributes();
          if (notEmpty(attributes)) {
            for (GraphAttributeEvent attr : attributes) {
              sendAttribute(sourceId, ElementType.EDGE, ae.getEdgeId(), attr);
            }
          }
        } else if (event instanceof Edge.Change) {
          Edge.Change ce = (Edge.Change) event;
          Set<GraphAttributeEvent> attributes = ce.getAttributes();
          String edgeId = ce.getEdgeId();
          if (notEmpty(attributes)) {
            for (GraphAttributeEvent attr : attributes) {
              sendAttribute(sourceId, ElementType.EDGE, edgeId, attr);
            }
          }
        } else if (event instanceof Edge.Remove) {
          Edge.Remove re = (Edge.Remove) event;
          source.sendEdgeRemoved(sourceId, re.getEdgeId());
        } else if (event instanceof Change) {
          Change changeGraph = (Change) event;
          Set<? extends GraphAttributeEvent> attributes = changeGraph.getAttributes();
          if (notEmpty(attributes)) {
            for (GraphAttributeEvent attr : attributes) {
              sendAttribute(sourceId, ElementType.GRAPH, null, attr);
            }
          }
        } else if (event instanceof Clear) {
          source.sendGraphCleared(sourceId);
        } else if (event instanceof GraphEvent.Step) {
          Step step = (Step) event;
          source.sendStepBegins(sourceId, step.getStep());
        } else {
          throw new IllegalStateException("unknown event type: " + event.getClass());
        }
      }
      return iterator.hasNext();
    }

    private boolean notEmpty(Set<? extends GraphAttributeEvent> attributes) {
      return !(attributes == null || attributes.isEmpty());
    }

    private void sendAttribute(
        String sourceId, ElementType elementType, String elementId, GraphAttributeEvent attr) {
      if (attr instanceof GraphAttributeEvent.Add) {
        GraphAttributeEvent.Add sa = (GraphAttributeEvent.Add) attr;
        source.sendAttributeChangedEvent(
            sourceId, elementId, elementType, sa.getName(), ADD, null, sa.getValue());
      } else if (attr instanceof GraphAttributeEvent.Change) {
        GraphAttributeEvent.Change sa = (GraphAttributeEvent.Change) attr;
        source.sendAttributeChangedEvent(
            sourceId, elementId, elementType, sa.getName(), CHANGE, null, sa.getValue());
      } else if (attr instanceof GraphAttributeEvent.Remove) {
        GraphAttributeEvent.Remove sa = (GraphAttributeEvent.Remove) attr;
        source.sendAttributeChangedEvent(
            sourceId, elementId, elementType, sa.getName(), REMOVE, null, null);
      } else {
        throw new IllegalStateException("unknown attribute type: " + attr.getClass());
      }
    }

    @Override
    public void close() throws IOException {
      iterator.close();
      reader.close();
    }

    public boolean nextEvents() throws IOException {
      try {
        return next();
      } catch (ParseException e) {
        throw new IOException(e);
      }
    }

    public boolean nextStep() throws IOException {
      return nextEvents();
    }
  }
}
