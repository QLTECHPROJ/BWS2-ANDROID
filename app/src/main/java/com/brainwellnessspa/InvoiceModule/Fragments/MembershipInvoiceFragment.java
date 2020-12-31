package com.brainwellnessspa.InvoiceModule.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.InvoiceModule.Models.InvoiceListModel;
import com.brainwellnessspa.R;
import com.brainwellnessspa.UserModule.Activities.RequestPermissionHandler;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.FragmentInvoiceBinding;
import com.brainwellnessspa.databinding.InvoiceListLayoutBinding;
import com.segment.analytics.Properties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MembershipInvoiceFragment extends Fragment {
    FragmentInvoiceBinding binding;
    ArrayList<InvoiceListModel.MemberShip> memberShipList;
    private String downloadUrl = "", downloadFileName = "Data";
    RequestPermissionHandler mRequestPermissionHandler;
    private static final String TAG = "Download Task";
    private ProgressDialog progressDialog;
    String UserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invoice, container, false);
        View view = binding.getRoot();

        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared1.getString(CONSTANTS.PREF_KEY_UserID, ""));

        if (getArguments() != null) {
            memberShipList = getArguments().getParcelableArrayList("membershipInvoiceFragment");
        }

        /*Properties p = new Properties();
        p.putValue("userId", UserID);
        BWSApplication.addToSegment("Membership Invoice Screen Viewed", p, CONSTANTS.screen);*/

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvAIList.setLayoutManager(mLayoutManager);
        binding.rvAIList.setItemAnimator(new DefaultItemAnimator());

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
            binding.tvFound.setVisibility(View.VISIBLE);
        } else {
            binding.llError.setVisibility(View.GONE);
            MembershipInvoiceAdapter adapter = new MembershipInvoiceAdapter(historyList, getActivity());
            binding.rvAIList.setAdapter(adapter);
        }
    }

    public class MembershipInvoiceAdapter extends RecyclerView.Adapter<MembershipInvoiceAdapter.MyViewHolder> {
        private List<InvoiceListModel.MemberShip> listModelList;
        Context ctx;

        public MembershipInvoiceAdapter(List<InvoiceListModel.MemberShip> listModelList, Context ctx) {
            this.listModelList = listModelList;
            this.ctx = ctx;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            InvoiceListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.invoice_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
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

                /*Properties p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("invoiceId", listModelList.get(position).getInvoiceId());
                p.putValue("invoiceType", listModelList.get(position).getStatus());
                p.putValue("invoiceAmount", listModelList.get(position).getAmount());
                BWSApplication.addToSegment("Membership Invoice Clicked", p, CONSTANTS.track);*/
            });

            if (listModelList.get(position).getStatus().equalsIgnoreCase("paid")) {
                holder.binding.tvStatus.setText("Paid");
                holder.binding.tvStatus.setBackgroundResource(R.drawable.green_background);
            } else if (listModelList.get(position).getStatus().equalsIgnoreCase("open")) {
                holder.binding.tvStatus.setText("Open");
                holder.binding.tvStatus.setBackgroundResource(R.drawable.blue_background);
            }

            holder.binding.llDownloads.setOnClickListener(v -> {
                downloadUrl = listModelList.get(position).getInvoicePdf();
                new FileDownloader();
                /*Properties p = new Properties();
                p.putValue("userId", UserID);
                p.putValue("invoiceId", listModelList.get(position).getInvoiceId());
                p.putValue("invoiceType", listModelList.get(position).getStatus());
                p.putValue("invoiceAmount", listModelList.get(position).getAmount());
                BWSApplication.addToSegment("Memebrship Invoice Downloaded", p, CONSTANTS.track);*/
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

    public class CheckForSDCard {
        public boolean isSDCardPresent() {
            return Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);
        }
    }

    public class FileDownloader {
        public FileDownloader() {
            mRequestPermissionHandler = new RequestPermissionHandler();
            downloadFileName = downloadUrl.substring(downloadUrl.lastIndexOf('/'));//Create file name by picking download file name from URL
            Log.e(TAG, downloadFileName);
            isWriteStoragePermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    new DownloadingTask().execute();

                } else {
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

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                new DownloadingTask().execute();
                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else {
            return true;
        }
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

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {
        File apkStorage = null;
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Downloading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    progressDialog.dismiss();
                    ContextThemeWrapper ctw = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctw);
                    alertDialogBuilder.setTitle("Invoice Data Downloaded Successfully");
                    alertDialogBuilder.setMessage("Your invoice is in Storage/BWS");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                   /* alertDialogBuilder.setNegativeButton("Open", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            isreadStoragePermissionGranted();
                            openfile();
                        }
                    });*/
                    AlertDialog alert11 = alertDialogBuilder.create();
                    alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
                    alert11.show();

                    alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dark_blue_gray));
//                    BWSApplication.showToast( "Document Downloaded Successfully", context);
                } else {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 1000);

                    Log.e(TAG, "Download Failed");

                }
            } catch (Exception e) {
                e.printStackTrace();

                //Change button text if exception occurs

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 1000);
                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());

            }
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());
                }

                //Get File if SD card is present
                if (new CheckForSDCard().isSDCardPresent()) {
                    apkStorage = new File(Environment.getExternalStorageDirectory() + "/" + "BWS");
                } else
                    BWSApplication.showToast("Oops!! There is no SD Card.", getActivity());

                //If File is not present create directory
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }

                outputFile = new File(apkStorage, downloadFileName + ".pdf");//Create Output file in Main File

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }
                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location
                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }
                fos.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }
            return null;
        }
    }
}