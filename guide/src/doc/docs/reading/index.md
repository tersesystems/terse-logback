
### Best Practices

Many of these are logback specific, but still good overall.

* [9 Logging Best Practices Based on Hands-on Experience](https://www.loomsystems.com/blog/single-post/2017/01/26/9-logging-best-practices-based-on-hands-on-experience)
* [Woofer: logging in (best) practices](https://orange-opensource.github.io/woofer/logging-code/): Spring Boot
* [A whole product concern logging implementation](http://stevetarver.github.io/2016/04/20/whole-product-logging.html)
* [There is more to logging than meets the eye](https://allegro.tech/2015/10/there-is-more-to-logging-than-meets-the-eye.html)
* [Monitoring demystified: A guide for logging, tracing, metrics](https://techbeacon.com/enterprise-it/monitoring-demystified-guide-logging-tracing-metrics)
* [Application-Level Logging Best Practices](https://news.ycombinator.com/item?id=19497788)

Stack Overflow has a couple of good tips on SLF4J and Logging:

* [When to use the different log levels](https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels)
* [Why does the TRACE level exist, and when should I use it rather than DEBUG?](https://softwareengineering.stackexchange.com/questions/279690/why-does-the-trace-level-exist-and-when-should-i-use-it-rather-than-debug)
* [Best practices for using Markers in SLF4J/Logback](https://stackoverflow.com/questions/4165558/best-practices-for-using-markers-in-slf4j-logback)
* [Stackoverflow: Logging best practices in multi-node environment](https://stackoverflow.com/questions/43496695/java-logging-best-practices-in-multi-node-environment)

#### Level Up Logs

[Alberto Navarro](https://looking4q.blogspot.com/) has a great series

<ol>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-introduction.html">Introduction</a> (Everyone)</li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-your-logs-and-elk-json-logs.html">JSON as logs format</a> (Everyone)</li>
<li><b><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-logging-best.html">Logging best practices with Logback</a> (Targetting Java DEVs)</b></li>
<li><a href="https://looking4q.blogspot.com/2018/11/logging-cutting-edge-practices.html">Logging cutting-edge practices</a> (Targetting Java DEVs)&nbsp;</li>
<li><a href="https://looking4q.blogspot.com/2019/01/level-up-logs-and-elk-contract-first.html">Contract first log generator</a> (Targetting Java DEVs) </li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-elasticsearch.html">ElasticSearch VRR Estimation Strategy</a> (Targetting OPS)</li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-vrr-java-logback.html">VRR Java + Logback configuration</a> (Targetting OPS)</li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-vrr-filebeat.html">VRR FileBeat configuration</a> (Targetting OPS)</li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-vrr-logstash.html">VRR Logstash configuration and Index templates</a> (Targetting OPS)</li>
<li><a href="http://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-vrr-curator.html">VRR Curator configuration</a> (Targetting OPS)</li>
<li><a href="https://looking4q.blogspot.com/2018/10/level-up-logs-and-elk-logstash-grok.html">Logstash Grok, JSON Filter and JSON Input performance comparison</a> (Targetting OPS) </li>
</ol>

#### Logging Anti Patterns

Logging Anti-Patterns by [Rolf Engelhard](https://rolf-engelhard.de/):

* [Logging Anti-Patterns](http://rolf-engelhard.de/2013/03/logging-anti-patterns-part-i/)
* [Logging Anti-Patterns, Part II](http://rolf-engelhard.de/2013/04/logging-anti-patterns-part-ii/)
* [Logging Anti-Patterns, Part III](https://rolf-engelhard.de/2013/10/logging-anti-patterns-part-iii/)

#### Clean Code, clean logs

[Tomasz Nurkiewicz](https://www.nurkiewicz.com/) has a great series on logging:

* [Clean code, clean logs: use appropriate tools (1/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-use-appropriate.html)
* [Clean code, clean logs: logging levels are there for you (2/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-tune-your-pattern.html)
* [Clean code, clean logs: do you know what you are logging? (3/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-do-you-know-what.html)
* [Clean code, clean logs: avoid side effects (4/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-avoid-side.html)
* [Clean code, clean logs: concise and descriptive (5/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-concise-and.html)
* [Clean code, clean logs: tune your pattern (6/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-tune-your-pattern.html)
* [Clean code, clean logs: log method arguments and return values (7/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-log-method.html)
* [Clean code, clean logs: watch out for external systems (8/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-watch-out-for.html)
* [Clean code, clean logs: log exceptions properly (9/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-log-exceptions.html)
* [Clean code, clean logs: easy to read, easy to parse (10/10)](https://www.nurkiewicz.com/2010/05/clean-code-clean-logs-easy-to-read-easy.html)
* [Condensed 10 Tips on javacodegeeks](https://www.javacodegeeks.com/2011/01/10-tips-proper-application-logging.html)

### JSON Logging

* [Logging in JSON](http://www.asynchronous.org/blog/archives/2006/01/25/logging-in-json)
* [Write Logs for Machines, not Humans](https://paul.querna.org/articles/2011/12/26/log-for-machines-in-json/)

### Maple

* [Maple](https://github.com/Randgalt/maple)

### Eliot

* [Eliot](https://eliot.readthedocs.io/en/stable/quickstart.html)
* [Eliot Tree](https://github.com/jonathanj/eliottree)

### TreeLog

* [Treelog](https://github.com/lancewalton/treelog)

### Bunyan

* [Bunyan](https://timboudreau.com/blog/bunyan/read)
* [Comparison of Winston and Bunyan](https://strongloop.com/strongblog/compare-node-js-logging-winston-bunyan/)
* [Service logging in JSON with Bunyan](https://trentm.com/2012/03/service-logging-in-json-with-bunyan.html)
* [Bunyan Logging in Production at Joyent](https://trentm.com/talk-bunyan-in-prod/#/8)

### Timbre

* [Timbre](https://github.com/ptaoussanis/timbre/blob/master/README.md)

### Logback Encoders and Appenders

* [concurrent-build-logger](https://github.com/takari/concurrent-build-logger) (encoders and appenders both)
* [logzio-logback-appender](https://github.com/logzio/logzio-logback-appender)
* [logback-elasticsearch-appender](https://github.com/internetitem/logback-elasticsearch-appender)
* [logback-more-appenders](https://github.com/sndyuk/logback-more-appenders)
* [logback-steno](https://github.com/ArpNetworking/logback-steno)
* [logslack](https://github.com/gmethvin/logslack)
* [Lessons Learned Writing New Logback Appender](https://logz.io/blog/lessons-learned-writing-new-logback-appender/)
* [Extending logstash-logback-encoder](https://zenidas.wordpress.com/recipes/extending-logstash-logback-encoder/)
