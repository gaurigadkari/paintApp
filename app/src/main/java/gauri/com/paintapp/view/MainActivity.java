package gauri.com.paintapp.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import gauri.com.paintapp.R;
import gauri.com.paintapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String PAINT_FRAGMENT = "paint_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        PaintFragment paintFragment = (PaintFragment) fragmentManager.findFragmentByTag(PAINT_FRAGMENT);

        if (paintFragment == null) {
            paintFragment = PaintFragment.newInstance();
            fragmentManager
                    .beginTransaction()
                    .add(R.id.fragmentContainer, paintFragment, PAINT_FRAGMENT)
                    .commit();
        }
    }
}
