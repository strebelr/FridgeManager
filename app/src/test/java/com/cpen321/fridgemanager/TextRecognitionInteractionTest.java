package com.cpen321.fridgemanager;

import android.app.ApplicationErrorReport;
import android.content.Context;

import com.cpen321.fridgemanager.Algorithm.TextRecognitionInteraction;

import org.json.JSONArray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * TextRecognitionInteractionTest. Mocks Context with Mockitto dependency.
 */
public class TextRecognitionInteractionTest {
    @Mock
    Context mMockContext;

    // DatabaseInteraction used for test
    TextRecognitionInteraction ti;
    JSONArray array;

    @Before
    public void setUp() throws Exception {
        array = new JSONArray("[{\"name\":\"Apple\",\"expiry\":\"5\"},{\"name\":\"Orange\",\"expiry\":\"5\"}]");
        ti = new TextRecognitionInteraction(mMockContext, array);
    }

    @After
    public void tearDown() throws Exception {
        ti = null;
    }

    @Test
    public void isFood_test() throws Exception {
        // Test cases
        String test1 = "Apple";
        String test2 = "apple";
        String test3 = "app le";
        String test4 = "Orangeeeeeeeeeeeeeeeeeeeee";
        String test5 = "ORANGE";
        String test6 = "O r a           n g e";
        String test7 = "Oraaangae";

        // Test
        assertEquals("Fail: Test 1", "Apple", ti.isFood(test1).optString("name").toString());
        assertEquals("Fail: Test 2", "Apple", ti.isFood(test2).optString("name").toString());
        assertEquals("Fail: Test 3", "Apple", ti.isFood(test3).optString("name").toString());
        assertEquals("Fail: Test 4", "Orange", ti.isFood(test4).optString("name").toString());
        assertEquals("Fail: Test 5", "Orange", ti.isFood(test5).optString("name").toString());
        assertEquals("Fail: Test 6", "Orange", ti.isFood(test6).optString("name").toString());
        assertEquals("Fail: Test 7", "Orange", ti.isFood(test7).optString("name").toString());
    }
}