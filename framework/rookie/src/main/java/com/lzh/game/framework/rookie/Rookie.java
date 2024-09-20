package com.lzh.game.framework.rookie;

import com.lzh.game.framework.rookie.serializer.ClassInfo;
import com.lzh.game.framework.rookie.serializer.Serializer;
import com.lzh.game.framework.rookie.serializer.impl.*;
import com.lzh.game.framework.rookie.serializer.impl.collection.*;
import io.netty.buffer.ByteBuf;
import io.netty.util.collection.ShortObjectHashMap;
import io.netty.util.collection.ShortObjectMap;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.jar.Attributes;

/**
 * @author zehong.l
 * @since 2024-09-06 12:06
 **/
@Slf4j
public class Rookie {

    private static final int INNER_TYPE_ID_MAX = 100;
    private static final short ARRAY_SERIALIZER_ID = 0;
    private final ShortObjectMap<ClassInfo> DEFINED_CONTAIN = new ShortObjectHashMap<>();
    private final Map<Class<?>, ClassInfo> CLASS_CONTAIN = new HashMap<>();
    private ClassInfo arrayClassInfo;
    private volatile boolean init;

    public ClassInfo getClassInfo(short id) {
        var defined = DEFINED_CONTAIN.get(id);
        if (Objects.isNull(defined)) {
            throw new RuntimeException("Not defined id: " + id);
        } else if (!defined.isInit()) {
            throw new RuntimeException("Not init serializer: " + id);
        }
        return defined;
    }

    public ClassInfo getClassInfo(Class<?> clz) {
        var type = clz.isArray() ? clz.getComponentType() : clz;
        var info = CLASS_CONTAIN.get(type);
        if (Objects.isNull(info)) {
            throw new NullPointerException("Not defined " + clz.getName());
        }
        if (clz.isArray()) {
            return ClassInfo.array(arrayClassInfo.getId(), info.getClz(), arrayClassInfo.getSerializer());
        }
        return info;
    }

    public boolean containType(Class<?> clz) {
        return CLASS_CONTAIN.containsKey(clz);
    }

    public boolean containId(short id) {
        return DEFINED_CONTAIN.containsKey(id);
    }

    private void register(ClassInfo classInfo) {
        DEFINED_CONTAIN.put(classInfo.getId(), classInfo);
        CLASS_CONTAIN.put(classInfo.getClz(), classInfo);
    }

    private void checkInit() {
        if (!init) {
            synchronized (Rookie.class) {
                if (!init) {
                    init();
                    init = true;
                }
            }
        }
    }

    public void register(Class<?> clz) {
        if (containType(clz)) {
            throw new IllegalArgumentException("Repeat defined class: " + clz);
        }
        register(nextId(), clz);
    }

    private short nextId() {
        int value = DEFINED_CONTAIN.size();
        return value <= INNER_TYPE_ID_MAX ? INNER_TYPE_ID_MAX + 1 : (short) value;
    }

    public void register(short id, Class<?> clz) {
        register(id, clz, null);
    }

    public void register(short id, Class<?> clz, Serializer<?> serializer) {
        if (id >= 0 && id <= INNER_TYPE_ID_MAX) {
            throw new IllegalArgumentException();
        }
        if (clz.isArray()) {
            throw new IllegalArgumentException("Don't register array type!");
        }
        try {
            if (!clz.isEnum()) {
                clz.getConstructor();
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Register class no have NoArgsConstructor. " + clz.getName());
        }
        register0(id, clz, serializer);
    }

    private void register0(short id, Class<?> clz, Serializer<?> serializer) {
        if (containId(id)) {
            throw new IllegalArgumentException("Repeat defined id: " + id + "clz:" + clz);
        }
        if (containType(clz)) {
            throw new IllegalArgumentException("Repeat defined class: " + clz);
        }
        var defined = ClassInfo.of(id, clz, serializer);
        defined.setInit(false);
        register(defined);
    }

    @SuppressWarnings("unchecked")
    public <T> T deserializer(ByteBuf in, Class<T> clz) {
        checkInit();
        if (in.readableBytes() == 0) {
            throw new RuntimeException("Have not data.");
        }
        if (clz.isArray()) {
            return (T) arrayClassInfo.getSerializer().readObject(in, clz.getComponentType());
        }
        return (T) getClassInfo(clz).getSerializer().readObject(in, clz);
    }

    @SuppressWarnings("unchecked")
    public void serializer(ByteBuf out, Object value) {
        checkInit();
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("Value cannot null.");
        }
        if (value.getClass().isArray()) {
            arrayClassInfo.getSerializer().writeObject(out, value);
        } else {
            getClassInfo(value.getClass()).getSerializer().writeObject(out, value);
        }
    }

    private void init() {
        log.info("Staring rookie init..");
        var init = new Init();
        init.initRegister();
        init.creatSerializer();
        log.info("Rookie init finish.");
    }

    private class Init {
        private short starIndex = 0;

        private void initRegister() {
            initSpecialType();

            registerNext(Void.TYPE, new EmptySerializer());
            registerNext(Short.class, new ShotSerializer());
            registerNext(Short.TYPE, new ShotSerializer());
            registerNext(Integer.class, new IntSerializer());
            registerNext(Integer.TYPE, new IntSerializer());
            registerNext(Long.class, new LongSerializer());
            registerNext(Long.TYPE, new LongSerializer());
            registerNext(Float.class, new FloatSerializer());
            registerNext(Float.TYPE, new FloatSerializer());
            registerNext(Double.class, new DoubleSerializer());
            registerNext(Double.TYPE, new DoubleSerializer());
            registerNext(Character.class, new CharSerializer());
            registerNext(Character.TYPE, new CharSerializer());
            registerNext(String.class, new StringSerializer());

            initCollection();
            initMap();
            initQueue();
            initSet();
            initDequeue();
        }

        private void initSpecialType() {
            register(ARRAY_SERIALIZER_ID, ArraySerializer.class, new ArraySerializer<>());
            arrayClassInfo = DEFINED_CONTAIN.get(ARRAY_SERIALIZER_ID);
            arrayClassInfo.setArray(true);
            starIndex += 1;
        }

        private void initCollection() {
            var cls = new Class<?>[]{
                    List.class, ArrayList.class, LinkedList.class, CopyOnWriteArrayList.class,
                    Vector.class, AbstractCollection.class, AbstractList.class
            };
            register0(cls, new CollectionSerializer<>());
        }

        private void initMap() {
            var cls = new Class<?>[]{
                    Map.class, HashMap.class, LinkedHashMap.class,
                    TreeMap.class, ConcurrentHashMap.class, ConcurrentSkipListMap.class,
                    AbstractMap.class, IdentityHashMap.class, WeakHashMap.class, Attributes.class
            };
            register0(cls, new MapSerializer<>());
        }

        private void initSet() {
            var cls = new Class<?>[]{
                    Set.class, HashSet.class, TreeSet.class, LinkedHashSet.class,
                    ConcurrentSkipListSet.class, AbstractSet.class
            };
            register0(cls, new SetSerializer<>());
        }

        private void initQueue() {
            var cls = new Class<?>[]{
                    AbstractQueue.class, Queue.class, LinkedBlockingQueue.class,
                    ArrayBlockingQueue.class
            };
            register0(cls, new QueueSerializer<>());
        }

        private void initDequeue() {
            var cls = new Class<?>[]{
                    Deque.class, ArrayDeque.class, LinkedBlockingDeque.class
            };
            register0(cls, new DequeSerializer<>());
        }

        private void register0(Class<?>[] cls, Serializer<?> serializer) {
            for (Class<?> cl : cls) {
                registerNext(cl, serializer);
            }
        }

        void register(short id, Class<?> clz, Serializer<?> serializer) {
            Rookie.this.register0(id, clz, serializer);
        }

        void registerNext(Class<?> clz, Serializer<?> serializer) {
            var id = starIndex++;
            register(id, clz, serializer);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        void creatSerializer() {
            for (ClassInfo value : Rookie.this.DEFINED_CONTAIN.values()) {
                if (value.isInit()) {
                    continue;
                }
                computeParent(value);
                var serializer = value.getSerializer();
                if (Objects.isNull(serializer)) {
                    serializer = value.getClz().isEnum() ? new EnumSerializer() : new ObjectSerializer();
                }
                serializer.initialize(value.getClz(), Rookie.this);
                value.setSerializer(serializer);
                value.setInit(true);
            }
        }

        void computeParent(ClassInfo classInfo) {
            var cs = new Class<?>[]{Map.class, Set.class, List.class, Collection.class};
            for (Class<?> c : cs) {
                if (c.isAssignableFrom(classInfo.getClz())) {
                    classInfo.setCollection(true);
                    return;
                }
            }
        }
    }
}
