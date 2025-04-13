package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.*;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.animation.Easing;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity {

    SessionManager session;
    TransactionDb transactionDb;
    LinearLayout layoutTransactions;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);
        transactionDb = new TransactionDb(this);
        layoutTransactions = findViewById(R.id.layoutTransactions);
        pieChart = findViewById(R.id.pieChart);

        // Check if user is logged in
        if (session.getUserId() == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        findViewById(R.id.btnAddTransaction).setOnClickListener(v -> {
            startActivity(new Intent(this, AddTransactionActivity.class));
        });

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Setup pie chart first
        setupPieChart();

        // Then load data
        refreshData();
    }

    private void setupPieChart() {
        // Configure pie chart appearance
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setCenterText("Income vs Expense");
        pieChart.setCenterTextSize(16f);
        pieChart.setDrawEntryLabels(false); // Don't show labels directly on slices

        // Configure legend
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);
        legend.setForm(Legend.LegendForm.CIRCLE);

        // Configure description
        Description desc = new Description();
        desc.setText("");  // Empty description
        pieChart.setDescription(desc);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        loadPieChart();
        loadTransactions();
    }

    private void loadPieChart() {
        int userId = session.getUserId();
        List<Transaction> allTransactions = transactionDb.getTransactionsByUserId(userId);

        float totalIncome = 0f;
        float totalExpense = 0f;

        for (Transaction t : allTransactions) {
            if (t.getType().equalsIgnoreCase("income")) {
                totalIncome += t.getAmount();
            } else if (t.getType().equalsIgnoreCase("expense")) {
                totalExpense += t.getAmount();
            }
        }

        ArrayList<PieEntry> entries = new ArrayList<>();

        // Only add entries with positive values
        if (totalIncome > 0) entries.add(new PieEntry(totalIncome, "Income"));
        if (totalExpense > 0) entries.add(new PieEntry(totalExpense, "Expense"));

        // If both values are zero, show a message
        if (entries.isEmpty()) {
            pieChart.setNoDataText("No transaction data available");
            pieChart.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");

        // Set distinct colors - green for income, red for expense
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#4CAF50")); // Green for income
        colors.add(Color.parseColor("#F44336")); // Red for expense
        dataSet.setColors(colors);

        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(3f); // Space between slices

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart)); // Format values as percentages

        pieChart.setData(pieData);
        pieChart.highlightValues(null);
        pieChart.animateY(1000, Easing.EaseInOutQuad);
        pieChart.invalidate(); // refresh chart
    }

    private void loadTransactions() {
        layoutTransactions.removeAllViews();
        List<Transaction> list = transactionDb.getTransactionsByUserId(session.getUserId());

        if (list.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("No transactions");
            layoutTransactions.addView(tv);
            return;
        }

        // Sort by timestamp (newest first)
        Collections.sort(list, (t1, t2) -> Long.compare(t2.getTimestamp(), t1.getTimestamp()));

        // Display total balance at the top of transactions
        float totalBalance = 0f;
        for (Transaction t : list) {
            if (t.getType().equalsIgnoreCase("income")) {
                totalBalance += t.getAmount();
            } else if (t.getType().equalsIgnoreCase("expense")) {
                totalBalance -= t.getAmount();
            }
        }

        TextView totalView = new TextView(this);
        totalView.setText("Total Balance: ₹" + String.format("%.2f", totalBalance));
        totalView.setTextSize(18);
        totalView.setPadding(20, 10, 20, 30);
        totalView.setTextColor(Color.BLACK);
        layoutTransactions.addView(totalView);

        // Add a divider after total balance
        View dividerTop = new View(this);
        dividerTop.setBackgroundColor(Color.LTGRAY);
        dividerTop.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 2));
        layoutTransactions.addView(dividerTop);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault());

        for (Transaction t : list) {
            TextView tv = new TextView(this);
            String amountText = t.getType().equalsIgnoreCase("income") ?
                    "+" + String.format("₹%.2f", t.getAmount()) :
                    "-" + String.format("₹%.2f", t.getAmount());

            tv.setText(String.format("%s - %s\n%s",
                    amountText,
                    t.getDescription(),
                    sdf.format(new Date(t.getTimestamp()))));

            tv.setPadding(20, 20, 20, 20);

            // Set text color based on transaction type
            if (t.getType().equalsIgnoreCase("income")) {
                tv.setTextColor(Color.parseColor("#4CAF50")); // Green for income
            } else {
                tv.setTextColor(Color.parseColor("#F44336")); // Red for expense
            }

            layoutTransactions.addView(tv);

            // Add divider
            View divider = new View(this);
            divider.setBackgroundColor(Color.LTGRAY);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));
            layoutTransactions.addView(divider);
        }
    }
}