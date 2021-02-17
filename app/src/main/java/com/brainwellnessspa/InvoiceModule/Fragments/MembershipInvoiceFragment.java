package com.brainwellnessspa.InvoiceModule.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.InvoiceModule.Models.InvoiceListModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentInvoiceBinding;
import com.brainwellnessspa.databinding.InvoiceListLayoutBinding;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.segment.analytics.Properties;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MembershipInvoiceFragment extends Fragment {
    FragmentInvoiceBinding binding;
    ArrayList<InvoiceListModel.MemberShip> memberShipList;
    private String downloadUrl = "", downloadFileName = "Invoice";
    private static final String TAG = "Download Task";
    private ProgressDialog progressDialog;
    String UserID;
    File apkStorage = null;
    File outputFile = null;
    int downloadIdInvoice = 0;
    String file_name_path = "BWS";
    String[] PERMISSIONS_ABOVE_Q = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission_group.STORAGE,
    };
    String[] PERMISSIONS_BELOW_Q = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invoice, container, false);
        View view = binding.getRoot();

        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));
        if (getArguments() != null) {
            memberShipList = getArguments().getParcelableArrayList("membershipInvoiceFragment");
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvAIList.setLayoutManager(mLayoutManager);
        binding.rvAIList.setItemAnimator(new DefaultItemAnimator());
        binding.llError.setVisibility(View.GONE);
        binding.tvFound.setText("Your membership invoices will appear here");
        return view;
    }

    @Override
    public void onResume() {
        if (memberShipList.size() != 0) {
            getDataList(memberShipList);
            binding.llError.setVisibility(View.GONE);
            binding.rvAIList.setVisibility(View.VISIBLE);
        } else {
            binding.llError.setVisibility(View.VISIBLE);
            binding.rvAIList.setVisibility(View.GONE);
        }
        super.onResume();
    }

    private void getDataList(ArrayList<InvoiceListModel.MemberShip> historyList) {
        if (historyList.size() == 0) {
            binding.llError.setVisibility(View.VISIBLE);
            binding.rvAIList.setVisibility(View.GONE);
        } else {
            binding.llError.setVisibility(View.GONE);
            binding.rvAIList.setVisibility(View.VISIBLE);
            MembershipInvoiceAdapter adapter = new MembershipInvoiceAdapter(historyList);
            binding.rvAIList.setAdapter(adapter);
        }
    }

    public class MembershipInvoiceAdapter extends RecyclerView.Adapter<MembershipInvoiceAdapter.MyViewHolder> {
        private List<InvoiceListModel.MemberShip> listModelList;

        public MembershipInvoiceAdapter(List<InvoiceListModel.MemberShip> listModelList) {
            this.listModelList = listModelList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            InvoiceListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.invoice_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            downloadUrl = listModelList.get(position).getInvoicePdf();
            holder.binding.tvStatus.setVisibility(View.VISIBLE);
            holder.binding.tvInvoiceID.setText("Invoice #" + listModelList.get(position).getInvoiceId());
            holder.binding.tvTitle.setText(listModelList.get(position).getName());
            holder.binding.tvDate.setText(listModelList.get(position).getDate());
            holder.binding.tvDoller.setText("$" + listModelList.get(position).getAmount());
            holder.binding.llViewReceipt.setOnClickListener(view -> {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                InvoiceReceiptFragment receiptFragment = new InvoiceReceiptFragment();
                receiptFragment.setCancelable(true);
                receiptFragment.setValues(listModelList.get(position).getInvoiceId(), "1");
                receiptFragment.show(fragmentManager, "receipt");
            });

            if (listModelList.get(position).getStatus().equalsIgnoreCase("paid")) {
                holder.binding.tvStatus.setText("Paid");
                holder.binding.tvStatus.setBackgroundResource(R.drawable.green_background);
            } else if (listModelList.get(position).getStatus().equalsIgnoreCase("open")) {
                holder.binding.tvStatus.setText("Open");
                holder.binding.tvStatus.setBackgroundResource(R.drawable.blue_background);
            }

            holder.binding.llDownloads.setOnClickListener(v -> {
                requestPermissionDownlaod();
                Properties p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("invoiceId", listModelList.get(position).getInvoiceId());
                p.putValue("invoiceType", "Appointment");
                p.putValue("invoiceAmount", listModelList.get(position).getAmount());
                p.putValue("invoiceDate", listModelList.get(position).getDate());
                p.putValue("invoiceCurrency", "");
                p.putValue("plan", "");
                p.putValue("planStartDt", "");
                p.putValue("planExpiryDt", "");
                BWSApplication.addToSegment("Invoice Downloaded", p, CONSTANTS.track);
            });
        }

        @Override
        public int getItemCount() {
            return listModelList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            InvoiceListLayoutBinding binding;

            public MyViewHolder(InvoiceListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public void requestPermissionDownlaod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            for (String permission : PERMISSIONS_BELOW_Q) {
                if (ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_BELOW_Q, 1);
                } else {
                    DownloadFile();
                }
            }
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            for (String permission : PERMISSIONS_ABOVE_Q) {
                if (ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_DENIED) {
                        AlertDialog.Builder buildermain = new AlertDialog.Builder(getActivity());
                        buildermain.setMessage("To download invoice allow " + getActivity().getString(R.string.app_name) + " access to your device's files. " +
                                "\nTap Setting > permission, and turn \"Files and media\" on.");
                        buildermain.setCancelable(true);
                        buildermain.setPositiveButton(
                                getString(R.string.Settings),
                                (dialogmain, id1) -> {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    dialogmain.dismiss();
                                });
                        buildermain.setNegativeButton(
                                getString(R.string.not_now),
                                (dialogmain, id1) -> {
                                    dialogmain.dismiss();
                                });
                        AlertDialog alert11 = buildermain.create();
                        alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
                        alert11.show();
                        alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
                        alert11.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blue));
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_ABOVE_Q, 2);
                    }
                } else {
                    DownloadFile1();
                }
            }
        } else {
            DownloadFile();
        }
    }

    public class CheckForSDCard {
        public boolean isSDCardPresent() {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                    Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DownloadFile();
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);

                } else {
                    callpermissionAlert();
                }
                break;
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DownloadFile1();
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                } else {
                    callpermissionAlert();
                }
                break;
            case 6:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    openfile();

                } else {
                }
                break;
        }
    }

    private void callpermissionAlert() {
        AlertDialog.Builder buildermain = new AlertDialog.Builder(getActivity());
        buildermain.setMessage("To download invoice allow " + getActivity().getString(R.string.app_name) + " access to your device's files. " +
                "\nTap Setting > permission, and turn \"Files and media\" on.");
        buildermain.setCancelable(true);
        buildermain.setPositiveButton(
                getString(R.string.ok),
                (dialogmain, id1) -> {
                    requestPermissionDownlaod();
                    dialogmain.dismiss();
                });
        AlertDialog alert11 = buildermain.create();
        alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        alert11.show();
        alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
    }

    private void openfile() {
        File pdfFile = new File(Environment.getExternalStorageState() + "/BWS" + downloadFileName + ".pdf");  // -> filename = maven.pdf
        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfIntent.setDataAndType(path, "application/pdf");
        try {
            startActivity(pdfIntent);
        } catch (Exception e) {
            BWSApplication.showToast("No Application available to viewPDF", getActivity());
        }
    }

    private void DownloadFile1() {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        File pdfFile = new File(docsFolder.getAbsolutePath(), file_name_path);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Downloading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        PRDownloader.initialize(getActivity());

        downloadIdInvoice = PRDownloader.download(downloadUrl, pdfFile.getAbsolutePath(), downloadFileName + System.currentTimeMillis() + ".pdf")
                .build()
                .setOnStartOrResumeListener(() -> {
                    Log.e(TAG, "Download Failed Start");
                }).start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        progressDialog.dismiss();
                        ContextThemeWrapper ctw = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                        alertDialogBuilder.setTitle("Invoice Data Downloaded Successfully");
                        alertDialogBuilder.setMessage("Your invoice is in Documents/BWS");
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("Ok", (dialog, id) -> dialog.dismiss());
                        AlertDialog alert11 = alertDialogBuilder.create();
                        alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
                        alert11.show();

                        alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dark_blue_gray));
                    }

                    @Override
                    public void onError(Error error) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> progressDialog.dismiss(), 1000);
                        Log.e(TAG, "Download Failed" + error.toString());
                    }
                });
    }

    private void DownloadFile() {
        if (new CheckForSDCard().isSDCardPresent()) {
            apkStorage = new File(Environment.getRootDirectory() + "/" + "BWS");
        } else
            BWSApplication.showToast("Oops!! There is no SD Card.", getActivity());
        if (!apkStorage.exists()) {
            apkStorage.mkdir();
            Log.e(TAG, "Directory Created.");
        }
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Downloading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        downloadIdInvoice = PRDownloader.download(downloadUrl, apkStorage.getPath(), downloadFileName + System.currentTimeMillis() + ".pdf")
                .build()
                .setOnProgressListener(progress -> {
                }).start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        progressDialog.dismiss();
                        ContextThemeWrapper ctw = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                        alertDialogBuilder.setTitle("Invoice Data Downloaded Successfully");
                        alertDialogBuilder.setMessage("Your invoice is in Documents/BWS");
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("Ok", (dialog, id) -> dialog.dismiss());
                        AlertDialog alert11 = alertDialogBuilder.create();
                        alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
                        alert11.show();
                        alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dark_blue_gray));
                        BWSApplication.showToast("Document Downloaded Successfully", getActivity());
                    }

                    @Override
                    public void onError(Error error) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> progressDialog.dismiss(), 1000);
                        Log.e(TAG, "Download Failed");
                    }
                });
    }
}