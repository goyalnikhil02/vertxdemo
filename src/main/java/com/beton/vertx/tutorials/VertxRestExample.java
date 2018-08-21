package com.beton.vertx.tutorials;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.sync.Sync;
import io.vertx.ext.sync.SyncVerticle;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.core.AsyncResult;

//https://github.com/cescoffier/my-vertx-first-app/blob/post-6/src/main/java/io/vertx/blog/first/MyFirstVerticle.java
//https://vertx.io/blog/combine-vert-x-and-mongo-to-build-a-giant/
//http://www.smartjava.org/content/create-simpe-restful-service-vertx-20-rxjava-and-mongodb
//https://github.com/cescoffier/my-vertx-first-app/blob/post-6/src/main/java/io/vertx/blog/first/MyFirstVerticle.java
//https://dzone.com/articles/vertx-and-fiber-perfect-synergy

public class VertxRestExample extends SyncVerticle {
	private static final Logger logger = LoggerFactory.getLogger(VertxRestExample.class);
	private static final String COLLECTION_NAME = "Entities";
	private MongoClient mongoClient;

	public static final String COLLECTION = "pets";

	@Override
	@Suspendable
	public void start(Future<Void> startFuture) throws Exception {

		super.start(startFuture);

		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);
		
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedHeader("Access-Control-Request-Method")
                .allowedHeader("Access-Control-Allow-Credentials")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Headers")
                .allowedHeader("Content-Type"));	
		
     // enable BodyHandler globally for easiness of body accessing
        router.route().handler(BodyHandler.create()).failureHandler(ErrorHandler.create());
		router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create());
        
        
		router.get("/hi/:name").handler(routingContext -> {
			String user = routingContext.request().getParam("name");
			HttpServerResponse response = routingContext.response();
			System.out.println("Coming from GET request");
			response.putHeader("content-type", "text/plain");
			response.end("Hi :" + user);

		});

		router.route(HttpMethod.GET, "/api/pets").handler(Sync.fiberHandler(this::getAll));
		 router.route(HttpMethod.POST, 
			        "/api/pets").handler(Sync.fiberHandler(this::saveNewEntity));
		 
		Route handler1 = router.post("/hi").consumes("*/json").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			response.setChunked(true);
			response.write("Coming from post handler .....");
			System.out.println("Inside handler1");
			response.end();

		});
		server.requestHandler(router::accept).listen(7007);
		
		mongoClient = MongoClient.createShared(vertx,
				new JsonObject().put("connection_string", "mongodb://127.0.0.1:27017/rest_tutorial"));

		JsonObject query = new JsonObject();
		mongoClient.find("pets", query, res -> {
			if (res.succeeded()) {
				for (JsonObject json : res.result()) {
					System.out.println(json.encodePrettily());
				}
			} else {
				res.cause().printStackTrace();
			}
		});
	}

	@Suspendable
	private void getAll(RoutingContext routingContext) {

		final List<JsonObject> entities = Sync.awaitResult(h -> mongoClient.find(COLLECTION, new JsonObject(), h));
		System.out.println("#########################" + entities);
		routingContext.response().end(Json.encodePrettily(entities));

		/*
		 * mongoClient.find(COLLECTION, new JsonObject(), results -> { List<JsonObject>
		 * objects = results.result(); System.out.println(objects.size()); List<Pets>
		 * whiskies = objects.stream().map(Pets::new).collect(Collectors.toList());
		 * logger.info("Response::::"+(Json.encodePrettily(whiskies)));
		 * routingContext.response() .putHeader("content-type",
		 * "application/json; charset=utf-8") .end(Json.encodePrettily(whiskies));
		 * 
		 * });
		 */
	}

	@Suspendable
	private void saveNewEntity(RoutingContext routingContext) {
	
		final JsonObject entry = routingContext.getBodyAsJson();
		
		final Pets pet = Json.decodeValue(routingContext.getBodyAsString(),
		        Pets.class);
		
		pet.set_id(ObjectId.get());
		
		
		 
	final String response = Sync
			.awaitResult(h -> mongoClient.save(COLLECTION, JsonObject.mapFrom(pet), h));
	
	routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(entry.encode());
	
		/*mongoClient.save(COLLECTION, JsonObject.mapFrom(pet), res -> {
	        if (res.succeeded()) {
	            logger.info("Account created: {}", res.result());
	            //account.setId(res.result());
	            //resultHandler.handle(Future.succeededFuture(account));
	        } else {
	            logger.error("Account not created", res.cause());
	            //resultHandler.handle(Future.failedFuture(res.cause()));
	        }
	    });
	    //return this;
*/		
		
		//routingContext.response().end(response);
	}

}
