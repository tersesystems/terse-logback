
## Further Reading

### APIs

SLF4J is essentially the assembly language of Java logging at this point, so if you want to use something else it had better wrap or interoperate with SLF4J.

There are various wrappers and APIs on top of SLF4J:

* [Godaddy Logger](https://github.com/godaddy/godaddy-logger)
* [LogMachine](https://github.com/UnquietCode/LogMachine)
* [structlog4j](https://github.com/jacek99/structlog4j)
* [slf4j-fluent](https://github.com/ffissore/slf4j-fluent)

I have not used these personally.  I usually roll my own code when I need something on top of SLF4J, because the wrappers tend to set their own encoders on top.

### Logback Encoders and Appenders

There's a useful blog post on [writing your own appender](https://logz.io/blog/lessons-learned-writing-new-logback-appender/) for [logzio](https://github.com/logzio/logzio-logback-appender).

There are also additional encoders and console appenders in [concurrent-build-logger](https://github.com/takari/concurrent-build-logger).
  
### Other Blog Posts

* [Logging Best Practices](https://www.loomsystems.com/blog/single-post/2017/01/26/9-logging-best-practices-based-on-hands-on-experience)
* [OWASP Logging Cheat Sheet](https://www.owasp.org/index.php/Logging_Cheat_Sheet)
* [Woofer: logging in (best) practices](https://orange-opensource.github.io/woofer/logging-code/): Spring Boot 
* [A whole product concern logging implementation](http://stevetarver.github.io/2016/04/20/whole-product-logging.html)
* [Level up logs and ELK - Logging best practices with Logback ](https://looking4q.blogspot.com/2018/09/level-up-logs-and-elk-logging-best.html)
* [Extending logstash-logback-encoder](https://zenidas.wordpress.com/recipes/extending-logstash-logback-encoder/)
* [There is more to logging than meets the eye](https://allegro.tech/2015/10/there-is-more-to-logging-than-meets-the-eye.html)
