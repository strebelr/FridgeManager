package com.cpen321.fridgemanager;

import android.annotation.TargetApi;
import android.content.Context;

import com.cpen321.fridgemanager.Database.DatabaseInteraction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * DatabaseInteractionTest. Mocks Context with Mockitto dependency.
 */

@RunWith(MockitoJUnitRunner.class)
public class DatabaseInteractionTest{
    @Mock
    Context mMockContext;

    // DatabaseInteraction used for test
    DatabaseInteraction di;

    @Before
    public void setUp() throws Exception {
        di = new DatabaseInteraction(mMockContext);
    }

    @After
    public void tearDown() throws Exception {
        di = null;
    }

    @Test
    public void read_test() throws Exception {
        // Reflection
        Method method = DatabaseInteraction.class.getDeclaredMethod("read", InputStreamReader.class);
        method.setAccessible(true);

        String test_string = "Test String"; // String to try to read
        InputStreamReader stub_reader = new InputStreamReader(new ByteArrayInputStream(test_string.getBytes())); // Stub input stream reader

        // Assert returned string equals input stream
        assertEquals("Fail", test_string, (String) method.invoke(di, stub_reader));
    }

    @Test
    public void write_test() throws Exception {
        // Reflection
        Method method = DatabaseInteraction.class.getDeclaredMethod("write", OutputStreamWriter.class, String.class);
        method.setAccessible(true);

        String test_string = "Test String"; // String to try to write

        // Stub output stream and output stream writer
        OutputStream stub_stream = new ByteArrayOutputStream();
        OutputStreamWriter stub_writer = new OutputStreamWriter(stub_stream);

        // Invoke
        method.invoke(di, stub_writer, test_string);

        // Assert written stream equals output stream
        assertEquals("Fail", test_string, stub_stream.toString());
    }

    @Test
    public void create_test() throws Exception {
        // Reflection
        Method method = DatabaseInteraction.class.getDeclaredMethod("create", String.class, double.class, int.class, int.class);
        method.setAccessible(true);

        // Expected Variables
        String name = "Pork";
        double quantity = 150.5;
        int unit = DatabaseInteraction.GRAM;
        int expiry = 10;

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        Calendar c = Calendar.getInstance();
        // Expected Bought Date
        String expected_bought = df.format(c.getTime());
        c.add(Calendar.DATE, expiry);
        // Expected Expiry Date
        String expected_expiry = df.format(c.getTime());

        // Invoke
        JSONObject food = (JSONObject) method.invoke(di, name, quantity, unit, expiry);

        // Test
        assertEquals("Fail: name not equal", name, food.optString("name"));
        assertEquals("Fail: quantity not equal", quantity, Double.parseDouble(food.optString("quantity")), 0);
        assertEquals("Fail: unit not equal", unit, Integer.parseInt(food.optString("unit")));
        assertEquals("Fail: bought day not equal", expected_bought, food.optString("bought"));
        assertEquals("Fail: expiry date not equal", expected_expiry, food.optString("expiry"));
    }

    @Test
    public void getCurrentDate_test() throws Exception {
        // Reflection
        Method method = DatabaseInteraction.class.getDeclaredMethod("getCurrentDate");
        method.setAccessible(true);

        // Create a expected String
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String str_day;
        if (day < 10)
            str_day = "0" + day;
        else
            str_day = "" + day;
        int month = cal.get(Calendar.MONTH) + 1;
        String str_month;
        if (month < 10)
            str_month = "0" + month;
        else
            str_month = month + "";

        // Expected Variable
        String exp_date = str_day + "-" + str_month + "-" + cal.get(Calendar.YEAR);

        // Test
        assertEquals("Fail:", exp_date, (String) method.invoke(di));
    }

    @Test
    public void getFutureDate_test() throws Exception {
        // Reflection
        Method method = DatabaseInteraction.class.getDeclaredMethod("getFutureDate", int.class);
        method.setAccessible(true);

        int future = 5; // expected days until future

        // Create a expected String
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, future);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String str_day;
        if (day < 10)
            str_day = "0" + day;
        else
            str_day = "" + day;
        int month = cal.get(Calendar.MONTH) + 1;
        String str_month;
        if (month < 10)
            str_month = "0" + month;
        else
            str_month = month + "";

        // Expected Variable
        String exp_date = str_day + "-" + str_month + "-" + cal.get(Calendar.YEAR);

        // Test
        assertEquals("Fail:", exp_date, (String) method.invoke(di, future));
    }

    @Test
    public void extractArray_test() throws Exception {
        // Reflection
        Method method = DatabaseInteraction.class.getDeclaredMethod("extractArray", String.class, String.class);
        method.setAccessible(true);

        // Expected Values
        String root = "{\"Foods\":[{\"name\":\"Apple\",\"expiry\":\"5\"}, {\"name\":\"Orange\",\"expiry\":\"5\"}], \"Non-Foods\":[{\"name\":\"Bee\"}, {\"name\":\"Chair\"}]}";
        String exp_array = "[{\"name\":\"Apple\",\"expiry\":\"5\"},{\"name\":\"Orange\",\"expiry\":\"5\"}]";

        // Test
        assertEquals("Fail:", exp_array, ((JSONArray) method.invoke(di, root, "Foods")).toString());
    }

    @Test
    public void add_to_root_test() throws Exception {
        // Reflection
        Method method = DatabaseInteraction.class.getDeclaredMethod("add_to_root", JSONObject.class, String.class, String.class);
        method.setAccessible(true);

        // Create a new JSON Object as test case
        JSONObject element = new JSONObject();
        try {
            element.put("name", "Apple");
        } catch (JSONException e) {}

        String root = "{\"Foods\":[{\"name\":\"Orange\"}], \"Non-Foods\":[{\"name\":\"Bee\"}, {\"name\":\"Chair\"}]}";

        // Expected Values
        String exp_root1 = "{\"Non-Foods\":[{\"name\":\"Bee\"},{\"name\":\"Chair\"}],\"Foods\":[{\"name\":\"Orange\"},{\"name\":\"Apple\"}]}";
        String exp_root2 = "{\"Foods\":[{\"name\":\"Apple\"}]}";

        // Test
        assertEquals("Fail: When root exists", exp_root1, (String) method.invoke(di, element, root, "Foods"));
        assertEquals("Fail: When root does not exist", exp_root2, (String) method.invoke(di, element, "", "Foods"));

    }

    @Test
    public void makeRoot_test() throws Exception {
        // Reflection
        Method method = DatabaseInteraction.class.getDeclaredMethod("makeRoot");
        method.setAccessible(true);

        // Expecte Value
        String exp_root = "{\"Pantry\":[],\"Freezer\":[],\"Fresh\":[],\"Fridge\":[]}";

        // Test
        assertEquals("Fail:", exp_root, ((JSONObject) method.invoke(di)).toString());
    }

    @Test
    public void remove_test() throws Exception {
        // Reflection
        Method method = DatabaseInteraction.class.getDeclaredMethod("remove", String.class, String.class, JSONObject.class);
        method.setAccessible(true);

        // Input Test Case
        String root_before = "{\"Foods\":[{\"name\":\"Apple\",\"bought\":\"05-11-2016\",\"quantity\":\"5\"},{\"name\":\"Orange\",\"bought\":\"01-12-2016\",\"quantity\":\"3\"}]}";
        JSONObject element = new JSONObject("{\"name\":\"Orange\",\"bought\":\"01-12-2016\",\"quantity\":\"3\"}");

        // Expected Value
        String exp_root1 = null;
        String exp_root2 = "{\"Foods\":[{\"quantity\":\"5\",\"bought\":\"05-11-2016\",\"name\":\"Apple\"}]}";

        // Test
        assertEquals("Fail: When removing from empty", exp_root1, (JSONObject) method.invoke(di, "", "Foods", element));
        assertEquals("Fail: When removing", exp_root2, ((JSONObject) method.invoke(di, root_before, "Foods", element)).toString());
    }

    @Test
    public void getSortedExpiry_test() throws  Exception {
        // Reflection
        Method method = DatabaseInteraction.class.getDeclaredMethod("getSortedExpiry", String.class, int.class);
        method.setAccessible(true);

        // Input Test Case
        long millisPerDay = (24 * 60 * 60 * 1000);
        Calendar actDate = Calendar.getInstance();

        Calendar spinachDate = Calendar.getInstance();
        spinachDate.setTimeInMillis(actDate.getTimeInMillis()+ millisPerDay * 1);

        Calendar peaDate = Calendar.getInstance();
        peaDate.setTimeInMillis(actDate.getTimeInMillis()+ millisPerDay * 2);

        Calendar zucchiniDate = Calendar.getInstance();
        zucchiniDate.setTimeInMillis(actDate.getTimeInMillis()+ millisPerDay * 3);

        Calendar codDate = Calendar.getInstance();
        codDate.setTimeInMillis(actDate.getTimeInMillis()+ millisPerDay * 4);

        Calendar porkDate = Calendar.getInstance();
        porkDate.setTimeInMillis(actDate.getTimeInMillis()+ millisPerDay * 5);

        String spinach = "{\"name\":\"Spinach\",\"bought\":\"20-11-2016\",\"expiry\":\"" + spinachDate.get(Calendar.DAY_OF_MONTH) + "-" + (spinachDate.get(Calendar.MONTH)+1) + "-" + spinachDate.get(Calendar.YEAR) + "\",\"quantity\":1,\"original_qty\":1,\"unit\":0,\"location\":\"Fridge\"}";
        String pea = "{\"name\":\"pea\",\"bought\":\"30-11-2016\",\"expiry\":\"" + peaDate.get(Calendar.DAY_OF_MONTH) + "-" + (peaDate.get(Calendar.MONTH)+1) + "-" + peaDate.get(Calendar.YEAR) + "\",\"quantity\":1,\"original_qty\":1,\"unit\":0,\"location\":\"Fresh\"}";
        String zucchini = "{\"name\":\"Zucchini\",\"bought\":\"30-11-2016\",\"expiry\":\"" + zucchiniDate.get(Calendar.DAY_OF_MONTH) + "-" + (zucchiniDate.get(Calendar.MONTH)+1) + "-" + zucchiniDate.get(Calendar.YEAR) + "\",\"quantity\":1,\"original_qty\":1,\"unit\":0,\"location\":\"Fridge\"}";
        String cod = "{\"name\":\"Cod\",\"bought\":\"30-11-2016\",\"expiry\":\"" + codDate.get(Calendar.DAY_OF_MONTH) + "-" + (codDate.get(Calendar.MONTH)+1) + "-" + codDate.get(Calendar.YEAR) + "\",\"quantity\":1,\"original_qty\":1,\"unit\":0,\"location\":\"Fridge\"}";
        String pork ="{\"name\":\"Pork\",\"bought\":\"30-11-2016\",\"expiry\":\"" + porkDate.get(Calendar.DAY_OF_MONTH) + "-" + (porkDate.get(Calendar.MONTH)+1) + "-" + porkDate.get(Calendar.YEAR) + "\",\"quantity\":1,\"original_qty\":1,\"unit\":0,\"location\":\"Fridge\"}";
        String root_before = "{\"Fridge\":[" + zucchini + "," + spinach + "," + pork + "," + cod + "],\"Fresh\":[" + pea + "],\"Pantry\":[],\"Freezer\":[]}";

        //Expected Value
        JSONArray expectedSort = new JSONArray();
        expectedSort.put(new JSONObject(spinach));
        expectedSort.put(new JSONObject(pea));
        expectedSort.put(new JSONObject(zucchini));
        expectedSort.put(new JSONObject(cod));


        //Test
        int daysToExpire = 4;
        JSONArray sortedFood = (JSONArray) method.invoke(di, root_before, daysToExpire);
        assertEquals(expectedSort.toString(), sortedFood.toString());
    }

    @Test
    public void daysToExpire_test() throws Exception {
        // Reflection
        Method method = DatabaseInteraction.class.getDeclaredMethod("daysToExpire", JSONObject.class);
        method.setAccessible(true);

        // Input Test Case
        long millisPerDay = (24 * 60 * 60 * 1000);
        long dayDiff = 10;
        Calendar actDate = Calendar.getInstance();
        Calendar spinachDate = Calendar.getInstance();
        spinachDate.setTimeInMillis(actDate.getTimeInMillis()+ millisPerDay * dayDiff);

        String spinach = "{\"name\":\"Spinach\",\"bought\":\"20-11-2016\",\"expiry\":\"" + spinachDate.get(Calendar.DAY_OF_MONTH) + "-" + (spinachDate.get(Calendar.MONTH)+1) + "-" + spinachDate.get(Calendar.YEAR) + "\",\"quantity\":1,\"original_qty\":1,\"unit\":0,\"location\":\"Fridge\"}";

        //Test
        assertEquals(dayDiff,  (int)method.invoke(di, new JSONObject(spinach)));

    }

}