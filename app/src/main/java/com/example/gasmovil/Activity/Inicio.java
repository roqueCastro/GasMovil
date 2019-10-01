package com.example.gasmovil.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.gasmovil.Adapter.SeccionesAdapter;
import com.example.gasmovil.Fragments.InicioFragment;
import com.example.gasmovil.Fragments.MovimientoFragment;
import com.example.gasmovil.Fragments.ProgramacionFragment;
import com.example.gasmovil.R;
import com.example.gasmovil.Utilidades.Utilidades_Clases;

public class Inicio extends AppCompatActivity {

    private SeccionesAdapter seccionesAdapter;
    private ViewPager viewPager;
    private  Toolbar toolbar;

    private  TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        seccionesAdapter = new SeccionesAdapter(getSupportFragmentManager());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);




        if(Utilidades_Clases.rotacion==0){
            if(tabLayout==null){
                tabLayout = (TabLayout) findViewById(R.id.tabs);
                viewPager = (ViewPager) findViewById(R.id.idViewPager);
                llenarViewPager(viewPager);

                viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }
                });

                tabLayout.setupWithViewPager(viewPager);
            }
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }else {
            Utilidades_Clases.rotacion=1;
        }






    }

    private void llenarViewPager(ViewPager viewPager) {
        SeccionesAdapter adapter= new SeccionesAdapter(getSupportFragmentManager());
        adapter.addFragment(new InicioFragment(),"Inicio");
        adapter.addFragment(new MovimientoFragment(),"Movimiento");
        adapter.addFragment(new ProgramacionFragment(), "Programacion");

        viewPager.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inicio, menu);

        Drawable yourdrawable = menu.getItem(0).getIcon();
        yourdrawable.mutate();
        yourdrawable.setColorFilter(getResources().getColor(R.color.colorLight), PorterDuff.Mode.SRC_IN);

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.idMenuSalir:
                cerrarApp();
                // startActivity(getIntent());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cerrarApp() {
      finish();

    }

    @Override
    public void onBackPressed() {

    }
}
