package com.nsg.boardingfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.nsg.boardingfinder.Utils.Navigation;
import com.nsg.boardingfinder.fragments.SeekerHomeFragment;
import com.nsg.boardingfinder.fragments.NotificationFragment;
import com.nsg.boardingfinder.fragments.ProfileFragment;
import com.nsg.boardingfinder.fragments.SearchFragment;

public class SeekerActivity extends AppCompatActivity {

    MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker);

        bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.home));
      //  bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.ic_notifications));
      //  bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.ic_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.ic_user_account));

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                /**
                 * Do NOT load fragment here..
                 */
            }
        });

        bottomNavigation.setCount(2,"10"); //set 10 notifications
        bottomNavigation.show(1, true);

        /**
         * Set Initial Fragment
         */
        loadFragment(new SeekerHomeFragment());

        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {

                Fragment fragment = null;

                switch (item.getId()){
                    case 1:
                        fragment = new SeekerHomeFragment();
                        break;
                    case 2:
                        fragment = new NotificationFragment();
                        break;
                    case 3:
                        fragment = new ProfileFragment();
                        break;
                    case 4:
                        fragment = new SearchFragment();
                }
                 loadFragment(fragment);
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> load Fragment (SeekerActivity.java) >>>>>>>>>>>>>>>>>>>>>>>>>");
            }
        });

        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
               /* Toast.makeText(getApplicationContext()
                , "You Reselected " + item.getId()
                        ,Toast.LENGTH_SHORT).show();*/
            }
        });

    }

    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout,fragment)
                .commit();

    }

    @Override
    public void onBackPressed() {
        //TODO: Avoid Go to previouse activity - > comment -> super.onBackPressed();
        //super.onBackPressed();
        switch (Navigation.currentScreen) {

            case "ViewBoardingFragment":
                loadFragment(new SeekerHomeFragment());
                break;
            case "AdminHomeFragment":
                // super.onBackPressed();
                break;
        }
    }
}