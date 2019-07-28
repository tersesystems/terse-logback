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
package com.tersesystems.logback.turbomarker;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.launchdarkly.client.LDClient;
import com.launchdarkly.client.LDClientInterface;
import com.launchdarkly.client.LDUser;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.logstash.logback.argument.StructuredArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import static java.util.Collections.singletonList;
import static net.logstash.logback.argument.StructuredArguments.kv;

public class DiagnosticLoggingExample {

    public static void main(String[] args) {
        Config config = ConfigFactory.load();
        LDClientInterface client = new LDClient(config.getString("launchdarkly.sdkkey"));
        Logger logger = LoggerFactory.getLogger(Order.class);
        LDMarkerFactory markerFactory = new LDMarkerFactory(client);
        LDUser ldUser = new LDUser.Builder("UNIQUE IDENTIFIER")
                .firstName("Bob")
                .lastName("Loblaw")
                .customString("groups", singletonList("beta_testers"))
                .build();
        Marker marker = markerFactory.create("diagnostics-order", ldUser);
        OrderDiagnosticLogging diagnostics = new OrderDiagnosticLogging(logger, marker);
        Order order = new Order("id1337", diagnostics);
        order.addToCart(new LineItem());
        order.addPayment(new Payment());
        order.addShipping(new Shipping());
        order.checkout();
        order.fulfill();
        order.complete();
    }

    static class Order {
        private final OrderDiagnosticLogging diagnostics;

        @JsonProperty("id") // Make available to logstash-logback-encoder
        private final String id;

        public Order(String id, OrderDiagnosticLogging diagnostics) {
            this.id = id;
            this.diagnostics = diagnostics;
        }

        public String getId() {
            return id;
        }

        public void addToCart(LineItem lineItem) {
            diagnostics.reportAddToCart(this, lineItem);
        }

        public void addPayment(Payment payment) {
            diagnostics.reportAddPayment(this, payment);
        }

        public void addShipping(Shipping shipping) {
            diagnostics.reportAddShipping(this, shipping);
        }

        public void checkout() {
            diagnostics.reportCheckout(this);
        }

        public void fulfill() {
            diagnostics.reportFulfill(this);
        }

        public void complete() {
            diagnostics.reportComplete(this);
        }

        @Override
        public String toString() {
            return String.format("Order(id = %s)", id);
        }
    }

    static class OrderDiagnosticLogging {
        private final Logger logger;
        private final Marker marker;

        OrderDiagnosticLogging(Logger logger, Marker marker) {
            this.logger = logger;
            this.marker = marker;
        }

        void reportAddToCart(Order order, LineItem lineItem) {
            reportArg("addToCart", order, kv("lineItem", lineItem));
        }

        void reportAddPayment(Order order, Payment payment) {
            reportArg("addPayment", order, kv("payment", payment));
        }

        void reportAddShipping(Order order, Shipping shipping) {
            reportArg("addShipping", order, kv("shipping", shipping));
        }

        void reportCheckout(Order order) {
            report("checkout", order);
        }

        void reportFulfill(Order order) {
            report("fulfill", order);
        }

        void reportComplete(Order order) {
            report("fulfill", order);
        }

        private void reportArg(String methodName, Order order, StructuredArgument arg) {
            if (logger.isDebugEnabled(marker)) {
                logger.debug(marker, "{}: {}, {}", kv("method", methodName), kv("order", order), arg);
            }
        }

        private void report(String methodName, Order order) {
            if (logger.isDebugEnabled(marker)) {
                logger.debug(marker, "{}: {}", kv("method", methodName), kv("order", order));
            }
        }
    }

    private static class Payment {
        @Override
        public String toString() {
            return "Payment()";
        }
    }

    private static class Shipping {

        @Override
        public String toString() {
            return "Shipping()";
        }
    }

    private static class LineItem {

        @Override
        public String toString() {
            return "LineItem()";
        }
    }
}
