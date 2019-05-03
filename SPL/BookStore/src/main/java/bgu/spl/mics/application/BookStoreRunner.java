package bgu.spl.mics.application;


import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.util.Pair;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    private static HashMap<Integer,Customer> customerHashMap;

    public static void main(String[] args) {
        Thread[][] threadArrays = new Thread[6][];
        customerHashMap = new HashMap<>();
        parseAndInit(threadArrays, args[0]);


        for (Thread[] threads : threadArrays)
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {

                }

            }
        try( FileOutputStream customersMapFile = new FileOutputStream(args[1]);
             FileOutputStream moneyRegisterFile = new FileOutputStream(args[4]);
             ObjectOutputStream customersMapOut = new ObjectOutputStream(customersMapFile);
             ObjectOutputStream moneyRegisterOut = new ObjectOutputStream(moneyRegisterFile)){

            customersMapOut.writeObject(customerHashMap);
            moneyRegisterOut.writeObject(MoneyRegister.getInstance());

        }catch (IOException e){}
        Inventory.getInstance().printInventoryToFile(args[2]);
        MoneyRegister.getInstance().printOrderReceipts(args[3]);


    }

    /**
     *
     * Parse the json file and initialize:
     * Inventory,
     * ResourcesHolder,
     * and threads for each Service.
     *
     * <p>
     *
     * @param threadArrays hold the thread arrays for each type of service.
     * @param path of the json file.
     */
    public static void parseAndInit(Thread[][] threadArrays, String path){
        Gson gson = new Gson();
        File jsonFile = Paths.get(path).toFile();
        Inventory inv = Inventory.getInstance();
        ResourcesHolder holder = ResourcesHolder.getInstance();



        JsonObject jsonObject = null;
        try {
            //Read the whole json file
            jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);

            //Count the services.
            JsonObject servicesJson = jsonObject.get("services").getAsJsonObject();
            int sellingQuant = servicesJson.get("selling").getAsInt();
            int inventoryQuant = servicesJson.get("inventoryService").getAsInt();
            int logisticsQuant = servicesJson.get("logistics").getAsInt();
            int resourceQuant = servicesJson.get("resourcesService").getAsInt();

            JsonArray customersJson = servicesJson.get("customers").getAsJsonArray();
            int customerQuant = customersJson.size();

            int serivcesQuant = sellingQuant + inventoryQuant + logisticsQuant + resourceQuant + customerQuant;

            CountDownLatch latch = new CountDownLatch(serivcesQuant);

            //Get the booksInfo list from the json.
            JsonArray inventoryInitArray = jsonObject.get("initialInventory").getAsJsonArray();
            BookInventoryInfo[] bookInventoryInfoArray = new BookInventoryInfo[inventoryInitArray.size()];

            int i = 0;
            //Iterate over the booksInfo and add them into the BookInventoryInfo array
            for (JsonElement bookInfo : inventoryInitArray) {
                JsonObject book = bookInfo.getAsJsonObject();
                String bookName;
                int amount;
                int price;

                bookName = book.get("bookTitle").getAsString();
                amount = book.get("amount").getAsInt();
                price = book.get("price").getAsInt();

                bookInventoryInfoArray[i] = new BookInventoryInfo(bookName, amount, price);
                i++;
            }
            inv.load(bookInventoryInfoArray);

            JsonArray resourcesInitArray = jsonObject.get("initialResources").getAsJsonArray();


            i = 0;

            JsonArray vehiclesArray = resourcesInitArray.get(0).getAsJsonObject().get("vehicles").getAsJsonArray();
            DeliveryVehicle[] deliveryVehicleArray = new DeliveryVehicle[vehiclesArray.size()];
            //Iterate over the booksInfo and add them into the BookInventoryInfo array
            for (JsonElement vehicleInfo : vehiclesArray) {
                JsonObject vehicle = vehicleInfo.getAsJsonObject();
                int license;
                int speed;

                license = vehicle.get("license").getAsInt();
                speed = vehicle.get("speed").getAsInt();

                deliveryVehicleArray[i] = new DeliveryVehicle(license, speed);
                i++;
            }
            holder.load(deliveryVehicleArray);


            //Init SellingService Threads
            Thread[] sellingThreadsArray = new Thread[sellingQuant];
            Thread sellingThread;
            for (int j = 1; j <= sellingQuant; j++) {
                sellingThread = new Thread(new SellingService(j,latch));
                sellingThreadsArray[j-1] = sellingThread;

                sellingThread.start();
            }
            threadArrays[1] = sellingThreadsArray;

            //Init InventoryService Threads
            Thread[] inventoryThreadsArray = new Thread[inventoryQuant];
            Thread inventoryThread;
            for (int j = 1; j <= inventoryQuant; j++) {
                inventoryThread = new Thread(new InventoryService(j,latch));
                inventoryThreadsArray[j-1] = inventoryThread;
                inventoryThread.start();
            }
            threadArrays[2] = inventoryThreadsArray;

            //Init LogisticsService Threads
            Thread[] logisticThreadsArray = new Thread[logisticsQuant];
            Thread logisticThread;
            for (int j = 1; j <= logisticsQuant; j++) {
                logisticThread = new Thread(new LogisticsService(j,latch));
                logisticThreadsArray[j-1] = logisticThread;
                logisticThread.start();
            }
            threadArrays[3] = logisticThreadsArray;

            //Init ResourceService Threads
            Thread[] resourceThreadsArray = new Thread[resourceQuant];
            Thread resourceThread;
            for (int j = 1; j <= resourceQuant; j++) {
                resourceThread = new Thread(new ResourceService(j,latch));
                resourceThreadsArray[j-1] = resourceThread;
                resourceThread.start();
            }
            threadArrays[4] = resourceThreadsArray;

             //Init APIService Threads


            Customer[] customers = new Customer[customerQuant];

            i=0;
            //Iterate over the customers info from json file, and create an array of customers.
            for (JsonElement customerInfo : customersJson) {
                JsonObject customerObj = customerInfo.getAsJsonObject();
                int id = customerObj.get("id").getAsInt();
                String name = customerObj.get("name").getAsString();
                String address = customerObj.get("address").getAsString();
                int distance = customerObj.get("distance").getAsInt();

                //Create creditCard info Pair.
                int creditNumber = customerObj.get("creditCard").getAsJsonObject().get("number").getAsInt();
                int creditAmount = customerObj.get("creditCard").getAsJsonObject().get("amount").getAsInt();
                Pair<Integer, Integer> creditCard = new Pair(creditNumber, creditAmount);

                //Create orderSchedule list.
                List<Pair<String, Integer>> orderScheduleList = new LinkedList<>();
                Pair<String, Integer> schedulePair;
                String bookTitle;
                int tick;
                JsonArray orderSchedule = customerObj.get("orderSchedule").getAsJsonArray();
                for (JsonElement order : orderSchedule) {
                    JsonObject orderObj = order.getAsJsonObject();
                    bookTitle = orderObj.get("bookTitle").getAsString();
                    tick = orderObj.get("tick").getAsInt();
                    schedulePair = new Pair(bookTitle, tick);
                    orderScheduleList.add(schedulePair);
                }

                    customers[i] = new Customer(id, name, address, distance, creditCard, orderScheduleList);
                    customerHashMap.put(customers[i].getId(),customers[i]);
                    i++;
            }

            //Create an APIService thread for each customer, starts it and put it into APIThreadsArray.
            Thread[] APIThreadsArray = new Thread[customerQuant];
            Thread thread;
            i = 0;
            for(Customer c : customers){
                thread = new Thread(new APIService(i+1,c,latch));
                APIThreadsArray[i] = thread;
                thread.start();
                i++;
            }

            threadArrays[5] = APIThreadsArray;

            //Initializing the TimeService.
            try {
                latch.await();
            }
            catch (InterruptedException ex){}
            int speed = servicesJson.get("time").getAsJsonObject().get("speed").getAsInt();
            int duration = servicesJson.get("time").getAsJsonObject().get("duration").getAsInt();

            Thread[] timeServiceArray = new Thread[1];
            Thread timeThread = new Thread(new TimeService(speed,duration));
            timeServiceArray[0] = timeThread;
            timeThread.start();

            threadArrays[0] = timeServiceArray;

        } catch (FileNotFoundException e) {}
    }

}
