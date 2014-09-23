
package net.wandroid.answer;

import net.wandroid.answer.view.EntryViewListFragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public class EntryViewActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entryview);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_add_entry:
                startActivity(new Intent(this, AddActivity.class));
                return true;
            case R.id.action_remove_expired_entry:
                FragmentManager manager=getFragmentManager();
                EntryViewListFragment fragment= (EntryViewListFragment)manager.findFragmentById(R.id.edit_list_fragment);
                fragment.removeAllExpired();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
