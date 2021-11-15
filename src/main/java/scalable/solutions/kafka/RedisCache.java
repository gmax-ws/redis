package scalable.solutions.kafka;

import io.lettuce.core.RedisClient;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;

import java.time.Duration;

public class RedisCache {

    private final StatefulRedisConnection<String, String> connection;

    private RedisCache(String uri) {
        this.connection = RedisClient.create(uri).connect();
    }

    public static RedisCache create(String uri) {
        return new RedisCache(uri);
    }

    public void close() {
        this.connection.close();
    }

    public boolean isNotDuplicateKey(Integer key, String message, long days) {
        return connection.sync().setGet(key.toString(), message,
                SetArgs.Builder.keepttl().ex(Duration.ofDays(days))) == null;
    }
}
