package com.tersesystems.logback;

import java.util.UUID;
import java.util.function.Supplier;

interface MarkedWithUUID {
    public UUID getUUID();
}

public class UUIDMarker extends BasicMarker implements MarkedWithUUID {

    private Supplier<UUID> uuidSupplier;

    public UUIDMarker(Supplier<UUID> uuidSupplier, String name) {
        super(name);
        this.uuidSupplier = uuidSupplier;
    }

    @Override
    public UUID getUUID() {
        return uuidSupplier.get();
    }
}