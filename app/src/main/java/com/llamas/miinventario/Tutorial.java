package com.llamas.miinventario;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

public class Tutorial extends FragmentActivity {

    FragmentPagerAdapter tutorialAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        final ViewPager tutorial = (ViewPager) findViewById(R.id.tutorialPager);
        tutorialAdapter = new tutorialAdapter(getSupportFragmentManager());
        tutorial.setAdapter(tutorialAdapter);

        RelativeLayout siguiente = (RelativeLayout) findViewById(R.id.siguiente);
        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tutorial.getCurrentItem() < 3){
                    tutorial.setCurrentItem(tutorial.getCurrentItem() + 1);
                } else {
                    Intent i = new Intent(getApplicationContext(), Inicio.class);
                    startActivity(i);
                    finish();
                }
            }
        });


    }

    public static class tutorialAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 4;

        public tutorialAdapter(FragmentManager fragmentManager) {
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
                    return TutorialFragment.newInstance("Inicio");
                case 1:
                    return TutorialFragment.newInstance("Inventario");
                case 2:
                    return TutorialFragment.newInstance("Pedido");
                case 3:
                    return TutorialFragment.newInstance("Venta");
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

    }

}
