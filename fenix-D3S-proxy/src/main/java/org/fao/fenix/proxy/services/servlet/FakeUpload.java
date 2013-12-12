package org.fao.fenix.proxy.services.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

@WebServlet("/CountrySTAT/upload")
public class FakeUpload extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JsonFactory factory = new JsonFactory();
		factory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false); // all configuration before use
		JsonParser jp = factory.createJsonParser(request.getInputStream());
		
		for (JsonToken token = jp.nextToken(); token!=null; token = jp.nextToken()) {
			System.out.println(token.name()+'|'+token.asString());
		}
		//Utilizzare la classe JacksonParser per prendere meta e dati
	}

}
