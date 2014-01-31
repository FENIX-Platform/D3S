package org.fao.fenix.server.services.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fao.fenix.server.tools.RestClient;
import org.jboss.resteasy.util.GenericType;

public abstract class Service extends RestClient {

    //INIT
    private static String basePath = "";

    public static void init(Properties initProperties) {
        basePath = initProperties.getProperty("D3S.baseURL","/");
        if (!basePath.endsWith("/"))
            basePath += '/';
    }

    protected String getBasePath() {
        return basePath;
    }


    protected <T> T getProxy(Class<T> interfaceClassObj) throws ClassNotFoundException {
        String path = this.getClass().getAnnotation(Path.class).value();
        path = basePath + (path.charAt(0)=='/' ? path.substring(1) : path);
        return getProxy(path, interfaceClassObj);
    }


    //STANDARD PROXY CALL
    @SuppressWarnings("unchecked")
	protected synchronized <T> Response defaultCall (HttpServletRequest request, Class<T> returnType, Object ... parameters) {
        try {
        	T result = makeRequest(request, returnType, parameters);
            return (result!=null ? Response.ok(result) : Response.ok(new HashMap<String,Object>())).build();
        } catch (Throwable e) {
/*            return  RestClient.response!=null ?
                    Response.status(RestClient.response.getResponseStatus()).entity(RestClient.response.getEntity(String.class)).build() :
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
*/
            return null;
        }
    }

    private <T> T makeRequest (HttpServletRequest request, Class<T> returnType, Object ... parameters) throws Exception {
    	//Retrieve http method
       	RestClient.HTTPMethod httpMethod = RestClient.HTTPMethod.valueOf(request.getMethod());

    	//Define parameters classes
        Class<?>[] parametersClass = new Class<?>[0];
        if (parameters!=null) {
            parametersClass = new Class<?>[parameters.length+1];
            parametersClass[0] = HttpServletRequest.class;
            for (int i = 0; i < parameters.length; i++)
                parametersClass[i+1] = parameters[i].getClass();
        }

        //Retrieve class and method objects
        StackTraceElement element = Thread.currentThread().getStackTrace()[3];
        Class<?> serviceClassObj = Class.forName(element.getClassName());
        Class<?> classObj = serviceClassObj.getInterfaces()[0];
        Method method = classObj.getMethod(element.getMethodName(), parametersClass);

        //Retrieve path
        Path[] paths = {serviceClassObj.getAnnotation(Path.class),method.getAnnotation(Path.class)};
        String path = getPath(paths);

        //Retrieve path params, query params and body
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object body = null;
        Map<String,Object> pathParams = new HashMap<String,Object>();
        Map<String,Object> queryParams = new HashMap<String,Object>();
        for (int i = 0; i < parameters.length; i++) {
            boolean isBody = true;
            for (Annotation a : parameterAnnotations[i+1])
                if (QueryParam.class.equals(a.annotationType())) {
                    queryParams.put(((QueryParam)a).value(), parameters[i]);
                    isBody = false;
                } else if (PathParam.class.equals(a.annotationType())) {
                    pathParams.put(((PathParam)a).value(), parameters[i]);
                    isBody = false;
                }
            if (isBody)
                body = parameters[i];
        }

        //Execute request
        MediaType sendProtocol = httpMethod==RestClient.HTTPMethod.GET || httpMethod==RestClient.HTTPMethod.DELETE ? MediaType.TEXT_PLAIN_TYPE : MediaType.APPLICATION_JSON_TYPE;
        String[] receiveProtocol = {httpMethod==RestClient.HTTPMethod.GET || httpMethod==RestClient.HTTPMethod.DELETE || httpMethod==RestClient.HTTPMethod.POST ? MediaType.APPLICATION_JSON + "; charset=UTF-8" : MediaType.TEXT_PLAIN};

//        return RestClient.request(httpMethod, path, pathParams, queryParams, body, returnType, sendProtocol, receiveProtocol, null);
        return null;
    }




    //UTILS
    private String getPath(Path[] paths) {
        StringBuilder buffer = new StringBuilder(basePath);
        for (Path path : paths)
            if (path!=null) {
                String p = path.value();
                buffer.append(p.charAt(0)=='/' ? p.substring(1) : p);
                if (p.charAt(p.length()-1)!='/')
                    buffer.append('/');
            }
        return buffer.toString();
    }


}
