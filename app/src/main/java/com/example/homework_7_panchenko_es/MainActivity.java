package com.example.homework_7_panchenko_es;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homework_7_panchenko_es.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Задание 2*: ключ для сохранения экрана
    private static final String KEY_DISPLAY = "display_text";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Восстановление состояния
        if (savedInstanceState != null) {
            String restored = savedInstanceState.getString(KEY_DISPLAY, getString(R.string.disp_zero));
            binding.displayText.setText(restored);
        }

        // Один обработчик для всех цифр
        View.OnClickListener digitListener = v -> {
            Button b = (Button) v;
            String digit = b.getText().toString();
            String current = binding.displayText.getText().toString();

            // Логика ввода: начинаем с "0" -> заменяем, иначе дописываем
            if (getString(R.string.disp_zero).contentEquals(current)) {
                binding.displayText.setText(digit);
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

        // Очистка
        binding.btnClear.setOnClickListener(v ->
                binding.displayText.setText(getString(R.string.disp_zero))
        );
        
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // Сохраняем состояние экрана
        outState.putString(KEY_DISPLAY, binding.displayText.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
