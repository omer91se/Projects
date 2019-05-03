package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireBookEvent;
import bgu.spl.mics.application.messages.AvailabilityAndPriceEvent;

import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.CountDownLatch;


/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private Inventory inventory;
	private CountDownLatch latch;

	public InventoryService(int count, CountDownLatch latch) {
		super("InventoryService" + count);
		inventory = Inventory.getInstance();
		this.latch = latch;

	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, message->{
			if(message.getLastTick() == message.getTick()) {
				terminate();
			}
		});

		// Handles the event AvailabilityAndPriceEvent with a relevant callback.
		subscribeEvent(AvailabilityAndPriceEvent.class, message->{
			Integer price = inventory.checkAvailabiltyAndGetPrice(message.getBookName());
			complete(message,price);
		});

		// Handles the event AcquireBookEvent with a relevant callback.
		subscribeEvent(AcquireBookEvent.class, message->{
			OrderResult order = inventory.take(message.getBookName());
			if(order == OrderResult.SUCCESSFULLY_TAKEN)
				complete(message, true);

			else
				complete(message, false);
		});

		latch.countDown();
	}

}
