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

import static com.lzh.game.framework.rookie.utils.Constant.INNER_TYPE_ID_MAX;
import static com.lzh.game.framework.rookie.utils.Constant.INNER_TYPE_ID_MIN;

/**
 * Simple serializer with netty
 * <p>Thread unsafe
 * <p>Please register class info before serializer or deserializer
 * <pre>
 * {@link Rookie}
 *
 * var buf = ByteBufAllocator.DEFAULT.heapBuffer();
 * var rook = new Rook();
 * rook.register(1, ConsumeObject.class);
 * var object = new ConsumeObject();
 * rook.serializer(buf, object);
 * rook.deserializer(buf, ConsumeObject.class);
 * </pre>
 *
 * @author zehong.l
 * @since 2024-09-06 12:06
 **/
@Slf4j
public class Rookie {

    private static final short ARRAY_SERIALIZER_ID = INNER_TYPE_ID_MIN;

    private final ShortObjectMap<ClassInfo> definedContain = new ShortObjectHashMap<>();
    private final Map<Class<?>, ClassInfo> classContain = new HashMap<>();
    private List<Class<?>> undistributedIdClass = new ArrayList<>();

    private ClassInfo arrayClassInfo;
    // False Sharing
    private long p1, p2, p3, p4, p5, p6, p7;
    private volatile long init;
    private long p11, p12, p13, p14, p15, p16, p17;

    public ClassInfo getClassInfo(short id) {
        var defined = definedContain.get(id);
        if (Objects.isNull(defined)) {
            throw new RuntimeException("Not defined id: " + id);
        } else if (!defined.isInit()) {
            throw new RuntimeException("Not init serializer: " + id);
        }
        return defined;
    }

    public ClassInfo getClassInfo(Class<?> clz) {
        var type = clz.isArray() ? clz.getComponentType() : clz;
        var info = classContain.get(type);
        if (Objects.isNull(info)) {
            info = nullFindCollectType(type);
        }
        if (Objects.isNull(info)) {
            throw new NullPointerException("Not defined " + clz.getName());
        }
        if (clz.isArray()) {
            return ClassInfo.array(arrayClassInfo.getId(), info.getClz(), arrayClassInfo.getSerializer());
        }
        return info;
    }

    private ClassInfo nullFindCollectType(Class<?> clz) {
        // Some protect type. em: ImmutableCollections
        if (List.class.isAssignableFrom(clz)) {
            return classContain.get(List.class);
        }
        if (Set.class.isAssignableFrom(clz)) {
            return classContain.get(Set.class);
        }
        if (Map.class.isAssignableFrom(clz)) {
            return classContain.get(Map.class);
        }
        return null;
    }

    public boolean containType(Class<?> clz) {
        return classContain.containsKey(clz);
    }

    public boolean containId(short id) {
        return definedContain.containsKey(id);
    }

    private void register(ClassInfo classInfo) {
        definedContain.put(classInfo.getId(), classInfo);
        classContain.put(classInfo.getClz(), classInfo);
    }

    private void checkInit() {
        if (init == 0) {
            synchronized (Rookie.class) {
                if (init == 0) {
                    init();
                    init = 1;
                }
            }
        }
    }

    /**
     * Register class information when initialization.
     * <p>Use default id {@link Init#nextId()}
     */
    public void register(Class<?> clz) {
        if (containType(clz)) {
            throw new IllegalArgumentException("Repeat defined class: " + clz);
        }
        undistributedIdClass.add(clz);
    }

    /**
     * Register class info with id
     * <p>Use default serialize
     *
     * @see #register0(short, Class, Serializer)
     */
    public void register(int id, Class<?> clz) {
        register(id, clz, null);
    }

    /**
     * Register class info
     *
     * @param id         consume class id
     * @param clz        class type
     * @param serializer consume serializer
     */
    public void register(int id, Class<?> clz, Serializer<?> serializer) {
        if (id < Short.MIN_VALUE || id > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Id: [" + id + "] is over rookie's limit.");
        }
        if (id >= INNER_TYPE_ID_MIN && id <= INNER_TYPE_ID_MAX) {
            throw new IllegalArgumentException("Cannot register id in [" + INNER_TYPE_ID_MAX + "," + INNER_TYPE_ID_MAX + "].");
        }
        if (clz.isArray()) {
            throw new IllegalArgumentException("Don't register array type!");
        }
        register0((short) id, clz, serializer);
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
        private short starIndex = INNER_TYPE_ID_MIN;

        private void initRegister() {
            initSpecialType();

            registerNext(Void.TYPE, new EmptySerializer());
            registerNext(Boolean.class, new BooleanSerializer());
            registerNext(Boolean.TYPE, new BooleanSerializer());
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
            registerNext(Date.class, new DateSerializer());
            registerNext(Calendar.class, new CalendarSerializer());

            initCollection();
            initMap();
            initQueue();
            initSet();
            initDequeue();
            initUndistributed();
        }

        private void initSpecialType() {
            register(ARRAY_SERIALIZER_ID, ArraySerializer.class, new ArraySerializer<>());
            arrayClassInfo = definedContain.get(ARRAY_SERIALIZER_ID);
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

        /**
         * @see #register(Class)
         */
        private void initUndistributed() {
            short id = 0;
            for (Class<?> clz : undistributedIdClass) {
                id = id == 0 ? nextId() : nextId(id + 1);
                register(id, clz, null);
            }
            undistributedIdClass = null;
        }

        private short nextId() {
            int value = definedContain.size();
            //  count: Short.MIN ~ Short.MAX-1
            if (value == Integer.MAX_VALUE - 1) {
                throw new IllegalArgumentException("Get next id error, case of class info contain is full.");
            }
            return nextId(value);
        }

        private short nextId(int value) {

            int start = value <= INNER_TYPE_ID_MAX ? INNER_TYPE_ID_MAX + 1 : value;

            for (int next = start; ; next++) {
                if (next > Short.MAX_VALUE) {
                    next = Short.MIN_VALUE;
                }
                short in = (short) next;
                if (!containId(in)) {
                    return in;
                }
            }
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
            for (ClassInfo value : Rookie.this.definedContain.values()) {
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
