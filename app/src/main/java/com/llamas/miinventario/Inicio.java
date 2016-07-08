package com.llamas.miinventario;

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
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.llamas.miinventario.Model.Categoria;
import com.llamas.miinventario.Model.Group;
import com.llamas.miinventario.Model.Item;
import com.llamas.miinventario.Model.Producto;

import java.util.ArrayList;

public class Inicio extends FragmentActivity {

    FragmentPagerAdapter adapterViewPager;
    private DatabaseReference mDatabase;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    public static ArrayList<Group> groups = new ArrayList<>();
    public static ArrayList<Categoria> categorias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        genarateData();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitleTextAppearance(this, R.style.MyTitleTextApperance);
        //toolbar.setTitle("Inicio");
        initNavigationDrawer();

        Dashboard newFragment = new Dashboard();
        Bundle args = new Bundle();
        args.putInt(Dashboard.ARG_POSITION, 1);
        newFragment.setArguments(args);
        acabarTransision(newFragment);

    }

    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.viewPagerContainer);
                FrameLayout fragment = (FrameLayout) findViewById(R.id.fragment);

                switch (id){
                    case R.id.inicio:
                        fragment.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                        Dashboard dashboard = new Dashboard();
                        Bundle dahsboardArgs = new Bundle();
                        dahsboardArgs.putInt(Dashboard.ARG_POSITION, 1);
                        dashboard.setArguments(dahsboardArgs);
                        acabarTransision(dashboard);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.inventario:
                        fragment.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
                        adapterViewPager = new inventarioAdapter(getSupportFragmentManager());
                        viewPager.setAdapter(adapterViewPager);
                        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
                        tabLayout.setupWithViewPager(viewPager);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.pedidos:
                        fragment.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                        Pedidos pedidos = new Pedidos();
                        Bundle pedidosArgs = new Bundle();
                        pedidosArgs.putInt(Dashboard.ARG_POSITION, 1);
                        pedidos.setArguments(pedidosArgs);
                        acabarTransision(pedidos);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.ventas:
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
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    public void acabarTransision(android.support.v4.app.Fragment newFragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static class inventarioAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = new String[] { "Inventario", "Catálogo"};
        private static int NUM_ITEMS = 2;

        public inventarioAdapter(android.support.v4.app.FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Inventario.newInstance(0, "Inventario");
                case 1:
                    return Catalogo.newInstance(1, "Catálogo");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position] ;
        }

    }

    public void genarateData() {

        mDatabase.child("catalogo").addListenerForSingleValueEvent(

            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Categoria categoriaModel;
                    groups.clear();
                    categorias.clear();

                    for (DataSnapshot categoria : dataSnapshot.getChildren()) {
                        ArrayList<Producto> productos = new ArrayList<>();
                        for (DataSnapshot producto : categoria.getChildren()) {
                            Producto p = producto.getValue(Producto.class);
                            p.setId(Integer.valueOf(producto.getKey()));
                            productos.add(p);
                        }

                        categoriaModel = new Categoria(categoria.getKey(), productos);
                        categorias.add(categoriaModel);

                    }
                    Log.d("TAG", "" + categorias.size());
                    Log.d("TAG", categorias.get(0).getProductos().get(1).getNombre());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("TAG", "getUser:onCancelled", databaseError.toException());
                }

            }

        );

    }

}
