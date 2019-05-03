package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	private ResourcesHolder resourcesHolder;
	private Queue<Future<DeliveryVehicle>> futureQueue;
	private CountDownLatch latch;

	public ResourceService(int count, CountDownLatch latch) {
		super("ResourceService" + count);
		resourcesHolder = ResourcesHolder.getInstance();
		futureQueue = new LinkedList<>();
		this.latch = latch;
	}

	@Override
	protected void initialize() {

		//Subscribing to TickBroadcast to know when to terminate.
		subscribeBroadcast(TickBroadcast.class, message->{
			if(message.getLastTick() == message.getTick()) {
				for(Future<DeliveryVehicle> future : futureQueue){
					future.resolve(null);
				}
				terminate();
			}
		});

		//Subscribe to AcquireVehicleEvent to to provide a Future<DeliveryVehicle>.
		subscribeEvent(AcquireVehicleEvent.class, message -> {
			Future<DeliveryVehicle> futureAcquireVehicle = resourcesHolder.acquireVehicle();
			futureQueue.add(futureAcquireVehicle);
			complete(message, futureAcquireVehicle);
		});

		//Subscribe to ReleaseVehicleEvent to return the sent vehicle to the ResourceHolder.
		subscribeEvent(ReleaseVehicleEvent.class, message -> resourcesHolder.releaseVehicle(message.getVehicle()));

		latch.countDown();
	}
}
