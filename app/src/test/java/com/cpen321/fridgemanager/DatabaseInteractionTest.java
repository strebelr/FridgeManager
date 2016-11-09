package com.cpen321.fridgemanager;

import android.content.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 * DatabaseInteractionTest. Mocks Context with Mockitto dependency.
 */

@RunWith(MockitoJUnitRunner.class)
public class DatabaseInteractionTest {

    @Mock
    Context mMockContext;

    @Test
    public void addition_isCorrect() throws Exception {
        DatabaseInteraction di = new DatabaseInteraction(mMockContext);

    }
}