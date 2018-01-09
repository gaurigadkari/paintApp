package gauri.com.paintapp.view;


import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import gauri.com.paintapp.Constants;
import gauri.com.paintapp.R;
import gauri.com.paintapp.component.PaintView;
import gauri.com.paintapp.databinding.FragmentPaintBinding;
import gauri.com.paintapp.network.JsonAPI;

public class PaintFragment extends Fragment {
    private RadioButton redBrush;
    private RadioButton greenBrush;
    private RadioButton blueBrush;
    private RadioButton blackBrush;
    private PaintView paintView;
    FragmentPaintBinding binding;
    private android.support.v7.widget.Toolbar toolbar;
    private ProgressBar spinner;
    private AsyncTask saveImageTask;

    public PaintFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Indicating the fragment will retain itself on config changes even if activity is destroyed
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Verifying if a saved instance exists before inflating the fragment
        if (savedInstanceState == null) {
            // Inflate the layout for this fragment
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_paint, container, false);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            redBrush = binding.redBrush;
            greenBrush = binding.greenBrush;
            blueBrush = binding.blueBrush;
            blackBrush = binding.blackBrush;
            paintView = binding.paintView;
            toolbar = binding.toolbarPaint;
            spinner = binding.spinner;

            redBrush.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paintView.setBrushColor(Color.RED);
                }
            });

            greenBrush.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paintView.setBrushColor(Color.GREEN);
                }
            });

            blueBrush.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paintView.setBrushColor(Color.BLUE);
                }
            });

            blackBrush.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paintView.setBrushColor(Color.BLACK);
                }
            });
        }

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.paint_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                paintView.clear();
                return true;
            case R.id.action_save:
                saveImageTask = new SaveImageTask(PaintFragment.this).execute(paintView.getCurrentBitmap());
        }
        return false;
    }

    public static PaintFragment newInstance() {
        return new PaintFragment();
    }

    private void showSpinner() {
        spinner.setVisibility(View.VISIBLE);
    }

    private void hideSpinner() {
        spinner.setVisibility(View.GONE);
    }

    private static class SaveImageTask extends AsyncTask<Bitmap, Void, String> {
        JsonAPI jsonAPI;
        String imageUrl;
        WeakReference<PaintFragment> fragment;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fragment.get().showSpinner();
        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            return getEncoded64ImageStringFromBitmap(bitmaps[0]);
        }

        private String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
            byte[] byteFormat = null;
            ByteArrayOutputStream stream;
            try {
                stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                byteFormat = stream.toByteArray();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // get the base 64 string
            String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
            return imgString;
        }

        @Override
        protected void onPostExecute(String imageAsString) {
            super.onPostExecute(imageAsString);
            jsonAPI = JsonAPI.getInstance();
            jsonAPI.addHeader(Constants.AUTHORIZATION_HEADER, Constants.CLIENT_ID);
            jsonAPI.addHeader(Constants.AUTHORIZATION_HEADER, Constants.BEARER_TOKEN);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(Constants.IMAGE_POST_KEY, imageAsString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonAPI.post(Constants.BASE_URL + Constants.IMAGE_END_POINT, jsonObject, new JsonAPI.JsonCallback() {
                @Override
                public void onResponse(int statusCode, JSONObject json) {
                    try {
                        if (fragment.get() != null) {
                            imageUrl = json.getJSONObject(Constants.RESPONSE_DATA).get(Constants.RESPONSE_LINK).toString();
                            Snackbar.make(fragment.get().getView(), R.string.image_saved, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.open_webpage, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String url = imageUrl;
                                            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                                            CustomTabsIntent customTabsIntent = builder.build();
                                            customTabsIntent.launchUrl(fragment.get().getContext(), Uri.parse(url));
                                            fragment.get().paintView.clear();
                                        }
                                    }).show();
                            fragment.get().hideSpinner();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public SaveImageTask(PaintFragment fragment) {
            this.fragment = new WeakReference<PaintFragment>(fragment);
        }
    }

    @Override
    public void onDestroy() {
        saveImageTask.cancel(true);
        super.onDestroy();
    }
}
