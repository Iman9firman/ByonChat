package com.byonchat.android.list;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.byonchat.android.ConversationActivity;
import com.byonchat.android.ConversationGroupActivity;
import com.byonchat.android.CreateGroupActivity;
import com.byonchat.android.PickUserActivity;
import com.byonchat.android.R;
import com.byonchat.android.adapter.ContactAdapter;
import com.byonchat.android.provider.Group;
import com.byonchat.android.utils.MediaProcessingUtil;
import com.byonchat.android.utils.RefreshContactService;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;

public class IconListFragment extends MainListFragment {
   Boolean search = false;
    Context ctx;
    private int mPreviousVisibleItem;
    @SuppressLint("ValidFragment")
    public IconListFragment(boolean b, Context context) {
        this.search = b;
        this.ctx = context;
    }
    public IconListFragment() {
        this.search = false;
    }
    @SuppressLint("ValidFragment")
    public IconListFragment(boolean a) {
        this.search = a;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(android.R.layout.list_content, container, false);
        if(search){
            view = inflater.inflate(R.layout.main_list_item, container, false);
           /* inputSearch = (EditText) view.findViewById(R.id.inputSearch);
            Drawable a =  new BitmapDrawable(Resources.getSystem(), FilteringImage.viewAll(ctx, Color.parseColor(new Validations().getInstance(ctx).colorTheme(false)), R.drawable.ic_search));
            inputSearch.setCompoundDrawablesWithIntrinsicBounds(null,null, a,null);
            inputSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    IconListFragment.this.adapter.getFilter().filter(cs);
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                }
            });*/

            final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
            fab.show(true);
           // fab.show(false);
            /*show with animation
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fab.show(true);
                    fab.setShowAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_from_bottom));
                    fab.setHideAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_to_bottom));
                }
            }, 300);
*/
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    share();
                }
            });


        }
setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        if(search){
            inflater.inflate(R.menu.menu_main_contact, menu);
            // Retrieves the system search manager service
            final SearchManager searchManager =
                    (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

            // Retrieves the SearchView from the search menu item
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

            // Assign searchable info to SearchView
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getActivity().getComponentName()));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String queryText) {
                    // Nothing needs to happen when the user submits the search string
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    IconListFragment.this.adapter.getFilter().filter(newText);
                    return true;
                }
            });
        }else{
            inflater.inflate(R.menu.menu_main, menu);
            // Retrieves the system search manager service
            final SearchManager searchManager =
                    (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

            // Retrieves the SearchView from the search menu item
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

            // Assign searchable info to SearchView
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getActivity().getComponentName()));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String queryText) {
                    // Nothing needs to happen when the user submits the search string
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    IconListFragment.this.adapter.getFilter().filter(newText);
                    return true;
                }
            });
        }

       /* final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getActivity().getApplicationContext().getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings_c:
                Intent i2 = new Intent(getActivity().getApplicationContext(), CreateGroupActivity.class);
                startActivity(i2);
                return true;
            case R.id.action_settings_d:
                Intent i = new Intent(getActivity().getApplicationContext(), PickUserActivity.class);
                i.putExtra(PickUserActivity.FROMACTIVITY,"Message Broadcast");
                startActivity(i);
                return true;
            case R.id.action_settings_r:
                getContext().startService(new Intent(getContext(), RefreshContactService.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        IconItem item = adapter.newList().get(position);

        if(item.getJabberId().equalsIgnoreCase("")){
            share();
        }else {
                Intent intent = new Intent(getActivity(), ConversationActivity.class);
                String jabberId = item.getJabberId();
                if (item.getChatParty() != null) {
                    jabberId = item.getChatParty().getJabberId();
                    if (item.getChatParty() instanceof Group) {
                        Group g = (Group) item.getChatParty();
                        intent = new Intent(getActivity(), ConversationGroupActivity.class);
                        intent.putExtra(ConversationGroupActivity.EXTRA_KEY_NEW_PERSON, "0");
                        intent.putExtra(ConversationGroupActivity.EXTRA_KEY_STICKY, "0");
                    }
                }
                String action = getActivity().getIntent().getAction();
                if (Intent.ACTION_SEND.equals(action)) {
                    Bundle extras = getActivity().getIntent().getExtras();
                    if (extras.containsKey(Intent.EXTRA_STREAM)) {
                        try {
                            Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
                            String pathToSend = MediaProcessingUtil.getRealPathFromURI(
                                    getActivity().getContentResolver(), uri);
                            intent.putExtra(ConversationActivity.KEY_FILE_TO_SEND,
                                    pathToSend);
                        } catch (Exception e) {
                            Log.e(getClass().getSimpleName(),
                                    "Error getting file from action send: "
                                            + e.getMessage(), e);
                        }
                    }
                }
                intent.putExtra(ConversationActivity.KEY_JABBER_ID, jabberId);
                startActivity(intent);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (items == null) {
            items = new ArrayList<IconItem>();
        }
        if (adapter == null) {
            adapter = new ContactAdapter(getActivity().getApplicationContext(), items);
        }
    }

    public void setEditMode(boolean editMode) {
        for (Iterator iterator = items.iterator(); iterator.hasNext();) {
            IconItem item = (IconItem) iterator.next();
            item.setEditMode(true);
        }
        adapter.notifyDataSetInvalidated();
    }

    public void share(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareSubject = getResources().getString(R.string.share_subject) ;
        String shareTitle = getResources().getString(R.string.share_title) ;
        String shareBody = getResources().getString(R.string.share_body) ;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, shareTitle));
    }


}
