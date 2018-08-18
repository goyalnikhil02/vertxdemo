package com.beton.vertx.tutorials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * Hello world!
 *
 */
public class ApplicationVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {

		System.out.println("Application verticle is starting...");

		vertx.createHttpServer().requestHandler(r -> {

			r.response().end("<h1> My first vertx running .... on port 7777</h1>");

		}).listen(7777, resp -> {

			if (resp.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail("Http server did not start...");
			}

		});

	}

	@Override
	public void stop() {
		System.out.println("Shutting down application");
	}

	public static void main(String[] args) {
        
		
		//List<Integer> number = Arrays.asList(1,3,41,61,81,101);
		
		Vertx vertx = Vertx.vertx();

		vertx.deployVerticle(new ApplicationVerticle());
		//System.out.println(sumStream(number));
		
		
	}
	private static int sumStream(List<Integer> list) {
		return list.stream().filter(i -> (i%2 == 0)).mapToInt(i -> i).sum();
	}

}
