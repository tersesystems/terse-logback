# Ring Buffers

There may be situations where there is no visible target for diagnostic logging, for example in the case where there is a race condition or a subtle data corruption that only shows up every so often.  In this case, the ideal workflow would be to keep the most recent diagnostic information available, but only see it when the appropriate condition is triggered.

This is a pattern called ring buffer logging, described in [Using Ring Buffer Logging to Help Find Bugs](http://www.exampler.com/writing/ring-buffer.pdf) by [Brian Marick](https://twitter.com/marick).  

In ring buffer logging, all debug events related to the logger are stored, but are stored in a [circular buffer](https://en.wikipedia.org/wiki/Circular_buffer) that is overwritten by the latest logs.  When triggered, the entire buffer is flushed to appenders.  This is in contrast to tap filters, which will immediately create events and then flush them to appenders, and do not keep them in memory.

This is also a pattern used by [restify](http://restify.com/) as RequestCaptureStream.

There are two implementations of ring buffer logging, in the `logback-ringbuffer` module: one that is threshold based, and another which is marker based.

TODO Finish this