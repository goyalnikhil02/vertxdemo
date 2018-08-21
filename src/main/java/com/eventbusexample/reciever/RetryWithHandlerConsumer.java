package com.eventbusexample.reciever;

import io.vertx.core.AbstractVerticle;

public class RetryWithHandlerConsumer extends AbstractVerticle {

	private int counter = 0;

	@Override
	public void start() throws Exception {
		 System.out.println("RetryWithHandlerConsumer.start");

		vertx.eventBus()

				.consumer("hello.handler.failure.retry", m -> {

					counter = counter + 1;

					if (counter < 3) {

						m.fail(500, "failed to reply, sorry... ¯\\_(⊙︿⊙)_/¯");

					} else {

						counter = 0;

						m.reply("World!!!");

					}

				});

	}

}
