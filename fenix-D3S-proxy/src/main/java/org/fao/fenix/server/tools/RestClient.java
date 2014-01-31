package org.fao.fenix.server.tools;

import java.util.Map;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

public class RestClient {


    public static enum HTTPMethod {GET,POST,PUT,DELETE}


    //Utils methods
    public <T> T get(String baseURL, Map<String,Object> pathParams, Map<String,Object> queryParams, Class<T> returnType, Map<String,Object> headerParams) throws Exception {
        return request(HTTPMethod.GET, baseURL, pathParams, queryParams, null, returnType, null, null, headerParams);
    }
    public void post(String baseURL, Map<String,Object> pathParams, Map<String,Object> queryParams, Object body, MediaType sendProtocol, Map<String,Object> headerParams) throws Exception {
        request(HTTPMethod.POST, baseURL, pathParams, queryParams, body, sendProtocol, null, headerParams);
    }
    public void put(String baseURL, Map<String,Object> pathParams, Map<String,Object> queryParams, Object body, MediaType sendProtocol, Map<String,Object> headerParams) throws Exception {
        request(HTTPMethod.PUT, baseURL, pathParams, queryParams, body, sendProtocol, null, headerParams);
    }
    public void delete(String baseURL, Map<String,Object> pathParams, Map<String,Object> queryParams, Map<String,Object> headerParams) throws Exception {
        request(HTTPMethod.DELETE, baseURL, pathParams, queryParams, null, null, null, headerParams);
    }


    //Standard request methods
    public <T> T request(HTTPMethod httpMethod, String baseURL, Map<String,Object> pathParams, Map<String,Object> queryParams, Object body, Class<T> returnType, MediaType sendProtocol, MediaType[] receiveProtocols, Map<String,Object> headerParams) throws Exception {
        Response response = null;
        try {
            response = request(httpMethod, baseURL, pathParams, queryParams, body, sendProtocol, receiveProtocols, headerParams);
            if (response.getStatus()!=200)
                throw new Exception("Connection error: ("+response.getStatus()+") "+(response.hasEntity()?response.getEntity():""));
            return returnType!=null && response.hasEntity() ? response.readEntity(returnType) : null;
        } finally {
            if (response!=null)
                response.close();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Response request(HTTPMethod httpMethod, String baseURL, Map<String,Object> pathParams, Map<String,Object> queryParams, Object body, MediaType sendProtocol, MediaType[] receiveProtocols, Map<String,Object> headerParams) throws Exception {
        assert baseURL!=null && httpMethod!=null;
        assert body==null || sendProtocol!=null;

        if (pathParams!=null)
            for (Map.Entry<String,Object> param : pathParams.entrySet())
                baseURL = baseURL.replace('{'+param.getKey()+'}', param.getValue().toString());

        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target(baseURL);
        Invocation.Builder requestBuilder = target.request();

        if (queryParams!=null)
            for (Map.Entry<String,Object> param : queryParams.entrySet())
                target.queryParam(param.getKey(), param.getValue());

        if (receiveProtocols!=null)
            requestBuilder.accept(receiveProtocols);

        if (headerParams!=null)
            for (Map.Entry<String,Object> param : headerParams.entrySet())
                requestBuilder.header(param.getKey(), param.getValue());

        return requestBuilder.method(httpMethod.name(), body!=null ? Entity.entity(body, sendProtocol) : null);
    }


    //Proxy methodology
    public <T> T getProxy(String baseURL, Class<T> serviceInterfaceClass) {
        return new ResteasyClientBuilder().build().target(baseURL).proxy(serviceInterfaceClass);
    }

/*
    public static ClientResponse response;
	
	public static enum HTTPMethod {GET,POST,PUT,DELETE}
	
	public static void delete(String baseURL, Map<String,Object> pathParams, D3SIdentity identity) throws Exception {
		request(HTTPMethod.DELETE, baseURL, pathParams, null, null, null, null, new String[0], identity!=null ? identity.hasMap() : null);
	}
	
	//Main request method
	public static <T> T request(HTTPMethod httpMethod, String baseURL, Map<String,Object> pathParams, Map<String,Object> queryParams, Object body, Class<T> returnType, MediaType sendProtocol, MediaType[] receiveProtocols, Map<String,Object> headerParams) throws Exception {
		String[] receiveProtocolsString = null;
		if (receiveProtocols!=null) {
			receiveProtocolsString = new String[receiveProtocols.length];
			for (int i=0; i<receiveProtocols.length; i++)
				receiveProtocolsString[i] = receiveProtocols[i].getType();
		}

		return request(httpMethod, baseURL, pathParams, queryParams, body, returnType, sendProtocol, receiveProtocolsString, headerParams);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T request(HTTPMethod httpMethod, String baseURL, Map<String,Object> pathParams, Map<String,Object> queryParams, Object body, Class<T> returnType, MediaType sendProtocol, String[] receiveProtocols, Map<String,Object> headerParams) throws Exception {
		assert baseURL!=null && httpMethod!=null;
		
		ClientRequest request = new ClientRequest(baseURL);
		if(pathParams!=null)
			for (Map.Entry<String,Object> pathParam : pathParams.entrySet())
				request.pathParameter(pathParam.getKey(), pathParam.getValue());
		if(queryParams!=null)
			for (Map.Entry<String,Object> queryParam : queryParams.entrySet())
				request.queryParameter(queryParam.getKey(), queryParam.getValue());
		if (receiveProtocols!=null)
			for (String protocol : receiveProtocols)
				request.accept(protocol);
		if (sendProtocol!=null && body!=null)
			request.body(sendProtocol, body);
		if (headerParams!=null)
			for (Map.Entry<String,Object> headerParam : headerParams.entrySet())
				request.header(headerParam.getKey(), headerParam.getValue());

		response = null;
		switch (httpMethod) {
			case GET: response = request.get(); break;
			case PUT: response = request.put(); break;
			case POST: response = request.post(); break;
			case DELETE: response = request.delete(); break;
		}
		if (response.getResponseStatus() != Status.OK)
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());

		return returnType!=null ? (T)response.getEntity(returnType) : null;
	}
*/
}
