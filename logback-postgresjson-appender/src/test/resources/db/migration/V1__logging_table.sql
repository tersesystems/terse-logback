--
-- SPDX-License-Identifier: CC0-1.0
--
-- Copyright 2018-2019 Will Sargent.
--
-- Licensed under the CC0 Public Domain Dedication;
-- You may obtain a copy of the License at
--
--     http://creativecommons.org/publicdomain/zero/1.0/
--

-- timestamp will only give microsecond precision, so we store both timestamp and time since epoch in milliseconds.
-- store the start time in milliseconds.
CREATE TABLE logging_table (
   ID serial NOT NULL PRIMARY KEY,
   ts TIMESTAMPTZ(6) NOT NULL,
   tse_ms numeric NOT NULL,
   start_ms numeric NULL,
   level_value int NOT NULL,
   level VARCHAR(7) NOT NULL,
   evt jsonb NOT NULL
);

CREATE INDEX idxgin ON logging_table USING gin (evt);