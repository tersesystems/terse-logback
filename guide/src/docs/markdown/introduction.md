
## What is Structured Logging?

It's logging in JSON.  Technically, you could be logging in another structure like XML or JSON, but almost everyone uses JSON.  It's been around for [a while](https://www.kartar.net/2015/12/structured-logging/).  Technically, since there are several JSON objects all in one file / stream, this is called "newline delimited JSON" or [NDJSON](http://ndjson.org/) or [jsonlines](http://jsonlines.org/).  In this project, both text and JSON formats are rendered independently, but if you only output JSON it's not a huge deal, because you can read JSON logs as text with a special log viewer such as [jl](https://github.com/koenbollen/jl/blob/master/README.md).

Semantically, a log entry typically has multiple pieces of information associated with it, described as "high cardinality" by observability geeks.  Structured logging means that the cardinality goes from "closed" -- you can only log things that you have defined fields for -- to "open", where you can add arbitrary fields and objects to your log entry as long as it's JSON.

Structured logging means that you can add more context to logs and do more with them without having to do regexes.  As [Honeycomb](https://honeycomb.io) [describes it](
https://www.honeycomb.io/blog/you-could-have-invented-structured-logging/):

> Structured logging is really all about giving yourself — and your team — a logging API to help you provide consistent context in events. An unstructured logger accepts strings. A structured logger accepts a map, hash, or dictionary that describes all the attributes you can think of for an event.

Logs are [different from events](https://www.honeycomb.io/blog/how-are-structured-logs-different-from-events/).  All events can be represented as logs, but not all logs are events.  Many logs are only portions of events.  An event is a conceptual abstraction and a log is one possible representation of that abstraction.

Logs are also different from metrics.  A metric represents a single number.  You can extract metrics from logs, but it's a very expensive way of going about it.

## Adding Context

There is a question of what you want to add when you log.  This is a matter of taste, but in general you should log so that you [create a consistent narrative](https://www.honeycomb.io/blog/event-foo-constructing-a-coherent-narrative/).  As previously mentioned, a log may indicate a portion of an event, so you want to log where doing so would help tell a story of what happened afterwards.

There are some things you should [always add to an event](https://www.honeycomb.io/blog/event-foo-what-should-i-add-to-an-event/), such as who is talking to your service, what they're asking, business relevant fields, additional context around your service / environment, response time and particulars. You should add units to your field names when you measure a quantity, i.e. `response_time_ms`, and add a "human readable" version of internal information if available.

You should add [context to your logs](https://www.honeycomb.io/blog/event-foo-moar-context-better-events/) that helps differentiate it from its peers, so you never have to guess where the source of a log is coming from.

Adding a [correlation id](https://blog.rapid7.com/2016/12/23/the-value-of-correlation-ids/) helps you [design for results](https://www.honeycomb.io/blog/event-foo-designing-for-results/) and tie your logs into a coherent event.  You don't need to use a UUID: a [flake id](https://github.com/boundary/flake) will probably be better for you.  I'm using [idem](https://github.com/mguenther/idem/) here, but most things will work.

So, we know what structured logging is now.  What does it look like in SLF4J?
