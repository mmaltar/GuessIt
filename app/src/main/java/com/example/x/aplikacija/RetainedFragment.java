package com.example.x.aplikacija;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RetainedFragment extends Fragment {

    // data object we want to retain
    private GameLevel lvl;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(GameLevel data) {
        this.lvl = data;
    }

    public GameLevel getData() {
        return lvl;
    }
}