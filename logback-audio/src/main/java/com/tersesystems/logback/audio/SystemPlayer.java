package com.tersesystems.logback.audio;

import java.awt.*;

public class SystemPlayer implements Player {
    @Override
    public void play() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        final Runnable exclam =
                (Runnable) toolkit.getDesktopProperty("win.sound.exclamation");
        if (exclam != null) {
            exclam.run();
        } else {
            toolkit.beep();
        }
    }
}
