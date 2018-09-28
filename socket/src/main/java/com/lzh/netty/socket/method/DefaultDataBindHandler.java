package com.lzh.netty.socket.method;

import com.lzh.netty.socket.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DefaultDataBindHandler implements DataBindHandler, DisposableBean {

    private Map<MethodParameter,ConventType> conventTypeMap = new ConcurrentHashMap<>();

    @Override
    public Object conventData(String value, MethodParameter parameter) {
        ConventType convent = conventTypeMap.get(parameter);
        if (null == convent) {
            convent = ConventType.getConventType(parameter.getParameterType());
            if (null == convent) {
                if (log.isDebugEnabled())
                    log.debug("convent fail. case not defined the type..");
                return null;
            }
            conventTypeMap.put(parameter,convent);
        }
        return convent.conventData(value, parameter);
    }

    @Override
    public void destroy() throws Exception {
        conventTypeMap.clear();
    }

    enum ConventType {
        INT(Integer.class,int.class) {
            @Override
            public Object convent(String value, MethodParameter parameter) {
                return Integer.valueOf(value);
            }
        },
        INT_ARR(int[].class,Integer[].class) {
            @Override
            public Object convent(String value, MethodParameter parameter) {
                return JsonUtil.jsonToList(value,Integer.class)
                        .stream().toArray(Integer[]::new);
            }
        },
        FLOAT(Float.class,float.class) {
            @Override
            public Object convent(String value, MethodParameter parameter) {
                return Float.valueOf(value);
            }
        },
        FLOAT_ARR(Float[].class,float[].class) {
            @Override
            public Object convent(String value, MethodParameter parameter) {
                return JsonUtil.jsonToList(value,Float.class)
                        .stream().toArray(Float[]::new);
            }
        },
        BOOLEAN(Boolean.class,boolean.class) {
            @Override
            public Object convent(String value, MethodParameter parameter) {
                return Boolean.valueOf(value);
            }
        },
        BOOLEAN_ARR(Boolean[].class,boolean[].class) {
            @Override
            public Object convent(String value, MethodParameter parameter) {
                return JsonUtil.jsonToList(value,Boolean.class)
                        .stream().toArray(Boolean[]::new);
            }
        },
        LONG(Long.class,long.class) {
            @Override
            public Object convent(String value, MethodParameter parameter) {
                return Long.valueOf(value);
            }
        },
        LONG_ARR(Long[].class,long[].class) {
            @Override
            public Object convent(String value, MethodParameter parameter) {
                return JsonUtil.jsonToList(value,Long.class)
                        .stream().toArray(Long[]::new);
            }
        },
        STRING(String.class) {
            @Override
            public boolean noNull() {
                return false;
            }
        },
        STING_ARR(String[].class) {
            @Override
            public Object convent(String value, MethodParameter parameter) {
                return JsonUtil.jsonToList(value,String.class)
                        .stream().toArray(String[]::new);
            }

            @Override
            public boolean noNull() {
                return false;
            }
        },
        BIG_ITN(BigInteger.class) {
            @Override
            public Object convent(String value, MethodParameter parameter) {
                return new BigInteger(value);
            }
        },
        BIG_ITN_ARR(BigInteger[].class) {
            @Override
            public Object convent(String value, MethodParameter parameter) {
                return JsonUtil.jsonToList(value,String.class)
                        .stream()
                        .map(BigInteger::new)
                        .toArray(BigInteger[]::new);
            }
        },
        BIG_DECIMAl(BigDecimal.class) {
            @Override
            public Object convent(String value, MethodParameter parameter) {
                return new BigDecimal(value);
            }
        },
        BIG_DECIMAl_ARR(BigDecimal[].class) {
            @Override
            public Object convent(String value, MethodParameter parameter) {
                return JsonUtil.jsonToList(value,String.class)
                        .stream()
                        .map(BigDecimal::new)
                        .toArray(BigDecimal[]::new);
            }
        },
        LIST {
            @Override
            public Object convent(String value, MethodParameter parameter) {
                Type type = parameter.getGenericParameterType();
                if (type == null) {
                    return JsonUtil.jsonToList(value,Object.class);
                } else if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType)type;
                    Type[] ps = parameterizedType.getActualTypeArguments();
                    if (ps.length == 0) {
                        return JsonUtil.jsonToList(value,Object.class);
                    }
                    Class<?> clazz = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    //ParameterizedType
                    return JsonUtil.jsonToList(value, clazz);
                } else if (type instanceof GenericArrayType) {
                    GenericArrayType arrayType = (GenericArrayType)type;
                    return JsonUtil.jsonToList(value,(Class<?>) arrayType.getGenericComponentType());
                }
                return null;
            }
        },
        MAP {
            @Override
            public Object convent(String value, MethodParameter parameter) {
                return JsonUtil.jsonToMap(value);
            }
        },
        CUSTOM {
            @Override
            public Object convent(String value, MethodParameter parameter) {
                return JsonUtil.jsonParse(value,parameter.getParameterType());
            }
        }
        ;
        private List<Class<?>> list;

        ConventType(Class<?> ...type) {
            this.list = Arrays.asList(type);
        }

        public Object conventData(String value, MethodParameter parameter) {
            if (noNull()) {
                if (!StringUtils.hasText(value)) {
                    return null;
                }
            }
            return convent(value,parameter);
        }
        public Object convent(String value, MethodParameter parameter) {
            return value;
        }

        public static ConventType getConventType(Class<?> type) {

            if (Collection.class.isAssignableFrom(type)) {
                return LIST;
            } else if (Map.class.isAssignableFrom(type)) {
                return MAP;
            }
            for (ConventType conventType: ConventType.values()) {
                for (Class clazz: conventType.list) {
                    if (clazz.equals(type) ) {
                        return conventType;
                    }
                }
            }

            return CUSTOM;
        }

        public boolean noNull() {
            return true;
        }
    }
}
