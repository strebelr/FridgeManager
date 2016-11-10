package com.cpen321.fridgemanager;

import android.content.Context;

import com.cpen321.fridgemanager.Algorithm.Levenshtein;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * DatabaseInteractionTest. Mocks Context with Mockitto dependency.
 */

@RunWith(MockitoJUnitRunner.class)
public class LevenshteinTest {

    @Test
    public void distance_test() throws Exception {
        // Test Cases
        String test1_a = "Orange";
        String test1_b = "O range";
        String test2_a = "Apple";
        String test2_b = "Appple";
        String test3_a = "Grape";
        String test3_b = "Graep";
        String test4_a = "";
        String test4_b = "Gum";
        String test5_a = "Green Onion";
        String test5_b = "Onion";
        String test6_a = "Apple";
        String test6_b = "Orange";

        // Test
        assertEquals("Fail: Test 1", 1, Levenshtein.distance(test1_a,test1_b));
        assertEquals("Fail: Test 2", 1, Levenshtein.distance(test2_a,test2_b));
        assertEquals("Fail: Test 3", 2, Levenshtein.distance(test3_a,test3_b));
        assertEquals("Fail: Test 4", 3, Levenshtein.distance(test4_a,test4_b));
        assertEquals("Fail: Test 5", 6, Levenshtein.distance(test5_a,test5_b));
        assertEquals("Fail: Test 6", 5, Levenshtein.distance(test6_a,test6_b));
    }

}