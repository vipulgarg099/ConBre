package com.conbre.vipul.conbre;



import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditFriendsActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);


    }
    @Override
    protected void onResume() {
        super.onResume();
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        EditFriendsFragment list = new EditFriendsFragment();
        fm.beginTransaction().add(android.R.id.content, list).commit();

    }


    public static class EditFriendsFragment extends ListFragment {

        public static final String TAG = EditFriendsActivity.class.getSimpleName();
        protected List<ParseUser> mUsers;
        protected ParseRelation<ParseUser> mFriendsRelation;
        protected ParseUser mCurrentUser;



        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onResume() {
            super.onResume();


            mCurrentUser = ParseUser.getCurrentUser();
            mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIEND_RELATION);
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.orderByAscending(ParseConstants.KEY_USERNAME);
            query.setLimit(1000);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> parseUsers, ParseException e) {
                    if (e == null) {
                        //success
                        mUsers = parseUsers;
                        String[] userNames = new String[mUsers.size()];
                        int i = 0;
                        for (ParseUser user : mUsers) {
                            userNames[i] = user.getUsername();
                            i++;
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                getListView().getContext(),
                                android.R.layout.simple_list_item_checked,
                                userNames);
                        setListAdapter(adapter);
                        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        addFriendCheckMarks();
                    } else {
                        //error
                        Log.e(TAG, e.getMessage());
                        AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                        builder.setMessage(e.getMessage())
                                .setTitle(getString(R.string.query_error_title))
                                .setPositiveButton(android.R.string.ok, null);

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });

        }


        private void addFriendCheckMarks() {
            mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> friends, ParseException e) {
                    if (e == null) {
                        //list returned
                        for (int i = 0; i < mUsers.size(); i++) {
                            ParseUser user = mUsers.get(i);
                            for (ParseUser friend : friends) {
                                if (friend.getObjectId().equals(user.getObjectId())) {
                                    getListView().setItemChecked(i, true);
                                }
                            }
                        }
                    } else {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);
            if (getListView().isItemChecked(position)) {
                mFriendsRelation.add(mUsers.get(position));


            } else {
                //remove
                mFriendsRelation.remove(mUsers.get(position));

            }
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });

        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_edit_friends, menu);
            return true;
        }

}