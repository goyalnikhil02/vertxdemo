package com.beton.vertx.tutorials;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class VertXTest {
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		HttpServer httpServer = vertx.createHttpServer();
		
		Router router = Router.router(vertx);

		router.route("/all").handler(routingContext -> {

			HttpServerResponse response = routingContext.response();
			response.putHeader("content-type", "text/plain");
			response.end("I am done");

		});
		

		
		//filter chaining example
		Route handler1=router.route("/hi").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			response.setChunked(true);
			response.write("SENDING DATA .....");
			System.out.println("Inside handler1");
			routingContext.vertx().setTimer(15000, tid -> routingContext.next());

		});
		
		Route handler2=router.route("/hi").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			response.write("SENDING DATA from handler2.....");
			System.out.println("Inside handler2");
			routingContext.vertx().setTimer(15000, tid -> routingContext.next());

		});

		Route handler3=router.route("/hi").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			System.out.println("Inside handler3");
			response.write("SENDING DATA from handler3.....");
			response.end("ENd my handler 3");
			
		});
		httpServer.requestHandler(router::accept).listen(7007);

	}

}
