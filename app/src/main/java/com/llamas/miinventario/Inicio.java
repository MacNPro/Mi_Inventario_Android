package com.llamas.miinventario;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llamas.miinventario.CustomClasses.CustomTypefaceSpan;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class Inicio extends FragmentActivity {

    FragmentPagerAdapter adapterViewPager;
    private DatabaseReference mDatabase;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;

    LinearLayout linearLayout;
    FrameLayout fragment;
    ViewPager viewPager;

    boolean menuAbierto = false;
    boolean enDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        adapterViewPager = new inventarioAdapter(getSupportFragmentManager());

        actializarDB();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navView = (NavigationView) findViewById(R.id.navigation_view);
        linearLayout = (LinearLayout) findViewById(R.id.viewPagerContainer);
        fragment = (FrameLayout) findViewById(R.id.fragment);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        initNavigationDrawer();

        Menu m = navView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        obtenerUsuario();

        Dashboard newFragment = new Dashboard();
        iniciarFragmento(newFragment);
        enDashboard = true;

    }

    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id){
                    case R.id.inicio:
                        enDashboard = true;
                        fragment.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                        iniciarFragmento(new Dashboard());
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.inventario:
                        enDashboard = false;
                        fragment.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                        viewPager.setAdapter(adapterViewPager);
                        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
                        tabLayout.setupWithViewPager(viewPager);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.pedidos:
                        enDashboard = false;
                        fragment.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                        iniciarFragmento(new Pedidos());
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.ventas:
                        enDashboard = false;
                        fragment.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Ventas",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();

                }
                return true;
            }
        });

        View header = navigationView.getHeaderView(0);
        TextView nombreTxt = (TextView)header.findViewById(R.id.nombre);
        TextView nivelTxt = (TextView)header.findViewById(R.id.nivel);
        nombreTxt.setText("Daniel Llamas");
        nivelTxt.setText("Director de Tecnologías");
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                menuAbierto = false;
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                menuAbierto = true;
                super.onDrawerOpened(v);
            }

        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    public void obtenerUsuario(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            Uri photoUrl = user.getPhotoUrl();
            String uid = user.getUid();

            View header = navView.getHeaderView(0);
            ImageView avatar = (ImageView) header.findViewById(R.id.avatar);
            Glide.with(this).load(photoUrl).bitmapTransform(new CropCircleTransformation(this)).into(avatar);

            TextView nombre = (TextView) header.findViewById(R.id.nombre);
            nombre.setText(name);

            mDatabase.child("usuarios").child(uid).child("nombre").setValue(name);

        }
    }

    public void iniciarFragmento(Fragment newFragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static class inventarioAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = { "Inventario", "Catálogo"};
        private static int NUM_ITEMS = 2;

        public inventarioAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Inventario();
                case 1:
                    return new Catalogo();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position] ;
        }

    }

    public void actializarDB() {

        mDatabase.child("catalogo").addListenerForSingleValueEvent(

            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("TAG", "getUser:onCancelled", databaseError.toException());
                }

            });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (menuAbierto){
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            if (enDashboard){
                finish();
            } else {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Avenir-Bold.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

}
