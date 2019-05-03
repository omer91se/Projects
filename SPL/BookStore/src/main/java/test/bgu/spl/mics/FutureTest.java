package bgu.spl.mics;

        import org.junit.After;
        import org.junit.Before;
        import org.junit.Test;

        import static org.junit.Assert.*;
        import java.util.concurrent.TimeUnit;

class FutureTest {

    Future<Integer> future;

    @Before
    void setUp() throws Exception {
        future = new Future<Integer>();
    }

    @After
    void tearDown() throws Exception {
    }

    @Test
    void testFuture() {
        fail("Not yet implemented");
    }

    @Test
    void testGet() {
        Integer i5 = new Integer(5);
        future.resolve(i5);
        assertEquals(i5, future.get());
    }

    @Test
    void testResolve(){
        assertFalse(future.isDone());
        Integer i5 = new Integer(5);
        future.resolve(i5);
        assertEquals(i5, future.get(0,TimeUnit.SECONDS));
        assertTrue(future.isDone());
    }

    @Test
    void testIsDone() {
        assertFalse(future.isDone());
        future.resolve(5);
        assertTrue(future.isDone());
    }


    @Test
    void testGetLongTimeUnit() {
        future.get(1, TimeUnit.SECONDS);
        assertFalse(future.isDone());
        Integer i5 = new Integer(5);
        future.resolve(i5);
        assertEquals(i5, future.get());
    }

}