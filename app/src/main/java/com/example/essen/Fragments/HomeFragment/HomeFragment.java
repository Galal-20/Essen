package com.example.essen.Fragments.HomeFragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.essen.Activities.AuthActivities.Login.Login_Screen;
import com.example.essen.Activities.CategoryMealActivity.CategoryMeal;
import com.example.essen.Activities.MealActivity.MealActivity;
import com.example.essen.Activities.Profile.ProfileActivity;
import com.example.essen.R;
import com.example.essen.pojo.Category;
import com.example.essen.pojo.MainMeal;
import com.example.essen.repository.MealRepositoryImpl;

import java.util.List;

public class HomeFragment extends Fragment implements HomeContract.View {

    public static final String Cat = "com.example.essen.HomeFragment.strCat";
    public static final String NAME_MEAL = "com.example.essen.HomeFragment.nameMeal";
    public static final String THUMB_MEAL = "com.example.essen.HomeFragment.thumbMeal";
    public static final String LOCATION = "com.example.essen.HomeFragment.location";
    public static final String INSTRUCTIONS = "com.example.essen.HomeFragment.Instructions";
    public static final String YOUTUBE = "com.example.essen.HomeFragment.youtube";
    public static final String CATEGORY_NAME = "com.example.essen.HomeFragment.categoryName";
    public static final String INGREDIENTS = "com.example.essen.HomeFragment.ingredients";


    RecyclerView categoryRecyclerView;
    private ImageView imageView;
    private HomeContract.Presenter presenter;
    private CardView cardView;
    private RecyclerView recyclerView;
    private PopularFoodAdapter popularFoodAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MainMeal randomMeal;
    private ProgressBar progressBar;
    private ImageView settings;
    private boolean isGuest;
    LottieAnimationView lottieAnimationView;
    Runnable runnable;
    private Handler handler;

    private SharedPreferences sharedPreferences;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        MealRepositoryImpl repository = new MealRepositoryImpl(sharedPreferences);
        presenter = new HomePresenter(this, repository);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        imageView = view.findViewById(R.id.imageView);
        cardView = view.findViewById(R.id.cardF);
        recyclerView = view.findViewById(R.id.recycleP);
        categoryRecyclerView = view.findViewById(R.id.recycleC);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progressBar);
        lottieAnimationView = view.findViewById(R.id.settings);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        isGuest = sharedPreferences.getBoolean("isGuest", true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        showLoading(true);

        handler = new Handler();


        if (isInternetAvailable()) {
            presenter.getRandomMeal();
            presenter.getPopularMeals();
            presenter.getCategories();
        } else {
            showError("No internet connection. Loading cached data.");
            presenter.getRandomMeal();
            presenter.getPopularMeals();
            presenter.getCategories();
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            showLoading(true);
            lottieAnimationView.playAnimation();
            presenter.getRandomMeal();
            presenter.getPopularMeals();
            presenter.getCategories();
        });

        cardView.setOnClickListener(v -> {
            if (randomMeal != null) {
                Intent intent = new Intent(getContext(), MealActivity.class);
                intent.putExtra(Cat, randomMeal.getStrCategory());
                intent.putExtra(NAME_MEAL, randomMeal.getStrMeal());
                intent.putExtra(THUMB_MEAL, randomMeal.getStrMealThumb());
                intent.putExtra(LOCATION, randomMeal.getStrArea());
                intent.putExtra(INSTRUCTIONS, randomMeal.getStrInstructions());
                intent.putExtra(INGREDIENTS,
                        randomMeal.getStrIngredient1() + "\n" + randomMeal.getStrIngredient2() +
                                randomMeal.getStrIngredient3() + "\n" + randomMeal.getStrIngredient4() +
                                randomMeal.getStrIngredient5() + "\n" + randomMeal.getStrIngredient6() +
                                randomMeal.getStrIngredient7() + "\n" + randomMeal.getStrIngredient8() +
                                randomMeal.getStrIngredient9() + "\n" + randomMeal.getStrIngredient10() +
                                randomMeal.getStrIngredient11() + "\n" + randomMeal.getStrIngredient12() +
                                randomMeal.getStrIngredient13() + "\n" + randomMeal.getStrIngredient14() +
                                randomMeal.getStrIngredient15() + "\n" + randomMeal.getStrIngredient16() +
                                randomMeal.getStrIngredient17() + "\n" + randomMeal.getStrIngredient18() +
                                randomMeal.getStrIngredient19() + "\n" + randomMeal.getStrIngredient20()

                );
                intent.putExtra(YOUTUBE, randomMeal.getStrYoutube());
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Meal data not available", Toast.LENGTH_SHORT).show();
            }
        });

        lottieAnimationView.setAnimation(R.raw.options);
        lottieAnimationView.playAnimation();
        lottieAnimationView.setOnClickListener(v -> {
            lottieAnimationView.playAnimation();
            runnable = () -> {
                GoToProfile();
            };

            handler.postDelayed(runnable, 1200);


        });

        return view;
    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public void showRandomMeal(MainMeal meal) {
        randomMeal = meal;
        Glide.with(requireContext())
                .load(meal.getStrMealThumb())
                .placeholder(R.drawable.coffe)
                .error(R.drawable.coffe)
                .into(imageView);
        swipeRefreshLayout.setRefreshing(false);
        showLoading(false);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
        showLoading(false);
    }

    @Override
    public void showPopularMeals(List<MainMeal> meal) {
        if (popularFoodAdapter == null) {
            popularFoodAdapter = new PopularFoodAdapter(getContext(), meal);
            recyclerView.setAdapter(popularFoodAdapter);
        } else {
            popularFoodAdapter.setMealsList(meal);
        }
        swipeRefreshLayout.setRefreshing(false);
        showLoading(false);

    }

    @Override
    public void showCategories(List<Category> categories) {
        if (categories != null && !categories.isEmpty()) {
            categoryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            CategoryAdapter categoryAdapter = new CategoryAdapter();
            categoryAdapter.setCategoriesList(categories);
            categoryRecyclerView.setAdapter(categoryAdapter);

            categoryAdapter.setOnItemClicked(category -> {
                Intent intent = new Intent(getContext(), CategoryMeal.class);
                intent.putExtra(CATEGORY_NAME, category.getStrCategory());
                startActivity(intent);
            });
        } else {
            showError("No categories available");
        }
        showLoading(false);
    }


    public void GoToProfile() {
        if (isGuest) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.Please_login_to_access_Setting);
            builder.setMessage(R.string.Are_you_want_to_join_with_us);

            builder.setPositiveButton(R.string.Go_to_login, (dialog, which) -> {
                startActivity(new Intent(getContext(), Login_Screen.class));
                getActivity().finish();
                dialog.dismiss();
            });

            builder.setNegativeButton(R.string.Still_Guest, (dialog, which) -> {
                dialog.dismiss();

            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        } else {
            startActivity(new Intent(getContext(), ProfileActivity.class));
            requireActivity().finish();
        }

    }
}



