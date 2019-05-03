package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.concurrent.CountDownLatch;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private CountDownLatch latch;

	public LogisticsService(int count, CountDownLatch latch) {
		super("LogisticsService" + count);
		this.latch = latch;
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, message->{
			if(message.getLastTick() == message.getTick()) {
				terminate();
			}
		});


		subscribeEvent(DeliveryEvent.class, message -> {

			//Send an event to ResourceService to acquire a vehicle.
			Future<Future<DeliveryVehicle>> futureObject = sendEvent(new AcquireVehicleEvent());
			if(futureObject != null) {

				Future<DeliveryVehicle> futureVan = futureObject.get();



				//Wait for the future that holds the DeliveryVehicle to be resolved and deliver the Book.
				DeliveryVehicle van = futureVan.get();
				if(van != null) {
					van.deliver(message.getAddress(), message.getDistance());

					//Send an event to ResourceService to release the vehicle.
					sendEvent(new ReleaseVehicleEvent(van));
				}

			}
			complete(message,futureObject);
		});

		latch.countDown();
	}

}
