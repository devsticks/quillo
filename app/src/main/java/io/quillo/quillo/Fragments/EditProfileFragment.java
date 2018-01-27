package io.quillo.quillo.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.quillo.quillo.R;
import io.quillo.quillo.data.IntentExtras;
import io.quillo.quillo.data.Person;

/**
 * Created by shkla on 2018/01/27.
 */

//TODO: Clean up UI

public class EditProfileFragment extends Fragment {

    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.input_name)
    EditText nameInput;
    @BindView(R.id.input_email)
    EditText emailInput;
    @BindView(R.id.input_phone)
    EditText phoneInput;
    @BindView(R.id.input_university)
    EditText universityInput;

    private Person person;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ButterKnife.bind(this, view);


        person = (Person)this.getArguments().getSerializable(IntentExtras.EXTRA_SELLER);
        bindPersonToViews();

        return view;
    }


    private void bindPersonToViews(){
        nameInput.setText(person.getName());
        emailInput.setText(person.getEmail());
        if(person.getPhoneNumber() != null) {
            phoneInput.setText(person.getPhoneNumber());
        }
        universityInput.setText(person.getUniversityUid());

    }


}
