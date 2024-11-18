package com.example.asm_and103;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.example.asm_and103.Adapter.AdapterProduct;
import com.example.asm_and103.Fragment.FragmentCard;
import com.example.asm_and103.Fragment.HomeFragment;
import com.example.asm_and103.Models.ProductModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment fragmentHome = new HomeFragment();
    Fragment fragmentCard = new FragmentCard();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottom_nav01);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frag_container002, fragmentHome)
                .commit();


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fr;
                if(item.getItemId() == R.id.nav_home){
                    fr = fragmentHome;
                }else if( item.getItemId() == R.id.nav_shop){
                    fr = fragmentCard;

                }else{
                    fr = fragmentCard;
                }

                // cập nhật fragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frag_container002, fr).commit();
                return true;
            }
        });




    }
}