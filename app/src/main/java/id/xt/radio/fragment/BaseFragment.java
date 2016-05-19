package id.xt.radio.fragment;

import android.support.v4.app.Fragment;

import id.xt.radio.MainActivity;

/**
 * Created by Kido1611 on 07-May-16.
 */
public abstract class BaseFragment extends Fragment {

    public void showSnackBar(String message){
        ((MainActivity)getActivity()).showSnackbar(message);
    }

    public void showSnackBar(int resMessage){
        showSnackBar(getString(resMessage));
    }

    public void showSnackBar(String message, int length){
        ((MainActivity)getActivity()).showSnackbar(message, length);
    }
    public void showSnackBar(int resMessage, int length){
        showSnackBar(getString(resMessage), length);
    }
}
