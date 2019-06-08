package com.tersesystems.logback.core;

import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.EncoderBase;

/**
 * Takes another encoder as input and performs transformation on it.
 *
 * @param <E>
 */
public abstract class TransformingEncoder<E> extends EncoderBase<E> {

    private Encoder<E> encoder;

    public Encoder<E> getEncoder() {
        return encoder;
    }

    public void setEncoder(Encoder<E> encoder) {
        this.encoder = encoder;
    }

    @Override
    public void start() {
        if (encoder == null) {
            addError("No encoder set!");
        } else {
            super.start();
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (encoder != null) {
            encoder.stop();
        }
    }

    @Override
    public byte[] headerBytes() {
        return transformHeaderBytes(encoder.headerBytes());
    }

    @Override
    public byte[] encode(E event) {
        byte[] encodedEvent = encoder.encode(event);
        return transform(encodedEvent);
    }

    @Override
    public byte[] footerBytes() {
        return transformFooterBytes(encoder.footerBytes());
    }

    protected byte[] transformHeaderBytes(byte[] headerBytes) {
        return headerBytes;
    }

    protected byte[] transformFooterBytes(byte[] footerBytes) {
        return footerBytes;
    }

    protected byte[] transform(byte[] encodedEvent) {
        return encodedEvent;
    }

}
