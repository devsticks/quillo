package io.quillo.quillo.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

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
    @BindView(R.id.btn_skip)
    TextView btnSkip;

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

        return view;
    }

    //TODO: Find a way to do auto complete
    private void setupUniversityTextField(){
        String[] universities = getResources().getStringArray(R.array.universities);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.fragment_landing, universities);

        //universityInput.setAdapter(adapter);
    }

    @OnClick(R.id.btn_start)
    public void handleStartClick (View v) {
        if (universityIsValid()){
            String universityUid = universityInput.getText().toString();
            ((MainActivity)getActivity()).saveUniversityUidToSharedPrefrences(universityUid);
            ((MainActivity)getActivity()).showSearchFragmentAfterLanding();
        }
    }

    @OnClick(R.id.btn_skip)
    public void handleSkipClick (View v) {
        //TODO What if they aren't in university?
        ((MainActivity)getActivity()).showSearchFragmentAfterLanding();
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
