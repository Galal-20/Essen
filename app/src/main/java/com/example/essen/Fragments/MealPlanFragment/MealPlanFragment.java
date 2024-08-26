package com.example.essen.Fragments.MealPlanFragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essen.R;
import com.example.essen.repository.MealRepository;
import com.example.essen.repository.MealRepositoryImpl;
import com.example.essen.room.AppDatabase;
import com.example.essen.room.MealPlanEntity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MealPlanFragment extends Fragment implements MealPlanContract.View {

    private RecyclerView mealPlanRecyclerView;
    private RecyclerView calendarRecyclerView;
    private CalendarAdapter calendarAdapter;
    private MealPlanAdapter mealPlanAdapter;
    private AppDatabase appDatabase;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUse;
    private String selectedDay;
    private ExecutorService executorService;
    TextView textView;
    private String currentMode = "Days of week";

    private MealRepository mealRepository;
    private MealPlanContract.Presenter presenter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        executorService = Executors.newSingleThreadExecutor();

        View view = inflater.inflate(R.layout.fragment_meal_plan, container, false);

        mealPlanRecyclerView = view.findViewById(R.id.food_recycler_view);
        calendarRecyclerView = view.findViewById(R.id.calendar_recycler_view);
        textView = view.findViewById(R.id.text_week);

        mealPlanRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        calendarRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        presenter = new MealPlanPresenter(this, new MealRepositoryImpl(
                AppDatabase.getDatabase(getContext()), FirebaseFirestore.getInstance(),
                FirebaseAuth.getInstance().getCurrentUser()
        ));
        view.findViewById(R.id.next).setOnClickListener(v -> onNextClicked());
        view.findViewById(R.id.back).setOnClickListener(v -> onBackClicked());


        appDatabase = AppDatabase.getDatabase(getContext());
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUse = firebaseAuth.getCurrentUser();

        setupCalendar();

        if (!isConnectedToInternet()) {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            new Thread(() -> {
                List<MealPlanEntity> mealPlans = appDatabase.mealPlanDao().getAllMealPlans();
                getActivity().runOnUiThread(() -> {
                    updateAdapter(mealPlans);
                });
            }).start();
        } else {
            presenter.listenForMealPlanUpdates();
        }

        return view;
    }




    private void setupCalendar() {
        calendarAdapter = new CalendarAdapter(getCalendarData(currentMode), item -> {
            if (currentMode.equals("months")) {
                int month = getMonthNumber(item) - 1;
                presenter.loadMealsForMonth(month);
            } else if (currentMode.equals("Days of week")) {
                selectedDay = item;
                presenter.loadMealsForDay(selectedDay);
            } else if (currentMode.equals("years")) {
                presenter.loadMealsForYear(Integer.parseInt(item));
            } else if (currentMode.equals("Days")) {
                presenter.loadMealsForDayNumber(Integer.parseInt(item));
            }
        }, AppDatabase.getDatabase(getContext()), executorService);
        calendarRecyclerView.setAdapter(calendarAdapter);

        updateCalendarData();
    }


    private int getMonthNumber(String monthName) {
        if (monthName.equals("January")) {
            return 1;
        } else if (monthName.equals("February")) {
            return 2;
        } else if (monthName.equals("March")) {
            return 3;
        } else if (monthName.equals("April")) {
            return 4;
        } else if (monthName.equals("May")) {
            return 5;
        } else if (monthName.equals("June")) {
            return 6;
        } else if (monthName.equals("July")) {
            return 7;
        } else if (monthName.equals("August")) {
            return 8;
        } else if (monthName.equals("September")) {
            return 9;
        } else if (monthName.equals("October")) {
            return 10;
        } else if (monthName.equals("November")) {
            return 11;
        } else if (monthName.equals("December")) {
            return 12;
        } else {
            throw new IllegalArgumentException("Invalid month name: " + monthName);
        }
    }


    private List<String> getCalendarData(String mode) {
        List<String> data = new ArrayList<>();
        switch (mode) {
            case "Days of week":
                data.add("Monday");
                data.add("Tuesday");
                data.add("Wednesday");
                data.add("Thursday");
                data.add("Friday");
                data.add("Saturday");
                data.add("Sunday");
                break;
            case "months":
                data.add("January");
                data.add("February");
                data.add("March");
                data.add("April");
                data.add("May");
                data.add("June");
                data.add("July");
                data.add("August");
                data.add("September");
                data.add("October");
                data.add("November");
                data.add("December");
                break;
            case "years":
                for (int i = 2024; i <= 2099; i++) {
                    data.add(String.valueOf(i));
                }
                break;
            case "Days":
                for (int i = 1; i <= 31; i++) {
                    data.add(String.valueOf(i));
                }
                break;
        }
        return data;
    }


    private void onNextClicked() {
        switch (currentMode) {
            case "Days of week":
                currentMode = "months";
                break;
            case "months":
                currentMode = "years";
                break;
            case "years":
                currentMode = "Days";
                break;
        }
        if (calendarAdapter != null) {
            calendarAdapter.selectedPosition = -1;
            calendarAdapter.notifyDataSetChanged();
        }

        updateCalendarData();
    }

    private void onBackClicked() {
        switch (currentMode) {
            case "Days of week":
                break;
            case "months":
                currentMode = "Days of week";
                break;
            case "years":
                currentMode = "months";
                break;
            case "Days":
                currentMode = "years";
                break;
        }
        if (calendarAdapter != null) {
            calendarAdapter.selectedPosition = -1;
            calendarAdapter.notifyDataSetChanged();
        }

        updateCalendarData();
    }

    private void updateCalendarData() {
        if (calendarAdapter != null) {
            calendarAdapter.updateData(getCalendarData(currentMode));
        } else {
            Log.e("MealPlanFragment", "CalendarAdapter is not initialized");
        }

        if (textView != null) {
            switch (currentMode) {
                case "Days of week":
                    textView.setText("Days of Week");
                    break;
                case "months":
                    textView.setText("Months");
                    break;
                case "years":
                    textView.setText("Years");
                    break;
                case "Days":
                    textView.setText("Days");
                    break;
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
    }


   /* private void listenForMealPlanUpdates() {

        if (currentUse != null){
            mealRepository.addMealPlanUpdateListener(new MealPlanUpdateListener() {
                @Override
                public void onMealPlansUpdated(List<MealPlanEntity> mealPlans) {
                    requireActivity().runOnUiThread(() -> updateAdapter(mealPlans));
                }

                @Override
                public void onError(Exception e) {
                    showMessage("Error fetching updates: " + e.getMessage());
                }
            });
        }else {
            loadMealPlansFromRoom();
        }
    }*/


    private void loadMealPlansFromRoom() {
        if (!isConnectedToInternet()) {
            new Thread(() -> {
                List<MealPlanEntity> mealPlans = appDatabase.mealPlanDao().getAllMealPlans();
                getActivity().runOnUiThread(() -> {
                    updateAdapter(mealPlans);
                });
            }).start();
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateAdapter(List<MealPlanEntity> mealPlans) {
        if (mealPlanAdapter == null) {
            mealPlanAdapter = new MealPlanAdapter(mealPlans, AppDatabase.getDatabase(getContext()), getContext());
            mealPlanRecyclerView.setAdapter(mealPlanAdapter);
        } else {
            mealPlanAdapter.updateMealPlans(mealPlans);
        }
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showMessage(String message) {
        View rootView = getView();
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        } else {
            Log.e("MealPlanFragment", "Root view is null, cannot show Snackbar");
        }
    }

    @Override
    public void showMeals(List<MealPlanEntity> meals) {
        if (mealPlanAdapter == null) {
            mealPlanAdapter = new MealPlanAdapter(meals, AppDatabase.getDatabase(getContext()), getContext());
            mealPlanRecyclerView.setAdapter(mealPlanAdapter);
        } else {
            mealPlanAdapter.updateMealPlans(meals);
        }
    }

    @Override
    public void showError(String message) {

    }
}