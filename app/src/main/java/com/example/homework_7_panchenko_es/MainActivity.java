package com.example.homework_7_panchenko_es;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    // Внутреннее состояние калькулятора
    private long accumulator = 0L;
    private String pendingOp = null;
    private boolean clearOnNextDigit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Восстановление состояния
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

        // Один обработчик для всех цифр
        View.OnClickListener digitListener = v -> {
            Button b = (Button) v;
            String digit = b.getText().toString();
            String current = binding.displayText.getText().toString();

            // Если только что было "=" или операция — начинаем новое число
            if (clearOnNextDigit || getString(R.string.disp_zero).contentEquals(current)) {
                binding.displayText.setText(digit);
                clearOnNextDigit = false;
            } else {
                binding.displayText.setText(current + digit);
            }
        };

        // Навешиваем на все цифровые кнопки
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

        // Обработчики операций
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

        // Кнопка "="
        binding.btnEq.setOnClickListener(v -> handleEquals());

        // Очистка — полностью сбрасываем состояние
        binding.btnClear.setOnClickListener(v -> resetCalculator());
    }

    // Обработка выбора операции (+, −, ×, ÷)
    private void handleOperation(String opCode) {
        String currentText = binding.displayText.getText().toString();

        // Если был показан какой-нибудь "Err" — начнём заново
        long currentValue;
        try {
            currentValue = Long.parseLong(currentText);
        } catch (NumberFormatException e) {
            currentValue = 0L;
        }

        if (pendingOp == null) {
            // Первая операция — просто запоминаем число в аккумулятор
            accumulator = currentValue;
        } else if (!clearOnNextDigit) {
            // Вторая и последующие операции — сначала выполняем предыдущую
            accumulator = applyOperation(accumulator, currentValue, pendingOp);
            binding.displayText.setText(String.valueOf(accumulator));
        }

        // Обновляем отложенную операцию
        pendingOp = opCode;
        // Следующая цифра начнёт новое число
        clearOnNextDigit = true;
    }

    // Обработка нажатия "="
    private void handleEquals() {
        if (pendingOp == null) {
            // Нечего считать, просто выходим
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

        // После "=" продолжаем считать дальше от результата
        pendingOp = null;
        clearOnNextDigit = true;
    }

    // Непосредственно выполнение арифметики
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
                    // Деление на ноль — просто сбрасываем и показываем 0
                    return 0L;
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
        // Сохраняем всё, чтобы после поворота логика не ломалась
        outState.putString(KEY_DISPLAY, binding.displayText.getText().toString());
        outState.putLong(KEY_ACCUMULATOR, accumulator);
        outState.putString(KEY_PENDING_OP, pendingOp);
        outState.putBoolean(KEY_CLEAR_NEXT, clearOnNextDigit);
        super.onSaveInstanceState(outState);
    }
}