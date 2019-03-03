/*
 * SPDX-License-Identifier: CC0-1.0
 *
 * Copyright 2018-2019 Will Sargent.
 *
 * Licensed under the CC0 Public Domain Dedication;
 * You may obtain a copy of the License at
 *
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */
package example;

import org.junit.Test;

public class ClassWithMarkersTest {

    @Test
    public void doThingsWithMarker() {
        ClassWithMarkers classWithMarkers = new ClassWithMarkers();
        classWithMarkers.doThingsWithMarker("12345");
    }

}
