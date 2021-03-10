package com.app.triponeer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;


public class MainActivity extends AppCompatActivity {
    MeowBottomNavigation meowBottomNavigation;
    private final static int ID_HISTORY = 1;
    private final static int ID_UPCOMING = 2;
    private final static int ID_PROFILE = 3;
    int previousFragmentNumber = 2;
    private Fragment selectedFragment;
    boolean isClicked;
    boolean isEditClicked = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomBar();

    }

    private void setupBottomBar() {
        meowBottomNavigation = findViewById(R.id.bottom_nav);
        meowBottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.history));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.upcoming));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.profile));
        meowBottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                switch (item.getId()) {
                    case ID_HISTORY:
                        selectedFragment = new History();
                        break;
                    case ID_UPCOMING:
                        selectedFragment = new Upcoming();
                        break;
                    case ID_PROFILE:
                        selectedFragment = new Profile();
                        break;
                }
                if (item.getId() > previousFragmentNumber) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fragment_enter_right_to_left, R.anim.fragment_exit_to_left)
                            .replace(R.id.fragment_container, selectedFragment).commit();
                } else if (item.getId() < previousFragmentNumber) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.fragment_enter_left_to_right, R.anim.fragment_exit_to_right)
                            .replace(R.id.fragment_container, selectedFragment).commit();
                }
                previousFragmentNumber = item.getId();
            }
        });

        meowBottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                if (isClicked && item.getId() == 1) {
                    selectedFragment = new History();

                    if (item.getId() > previousFragmentNumber) {
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.fragment_enter_right_to_left, R.anim.fragment_exit_to_left)
                                .replace(R.id.fragment_container, selectedFragment).commit();

                    } else if (item.getId() < previousFragmentNumber) {
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.fragment_enter_left_to_right, R.anim.fragment_exit_to_right)
                                .replace(R.id.fragment_container, selectedFragment).commit();
                    }
                    previousFragmentNumber = item.getId();
                    isClicked = false;

                } else if (isEditClicked && item.getId() == 3) {
                    selectedFragment = new Profile();
                    Bundle bundle = new Bundle();
                    selectedFragment.setArguments(bundle);
                    if (item.getId() > previousFragmentNumber) {
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.fragment_enter_right_to_left, R.anim.fragment_exit_to_left)
                                .replace(R.id.fragment_container, selectedFragment).commit();
                    } else if (item.getId() < previousFragmentNumber) {
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.fragment_enter_left_to_right, R.anim.fragment_exit_to_right)
                                .replace(R.id.fragment_container, selectedFragment).commit();
                    }
                    previousFragmentNumber = item.getId();
                    isEditClicked = false;
                }
            }
        });
        meowBottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });
        meowBottomNavigation.show(2, true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new Upcoming()).commit();
    }
}