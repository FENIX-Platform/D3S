package org.fao.fenix.d3s.server.services.rest;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

@Provider
@ServerInterceptor
public class CrossDomainInterceptor implements PreProcessInterceptor, MessageBodyWriterInterceptor {
    private static final String ALL_ORIGINS = "*";
    private static final String ALL_HTTP_METHODS = "OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT";
    private static final String ALL_HTTP_REQUEST_HEADERS = "Accept, Accept-Charset, Accept-Encoding, Accept-Language, Authorization, Expect, From, Host, If-Match, If-Modified-Since, If-None-Match, If-Range, If-Unmodified-Since, Max-Forwards, Proxy-Authorization, Range, Referer, TE, User-Agent";
    
    public static void init(Properties initProperties) {
    	DEFAULT_ALLOWED_ORIGINS = initProperties.getProperty("cors.origins");
    	DEFAULT_ALLOWED_METHODS = initProperties.getProperty("cors.methods");
    	DEFAULT_ALLOWED_HEADERS = initProperties.getProperty("cors.headers");
    	if (DEFAULT_ALLOWED_ORIGINS==null)
    		DEFAULT_ALLOWED_ORIGINS = ALL_ORIGINS;
    	if (DEFAULT_ALLOWED_METHODS==null)
    		DEFAULT_ALLOWED_METHODS = ALL_HTTP_METHODS;
    	if (DEFAULT_ALLOWED_HEADERS==null)
    		DEFAULT_ALLOWED_HEADERS = ALL_HTTP_REQUEST_HEADERS;
    }

    private static String DEFAULT_ALLOWED_ORIGINS;
    private static String DEFAULT_ALLOWED_METHODS;
    private static String DEFAULT_ALLOWED_HEADERS;
    private static final String DEFAULT_AGE = "60"; //1 minute

    private static final String ORIGIN = "Origin";
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

    private static final ThreadLocal<String> REQUEST_ORIGIN = new ThreadLocal<String>();

    private volatile boolean enabled;
    private final Set<String> allowedOrigins;
    private final String accessControlAllowMethods;
    private final String accessControlAllowHeaders;
    private final String accessControlMaxAge;

    public CrossDomainInterceptor() {
    	this(DEFAULT_ALLOWED_ORIGINS, DEFAULT_ALLOWED_METHODS, DEFAULT_ALLOWED_HEADERS, DEFAULT_AGE);
    }
    public CrossDomainInterceptor(String accessControlAllowOrigins, String accessControlAllowMethods, String accessControlAllowHeaders, String accessControlMaxAge) {
        this.allowedOrigins = extractAllowedOrigins(accessControlAllowOrigins);
        this.accessControlAllowMethods = accessControlAllowMethods;
        this.accessControlAllowHeaders = accessControlAllowHeaders;
        this.accessControlMaxAge = accessControlMaxAge;
    }


    @Override
    public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException {
        if (enabled) {
            boolean allowsAll = allowedOrigins.isEmpty();
            if (allowsAll || allowedOrigins.contains(REQUEST_ORIGIN.get())) {
                context.getHeaders().add(ACCESS_CONTROL_ALLOW_ORIGIN, allowsAll ? "*" : REQUEST_ORIGIN.get());
                context.getHeaders().add(ACCESS_CONTROL_ALLOW_METHODS, accessControlAllowMethods);
                context.getHeaders().add(ACCESS_CONTROL_ALLOW_HEADERS, accessControlAllowHeaders);
                context.getHeaders().add(ACCESS_CONTROL_MAX_AGE, accessControlMaxAge);
            }
        }
        context.proceed();
    }

    //Utils
    private Set<String> extractAllowedOrigins(String accessControlAllowOrigins) {
        HashSet<String> result = new HashSet<String>(Arrays.asList(accessControlAllowOrigins.split(",")));
        if (result.isEmpty() || result.contains("!")) {
            enabled = false;
            return new HashSet<String>();
        } else {
            enabled = true;
            if (result.contains("*")) {
                return new HashSet<String>();
            } else {
                return result;
            }
        }
    }

    @Override
    public ServerResponse preProcess(HttpRequest httpRequest, ResourceMethodInvoker resourceMethodInvoker) throws Failure, WebApplicationException {
        if (enabled) {
            REQUEST_ORIGIN.set("" + httpRequest.getHttpHeaders().getRequestHeaders().getFirst(ORIGIN));
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
