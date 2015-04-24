package com.kamil.viewapps;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends ListActivity {

    private PackageManager packageManager = null;
    private List<ApplicationInfo> appList = null;
    private AppAdapter appAdapter = null;
    private static Parcelable state;
    String LOG = "log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        packageManager = getPackageManager();
        new LoadAppplications().execute();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ApplicationInfo app = appList.get(position);


        try {
            Intent intent = packageManager.getLaunchIntentForPackage(app.packageName);
            if (intent != null) {
                startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> appList = new ArrayList<ApplicationInfo>();

        for (ApplicationInfo info : list) {
            try {
                if (packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                    appList.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return appList;
    }

    private class LoadAppplications extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            appList = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            Collections.sort(appList, new Comparator<ApplicationInfo>() {
                @Override
                public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
                    return lhs.loadLabel(packageManager).toString().compareTo(rhs.loadLabel(packageManager
                    ).toString());
                }
            });
            appAdapter = new AppAdapter(MainActivity.this, R.layout.item_list, appList);
            Log.i(LOG,"async");
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            setListAdapter(appAdapter);
            Log.i(LOG,"onpost");
            if (state != null)
                getListView().onRestoreInstanceState(state);
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            Log.i(LOG,"onpre");
            super.onPreExecute();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        new LoadAppplications().execute();
        Log.i( LOG,"onresume");
    }


    @Override
    protected void onPause() {
        state = getListView().onSaveInstanceState();
        Log.i(LOG,"onpause");
        super.onPause();
    }

}

