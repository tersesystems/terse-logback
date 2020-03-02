# Compression

Encoders are powerful and useful.  They give you access to the raw bytes, and let you manipulate them before they get to an appender.  But you'll have to put them together inside an appender if you want to do byte transformation.

As an example, say that we want to write out files directly in [zstandard](http://facebook.github.io/zstd/) or [brotli](https://en.wikipedia.org/wiki/Brotli) using Logback.  The easiest way to do this is to provide a `FileAppender` with a swapped out compression encoder, while presenting a public API that looks just like a regular encoder.

Here's the appender as `logback.xml` sees it:

```xml
<appender name="COMPRESS_FILE" class="com.tersesystems.logback.compress.CompressingFileAppender">
    <file>encoded.zst</file>

    <compressAlgo>zstd</compressAlgo>
    <bufferSize>1024000</bufferSize>

    <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
        <charset>UTF-8</charset>
        <pattern>%-5level %logger{35} - %msg%n</pattern>
    </encoder>
</appender>
```

Under the hood, `CompressingFileAppender` delegates to a regular file appender, but uses [commons-compress](https://commons.apache.org/proper/commons-compress/) and a `CompressingEncoder` to wrap `PatternLayoutEncoder`:

```java
public class CompressingFileAppender<E> extends UnsynchronizedAppenderBase<E> {
    // ...

    @Override
    public void start() {
        fileAppender = new FileAppender<>();
        fileAppender.setContext(getContext());
        fileAppender.setFile(getFile());
        fileAppender.setImmediateFlush(false);
        fileAppender.setPrudent(isPrudent());
        fileAppender.setAppend(isAppend());
        fileAppender.setName(name+"-embedded-file");

        CompressingEncoder<E> compressedEncoder = createCompressingEncoder(getEncoder());
        fileAppender.setEncoder(compressedEncoder);
        fileAppender.start();

        super.start();
    }

    public void stop() {
        fileAppender.stop();
        super.stop();
    }

    @Override
    protected void append(E eventObject) {
        fileAppender.doAppend(eventObject);
    }

    protected CompressingEncoder<E> createCompressingEncoder(Encoder<E> e) {
        int bufferSize = getBufferSize();
        String compressAlgo = getCompressAlgo();

        CompressorStreamFactory factory = CompressorStreamFactory.getSingleton();
        Set<String> names = factory.getOutputStreamCompressorNames();
        if (names.contains(getCompressAlgo())) {
            try {
                return new CompressingEncoder<>(e, compressAlgo, factory, bufferSize);
            } catch (CompressorException ex) {
                throw new RuntimeException("Cannot create CompressingEncoder", ex);
            }
        } else {
            throw new RuntimeException("No such compression algorithm: " + compressAlgo);
        }
    }
}
```

From there, the encoder will shove all the input bytes into a compressed stream until there's enough data to make compression worthwhile, and then flush the compressed bytes out through a byte array output stream:

```java
public class CompressingEncoder<E> extends EncoderBase<E> {
    private final Accumulator accumulator;
    private final Encoder<E> encoder;

    public CompressingEncoder(Encoder<E> encoder, 
                              String compressAlgo,
                              CompressorStreamFactory factory, 
                              int bufferSize) throws CompressorException {
        this.encoder = encoder;
        this.accumulator = new Accumulator(compressAlgo, factory, bufferSize);
    }

    @Override
    public byte[] headerBytes() {
        try {
            return accumulator.apply(encoder.headerBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] encode(E event) {
        try {
            return accumulator.apply(encoder.encode(event));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] footerBytes() {
        try {
            return accumulator.drain(encoder.footerBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class Accumulator {
        private final ByteArrayOutputStream byteOutputStream;
        private final CompressorOutputStream stream;
        private final LongAdder count = new LongAdder();
        private final int bufferSize;

        public Accumulator(String compressAlgo, 
                           CompressorStreamFactory factory, 
                           int bufferSize) throws CompressorException {
            this.bufferSize = bufferSize;
            this.byteOutputStream = new ByteArrayOutputStream();
            this.stream = factory.createCompressorOutputStream(compressAlgo, byteOutputStream);
        }

        boolean isFlushable() {
            return count.intValue() >= bufferSize;
        }

        byte[] apply(byte[] bytes) throws IOException {
            count.add(bytes.length);
            stream.write(bytes);

            if (isFlushable()) {
                stream.flush();
                byte[] output = byteOutputStream.toByteArray();
                byteOutputStream.reset();
                count.reset();
                return output;
            } else {
                return new byte[0];
            }
        }

        byte[] drain(byte[] inputBytes) throws IOException {
            if (inputBytes != null) {
                stream.write(inputBytes);
            }
            stream.close();
            count.reset();
            return byteOutputStream.toByteArray();
        }
    }
}
```

This keeps both `FileAppender` and `PatternLayoutEncoder` happy, while feeding compressed bytes as the stream.  Using delegation is generally much easier than trying to extend from `FileAppender`, because `FileAppender` has very definite ideas about what kind of output stream it is using, and has all the logic of file rotation and backups encorporated into it, including its own gzip compression scheme for rotated files.

You can also extend this to add [dictionary support](https://facebook.github.io/zstd/#small-data) for ZStandard, and that would remove the need for a buffer to provide effective compression.  This does come with the downside of needing to pass the dictionary out of band though.

See [Application Logging in Java: Encoders](https://tersesystems.com/blog/2019/06/09/application-logging-in-java-part-7/) for more details.
