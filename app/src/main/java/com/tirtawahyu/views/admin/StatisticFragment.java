package com.tirtawahyu.views.admin;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tirtawahyu.R;
import com.tirtawahyu.util.Loading;
import com.tirtawahyu.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticFragment extends Fragment {
    @BindView(R.id.bar_chart_view)
    BarChart barChart;

    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private Context context;

    public StatisticFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_statistic, container, false);
        ButterKnife.bind(this, view);

        context = view.getContext();

        initComponent();

        return view;
    }

    private void initComponent() {
        getStatistic();
    }

    private void getStatistic() {
        final Loading loadingContext = (Loading) context;
        final List<BarEntry> data = new ArrayList<>();

        loadingContext.showLoading();
        mFirestore.collection("receipt").orderBy("createdAt").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        loadingContext.hideLoading();
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> results = task.getResult().getDocuments();

                            Date date = null;
                            long total = 0;
                            for (int i = 0; i < results.size(); i++) {
                                DocumentSnapshot snapshot = results.get(i);

                                Date snapshotDate = (Date) snapshot.get("createdAt");
                                if (date == null) {
                                    date = snapshotDate;
                                }


                                total += (long) snapshot.get("total");
                                if (Util.sameDay(date, snapshotDate)) {

                                } else {
                                    data.add(new BarEntry((float)(i), (float) total));
                                    total = 0;
                                }
                                long dateLong = 0;
                                if (snapshotDate != null) {
                                    dateLong = snapshotDate.getTime();
                                }
                            }
                            initBarChart(data);
                        }
                    }
                });
    }

    private void initBarChart(List<BarEntry> entries) {
        BarDataSet set = new BarDataSet(entries, "Statistik Keseluruhan");

        BarData data = new BarData(set);

        barChart.setFitBars(true);
        barChart.setData(data);
    }
}
