package com.lzh.game.repository;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.lzh.game.common.bean.BeanUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.*;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonTest {

    @Test
    public void lock() throws IOException {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://111.230.149.152:16379")

                .setPassword("as123456");

//        config.useClusterServers()
//                // use "rediss://" for SSL connection
//                //redis://111.230.149.152:16379
//                .addNodeAddress("redis://localhost:6379");
//                //.setPassword("as123456");
        RedissonClient client = Redisson.create(config);

        /*List<Integer> list = client.getList("lzh", new JsonJacksonCodec());
        System.out.println(list);
        list.add(1);
        list.add(2);
        list.add(3);*/
        /*User newUser = User.createUser();
        User a = (User) client.getBucket("lx", new JsonJacksonCodec()).getAndSet(newUser);
        System.out.println(a);*/
        /*Map<String, Object> map = transfer(newUser);
        System.out.println(map);
        Map<String, Object> objectMap = client.getMap("xx", new JsonJacksonCodec());
//        objectMap.forEach((k,v) -> System.out.println(k + " " + v));
       // objectMap.putAll(map);
        User user = BeanUtil.mapToBean(objectMap, User.class);
        user.getItems().add(new User.Item());
        System.out.println(user);*/
        Map<String, Object> objectMap = client.getMap("xx", new JsonJacksonCodec());
        objectMap.clear();
    }

    private Map<String, Object> transfer(Object o) {
        return BeanUtil.beanToMap(o);
    }

    class FastJsonCode extends BaseCodec {
        private final ParserConfig defaultRedisConfig = new ParserConfig();

        private final Encoder encoder = in -> {
            ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
            try {
                ByteBufOutputStream os = new ByteBufOutputStream(out);
                JSON.writeJSONString(os, in, SerializerFeature.WriteClassName);
                return os.buffer();
            } catch (IOException e) {
                out.release();
                throw e;
            } catch (Exception e) {
                out.release();
                throw new IOException(e);
            }
        };
        private final Decoder<Object> decoder = (buf, state) ->

                JSON.parseObject(new ByteBufInputStream(buf), Object.class);
        @Override
        public Decoder<Object> getValueDecoder() {
            return decoder;
        }
        @Override
        public Encoder getValueEncoder() {
            return encoder;
        }
    }

    @Test
    public void t() throws IOException, ParseException {
        Map<Integer, Long> data = data();
        Map<Long, Integer> money = new HashMap<>();
        Map<Long, Long> times = new HashMap<>();
        Map<Long, String> names = new HashMap<>();
        Set<Integer> reason = fundReason();

        long fiveDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-10-16 04:59:59").getTime();
        Files.walk(Paths.get("/Users/jsonp/Downloads/37wan/"))
                .forEach(e -> {

                    File file = e.toFile();
                    if (!file.isFile()) {
                        return;
                    }
                    String name = file.getName();
                    Integer id = Integer.valueOf(name.split("_")[1]);
                    long asuram = data.get(id);
                    readFile(file, inner -> {
                        if (inner.getTime() > fiveDate) {
                            return;
                        }
                        Inner.Prop prop = inner.getProp();

                        if (prop.getAsuramId() == asuram) {
                            /*if (prop.reason == 18036) {
                                long time = inner.time;
                                long preTime = times.getOrDefault(asuram, 0L);

                                if (time >= preTime) {
                                    times.put(asuram, time);
                                    names.put(asuram, prop.newName);
                                }
                            }*/
                            // 资金
                            /*if (reason.contains(prop.reason)) {
                                if (prop.afterValue == 0) {
                                    return;
                                }
                                long time = inner.time;
                                long preTime = times.getOrDefault(asuram, 0L);

                                if (time >= preTime) {
                                    times.put(asuram, time);
                                    money.put(asuram, prop.afterValue);
                                }
                            }*/
                            // 建材
                            /*if (prop.reason == 11420) {
                                *//*long time = inner.time;
                                long preTime = times.getOrDefault(asuram, 0L);
                                if (time >= preTime) {

                                }*//*
                                int changeValue = prop.changeValue;
                                int value = money.getOrDefault(asuram, 0);
                                money.put(asuram, Math.max(changeValue, value));
                            }*/
                            // 建筑升级
                            if (prop.reason == 11420) {
                                /*if (prop.changeValue == 197962) {
                                    times.put(asuram, inner.time);
                                }*/
                                if (prop.getHadBuildType() == 1005) {

                                    //money.put(asuram, Math.max(money.getOrDefault(asuram, 0), prop.changeValue));
                                    long time = inner.time;
                                    long preTime = times.getOrDefault(asuram, 0L);

                                    if (time >= preTime) {
                                        times.put(asuram, time);
                                        money.put(asuram, prop.nowLevel);
                                    }
                                }
                            }

                        }
                    });
                });
        System.out.println(money);
        times.forEach((k,v) -> {
            System.out.println(k + " -> " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(v)));
        });
    }

    private void readFile(File file, Consumer<Inner> consumer) {
        Pattern pattern = Pattern.compile("\\{.*\\}");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    consumer.accept(JSON.parseObject(matcher.group(), Inner.class));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Map<Integer, Long> data() {
        Map<Integer, Long> map = new HashMap<>();
        map.put(300020, 1448043928879108L);
        map.put(100874, 1234212539596836L);
        map.put(300009, 1448032117719044L);
        map.put(320076, 1469578894906313L);
        map.put(101323, 1234694649680777L);
        map.put(101069, 1234421919257564L);
        map.put(300125, 1448156671775632L);
        map.put(340067, 1491044067709841L);
        map.put(300551, 1448614085792657L);
        map.put(100056, 1233334218789777L);
        map.put(100428, 1233733650748314L);
        map.put(300490, 1448548587541404L);
        map.put(300040, 1448065403720637L);
        map.put(101380, 1234755852964766L);
        return map;
    }

    @Data
    public static class Inner {
        private long time;
        private Prop prop;
        @Data
        public static class Prop {
            private int reason;
            private long asuramId;
            private int afterValue;
            private int hadBuildType;
            private int nowLevel;
            private int changeValue;
            private String newName;
        }
    }

    private Set<Integer> fundReason() {
        Set<Integer> set = new HashSet<>();
        set.add(12801);
        set.add(203410);
        set.add(203409);
        set.add(11411);
        set.add(11420);
        set.add(18014);
        return set;
    }

    @Data
    @AllArgsConstructor
    public static class ValueTime {
        private long time;
        private int value;
        private int reason;
        private int changeValue;
        @Override
        public String toString() {
            /*return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date(time)) + " reason:" + reason
                    + " changeValue:" + changeValue
                    + " after:" + value ;*/
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date(time)) +  "," + + reason + "," + value;
        }
    }

    @Test
    public void newT() throws IOException, ParseException {
        Map<Integer, Long> data = data();
        Map<Long, List<ValueTime>> parse = new HashMap<>();
        Set<Integer> reason = fundReason();
        long fiveDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-10-16 04:59:59").getTime();
        Files.walk(Paths.get("/Users/jsonp/Downloads/37wan/"))
                .forEach(e -> {
                    File file = e.toFile();
                    if (!file.isFile()) {
                        return;
                    }
                    String name = file.getName();
                    Integer id = Integer.valueOf(name.split("_")[1]);
                    long asuram = data.get(id);
                    readFile(file, inner -> {
                        if (inner.getTime() > fiveDate) {
                            return;
                        }
                        Inner.Prop prop = inner.getProp();

                        if (prop.getAsuramId() == asuram) {
                            // 资金
                            /*if (reason.contains(prop.reason)) {
                                List<ValueTime> list = parse.getOrDefault(asuram, new ArrayList<>());
                                list.add(new ValueTime(inner.time, prop.afterValue, prop.reason));
                                parse.put(asuram, list);
                            }*/
                            // 建材
                            if (prop.reason == 203400 || prop.reason == 203409 || prop.reason == 203410) {
                                List<ValueTime> list = parse.getOrDefault(asuram, new ArrayList<>());
                                list.add(new ValueTime(inner.time, prop.afterValue, prop.reason, prop.changeValue));
                                parse.put(asuram, list);
                            }
                            // 建筑升级
                            /*if (prop.reason == 11420) {
                                if (prop.getHadBuildType() == 1005) {
                                    if (prop.nowLevel == 0) {
                                        return;
                                    }
                                    long time = inner.time;
                                    long preTime = times.getOrDefault(asuram, 0L);

                                    if (time >= preTime) {
                                        times.put(asuram, time);
                                        money.put(asuram, prop.nowLevel);
                                    }
                                }
                            }*/

                        }
                    });
                });
        parse.forEach((k,v) -> {
            v.sort((o1,o2) -> o2.getTime() > o1.getTime() ? 1 : o2.getTime() == o1.getTime() ? o1.changeValue - o2.changeValue : -1);
            System.out.println(k + " -> " + v);
        });
    }

    @Test
    public void s() {
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
