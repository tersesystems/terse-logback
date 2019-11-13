package com.tersesystems.logback.graphstream;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.read.ListAppender;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.logstash.logback.encoder.LogstashEncoder;
import org.junit.jupiter.api.Test;

public class GraphMarkerTest {

  @Test
  public void testAddNode() throws JoranException {
    Logger logger = getLogger();

    GraphMarkers gm = new GraphMarkers("myGraph");
    GraphEventMarker node1 = gm.addNode("node1").addAttribute("ui.label", "Node 1").build();
    logger.info(node1, "added node1");

    String s = getString(logger);
    assertThat(s)
        .contains(
            "\"graphstream\":[{\"graphId\":\"myGraph\",\"nodeId\":\"node1\",\"attributes\":[{\"op\":\"add\",\"name\":\"ui.label\",\"value\":\"Node 1\"}],\"op\":\"an\"}]");
  }

  @Test
  public void testChangeNode() throws JoranException {
    Logger logger = getLogger();

    GraphMarkers gm = new GraphMarkers("myGraph");
    GraphEventMarker ch = gm.changeNode("node1").changeAttribute("ui.label", "Node 1111").build();
    logger.info(ch, "changed node1");

    String s = getString(logger);
    assertThat(s)
        .contains(
            "\"graphstream\":[{\"graphId\":\"myGraph\",\"nodeId\":\"node1\",\"attributes\":[{\"op\":\"set\",\"name\":\"ui.label\",\"value\":\"Node 1111\"}],\"op\":\"cn\"}");
  }

  @Test
  public void testRemoveNode() {
    Logger logger = getLogger();

    GraphMarkers gm = new GraphMarkers("myGraph");
    GraphEventMarker rm = gm.removeNode("node1");
    logger.info(rm, "removed node1");

    String s = getString(logger);
    assertThat(s)
        .contains("\"graphstream\":[{\"graphId\":\"myGraph\",\"nodeId\":\"node1\",\"op\":\"dn\"}");
  }

  @Test
  public void testAddEdge() {
    Logger logger = getLogger();

    GraphMarkers gm = new GraphMarkers("myGraph");
    GraphEventMarker edge = gm.addEdge("edge1").from("node1").to("node2").directed(false).build();
    logger.info(edge, "added edge between node1 and node2");

    String s = getString(logger);
    assertThat(s)
        .contains(
            "\"graphstream\":[{\"graphId\":\"myGraph\",\"edgeId\":\"edge1\",\"fromNodeId\":\"node1\",\"toNodeId\":\"node2\",\"directed\":false,\"attributes\":[],\"op\":\"ae\"}]");
  }

  @Test
  public void testChangeEdge() {
    Logger logger = getLogger();

    GraphMarkers gm = new GraphMarkers("myGraph");
    GraphEventMarker edge = gm.changeEdge("edge1").addAttribute("herp", "derp").build();
    logger.info(edge, "changed edge1");

    String s = getString(logger);
    assertThat(s)
        .contains(
            "\"graphstream\":[{\"graphId\":\"myGraph\",\"edgeId\":\"edge1\",\"attributes\":[{\"op\":\"add\",\"name\":\"herp\",\"value\":\"derp\"}],\"op\":\"ce\"}]");
  }

  @Test
  public void testRemoveEdge() {
    Logger logger = getLogger();

    GraphMarkers gm = new GraphMarkers("myGraph");
    GraphEventMarker rm = gm.removeEdge("edge1");
    logger.info(rm, "removed edge1");

    String s = getString(logger);
    assertThat(s)
        .contains("\"graphstream\":[{\"graphId\":\"myGraph\",\"edgeId\":\"edge1\",\"op\":\"de\"}]");
  }

  @Test
  public void testAddStep() {
    Logger logger = getLogger();

    GraphMarkers gm = new GraphMarkers("myGraph");
    GraphEventMarker rm = gm.addStep(1234);
    logger.info(rm, "added step");

    String s = getString(logger);
    assertThat(s)
        .contains("\"graphstream\":[{\"graphId\":\"myGraph\",\"step\":1234.0,\"op\":\"st\"}]");
  }

  @Test
  public void testChangeGraph() {
    Logger logger = getLogger();

    GraphMarkers gm = new GraphMarkers("myGraph");
    GraphEventMarker rm = gm.changeGraph().addAttribute("herp", "derp").build();
    logger.info(rm, "changing graph");

    String s = getString(logger);
    assertThat(s)
        .contains(
            "\"graphstream\":[{\"graphId\":\"myGraph\",\"attributes\":[{\"op\":\"add\",\"name\":\"herp\",\"value\":\"derp\"}],\"op\":\"cg\"}]");
  }

  @Test
  public void testClearGraph() {
    Logger logger = getLogger();

    GraphMarkers gm = new GraphMarkers("myGraph");
    GraphEventMarker rm = gm.clearGraph();
    logger.info(rm, "clearing graph");

    String s = getString(logger);
    assertThat(s).contains("\"graphstream\":[{\"graphId\":\"myGraph\",\"op\":\"cl\"}]");
  }

  private String getString(Logger logger) {
    LogstashEncoder encoder = new LogstashEncoder();
    encoder.start();
    List<ILoggingEvent> list = getEventList(logger);
    return new String(encoder.encode(list.get(0)), StandardCharsets.UTF_8);
  }

  private List<ILoggingEvent> getEventList(Logger logger) {
    return ((ListAppender<ILoggingEvent>) logger.getAppender("LIST")).list;
  }

  private Logger getLogger() {
    try {
      LoggerContext context = new LoggerContext();
      URL resource = getClass().getResource("/logback-list.xml");
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.setContext(context);
      configurator.doConfigure(resource);

      return context.getLogger(Logger.ROOT_LOGGER_NAME);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
