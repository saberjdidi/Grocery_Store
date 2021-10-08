package tn.app.grocerystore.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import tn.app.grocerystore.LoginActivity;
import tn.app.grocerystore.R;
import tn.app.grocerystore.adapters.HomeCategoryAdapters;
import tn.app.grocerystore.adapters.PopularAdapters;
import tn.app.grocerystore.adapters.RecommendedAdapter;
import tn.app.grocerystore.adapters.SliderAdapter;
import tn.app.grocerystore.adapters.ViewAllAdapter;
import tn.app.grocerystore.models.Category;
import tn.app.grocerystore.models.PopularModel;
import tn.app.grocerystore.models.ViewAllModel;

public class HomeFragment extends Fragment {

    ScrollView scrollView;
    ProgressBar progressBar;

    FirebaseFirestore db;

    RecyclerView popularRec, homeCatRec, recommemdedRec;
    SliderView sliderView;

    String url1 = "https://www.geeksforgeeks.org/wp-content/uploads/gfg_200X200-1.png";
    String url2 = "https://qphs.fs.quoracdn.net/main-qimg-8e203d34a6a56345f86f1a92570557ba.webp";
    String url3 = "https://bizzbucket.co/wp-content/uploads/2020/08/Life-in-The-Metro-Blog-Title-22.png";

    //Popular items
    List<PopularModel> popularModelList;
    PopularAdapters popularAdapters;
    SliderAdapter sliderAdapter;
    //Home Category
    List<Category> categoryList;
    HomeCategoryAdapters homeCategoryAdapters;
    //Recommended
    List<ViewAllModel> recommendedModelList;
    RecommendedAdapter recommendedAdapter;

    //searchView
    EditText search_box;
    List<ViewAllModel> viewAllModelList;
    RecyclerView recyclerViewSearch;
    ViewAllAdapter viewAllAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        scrollView = root.findViewById(R.id.scrollview);
        progressBar = root.findViewById(R.id.progressbar);
        popularRec = root.findViewById(R.id.pop_rec);
        homeCatRec = root.findViewById(R.id.explore_rec);
        recommemdedRec = root.findViewById(R.id.recommended_rec);
        sliderView = root.findViewById(R.id.slider);

        db = FirebaseFirestore.getInstance();

         progressBar.setVisibility(View.VISIBLE);
         scrollView.setVisibility(View.GONE);

        loadDataProducts();
        loadDataCategories();
        loadDataRecommended();

        search_box = root.findViewById(R.id.search_box);
        recyclerViewSearch = root.findViewById(R.id.search_rec);
        searchData();

        return root;
    }

    private void loadDataProducts(){
        popularModelList = new ArrayList<>();
       /* popularModelList.add(new PopularModel("name1", "description1", "rating", "discount", "type", url1));
        popularModelList.add(new PopularModel("name2", "description1", "rating", "discount", "type", url2));
        popularModelList.add(new PopularModel("name3", "description1", "rating", "discount", "type", url2)); */
        //Popular items

        //recycler view
        popularRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        popularAdapters = new PopularAdapters(getActivity(), popularModelList);
        popularRec.setAdapter(popularAdapters);
        //slider view
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderAdapter = new SliderAdapter(getActivity(), popularModelList);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();

        db.collection("PopularProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PopularModel popularModel = document.toObject(PopularModel.class);
                                popularModelList.add(popularModel);
                                popularAdapters.notifyDataSetChanged();
                                sliderAdapter.notifyDataSetChanged();

                                progressBar.setVisibility(View.GONE);
                                scrollView.setVisibility(View.VISIBLE);
                            }

                        } else {
                            Toast.makeText(getActivity(), "Error "+task.getException(), Toast.LENGTH_SHORT).show();
                            Log.w("TAG", "Error getting documents.", task.getException());
                            progressBar.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
    private void loadDataCategories(){
        //Home Category
        homeCatRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        categoryList = new ArrayList<>();
        homeCategoryAdapters = new HomeCategoryAdapters(getActivity(), categoryList);
        homeCatRec.setAdapter(homeCategoryAdapters);

        db.collection("NavCategory")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Category homeCategory = document.toObject(Category.class);
                                categoryList.add(homeCategory);
                                homeCategoryAdapters.notifyDataSetChanged();

                                progressBar.setVisibility(View.GONE);
                                scrollView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error "+task.getException(), Toast.LENGTH_SHORT).show();
                            Log.w("TAG", "Error getting documents.", task.getException());
                            progressBar.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void loadDataRecommended() {
        //Popular items
        recommemdedRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        recommendedModelList = new ArrayList<>();
        recommendedAdapter = new RecommendedAdapter(getActivity(), recommendedModelList);
        recommemdedRec.setAdapter(recommendedAdapter);

        db.collection("AllProducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ViewAllModel recommendedModel = document.toObject(ViewAllModel.class);
                                recommendedModelList.add(recommendedModel);
                                popularAdapters.notifyDataSetChanged();

                                progressBar.setVisibility(View.GONE);
                                scrollView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error "+task.getException(), Toast.LENGTH_SHORT).show();
                            Log.w("TAG", "Error getting documents.", task.getException());
                            progressBar.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void searchData() {
        viewAllModelList = new ArrayList<>();
        viewAllAdapter = new ViewAllAdapter(getContext(), viewAllModelList);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSearch.setAdapter(viewAllAdapter);
        recyclerViewSearch.setHasFixedSize(true);
        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()){
                    viewAllModelList.clear();
                    viewAllAdapter.notifyDataSetChanged();
                }
                else {
                    searchProduct(editable.toString());
                }
            }
        });
    }

    private void searchProduct(String type) {
        db.collection("AllProducts").whereEqualTo("type", type )
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            viewAllModelList.clear();
                            viewAllAdapter.notifyDataSetChanged();
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                ViewAllModel model = document.toObject(ViewAllModel.class);
                                viewAllModelList.add(model);
                                viewAllAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error "+task.getException(), Toast.LENGTH_SHORT).show();
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); //to show menu option in fragment
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //inflater menu
        inflater.inflate(R.menu.main, menu);
        //hide addpost icon from this fragment
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_logout).setVisible(true);

        super.onCreateOptionsMenu(menu, inflater);
    }

}