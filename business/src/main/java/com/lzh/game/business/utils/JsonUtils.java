package com.lzh.game.business.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.lzh.game.business.exception.SerializationException;

import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Json utils implement by Jackson.
 *
 */
public final class JsonUtils {

    private JsonUtils() {

    }

    private static ObjectMapper mapper = new ObjectMapper();
    private static TypeFactory typeFactory = TypeFactory.defaultInstance();

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setSerializationInclusion(Include.NON_NULL);
    }

    /**
     * Object to json string.
     *
     * @param obj obj
     * @return json string
     */
    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new SerializationException(obj.getClass(), e);
        }
    }

    /**
     * Json string deserialize to Object.
     *
     * @param inputStream json string input stream
     * @param cls         class of object
     * @param <T>         General type
     * @return object
     */
    public static <T> T toObj(InputStream inputStream, Class<T> cls) {
        try {
            return mapper.readValue(inputStream, cls);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * Json string to java Object
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static <T>T toObj(String json, Class<T> cls) {
        try {
            return mapper.readValue(json, cls);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * Register sub type for child class.
     *
     * @param clz  child class
     * @param type type name of child class
     */
    public static void registerSubtype(Class<?> clz, String type) {
        mapper.registerSubtypes(new NamedType(clz, type));
    }

    /**
     * Create a new empty Jackson {@link ObjectNode}.
     *
     * @return {@link ObjectNode}
     */
    public static ObjectNode createEmptyJsonNode() {
        return new ObjectNode(mapper.getNodeFactory());
    }

    /**
     * Create a new empty Jackson {@link ArrayNode}.
     *
     * @return {@link ArrayNode}
     */
    public static ArrayNode createEmptyArrayNode() {
        return new ArrayNode(mapper.getNodeFactory());
    }

    /**
     * Parse object to Jackson {@link JsonNode}.
     *
     * @param obj object
     * @return {@link JsonNode}
     */
    public static JsonNode transferToJsonNode(Object obj) {
        return mapper.valueToTree(obj);
    }

    /**
     * construct java type -> Jackson Java Type.
     *
     * @param type java type
     * @return JavaType {@link JavaType}
     */
    public static JavaType constructJavaType(Type type) {
        return mapper.constructType(type);
    }

    /**
     *  Json string deserialize to Java Array.
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T>T[] toArray(String json, Class<T> clazz) {

        ArrayType type = ArrayType.construct(typeFactory.constructType(clazz), TypeBindings.emptyBindings());
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * Json string deserialize to Java Collection.
     * @param json
     * @param collectionType
     * @param elementType
     * @param <C>
     * @param <E>
     * @return
     */
    public static <C extends Collection<E>, E> C toCollection(String json
            , Class<C> collectionType, Class<E> elementType) {
        try {
            CollectionType e = typeFactory.constructCollectionType(collectionType, elementType);
            return mapper.readValue(json, e);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
}
