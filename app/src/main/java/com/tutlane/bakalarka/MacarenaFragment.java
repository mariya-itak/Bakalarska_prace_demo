package com.tutlane.bakalarka;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MacarenaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MacarenaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MacarenaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MacarenaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MacarenaFragment newInstance(String param1, String param2) {
        MacarenaFragment fragment = new MacarenaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_macarena, container, false);
        GridLayout gl = view.findViewById(R.id.mainGrid);
        int itemsCount = gl.getColumnCount()*gl.getRowCount();
        for(int i = 1; i <= itemsCount; i++){
            String moveId = "mac_grid_imageview_" + i;
            gl.findViewById(StartPage.getResId(moveId, R.id.class)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((NavigationHost) getActivity())
                            .navigateTo(new MovesSettings(moveId), false);

                }
            });
        }

        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationHost) getActivity()).navigateTo(new StartPage("", null), false);
            }
        });
        return view;
    }
}