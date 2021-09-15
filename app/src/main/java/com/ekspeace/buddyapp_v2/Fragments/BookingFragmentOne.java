package com.ekspeace.buddyapp_v2.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ekspeace.buddyapp_v2.Common.Common;
import com.ekspeace.buddyapp_v2.Common.PopUp;
import com.ekspeace.buddyapp_v2.Interface.ICategoryLoadListener;
import com.ekspeace.buddyapp_v2.Model.Category;
import com.ekspeace.buddyapp_v2.Model.EventBus.CategoryLoadDoneEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.EnableNextButton;
import com.ekspeace.buddyapp_v2.Model.EventBus.NetworkConnectionEvent;
import com.ekspeace.buddyapp_v2.Model.EventBus.DestroyFragmentManager;
import com.ekspeace.buddyapp_v2.Model.EventBus.loadingEvent;
import com.ekspeace.buddyapp_v2.Model.PickUp;
import com.ekspeace.buddyapp_v2.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BookingFragmentOne extends Fragment implements ICategoryLoadListener {
    private static BookingFragmentOne instance;
    private Category category;
    private MaterialSpinner spinner;
    private TextInputLayout pickup_type, pickup_info;
    private CollectionReference categoryTypeRef;
    private ICategoryLoadListener iCategoryLoadListener;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private MaterialSpinner sRooms, sField;
    private RelativeLayout relative_layout_booking, relative_layout_order;
    private View layout;


    public static BookingFragmentOne getInstance() {
        if (instance == null) {
            instance = new BookingFragmentOne();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryTypeRef = FirebaseFirestore.getInstance()
                .collection("Category").document(Common.Service).collection("Sub Category");
        iCategoryLoadListener = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_one, container, false);
        init(view);
        return view;
    }
    private void loadCategory() {
        EventBus.getDefault().postSticky(new loadingEvent(true));
        if (Common.isOnline(getContext())) {
            categoryTypeRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<String> items = new ArrayList<>();
                            List<Category> list = new ArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Category category = documentSnapshot.toObject(Category.class);
                                category.setCategoryName(documentSnapshot.getId());
                                category.setCategoryPrice(documentSnapshot.getString("price"));
                                items.add(documentSnapshot.getId());
                                list.add(category);
                            }
                            iCategoryLoadListener.onCategoryLoadSuccess(list, items);
                        }
                    }).addOnFailureListener(e -> iCategoryLoadListener.onCategoryLoadFailed(e.getMessage()));
        }else{
            PopUp.Toast(getContext(), "Connection...", "Please check your internet connectivity and try again");
            EventBus.getDefault().postSticky(new loadingEvent(false));
        }
    }
    private void init(View view){
        relative_layout_booking = view.findViewById(R.id.relative_layout_booking);
        relative_layout_order = view.findViewById(R.id.relative_layout_order);
        spinner = view.findViewById(R.id.spinner);
        pickup_info = view.findViewById(R.id.pickup_more_info);
        pickup_type = view.findViewById(R.id.pickup_type);
        radioGroup = view.findViewById(R.id.radio_group);
        sField = view.findViewById(R.id.ground_meters);
        sRooms = view.findViewById(R.id.rooms_number_one);

        LayoutInflater inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.custom_toast, view.findViewById(R.id.custom_toast_container));
       setLayout();
    }
    private void setLayout(){
        if(Common.Service.contains("Pick")){
            relative_layout_order.setVisibility(View.VISIBLE);
        }
        else {
            relative_layout_booking.setVisibility(View.VISIBLE);
            loadCategory();
            if (Common.Service.contains("Car")) {
                radioGroup.setVisibility(View.VISIBLE);
                radioGroup.clearCheck();
            }
            else if(Common.Service.contains("Cleaning")){
                sRooms.setVisibility(View.VISIBLE);
                List<Integer> integerList = new ArrayList<>();
                integerList.add(1); integerList.add(2); integerList.add(3); integerList.add(4); integerList.add(5);
                sRooms.setItems(integerList);
                sRooms.setOnItemSelectedListener((view, position, id, item) -> {
                    if (position > -1 && category != null) {
                        category.setCategoryPart(String.valueOf(item));
                    }
                });
            }
            else{
                sField.setVisibility(View.VISIBLE);
                List<String> stringList = new ArrayList<>();
                stringList.add("Mowing lawn");stringList.add("Trim trees");stringList.add("Install seasonal plants");stringList.add("Other");
                sField.setItems(stringList);
                sField.setOnItemSelectedListener((view, position, id, item) -> {
                    if (position > - 1 && category != null)  {
                        category.setCategoryPart(String.valueOf(item));
                    }
                });
            }

        }
    }
    private void setLayoutLogic(){
        if(Common.Service.contains("Pick")){
            String type = pickup_type.getEditText().getText().toString().trim();
            String info = pickup_info.getEditText().getText().toString().trim();
            if (TextUtils.isEmpty(type)) {
                pickup_type.setError("Please provide the type of the pick up");
                Common.step--;
                return;
            }
            if(TextUtils.isEmpty(info)) {
                pickup_info.setError("Please provide additional information about the pick up");
                Common.step--;
                return;
            }
            PickUp pickUp = new PickUp(type, info);
            EventBus.getDefault().postSticky(new EnableNextButton(1, pickUp));

        }
        else {
            if (Common.Service.contains("Car")) {
                radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
                    radioButton = radioGroup.findViewById(i);
                });
                radioButton = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                if (radioButton != null && category != null) {
                    category.setCategoryPart(radioButton.getText().toString());
                    EventBus.getDefault().postSticky(new EnableNextButton(1, category));
                }else {
                    PopUp.smallToast(getContext(), layout, R.drawable.error, "Please choose all necessary option", Toast.LENGTH_SHORT);
                    Common.step--;}
            }
            else if(Common.Service.contains("Cleaning")) {
                if(category != null) {
                    if (category.getCategoryPart() != null) {
                        EventBus.getDefault().postSticky(new EnableNextButton(1, category));
                    } else {
                        PopUp.smallToast(getContext(), layout, R.drawable.error, "Please specify number of rooms", Toast.LENGTH_SHORT);
                        Common.step--;
                    }
                }else {PopUp.smallToast(getContext(), layout, R.drawable.error, "Please choose all necessary option", Toast.LENGTH_SHORT);
                    Common.step--;}
            }
            else if(Common.Service.contains("Gardening")){
                if(category != null) {
                    if (category.getCategoryPart() != null) {
                        EventBus.getDefault().postSticky(new EnableNextButton(1, category));
                    } else {
                        PopUp.smallToast(getContext(), layout, R.drawable.error, "Please specify type of gardening", Toast.LENGTH_SHORT);
                        Common.step--;
                    }
                }else {
                    PopUp.smallToast(getContext(), layout, R.drawable.error, "Please choose all necessary option", Toast.LENGTH_SHORT);
                    Common.step--;}

            }
        }

    }
    @Override
    public void onCategoryLoadSuccess(List<Category> categoryList, List<String> stringList) {
        EventBus.getDefault().postSticky(new loadingEvent(false));
        spinner.setItems(stringList);
        spinner.setOnItemSelectedListener((view, position, id, item) -> {
            if (position > -1 )
            {
                category = new Category(item.toString(), categoryList.get((int)id).getCategoryPrice());
            }

        });
    }
    private void unableButtonNext() {
        EventBus.getDefault().postSticky(new DestroyFragmentManager(true));

    }
    @Override
    public void onCategoryLoadFailed(String message) {
        EventBus.getDefault().postSticky(new loadingEvent(false));
        PopUp.smallToast(getContext(), layout, R.drawable.error, message, Toast.LENGTH_SHORT);
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void setCategory(CategoryLoadDoneEvent event) {
        if (event.isCategoryLoadDone()) {
            setLayoutLogic();
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void Connection(NetworkConnectionEvent event) {
        if (event.isNetworkConnected()) {
           setLayout();
        }
    }
}