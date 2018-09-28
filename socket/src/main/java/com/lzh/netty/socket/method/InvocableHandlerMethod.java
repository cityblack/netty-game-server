package com.lzh.netty.socket.method;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.lzh.netty.socket.annotation.RequestMapping;
import com.lzh.netty.socket.annotation.ResponseMapping;
import com.lzh.netty.socket.dispatcher.protocol.ProtocolModel;
import com.lzh.netty.socket.protocol.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

@Slf4j
public class InvocableHandlerMethod extends HandlerMethod {

    private final ProtocolModel requestProtoModel;
    private final ProtocolModel responseProtoModel;

    private DataBindHandler dataBindHandler = new DefaultDataBindHandler();
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public InvocableHandlerMethod(Object bean, Method method) {
        super(bean, method);
        this.requestProtoModel = this.initRequestProtocol();
        this.responseProtoModel = this.initResponseProto();
    }

    private final ProtocolModel initRequestProtocol() {
        RequestMapping mapping = getMethod().getAnnotation(RequestMapping.class);
        ProtocolModel model = null;
        if (mapping != null) {
            model = new ProtocolModel();
            int id = mapping.value();
            if (id == -1) { //默认值
                throw new IllegalArgumentException("protocol must be fill");
            }
            model.setValue(id);
            model.setDesc(mapping.desc());
        }
        return model;
    }

    private final ProtocolModel initResponseProto() {
        ResponseMapping response = getMethod().getAnnotation(ResponseMapping.class);
        ProtocolModel model = null;
        if (response != null) {
            model = new ProtocolModel();
            int id = response.value();
            if (id == -1) {
                throw new IllegalArgumentException("response must be fill");
            }
            model.setValue(id);
            model.setDesc(response.desc());
        }
        return model;
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Nullable
    public Object invokeForRequest(Request request) throws Exception {
        Object[] args = this.getMethodArgumentValues(request);

        Object returnValue = this.doInvoke(args);

        return returnValue;
    }

    private Object[] getMethodArgumentValues(Request request) throws Exception {
        MethodParameter[] parameters = this.getMethodParameters();
        Object[] args = new Object[parameters.length];

        for(int i = 0; i < parameters.length; ++i) {
            MethodParameter parameter = parameters[i];
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
            args[i] = this.resolveProvidedArgument(parameter, buildInArguments(request));
            if (args[i] == null) {
                RequestParseSupport support = new RequestParseSupport(request);
                args[i] = support.getArgumentValue(parameter,dataBindHandler);
            } else if (args[i] == null) {
                throw new IllegalStateException("Could not resolve method parameter at index " + parameter.getParameterIndex() + " in " + parameter.getExecutable().toGenericString() + ": " + this.getArgumentResolutionErrorMessage("No suitable resolver for", i));
            }
        }

        return args;
    }

    private String getArgumentResolutionErrorMessage(String text, int index) {
        Class<?> paramType = this.getMethodParameters()[index].getParameterType();
        return text + " argument " + index + " of type '" + paramType.getName() + "'";
    }

    @Nullable
    private Object resolveProvidedArgument(MethodParameter parameter, @Nullable Object... providedArgs) {
        if (providedArgs == null) {
            return null;
        } else {
            Object[] var3 = providedArgs;
            int var4 = providedArgs.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Object providedArg = var3[var5];
                if (parameter.getParameterType().isInstance(providedArg)) {
                    return providedArg;
                }
            }

            return null;
        }
    }
    private Object[] buildInArguments(Request request) {

        return new Object[] { request,request.getSession() };
    }

    protected Object doInvoke(Object... args) throws Exception {
        ReflectionUtils.makeAccessible(this.getBridgedMethod());

        try {
            return this.getBridgedMethod().invoke(this.getBean(), args);
        } catch (IllegalArgumentException var5) {
            this.assertTargetBean(this.getBridgedMethod(), this.getBean(), args);
            String text = var5.getMessage() != null ? var5.getMessage() : "Illegal argument";
            throw new IllegalStateException(this.getInvocationErrorMessage(text, args), var5);
        } catch (InvocationTargetException var6) {
            Throwable targetException = var6.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw (RuntimeException)targetException;
            } else if (targetException instanceof Error) {
                throw (Error)targetException;
            } else if (targetException instanceof Exception) {
                throw (Exception)targetException;
            } else {
                String text = this.getInvocationErrorMessage("Failed to invoke handler method", args);
                throw new IllegalStateException(text, targetException);
            }
        }
    }

    private void assertTargetBean(Method method, Object targetBean, Object[] args) {
        Class<?> methodDeclaringClass = method.getDeclaringClass();
        Class<?> targetBeanClass = targetBean.getClass();
        if (!methodDeclaringClass.isAssignableFrom(targetBeanClass)) {
            String text = "The mapped handler method class '" + methodDeclaringClass.getName() + "' is not an instance of the actual controller bean class '" + targetBeanClass.getName() + "'. If the controller requires proxying (e.g. due to @Transactional), please use class-based proxying.";
            throw new IllegalStateException(this.getInvocationErrorMessage(text, args));
        }
    }

    private String getInvocationErrorMessage(String text, Object[] resolvedArgs) {
        StringBuilder sb = new StringBuilder(this.getDetailedErrorMessage(text));
        sb.append("Resolved arguments: \n");

        for(int i = 0; i < resolvedArgs.length; ++i) {
            sb.append("[").append(i).append("] ");
            if (resolvedArgs[i] == null) {
                sb.append("[null] \n");
            } else {
                sb.append("[type=").append(resolvedArgs[i].getClass().getName()).append("] ");
                sb.append("[value=").append(resolvedArgs[i]).append("]\n");
            }
        }

        return sb.toString();
    }

    protected String getDetailedErrorMessage(String text) {
        StringBuilder sb = (new StringBuilder(text)).append("\n");
        sb.append("HandlerMethod details: \n");
        sb.append("Controller [").append(this.getBeanType().getName()).append("]\n");
        sb.append("Method [").append(this.getBridgedMethod().toGenericString()).append("]\n");
        return sb.toString();
    }

    public ProtocolModel getRequestProtoModel() {
        return requestProtoModel;
    }

    public ProtocolModel getResponseProtoModel() {
        return responseProtoModel;
    }
}
