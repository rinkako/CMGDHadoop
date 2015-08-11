import java.util.*;
import java.io.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.SortingParams;

public class RedisIOUtils {

    // 声明静态配置
    static Jedis client = new Jedis("192.168.1.128", 6379);

    // 设置一个缓存
    public static void setMyRedis(String myKey, String myVal) throws IOException {
        long a = System.currentTimeMillis();
        String result = client.set(myKey, myVal);
        System.out.println(String.format("Redis set: %s", result));
        System.out.println("Cost: " + (System.currentTimeMillis() - a) / 1000f + " sec\n");
    }

    // 获取一个缓存
    public static String getMyRedis(String myKey) throws IOException {
        String myVal = client.get(myKey);
        long a = System.currentTimeMillis();
        System.out.println(String.format("Redis get: %s", myVal));
        System.out.println("Cost: " + (System.currentTimeMillis() - a) / 1000f + " sec\n");
        return myVal;
    }

    // 清空缓存
    public static void clearMyRedis() throws IOException {
        client.flushDB();
        System.out.println("Redis flushDB\n");
    }

    public static void main(String[] args) throws IOException {
        setMyRedis("key-string", "Hello, Redis!");
        String ff = getMyRedis("key-string");
        System.out.println("Return value is: " + ff);
        clearMyRedis();
        ff = getMyRedis("key-string");
        System.out.println("Return value is: " + ff);
    }
}
