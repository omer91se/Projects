package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	private Customer customer;
	private CountDownLatch latch;

	public APIService(int count, Customer customer, CountDownLatch latch) {
		super("APIService" + count );
		this.latch = latch;

		if(count<0 | customer == null)
			throw new IllegalArgumentException("customer can not be null and count must be a positive number");

		this.customer = customer;
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(TickBroadcast.class, message -> {
		    List<Pair<String,Integer>> schedule = customer.getOrderSchedule();

            //If the current tick is a scheduled one, order the book (by sending BookOrderEvent).
            List<String> booksToOrder = new LinkedList<>();

            //Collecting books needed to be order on current message tick.
            for(Pair<String,Integer> schedulePair : schedule) {
                if (message.getTick() == schedulePair.getValue()) {
                    booksToOrder.add(schedulePair.getKey());
                }
            }

            List<Future<OrderReceipt>> receiptFutureList = new LinkedList<>();
            //Ordering those books.
            for(String book : booksToOrder){
               Future<OrderReceipt> futureReceipt = sendEvent(new BookOrderEvent(book, customer,message.getTick()));
               receiptFutureList.add(futureReceipt);
            }

            OrderReceipt resolved = null;

            //Getting the receipts from the futures.
            for(Future<OrderReceipt> future : receiptFutureList){
                if(future != null){
                    resolved = future.get();
                    if(resolved != null){
                        customer.addRecipt(resolved);
                    }
                }
            }


            //If this customer done ordering, terminate.
			if(message.getTick() >= customer.getMaxTick()) {
                terminate();
            }
		});
		latch.countDown();
	}
}
