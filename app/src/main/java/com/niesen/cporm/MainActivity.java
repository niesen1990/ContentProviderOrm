package com.niesen.cporm;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.niesen.cpo.lib.model.CPOrm;
import com.niesen.cpo.lib.model.loader.support.CPOrmLoader;
import com.niesen.cpo.lib.model.query.Select;
import com.niesen.cpo.lib.model.util.CPOrmBatchDispatcher;
import com.niesen.cpo.lib.model.util.CPOrmCursor;
import com.niesen.cporm.model.MyCPOrmConfiguration;
import com.niesen.cporm.model.domain.Role;
import com.niesen.cporm.model.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "CPOrm Example";

    private ListView listview;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deleteDatabase(new MyCPOrmConfiguration().getDatabaseName());

        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{"user_name"},
                new int[]{android.R.id.text1},
                0);
        listview = (ListView) findViewById(R.id.listView);
        listview.setAdapter(adapter);

        getSupportLoaderManager().initLoader(1, Bundle.EMPTY, this);

        new PopulateDataTask(this).execute();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Select selectUser = Select.from(User.class);
        Select selectRole = Select.from(Role.class) //Specify the class to select from
                .include("_id") //Restrict the selection to only required columns
                .and() //Add a new criterion
                .greaterThan("_id", 0); //Specify the criterion column, type and value

        selectUser.and().in("role_id", selectRole); //Add role selection as inner query
        selectUser.limit(1000); //Limit the select to 1000 records

        Log.e("Select", "selectRole =" + selectRole.toString());
        Log.e("Select", "selectUser =" + selectUser.toString());


        CPOrmLoader<User> userCPOrmLoader = new CPOrmLoader<>(this, selectUser);//Give the select to the cursor loader to load the data
        userCPOrmLoader.setUpdateThrottle(1000); //Set an update throttle because we will be inserting a lot of data causing frequent changes

        return userCPOrmLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private static class PopulateDataTask extends AsyncTask<Void, Void, Void> {

        private final Context context;

        private long testTime = TimeUnit.SECONDS.toMillis(10);

        public PopulateDataTask(Context context) {
            //Use the app context for example purpose only
            this.context = context.getApplicationContext();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            CPOrm.deleteAll(context, Role.class);
            CPOrm.deleteAll(context, User.class);

            Role role = new Role();
            role.setRoleName("role " + Select.from(Role.class).queryAsCount(context));
            role = role.insertAndReturn(context); //We need the returned object to get the database assigned id

            //Demonstrates cursor begin notified of data source changes
            Log.i(TAG, "Testing single insert performance");
            long time = System.currentTimeMillis();
            int recordCount = 0;

            while ((System.currentTimeMillis() - time) < testTime) {
                User user = new User();
                user.setUserName("user loader " + recordCount);
                user.setGivenName("Loading " + recordCount);
                user.setFamilyName("User");
                user.setRoleId(role.getId());
                user.insert(context);
                recordCount++;
            }

            long testCompleteTime = System.currentTimeMillis();
            Log.i(TAG, "Inserted " + recordCount + " records in " + (testCompleteTime - time) + " seconds");
            Log.i(TAG, "Inserted " + (recordCount / TimeUnit.MILLISECONDS.toSeconds(testTime)) + " records in 1 second");
            Log.i(TAG, "Average insert time " + ((testCompleteTime - time) / recordCount) + " ms");

            //Insert example mobile numbers
            List<String> mobileNumbers = new ArrayList<>();
            mobileNumbers.add("12345");
            mobileNumbers.add("67890");
            User userMobile = new User();
            userMobile.setUserName("user loader " + recordCount);
            userMobile.setGivenName("Loading " + recordCount);
            userMobile.setFamilyName("User");
            userMobile.setRoleId(role.getId());
            userMobile.setMobileNumbers(mobileNumbers);
            userMobile.insert(context);

            int batchSize = 500;
            Log.i(TAG, "Testing batch insert performance, " + batchSize + " record batch size");
            time = System.currentTimeMillis();
            recordCount = 0;

            CPOrmBatchDispatcher<User> dispatcher = new CPOrmBatchDispatcher<User>(context, User.class, batchSize);
            while ((System.currentTimeMillis() - time) < testTime) {

                User user = new User();
                user.setUserName("user loader batch " + (recordCount));
                user.setGivenName("Loading batch " + (recordCount));
                user.setFamilyName("User");
                user.setRoleId(role.getId());
                dispatcher.add(user);
                recordCount++;
            }
            dispatcher.release(true);

            Log.i(TAG, "Inserted " + recordCount + " records in " + (System.currentTimeMillis() - time) + " seconds");
            Log.i(TAG, "Inserted " + (recordCount / TimeUnit.MILLISECONDS.toSeconds(testTime)) + " records in 1 second");

            Log.i(TAG, "Testing single update performance");
            User userToUpdate = Select.from(User.class).first(context);
            recordCount = 0;
            time = System.currentTimeMillis();

            while ((System.currentTimeMillis() - time) < testTime) {

                userToUpdate.setFamilyName("User Updated");
                userToUpdate.update(context);
                recordCount++;
            }
            testCompleteTime = System.currentTimeMillis();

            Log.i(TAG, "Updated " + recordCount + " records in " + (testCompleteTime - time) + " seconds");
            Log.i(TAG, "Updated " + (recordCount / TimeUnit.MILLISECONDS.toSeconds(testTime)) + " records in 1 second");
            Log.i(TAG, "Average update time " + ((testCompleteTime - time) / recordCount) + " ms");

            Log.i(TAG, "Testing read performance (No Cache)");
            time = System.currentTimeMillis();
            CPOrmCursor<User> cursor = Select.from(User.class).limit(1000).queryAsCursor(context);
            Log.i(TAG, "Read cursor returned in " + (System.currentTimeMillis() - time) + "ms");
            recordCount = 0;
            time = System.currentTimeMillis();

            while ((System.currentTimeMillis() - time) < testTime) {

                if (cursor.moveToNext()) {

                    User retrievedUser = cursor.inflate();
                    recordCount++;
                } else {

                    cursor.moveToFirst();
                }
            }
            testCompleteTime = System.currentTimeMillis();
            cursor.close();

            Log.i(TAG, "Read " + recordCount + " records in " + (testCompleteTime - time) + " seconds");
            Log.i(TAG, "Read " + (recordCount / TimeUnit.MILLISECONDS.toSeconds(testTime)) + " records in 1 second");


            Log.i(TAG, "Testing single read performance");
            time = System.currentTimeMillis();
            User firstUser = Select.from(User.class).and().isNotNull("user_name").first(context);
            testCompleteTime = System.currentTimeMillis();

            Log.i(TAG, "Read first user in " + (testCompleteTime - time) + "ms");

            Log.i(TAG, "Testing find by id performance");
            time = System.currentTimeMillis();
            User.findById(context, User.class, firstUser.getId());
            testCompleteTime = System.currentTimeMillis();

            Log.i(TAG, "Find user in " + (testCompleteTime - time) + "ms");

            Log.i(TAG, "Testing find by id performance");
            time = System.currentTimeMillis();
            Role.findById(context, Role.class, firstUser.getRoleId());
            testCompleteTime = System.currentTimeMillis();

            Log.i(TAG, "Find role in " + (testCompleteTime - time) + "ms");

            Log.i(TAG, "Testing read performance (Cache Enabled - Random Read)");
            cursor = Select.from(User.class).limit(1000).queryAsCursor(context);
            cursor.enableCache();
            int cursorCount = cursor.getCount();
            recordCount = 0;
            time = System.currentTimeMillis();

            while ((System.currentTimeMillis() - time) < testTime) {

                int position = (int) (Math.random() * cursorCount);
                if (position == cursorCount)
                    position = 0;

                if (cursor.moveToPosition(position)) {

                    User retrievedUser = cursor.inflate();
                    recordCount++;
                } else {

                    cursor.moveToFirst();
                }
            }
            testCompleteTime = System.currentTimeMillis();
            cursor.close();

            Log.i(TAG, "Read " + recordCount + " records in " + (testCompleteTime - time) + " seconds");
            Log.i(TAG, "Read " + (recordCount / TimeUnit.MILLISECONDS.toSeconds(testTime)) + " records in 1 second");

            Log.i(TAG, "Testing read performance (Cache Enabled - 200 objects - Random Read)");
            cursor = Select.from(User.class).limit(1000).queryAsCursor(context);
            cursor.enableCache(200);
            cursorCount = cursor.getCount();
            recordCount = 0;
            time = System.currentTimeMillis();

            while ((System.currentTimeMillis() - time) < testTime) {

                int position = (int) (Math.random() * cursorCount);
                if (position == cursorCount)
                    position = 0;

                if (cursor.moveToPosition(position)) {

                    User retrievedUser = cursor.inflate();
                    recordCount++;
                } else {

                    cursor.moveToFirst();
                }
            }
            testCompleteTime = System.currentTimeMillis();
            cursor.close();

            Log.i(TAG, "Read " + recordCount + " records in " + (testCompleteTime - time) + " seconds");
            Log.i(TAG, "Read " + (recordCount / TimeUnit.MILLISECONDS.toSeconds(testTime)) + " records in 1 second");

            Log.i(TAG, "Performance tests complete");
            return null;
        }
    }
}
