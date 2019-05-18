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
package com.tersesystems.logback.audio.appenders;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.tersesystems.logback.audio.Player;

public class AudioLevelAppender extends AppenderBase<ILoggingEvent> {

    private Player tracePlayer;
    private Player debugPlayer;
    private Player infoPlayer;
    private Player warnPlayer;
    private Player errorPlayer;

    @Override
    protected void append(ILoggingEvent eventObject) {
        Level level = eventObject.getLevel();
        switch (level.levelInt) {
            case Level.TRACE_INT:
                playTraceSound();
                break;
            case Level.DEBUG_INT:
                playDebugSound();
                break;
            case Level.INFO_INT:
                playInfoSound();
                break;
            case Level.WARN_INT:
                playWarnSound();
                break;
            case Level.ERROR_INT:
                playErrorSound();
                break;
        }
    }

    private void playTraceSound() {
        if (tracePlayer != null) {
            tracePlayer.play();
        }
    }

    private void playDebugSound() {
        if (debugPlayer != null) {
            debugPlayer.play();
        }
    }

    private void playInfoSound() {
        if (infoPlayer != null) {
            infoPlayer.play();
        }
    }

    private void playWarnSound() {
        if (warnPlayer != null) {
            warnPlayer.play();
        }
    }

    private void playErrorSound() {
        if (errorPlayer != null) {
            errorPlayer.play();
        }
    }

    public Player getTracePlayer() {
        return tracePlayer;
    }

    public void setTracePlayer(Player tracePlayer) {
        this.tracePlayer = tracePlayer;
    }

    public Player getDebugPlayer() {
        return debugPlayer;
    }

    public void setDebugPlayer(Player debugPlayer) {
        this.debugPlayer = debugPlayer;
    }

    public Player getInfoPlayer() {
        return infoPlayer;
    }

    public void setInfoPlayer(Player infoPlayer) {
        this.infoPlayer = infoPlayer;
    }

    public Player getWarnPlayer() {
        return warnPlayer;
    }

    public void setWarnPlayer(Player warnPlayer) {
        this.warnPlayer = warnPlayer;
    }

    public Player getErrorPlayer() {
        return errorPlayer;
    }

    public void setErrorPlayer(Player errorPlayer) {
        this.errorPlayer = errorPlayer;
    }
}
