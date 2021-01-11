package com.brainwellnessspa.DashboardModule.Appointment.AppointmentDetails;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.DashboardModule.Models.AppointmentDetailModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentAptAnswersBinding;
import com.segment.analytics.Properties;

public class AptAnswersFragment extends Fragment {
    FragmentAptAnswersBinding binding;
    AppointmentDetailModel.ResponseData appointmentDetail;
    Properties p;
    String UserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apt_answers, container, false);
        View view = binding.getRoot();
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        if (getArguments() != null) {
            appointmentDetail = getArguments().getParcelable("AppointmentDetail");
        }
        binding.tvTilte.setText(appointmentDetail.getName());

        binding.btnComplete.setOnClickListener(view1 -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(appointmentDetail.getMyAnswers()));
            startActivity(i);
            BWSApplication.showToast("Download PDF", getActivity());
            p = new Properties();
            p.putValue("userId", UserID);
            p.putValue("sessionId", appointmentDetail.getId());
            p.putValue("sessionName", appointmentDetail.getName());
            p.putValue("documentUrl", appointmentDetail.getMyAnswers());
            BWSApplication.addToSegment("Appointment Booklet Pdf Download Clicked", p, CONSTANTS.track);
        });
        return view;
    }
}