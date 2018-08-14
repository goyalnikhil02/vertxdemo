package com.beton.vertx.tutorials;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

public class VertxRestExample {
	
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		HttpServer httpServer = vertx.createHttpServer();
		
		Router router = Router.router(vertx);

		router.get("/hi/:name").handler(routingContext -> {
			String user=routingContext.request().getParam("name");
			HttpServerResponse response = routingContext.response();
			System.out.println("Coming from GET request");
			response.putHeader("content-type", "text/plain");
			response.end("Hi :"+user);

		});
		
		Route handler1=router.post("/hi").
				consumes("*/json").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			response.setChunked(true);
			response.write("Coming from post handler .....");
			System.out.println("Inside handler1");
			response.end();

		});
		httpServer.requestHandler(router::accept).listen(7007);
	}
	

}
