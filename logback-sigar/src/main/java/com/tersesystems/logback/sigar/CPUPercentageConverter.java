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
package com.tersesystems.logback.sigar;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.SigarException;

import java.util.Objects;

public class CPUPercentageConverter extends ClassicConverter implements SigarContextAware {

    @Override
    public String convert(ILoggingEvent event) {
        return getSigar().map(sigar -> {
            try {
                CpuPerc cpu = sigar.getCpuPerc();
                return format(cpu);
            } catch (SigarException e) {
                addError("Cannot retrieve CPU percentage", e);
                return "";
            }
        }).orElseGet(() -> {
            addError("Sigar is not registered!");
            return "";
        });
    }

    private String format(CpuPerc cpu) {
        String firstOption = getFirstOption();
        if (Objects.equals(firstOption, "sys")) {
            return sys(cpu);
        } if (Objects.equals(firstOption, "user")) {
            return user(cpu);
        } else {
            return sys(cpu) + " " + user(cpu);
        }
    }

    // Attempt logfmt https://www.brandur.org/logfmt
    private String sys(CpuPerc cpu) {
        return "sys=" + cpu.getSys();
    }

    private String user(CpuPerc cpu) {
        return "user=" + cpu.getUser();
    }
}
