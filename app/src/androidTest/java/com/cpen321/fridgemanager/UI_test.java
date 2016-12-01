package com.cpen321.fridgemanager;



        import android.app.Activity;
        import android.support.test.InstrumentationRegistry;
        import android.support.test.rule.ActivityTestRule;
        import android.support.test.runner.AndroidJUnit4;
        import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;

        import com.cpen321.fridgemanager.Activity.MainMenu;
        import com.cpen321.fridgemanager.Activity.ScanResults;
        import com.cpen321.fridgemanager.OcrReader.OcrCaptureActivity;

        import org.junit.Rule;
        import org.junit.Test;
        import org.junit.runner.RunWith;

        import java.util.Collection;

        import static android.support.test.espresso.Espresso.onView;
        import static android.support.test.espresso.action.ViewActions.click;
        import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
        import static android.support.test.espresso.action.ViewActions.typeText;
        import static android.support.test.espresso.assertion.ViewAssertions.matches;

        import static android.support.test.espresso.matcher.ViewMatchers.withId;
        import static android.support.test.espresso.matcher.ViewMatchers.withText;
        import static android.support.test.runner.lifecycle.Stage.RESUMED;
        import static junit.framework.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class UI_test {


    @Rule
    public ActivityTestRule<MainMenu> mActivityRule =
            new ActivityTestRule<>(MainMenu.class);

    @Test
    public void navigate() {

        // test 1: clicking on the camera floating button, click on the top layout
        // check if these actions lead to scan result page
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.topLayout)).perform(click());
        Activity activity = getActivityInstance();
        boolean b = (activity instanceof ScanResults);
        assertTrue(b);

        // do more

        // test 2: on the scan result page, click "ADD ALL" button
        // check if these actions lead to food stock page
        if (b == true) {
            onView(withId(R.id.buttonAddAll)).perform(click());
            Activity activity2 = getActivityInstance();
            boolean check2 = (activity2 instanceof MainMenu);
            assertTrue(check2);
        }
    }

    // getting the next activity according to the action performed
    public Activity getActivityInstance() {
        final Activity[] activity = new Activity[1];
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable( ) {
            public void run() {
                Activity currentActivity = null;
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()){
                    currentActivity = (Activity) resumedActivities.iterator().next();
                    activity[0] = currentActivity;
                }
            }
        });

        return activity[0];
    }


}