package example;

import org.junit.Test;

public class ClassWithMarkersTest {

    @Test
    public void doThingsWithMarker() {
        ClassWithMarkers classWithMarkers = new ClassWithMarkers();
        classWithMarkers.doThingsWithMarker("12345");
    }

}
