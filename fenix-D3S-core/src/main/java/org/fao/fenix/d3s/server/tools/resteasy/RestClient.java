package org.fao.fenix.d3s.server.tools.resteasy;

import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

public class RestClient {
	
	public static enum HTTPMethod {GET,POST,PUT,DELETE}
	
	public static void delete(String baseURL, Map<String,Object> pathParams, D3SIdentity identity) throws Exception {
		request(HTTPMethod.DELETE, baseURL, pathParams, null, null, null, null, null, identity!=null ? identity.hasMap() : null);
	}
	
	//Main request method
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T request(HTTPMethod httpMethod, String baseURL, Map<String,Object> pathParams, Map<String,Object> queryParams, Object body, Class<T> returnType, MediaType sendProtocol, MediaType[] receiveProtocols, Map<String,Object> headerParams) throws Exception {
		assert baseURL!=null && httpMethod!=null;
		
		ClientRequest request = new ClientRequest(baseURL);
		if(pathParams!=null)
			for (Map.Entry<String,Object> pathParam : pathParams.entrySet())
				request.pathParameter(pathParam.getKey(), pathParam.getValue());
		if(queryParams!=null)
			for (Map.Entry<String,Object> queryParam : queryParams.entrySet())
				request.queryParameter(queryParam.getKey(), queryParam.getValue());
		if (receiveProtocols!=null)
			for (MediaType protocol : receiveProtocols)
				request.accept(protocol);
		if (sendProtocol!=null)
			request.body(sendProtocol, body);
		if (headerParams!=null)
			for (Map.Entry<String,Object> headerParam : headerParams.entrySet())
				request.header(headerParam.getKey(), headerParam.getValue());

		ClientResponse response = null;
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

}
