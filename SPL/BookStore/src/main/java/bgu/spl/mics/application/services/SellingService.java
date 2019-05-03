package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;


import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private MoneyRegister moneyRegister;
	private int tick;
	private static AtomicInteger orderId = new AtomicInteger(1);
	private CountDownLatch latch;
	private Queue<Future<OrderReceipt>> futureQueue;

	public SellingService(int count, CountDownLatch latch) {
		super("SellingService" + count);
		moneyRegister = MoneyRegister.getInstance();
		tick = 1;
		this.latch = latch;
		futureQueue = new LinkedList<>();
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, message->{
			if(message.getLastTick() == message.getTick()) {
				terminate();
				return;
			}
			this.tick = message.getTick();
			});


		subscribeEvent(BookOrderEvent.class, message -> {
			Integer price = sendBookAvailabilityAndGetPriceEvent(message);

			//price == -1 means that the book is not available.
			Boolean gotBook = null;
				if(price != null && price != -1) {
					synchronized (message.getCustomer()) {

						//Checks if the customer has enough money to purchase the book and charges him if so.
						if (message.getCustomer().charge(price)) {

							//The book is out of stock, return the money to the customer.
							if (!(gotBook = sendBookAcquireEvent(message))) {
								message.getCustomer().refund(price);
							}
						}
					}
				}

			//Create the receipt if the book was successfully taken.
			OrderReceipt receipt = null;
			if(gotBook != null && gotBook){

				//Sending a DeliveryEvent.
				sendLogisticEvent(message);

				int orderId = this.orderId.incrementAndGet();
				int customerId = message.getCustomer().getId();
				String bookTitle = message.getBookName();

				receipt = new OrderReceipt(orderId,getName(),customerId,bookTitle,price,this.tick,message.getTick(),this.tick);
				moneyRegister.file(receipt);
			}
			complete(message, receipt);
		});

		latch.countDown();
	}

	/**
	 * Checks if the book exists in inventory, if so, return its price.
	 * @param message
	 * @return price of the book. null if the book does not exists in the inventory.
	 */
	private Integer sendBookAvailabilityAndGetPriceEvent(BookOrderEvent message) {
		Integer price = null;
		Future<Integer> bookAvailNPriceFuture = (Future<Integer>) sendEvent(new AvailabilityAndPriceEvent(message.getBookName()));
		if (bookAvailNPriceFuture != null) {
			price = bookAvailNPriceFuture.get();
		}
		return price;
	}

	/**
	 * send a request to take the book. if it is no longer available, return false.
	 * @param message
	 * @return
	 */
	private Boolean sendBookAcquireEvent(BookOrderEvent message) {
		Boolean bookAcquired = false;
		Future<Boolean> bookAcquiredFuture = (Future<Boolean>) sendEvent(new AcquireBookEvent(message.getBookName()));
		if (bookAcquiredFuture != null) {
			bookAcquired = bookAcquiredFuture.get();
		}

		return bookAcquired;
	}

	/**
	 * Send a {@link DeliveryEvent} with the distance of the customer
	 * @param message
	 */
	private void sendLogisticEvent(BookOrderEvent message) {
		sendEvent(new DeliveryEvent(message.getCustomer().getAddress(), message.getCustomer().getDistance()));
	}
}
