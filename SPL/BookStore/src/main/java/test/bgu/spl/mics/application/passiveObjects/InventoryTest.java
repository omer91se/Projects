package bgu.spl.mics.application.passiveObjects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.*;

import static org.junit.Assert.*;

class InventoryTest {
    Inventory inv;

    @Before
    void setUp() throws Exception {
        inv = Inventory.getInstance();
    }

    @After
    void tearDown() throws Exception {

    }

    @Test
    void testGetInstance() {
        Inventory inv1 = Inventory.getInstance();
        Inventory inv2 = Inventory.getInstance();
        assertEquals(inv1, inv2);
    }

    @Test
    void testLoad() {
        assertEquals(-1,inv.checkAvailabiltyAndGetPrice("Winnie the Pooh"));
        BookInventoryInfo info = new BookInventoryInfo("Winnie the Pooh",1,100);
        BookInventoryInfo[] books = {info};
        inv.load(books);
        assertEquals(100,inv.checkAvailabiltyAndGetPrice("Winnie the Pooh"));
    }

    @Test
    void testTake() {
        assertEquals(OrderResult.NOT_IN_STOCK, inv.take("A song of ice and fire"));
        BookInventoryInfo info = new BookInventoryInfo("The Shining",2,200);
        BookInventoryInfo[] books = {info};
        inv.load(books);
        assertEquals(OrderResult.NOT_IN_STOCK, inv.take("Mein Kampf"));
        assertEquals(OrderResult.SUCCESSFULLY_TAKEN, inv.take("The Shining"));
    }

    @Test
    void testCheckAvailabiltyAndGetPrice() {
        assertEquals(-1, inv.checkAvailabiltyAndGetPrice("The man in the high castle"));
        BookInventoryInfo info = new BookInventoryInfo("Thats how to draw Zbang",1,300);
        BookInventoryInfo[] books = {info};
        inv.load(books);
        assertEquals(300, inv.checkAvailabiltyAndGetPrice("Thats how to draw Zbang"));
    }

    @Test
    void testPrintInventoryToFile() {
        BookInventoryInfo info = new BookInventoryInfo("Where is Pluto",1,25);
        BookInventoryInfo[] books = {info};
        inv.load(books);
        inv.printInventoryToFile("AmazingBooks");
        File file = new File("AmazingBooks");

        BufferedReader reader = null;
        try {
            String text;
            boolean foundBook = false;
            reader = new BufferedReader(new FileReader(file));
            while (!foundBook & (text = reader.readLine()) != null)
                if(text.contains("Where is Pluto"))
                    foundBook = true;
        }
        catch (FileNotFoundException e) {
            fail("file was not found");
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
        finally {
            try {
                if(reader!=null)
                    reader.close();
            }
            catch(IOException e){}
        }
    }
}