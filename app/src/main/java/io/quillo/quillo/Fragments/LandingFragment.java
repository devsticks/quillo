package io.quillo.quillo.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.MainActivity;

/**
 * Created by shkla on 2018/01/27.
 *
 *
 */

public class LandingFragment extends Fragment{

    @BindView(R.id.input_university)
    EditText universityInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_landing, container, false);
        //TODO: Fix the theme of this view
        ButterKnife.bind(this, view);

        getContext().getTheme().applyStyle(R.style.AppTheme_Dark, true);

        View decorView = getActivity().getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        return view;
    }

    @OnClick(R.id.btn_start)
    public void handleStartClick (View v) {
        if(universityIsValid()){
            String universityUid = universityInput.getText().toString();
            ((MainActivity)getActivity()).saveUniversityUidToSharedPrefrences(universityUid);
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private boolean universityIsValid(){
        ArrayList<String> supportedUniversities = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.universities)));

        String university = universityInput.getText().toString();
        if(university == null){
            universityInput.setError("Please enter a university");
            return  false;
        }

        if(!supportedUniversities.contains(university)){
            universityInput.setError("Invalid Uni");
            return false;
        }

        return true;



    }


}
