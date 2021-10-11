package tn.app.grocerystore.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import tn.app.grocerystore.R;
import tn.app.grocerystore.ui.home.HomeFragment;

public class BottomNavigationFragment extends Fragment {

    MeowBottomNavigation bnv_main;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_navigation, container, false);

        bnv_main = view.findViewById(R.id.bnv_main);
        bnv_main.add(new MeowBottomNavigation.Model(1, R.drawable.ic_menuhome));
        bnv_main.add(new MeowBottomNavigation.Model(2, R.drawable.ic_information));
        bnv_main.add(new MeowBottomNavigation.Model(3, R.drawable.ic_contact));

        bnv_main.show(1, true);
        replace(new HomeFragment());
        bnv_main.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()){
                    case 1:
                        replace(new HomeFragment());
                        break;

                    case 2:
                        replace(new AboutFragment());
                        break;

                    case 3:
                        replace(new ContactFragment());
                        break;
                }
                return null;
            }
        });

        return view;
    }
    private void replace(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }
}