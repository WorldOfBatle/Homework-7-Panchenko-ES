package com.example.homework_7_panchenko_es;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.example.homework_7_panchenko_es.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    // Ключи для сохранения состояния
    private static final String KEY_DISPLAY = "display_text";
    private static final String KEY_ACCUMULATOR = "accumulator";
    private static final String KEY_PENDING_OP = "pending_op";
    private static final String KEY_CLEAR_NEXT = "clear_next";

    // "Коды" операций — не завязаны на текст на кнопках
    private static final String OP_PLUS = "PLUS";
    private static final String OP_MINUS = "MINUS";
    private static final String OP_MUL = "MUL";
    private static final String OP_DIV = "DIV";

    private ActivityMainBinding binding;
    private ActionBarDrawerToggle drawerToggle;

    // Внутреннее состояние калькулятора
    private long accumulator = 0L;
    private String pendingOp = null;
    private boolean clearOnNextDigit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ---------- Toolbar + Drawer ----------
        setSupportActionBar(binding.toolbar);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.nav_open,
                R.string.nav_close
        );
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        binding.navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_about) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                binding.drawerLayout.closeDrawers();
                return true;
            }
            return false;
        });

        // ---------- Восстановление состояния ----------
        if (savedInstanceState != null) {
            String restored = savedInstanceState.getString(
                    KEY_DISPLAY,
                    getString(R.string.disp_zero)
            );
            binding.displayText.setText(restored);

            accumulator = savedInstanceState.getLong(KEY_ACCUMULATOR, 0L);
            pendingOp = savedInstanceState.getString(KEY_PENDING_OP, null);
            clearOnNextDigit = savedInstanceState.getBoolean(KEY_CLEAR_NEXT, false);
        }

        // ---------- Цифры ----------
        View.OnClickListener digitListener = v -> {
            Button b = (Button) v;
            String digit = b.getText().toString();
            String current = binding.displayText.getText().toString();

            if (clearOnNextDigit || getString(R.string.disp_zero).contentEquals(current)) {
                binding.displayText.setText(digit);
                clearOnNextDigit = false;
            } else {
                binding.displayText.setText(current + digit);
            }
        };

        binding.btn0.setOnClickListener(digitListener);
        binding.btn1.setOnClickListener(digitListener);
        binding.btn2.setOnClickListener(digitListener);
        binding.btn3.setOnClickListener(digitListener);
        binding.btn4.setOnClickListener(digitListener);
        binding.btn5.setOnClickListener(digitListener);
        binding.btn6.setOnClickListener(digitListener);
        binding.btn7.setOnClickListener(digitListener);
        binding.btn8.setOnClickListener(digitListener);
        binding.btn9.setOnClickListener(digitListener);

        // ---------- Операции ----------
        View.OnClickListener opListener = v -> {
            String opCode = null;
            int id = v.getId();
            if (id == R.id.btnPlus) {
                opCode = OP_PLUS;
            } else if (id == R.id.btnMinus) {
                opCode = OP_MINUS;
            } else if (id == R.id.btnMul) {
                opCode = OP_MUL;
            } else if (id == R.id.btnDiv) {
                opCode = OP_DIV;
            }

            if (opCode != null) {
                handleOperation(opCode);
            }
        };

        binding.btnPlus.setOnClickListener(opListener);
        binding.btnMinus.setOnClickListener(opListener);
        binding.btnMul.setOnClickListener(opListener);
        binding.btnDiv.setOnClickListener(opListener);

        // "="
        binding.btnEq.setOnClickListener(v -> handleEquals());

        // "C" — теперь спрашиваем подтверждение перед полным сбросом
        binding.btnClear.setOnClickListener(v -> showClearConfirmDialog());
    }

    // Нужно для корректной работы ActionBarDrawerToggle
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle != null && drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Закрываем боковое меню по Back
    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void showClearConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.clear_dialog_title)
                .setMessage(R.string.clear_dialog_message)
                .setPositiveButton(R.string.clear_dialog_yes, (dialog, which) -> {
                    // Если пользователь подтвердил — сбрасываем калькулятор
                    resetCalculator();
                })
                .setNegativeButton(R.string.clear_dialog_no, (dialog, which) -> {
                    // Отмена — просто закрываем диалог
                    dialog.dismiss();
                })
                .show();
    }

    // Обработка выбора операции (+, −, ×, ÷)
    private void handleOperation(String opCode) {
        String currentText = binding.displayText.getText().toString();

        long currentValue;
        try {
            currentValue = Long.parseLong(currentText);
        } catch (NumberFormatException e) {
            currentValue = 0L;
        }

        if (pendingOp == null) {
            accumulator = currentValue;
        } else if (!clearOnNextDigit) {
            accumulator = applyOperation(accumulator, currentValue, pendingOp);
            binding.displayText.setText(String.valueOf(accumulator));
        }

        pendingOp = opCode;
        clearOnNextDigit = true;
    }

    // Обработка "="
    private void handleEquals() {
        if (pendingOp == null) {
            return;
        }

        String currentText = binding.displayText.getText().toString();
        long currentValue;
        try {
            currentValue = Long.parseLong(currentText);
        } catch (NumberFormatException e) {
            currentValue = 0L;
        }

        accumulator = applyOperation(accumulator, currentValue, pendingOp);
        binding.displayText.setText(String.valueOf(accumulator));

        pendingOp = null;
        clearOnNextDigit = true;
    }

    // Арифметика
    private long applyOperation(long left, long right, String opCode) {
        switch (opCode) {
            case OP_PLUS:
                return left + right;
            case OP_MINUS:
                return left - right;
            case OP_MUL:
                return left * right;
            case OP_DIV:
                if (right == 0L) {
                    return 0L; // деление на ноль — просто 0
                }
                return left / right;
            default:
                return right;
        }
    }

    // Полный сброс калькулятора
    private void resetCalculator() {
        accumulator = 0L;
        pendingOp = null;
        clearOnNextDigit = false;
        binding.displayText.setText(getString(R.string.disp_zero));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(KEY_DISPLAY, binding.displayText.getText().toString());
        outState.putLong(KEY_ACCUMULATOR, accumulator);
        outState.putString(KEY_PENDING_OP, pendingOp);
        outState.putBoolean(KEY_CLEAR_NEXT, clearOnNextDigit);
        super.onSaveInstanceState(outState);
    }
}
