package com.przyjaznyplanDisplayer.mymodule.appmanager.PlanActions;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.ListView;

import com.przyjaznyplan.models.Activity;
import com.przyjaznyplan.models.Slide;
import com.przyjaznyplan.repositories.ActionRepository;
import com.przyjaznyplan.repositories.DatabaseUtils;
import com.przyjaznyplanDisplayer.mymodule.appmanager.Czynnosci.ActionListView;
import com.przyjaznyplanDisplayer.mymodule.appmanager.R;
import com.przyjaznyplanDisplayer.mymodule.appmanager.TestUtils;
import com.przyjaznyplanDisplayer.mymodule.appmanager.Utils.SlidesAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ActionsListViewTest {

    @Rule
    public ActivityTestRule<ActionListView> activityRule = new ActivityTestRule<>(ActionListView.class, true, false);

    public static final int FIRST = 0;
    public static final int SECOND = 1;

    private Activity activity;
    private final int NUMBER_OF_ACTION = 2;

    @Before
    public void setUp() throws Exception {
        DatabaseUtils.rebuildDatabaseWithInitData();
        activity = TestUtils.createActivityWithActions(NUMBER_OF_ACTION, "ACTIVITY", "ACTION", "/path/to/audio", "path/to/image");
    }

    @Test
    public void showPlanActionListTest(){
        Intent intent = new Intent();
        intent.putExtra("ACTIVITY", activity);
        activityRule.launchActivity(intent);

        ListView listView = (ListView) activityRule.getActivity().findViewById(R.id.actionsListView);

        SlidesAdapter slidesAdapter = (SlidesAdapter) listView.getAdapter();
        assertEquals("Should show only actions for activity", slidesAdapter.getCount(), NUMBER_OF_ACTION);

    }

    @Test
    public void deleteActionTest(){
        Intent intent = new Intent();
        intent.putExtra("ACTIVITY", activity);
        activityRule.launchActivity(intent);

        onView(allOf(withId(R.id.usun), hasSibling(withText("ACTION0")))).perform(click());
        onView(withId(R.id.button3)).perform(click());

        List<Slide> actions = ActionRepository.getActionsByActivityId(activity.getId());

        assertEquals("Action should be deleted",1,actions.size());
    }

    @Test
    public void changeOrderTest(){
        Intent intent = new Intent();
        intent.putExtra("ACTIVITY", activity);
        activityRule.launchActivity(intent);

        onView(allOf(withId(R.id.przesunwdol), hasSibling(withText("ACTION0")))).perform(click());
        onView(withId(R.id.button3)).perform(click());

        List<Slide> actions = ActionRepository.getActionsByActivityId(activity.getId());

        for(Slide action : actions){
            if(action.getText().equals("ACTION0"))
                assertEquals("Action should have changed order", SECOND, action.getPosition());
            else
                assertEquals("Action should have changed order", FIRST, action.getPosition());
        }
    }

    @Test
    public void showEmptyActionList(){

        Intent intent = new Intent();
        activity.setSlides(null);
        intent.putExtra("ACTIVITY", activity);
        activityRule.launchActivity(intent);

        ListView listView = (ListView) activityRule.getActivity().findViewById(R.id.actionsListView);

        SlidesAdapter slidesAdapter = (SlidesAdapter) listView.getAdapter();
        assertEquals("List should be empty",0,slidesAdapter.getCount());

    }

    @After
    public void tearDown() throws Exception {
        DatabaseUtils.rebuildDatabaseWithInitData();
    }
}
