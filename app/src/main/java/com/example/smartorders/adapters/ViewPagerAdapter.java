package com.example.smartorders.adapters;

import com.example.smartorders.fragments.EmailAddressFragment;
import com.example.smartorders.fragments.EnterPasswordOrCodeFragment;
import com.example.smartorders.fragments.AcceptTermsFragment;
import com.example.smartorders.fragments.FullNameFragment;
import com.example.smartorders.fragments.PasswordFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter  extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                /*case 0:
                    return new EnterPhoneNumberFragment(); *///ChildFragment1 at position 0
                case 0:
                    return new EnterPasswordOrCodeFragment(); //ChildFragment2 at position 1
                case 1:
                    return new EmailAddressFragment(); //ChildFragment3 at position 2
                case 2:
                    return new PasswordFragment(); //ChildFragment2 at position 1
                case 3:
                    return new FullNameFragment(); //ChildFragment3 at position 2
                case 4:
                    return new AcceptTermsFragment();
            }
            return null; //does not happen
        }

        @Override
        public int getCount() {
            return 5;
        }


}

