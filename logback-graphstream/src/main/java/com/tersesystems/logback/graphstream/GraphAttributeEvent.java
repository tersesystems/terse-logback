package com.tersesystems.logback.graphstream;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "op", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = GraphAttributeEvent.Add.class),
  @JsonSubTypes.Type(value = GraphAttributeEvent.Change.class),
  @JsonSubTypes.Type(value = GraphAttributeEvent.Remove.class),
})
public abstract class GraphAttributeEvent {

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonTypeName("add")
  public static class Add extends GraphAttributeEvent {
    private Object value;

    public Add() {}

    public Add(String name, Object value) {
      setName(name);
      this.value = value;
    }

    public Object getValue() {
      return value;
    }

    public void setValue(Object value) {
      this.value = value;
    }
  }

  @JsonTypeName("set")
  public static class Change extends GraphAttributeEvent {
    private Object value;

    public Change() {}

    public Change(String name, Object newValue) {
      setName(name);
      setValue(newValue);
    }

    public Object getValue() {
      return value;
    }

    public void setValue(Object value) {
      this.value = value;
    }
  }

  @JsonTypeName("rm")
  public static class Remove extends GraphAttributeEvent {
    public Remove() {}

    public Remove(String name) {
      setName(name);
    }
  }
}
