package io.quillo.quillo.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.MainActivity;
import io.quillo.quillo.utils.FirebaseHelper;

/**
 * Created by shkla on 2018/01/27.
 *
 *
 */

public class LandingFragment extends Fragment{

    @BindView(R.id.input_university)
    AutoCompleteTextView universityInput;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        getActivity().setTheme(R.style.AppTheme_Dark);
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_landing, container, false);
        ButterKnife.bind(this, view);


        universityInput.setAdapter(FirebaseHelper.getSupportedUniversitiesAdapter(getActivity()));
        universityInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(arg1.getApplicationWindowToken(), 0);

            }

        });

        return view;
    }



    //TODO: Find a way to do auto complete
    private void setupUniversityTextField(){
        String[] universities = getResources().getStringArray(R.array.universities);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.fragment_landing, universities);

        //universityInput.setAdapter(adapter);
    }

    @OnClick(R.id.btn_start)
    public void handleStartClick () {
        if (universityIsValid()){
            String universityUid = universityInput.getText().toString();
            ((MainActivity)getActivity()).saveUniversityUidToSharedPrefrences(universityUid);
            ((MainActivity)getActivity()).showSearchFragmentAfterLanding();
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
            universityInput.setError("University not supported yet");
            return false;
        }

        return true;
    }

}
