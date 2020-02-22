
## Tap Filters

A tap filter is used to tap some amount of incoming process and pass them to a specially configured appender even if they do not qualify as a logging event under normal circumstances.
 
This is a <a href="https://www.enterpriseintegrationpatterns.com/patterns/messaging/WireTap.html">wiretap</a> pattern from Enterprise Integration Patterns.
 
Tap Filters are very useful as a way to send data to an appender.  They completely bypass any kind of logging level configured on the front end, so you can set a logger to INFO level but still have access to all TRACE events when an error occurs, through the tap filter's appenders.

For example, a tap filter can automatically log everything with a correlation id at a TRACE level, without requiring filters or altering the log level as a whole.  Let's run a simple HTTP client program that calls out to Google and prints a result.  

```java
package playwsclient;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import akka.stream.SystemMaterializer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import play.libs.ws.*;
import play.libs.ws.ahc.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JavaClient implements DefaultBodyReadables {
    private final StandaloneAhcWSClient client;
    private final ActorSystem system;

    HikariDataSource createDataSource(Config config) {
        Config jdbcConfig = config.getConfig("logback.jdbc");
        String driver = jdbcConfig.getString("driver");
        String url = jdbcConfig.getString("url");
        String user = jdbcConfig.getString("username");
        String password = jdbcConfig.getString("password");

        return createDataSource(driver, url, user, password);
    }

    protected HikariDataSource createDataSource(
            String driver, String url, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setPoolName("client-pool");
        config.setMaximumPoolSize(1);
        Properties props = new Properties();
        // props.put("dataSource.logWriter", new PrintWriter(System.out));
        config.setDataSourceProperties(props);
        return new HikariDataSource(config);
    }

    public static void main(String[] args) {
        // Set up Akka materializer to handle streaming
        final String name = "wsclient";
        ActorSystem system = ActorSystem.create(name);

        system.registerOnTermination(() -> System.exit(0));
        Materializer materializer = SystemMaterializer.get(system).materializer();

        // Create the WS client from the `application.conf` file, the current classloader and materializer.
        StandaloneAhcWSClient ws = StandaloneAhcWSClient.create(
                AhcWSClientConfigFactory.forConfig(ConfigFactory.load(), system.getClass().getClassLoader()),
                materializer
        );

        JavaClient javaClient = new JavaClient(system, ws);
        javaClient.run();
    }

    JavaClient(ActorSystem system, StandaloneAhcWSClient client) {
        this.system = system;
        this.client = client;
    }

    public void run() {
        String correlationId = "12345";
        MDC.put("correlationId", correlationId);
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.debug("I am not important");
        client.url("http://www.google.com").get()
                .whenComplete((response, throwable) -> {
                    //CorrelationIdMarker correlationIdMarker = CorrelationIdMarker.create("12345");
                    String statusText = response.getStatusText();
                    String body = response.getBody(string());
                    logger.info("Got a response " + statusText);
                })
                .thenRun(() -> {
                    try {
                        Config config = system.settings().config();
                        HikariDataSource dataSource = createDataSource(config);

                        List<String> results = queryDatabase(dataSource, correlationId);
                        results.forEach(System.out::println);
                        client.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .thenRun(system::terminate);
    }

    List<String> queryDatabase(javax.sql.DataSource datasource, String correlationId) throws SQLException {
        try (Connection conn = datasource.getConnection()) {
            try (PreparedStatement p = conn.prepareStatement("select * from events where correlation_id = ? order by ts")) {
                p.setString(1, correlationId);
                try (ResultSet rs = p.executeQuery()) {
                    List<String> results = new ArrayList<>();
                    while (rs.next()) {
                        Timestamp ts = rs.getTimestamp("ts");
                        String json = rs.getString("evt");
                        String s = String.format("ts = %s, json = %s", ts, json);
                        //int count = rs.getInt(1);
                        //String s = String.format("count = %d", count);
                        results.add(s);
                    }
                    return results;
                }
            }
        }
    }
}
```

The configuration here uses a tap filter with a correlation id match up, and writes out the correlation id to the in memory database:

```xml
<configuration>
  <shutdownHook class="ch.qos.logback.core.hook.DelayingShutdownHook"/>

  <newRule pattern="configuration/typesafeConfig"
           actionClass="com.tersesystems.logback.typesafeconfig.TypesafeConfigAction"/>

  <newRule pattern="configuration/turboFilter/appender-ref"
           actionClass="ch.qos.logback.core.joran.action.AppenderRefAction"/>

  <typesafeConfig>
  </typesafeConfig>

  <appender name="ASYNC_JDBC" class="net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender">
    <appender class="com.tersesystems.logback.correlationid.CorrelationIdJDBCAppender">
      <mdcKey>correlationId</mdcKey>
      
      <driver>${jdbc.driver}</driver>
      <url>${jdbc.url}</url>
      <username>${jdbc.username}</username>
      <password>${jdbc.password}</password>

      <createStatements>${jdbc.createStatements}</createStatements>
      <insertStatement>${jdbc.insertStatement}</insertStatement>
      <reaperStatement>${jdbc.reaperStatement}</reaperStatement>
      <reaperSchedule>${jdbc.reaperSchedule}</reaperSchedule>

      <encoder class="net.logstash.logback.encoder.LogstashEncoder">
      </encoder>
    </appender>
  </appender>

  <turboFilter class="com.tersesystems.logback.correlationid.CorrelationIdTapFilter">
    <mdcKey>correlationId</mdcKey>
    <appender-ref ref="ASYNC_JDBC"/>
  </turboFilter>

  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%-5relative %-5level %logger{35} - %msg%n</pattern>
    </encoder>
  </appender>

  <root level="INFO">
    <appender-ref ref="CONSOLE" />
  </root>
</configuration>
```

and the `logback.conf` file is:

```hocon
local {
  jdbc {
    url = "jdbc:h2:mem:terse-logback;DB_CLOSE_DELAY=-1"
    driver = "org.h2.Driver"
    username = "sa"
    password = ""
    insertStatement = "insert into events(ts, tse_ms, start_ms, level_value, level, evt, correlation_id) values(?, ?, ?, ?, ?, ?, ?)"
    createStatements = """
    CREATE TABLE IF NOT EXISTS events (
       ID NUMERIC NOT NULL PRIMARY KEY AUTO_INCREMENT,
       ts TIMESTAMP(9) WITH TIME ZONE NOT NULL,
       tse_ms numeric NOT NULL,
       start_ms numeric NULL,
       level_value int NOT NULL,
       level VARCHAR(7) NOT NULL,
       evt JSON NOT NULL,
       correlation_id VARCHAR(255) NULL
    );
    CREATE INDEX correlation_id_idx ON events(correlation_id);
    """
    reaperStatement = "delete from events where ts < ?"
    reaperSchedule = PT30
  }
}
```

The output from this program shows that we can log at a regular INFO level, and still get access to all the DEBUG information that was posted "under the hood" to the in memory database if we need to:

```
514   INFO  com.zaxxer.hikari.HikariDataSource - jdbc-appender-pool-1581912533237 - Starting...
699   INFO  com.zaxxer.hikari.HikariDataSource - jdbc-appender-pool-1581912533237 - Start completed.
761   INFO  playwsclient.JavaClient - Got a response OK
765   INFO  com.zaxxer.hikari.HikariDataSource - client-pool - Starting...
766   INFO  com.zaxxer.hikari.HikariDataSource - client-pool - Start completed.
ts = 2020-02-16 20:08:53.652, json = {"@timestamp":"2020-02-16T20:08:53.652-08:00","@version":"1","message":"I am not important","logger_name":"playwsclient.JavaClient","thread_name":"main","level":"DEBUG","level_value":10000,"correlationId":"12345"}
ts = 2020-02-16 20:08:53.692, json = {"@timestamp":"2020-02-16T20:08:53.692-08:00","@version":"1","message":"-Dio.netty.processId: 31802 (auto-detected)","logger_name":"play.shaded.ahc.io.netty.channel.DefaultChannelId","thread_name":"main","level":"DEBUG","level_value":10000,"correlationId":"12345"}
ts = 2020-02-16 20:08:53.693, json = {"@timestamp":"2020-02-16T20:08:53.693-08:00","@version":"1","message":"-Djava.net.preferIPv4Stack: false","logger_name":"play.shaded.ahc.io.netty.util.NetUtil","thread_name":"main","level":"DEBUG","level_value":10000,"correlationId":"12345"}
ts = 2020-02-16 20:08:53.693, json = {"@timestamp":"2020-02-16T20:08:53.693-08:00","@version":"1","message":"-Djava.net.preferIPv6Addresses: false","logger_name":"play.shaded.ahc.io.netty.util.NetUtil","thread_name":"main","level":"DEBUG","level_value":10000,"correlationId":"12345"}
ts = 2020-02-16 20:08:53.694, json = {"@timestamp":"2020-02-16T20:08:53.694-08:00","@version":"1","message":"Loopback interface: lo (lo, 0:0:0:0:0:0:0:1%lo)","logger_name":"play.shaded.ahc.io.netty.util.NetUtil","thread_name":"main","level":"DEBUG","level_value":10000,"correlationId":"12345"}
ts = 2020-02-16 20:08:53.695, json = {"@timestamp":"2020-02-16T20:08:53.695-08:00","@version":"1","message":"/proc/sys/net/core/somaxconn: 128","logger_name":"play.shaded.ahc.io.netty.util.NetUtil","thread_name":"main","level":"DEBUG","level_value":10000,"correlationId":"12345"}
ts = 2020-02-16 20:08:53.696, json = {"@timestamp":"2020-02-16T20:08:53.696-08:00","@version":"1","message":"-Dio.netty.machineId: 08:00:27:ff:fe:5a:5f:59 (auto-detected)","logger_name":"play.shaded.ahc.io.netty.channel.DefaultChannelId","thread_name":"main","level":"DEBUG","level_value":10000,"correlationId":"12345"}
```

This is only one approach to storing diagnostic information -- the other approach is to use turbo filters and markers based on ring buffers.