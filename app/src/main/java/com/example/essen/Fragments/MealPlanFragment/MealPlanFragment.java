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
import com.example.essen.room.AppDatabase;
import com.example.essen.room.MealPlanEntity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MealPlanFragment extends Fragment {

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
   /* EditText editYear;
    EditText editMonth;
    EditText editDay;
    Button go;*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        executorService = Executors.newSingleThreadExecutor();

        View view = inflater.inflate(R.layout.fragment_meal_plan, container, false);

        mealPlanRecyclerView = view.findViewById(R.id.food_recycler_view);
        calendarRecyclerView = view.findViewById(R.id.calendar_recycler_view);
       /* editYear = view.findViewById(R.id.edit_text_year);
        editMonth = view.findViewById(R.id.edit_text_month);
        editDay = view.findViewById(R.id.edit_text_day);
        go = view.findViewById(R.id.go);*/


        textView = view.findViewById(R.id.text_week);

        mealPlanRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        calendarRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        view.findViewById(R.id.next).setOnClickListener(v -> onNextClicked());
        view.findViewById(R.id.back).setOnClickListener(v -> onBackClicked());


        appDatabase = AppDatabase.getDatabase(getContext());
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUse = firebaseAuth.getCurrentUser();

       /* go.setOnClickListener(v -> {
            String yearStr = editYear.getText().toString().trim();
            String monthStr = editMonth.getText().toString().trim();
            String dayStr = editDay.getText().toString().trim();

            if (yearStr.isEmpty() || monthStr.isEmpty() || dayStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a valid date", Toast.LENGTH_SHORT).show();
                return;
            }

            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(monthStr);
            int day = Integer.parseInt(dayStr);

            loadMealsForSpecificDate(year, month, day);
        });*/




        setupCalendar();


        listenForMealPlanUpdates();

        return view;
    }

   /* private void loadMealsForSpecificDate(int year, int month, int day) {
        executorService.execute(() -> {
            List<MealPlanEntity> mealsForSpecificDate = appDatabase.mealPlanDao().getMealsForSpecificDate(year, month, day);
            requireActivity().runOnUiThread(() -> {
                if (mealPlanAdapter == null) {
                    mealPlanAdapter = new MealPlanAdapter(mealsForSpecificDate, appDatabase, getContext());
                    mealPlanRecyclerView.setAdapter(mealPlanAdapter);
                } else {
                    mealPlanAdapter.updateMealPlans(mealsForSpecificDate);
                }
            });
        });
    }*/



    private void setupCalendar() {
        calendarAdapter = new CalendarAdapter(getCalendarData(currentMode), item -> {
            if (currentMode.equals("months")) {
                int month = getMonthNumber(item) - 1;
                loadMealsForMonth(month);
            } else if (currentMode.equals("Days of week")) {
                selectedDay = item;
                loadMealsForDay(selectedDay);
            } else if (currentMode.equals("years")) {
                loadMealsForYear(Integer.parseInt(item));
            } else if (currentMode.equals("Days")) {
                loadMealsForDayNumber(Integer.parseInt(item));
            }
        }, appDatabase, executorService);
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
                for (int i = 2000; i <= 2099; i++) {
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



    private void loadMealsForDay(String day) {
        if (day == null) {
            return;
        }
        executorService.execute(() -> {
            List<MealPlanEntity> mealsForDay = appDatabase.mealPlanDao().getMealsForDay(day);
            requireActivity().runOnUiThread(() -> {
                if (mealPlanAdapter == null) {
                    mealPlanAdapter = new MealPlanAdapter(mealsForDay, appDatabase, getContext());
                    mealPlanRecyclerView.setAdapter(mealPlanAdapter);
                } else {
                    mealPlanAdapter.updateMealPlans(mealsForDay);
                }
            });
        });

    }

    private void loadMealsForMonth(int month) {
        Log.d("MealPlanFragment", "Loading meals for month: " + month);
        executorService.execute(() -> {
            List<MealPlanEntity> mealsForMonth = appDatabase.mealPlanDao().getMealsForMonth(month);
            Log.d("MealPlanFragment", "Meals retrieved: " + mealsForMonth);
            requireActivity().runOnUiThread(() -> {
                Log.d("MealPlanFragment", "Number of meals retrieved: " + mealsForMonth.size());
                if (mealPlanAdapter == null) {
                    mealPlanAdapter = new MealPlanAdapter(mealsForMonth, appDatabase, getContext());
                    mealPlanRecyclerView.setAdapter(mealPlanAdapter);
                } else {
                    mealPlanAdapter.updateMealPlans(mealsForMonth);
                }
            });
        });
    }

    private void loadMealsForYear(int year) {
        executorService.execute(() -> {
            List<MealPlanEntity> mealsForYear = appDatabase.mealPlanDao().getMealsForYear(year);
            requireActivity().runOnUiThread(() -> {
                if (mealPlanAdapter == null) {
                    mealPlanAdapter = new MealPlanAdapter(mealsForYear, appDatabase, getContext());
                    mealPlanRecyclerView.setAdapter(mealPlanAdapter);
                } else {
                    mealPlanAdapter.updateMealPlans(mealsForYear);
                }
            });
        });
    }

    private void loadMealsForDayNumber(int dayNumber) {
        executorService.execute(() -> {
            List<MealPlanEntity> mealsForDayNumber = appDatabase.mealPlanDao().getMealsForDayNumber(dayNumber);
            requireActivity().runOnUiThread(() -> {
                if (mealPlanAdapter == null) {
                    mealPlanAdapter = new MealPlanAdapter(mealsForDayNumber, appDatabase, getContext());
                    mealPlanRecyclerView.setAdapter(mealPlanAdapter);
                } else {
                    mealPlanAdapter.updateMealPlans(mealsForDayNumber);
                }
            });
        });
    }



    private void listenForMealPlanUpdates() {
        if (calendarAdapter != null) {
            calendarAdapter.selectedPosition = -1;
            calendarAdapter.notifyDataSetChanged();
        }
        if (currentUse != null) {
            firestore.collection("users").document(currentUse.getUid())
                    .collection("mealPlans")
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            showMessage("Error fetching updates: " + e.getMessage());
                            return;
                        }
                        if (snapshots != null) {
                            List<MealPlanEntity> mealPlans = new ArrayList<>();
                            for (DocumentSnapshot document : snapshots.getDocuments()) {
                                MealPlanEntity mealPlan = document.toObject(MealPlanEntity.class);

                                new Thread(() -> {
                                    int count =
                                            appDatabase.mealPlanDao().isMealInMealPlan(mealPlan.getStrMeal(), mealPlan.getDayName());
                                    if (count == 0) {
                                        appDatabase.mealPlanDao().insert(mealPlan);
                                    }
                                }).start();

                                mealPlans.add(mealPlan);
                            }

                            if (mealPlanAdapter == null) {
                                mealPlanAdapter = new MealPlanAdapter(mealPlans, appDatabase,
                                        getContext());
                                mealPlanRecyclerView.setAdapter(mealPlanAdapter);
                            } else {
                                mealPlanAdapter.updateMealPlans(mealPlans);
                            }
                        } else {
                            loadMealPlansFromRoom();
                        }
                    });
        }
    }

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
            mealPlanAdapter = new MealPlanAdapter(mealPlans, appDatabase, getContext());
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

    public void showMealForSpecificDay(View view) {

    }
}