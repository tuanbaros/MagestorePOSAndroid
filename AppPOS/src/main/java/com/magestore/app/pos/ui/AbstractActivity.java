package com.magestore.app.pos.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.magestore.app.lib.ui.PosUI;
import com.magestore.app.pos.CustomerListActivity;
import com.magestore.app.pos.OrderListActivity;
import com.magestore.app.pos.R;
import com.magestore.app.pos.SalesListActivity;

/**
 * Created by Mike on 12/24/2016.
 * Magestore
 * mike@trueplus.vn
 */

public abstract class AbstractActivity
        extends AppCompatActivity
        implements
            PosUI,
            NavigationView.OnNavigationItemSelectedListener{
    /**
     * Cấu hình lại các control layout
     */
    protected void initControlLayout() {

    }

    /**
     * Tự động điền giá trị cho các trường trong form
     */
    protected void initControlValue() {

    }

    protected void initTask() {

    }

    /**
     * Hiển thị thông báo lỗi
     * @param strMsg
     */
    public void showErrorMsg(String strMsg) {
        new AlertDialog.Builder(this)
                .setMessage(strMsg)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Hiển thị thông báo lỗi
     * @param ex
     */
    public void showErrorMsg(Exception ex) {
        new AlertDialog.Builder(this)
                .setMessage(ex.getLocalizedMessage())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Kiểm tra giá trịc ác control trước khi login
     * @return
     */
    protected boolean validControlValue() {
        return false;
    }

    /**
     * Lưu value trong shared preference
     * @param strKey
     * @param strValue
     */
    @Override
    public void saveSharedValue(String strKey, String strValue) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(strKey, strValue);
        editor.commit();
    }

    @Override
    public String getSharedValue(String strKey, String strDefault) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        return sharedPref.getString(strKey, strDefault);
    }

    public Context getContext() {
        return this;
    }

    @Override
    public void showProgress(boolean show) {

    }

    @Override
    public void showUI(PosUI ui) {

    }

    @Override
    public void close() {
        finish();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
//         Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_customers) {
            Intent intent = new Intent(getContext(), CustomerListActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_sales) {
            Intent intent = new Intent(getContext(), SalesListActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_orders_history) {
            Intent intent = new Intent(getContext(), OrderListActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_onhold_orders) {
            Intent intent = new Intent(getContext(), OrderListActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
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

    protected void initToolbarMenu(Toolbar toolbar) {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.sales_navigation_drawer_open, R.string.sales_navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // lấy menu và đặt các event xử lý menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
}
