/*
package com.brainwellnessspa.ReferralModule.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.brainwellnessspa.BWSApplication;
import com.brainwellnessspa.R;
import com.brainwellnessspa.ReferralModule.Model.ContactlistModel;
import com.brainwellnessspa.ReferralModule.Model.FavContactlistModel;
import com.brainwellnessspa.Utility.CONSTANTS;
import com.brainwellnessspa.databinding.ContactListLayoutBinding;
import com.brainwellnessspa.databinding.FavouriteContactListLayoutBinding;
import com.brainwellnessspa.databinding.FragmentContactBookBinding;
import com.segment.analytics.Properties;

import java.util.ArrayList;
import java.util.List;

public class ContactBookFragment extends Fragment {
    FragmentContactBookBinding binding;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 90;
    String UserPromoCode, ReferLink, UserID;
    EditText searchEditText;
    ContactListAdapter contactListAdapter;
    FavContactListAdapter favContactListAdapter;
    List<ContactlistModel> userList = new ArrayList<>();
    List<FavContactlistModel> favUserList = new ArrayList<>();
    Properties p;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_book, container, false);
        View view = binding.getRoot();

        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        UserID = (shared.getString(CONSTANTS.PREF_KEY_UserID, ""));
        SharedPreferences shareded = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_Referral, Context.MODE_PRIVATE);
        UserPromoCode = (shareded.getString(CONSTANTS.PREF_KEY_UserPromocode, ""));
        ReferLink = (shareded.getString(CONSTANTS.PREF_KEY_ReferLink, ""));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvFavContactList.setLayoutManager(mLayoutManager);
        binding.rvFavContactList.setItemAnimator(new DefaultItemAnimator());
        binding.rvContactList.setHasFixedSize(true);
        RecyclerView.LayoutManager mListLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.rvContactList.setLayoutManager(mListLayoutManager);
        binding.rvContactList.setItemAnimator(new DefaultItemAnimator());
        withoutSearch();
//        binding.llBack.setOnClickListener(v -> finish());
        p = new Properties();
        p.putValue("userId", UserID);
        p.putValue("referLink", ReferLink);
        p.putValue("userReferCode", UserPromoCode);
        BWSApplication.addToSegment("Invite Friends Screen Viewed", p, CONSTANTS.screen);
        binding.searchView.onActionViewExpanded();
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.dark_blue_gray));
        searchEditText.setHintTextColor(getResources().getColor(R.color.gray));
        ImageView closeButton = binding.searchView.findViewById(R.id.search_close_btn);
        binding.searchView.clearFocus();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                binding.searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String search) {
                try {
                    contactListAdapter.getFilter().filter(search);
                    p = new Properties();
                    p.putValue("userId", UserID);
                    p.putValue("searchKeyword", search);
                    BWSApplication.addToSegment("Contact Searched", p, CONSTANTS.track);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        closeButton.setOnClickListener(v -> {
            binding.searchView.clearFocus();
            searchEditText.setText("");
            binding.searchView.setQuery("", false);
        });
        return view;
    }


    @Override
    public void onResume() {
        binding.searchView.clearFocus();
        searchEditText.setText("");
        binding.searchView.setQuery("", false);
        super.onResume();
    }

    private void withoutSearch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS,
                                Manifest.permission.WRITE_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                */
/*Intent intent = new Intent();
                String manufacturer = Build.MANUFACTURER;
                if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                    intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                }

                List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list.size() > 0) {
                    startActivity(intent);
                }*//*

            } else {
                String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER*/
/*, ContactsContract.CommonDataKinds.Phone.PHOTO_URI*//*
};
                Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        projection, null, null,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                String lastPhoneName = " ";
                if (phones.getCount() > 0) {
                    while (phones.moveToNext()) {
                        String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        if (!name.equalsIgnoreCase(lastPhoneName)) {
                            lastPhoneName = name;
                            ContactlistModel user = new ContactlistModel();
                            user.setContactName(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                            user.setContactNumber(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                            userList.add(user);
                        }
                    }
                }
                phones.close();
                contactListAdapter = new ContactListAdapter(userList);
                binding.rvContactList.setAdapter(contactListAdapter);
                getFav();
            }
        }
    }

    private void getFav() {
        String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER*/
/*, ContactsContract.CommonDataKinds.Phone.PHOTO_URI*//*
};
        Cursor cur = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, "starred=?",
                new String[]{"1"}, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        String lastPhoneNameFav = " ";
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                if (!name.equalsIgnoreCase(lastPhoneNameFav)) {
                    lastPhoneNameFav = name;
                    FavContactlistModel user = new FavContactlistModel();
                    user.setContactName(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                    user.setContactNumber(cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    favUserList.add(user);
                }
            }
        }
        cur.close();
        if (favUserList.size() == 0) {
            binding.tvFavorites.setVisibility(View.GONE);
            binding.rvFavContactList.setVisibility(View.GONE);
        } else {
            binding.tvFavorites.setVisibility(View.VISIBLE);
            binding.rvFavContactList.setVisibility(View.VISIBLE);
            favContactListAdapter = new FavContactListAdapter(favUserList);
            binding.rvFavContactList.setAdapter(favContactListAdapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    withoutSearch();
                } else {
                    AlertDialog.Builder buildermain = new AlertDialog.Builder(getActivity());
                    buildermain.setMessage("Please Allow Contact Permission");
                    buildermain.setCancelable(true);
                    buildermain.setPositiveButton(
                            getString(R.string.ok),
                            (dialogmain, id1) -> {
                                dialogmain.dismiss();
                            });
                    AlertDialog alert11 = buildermain.create();
                    alert11.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
                    alert11.show();
                    alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
                }
                return;
            }
        }
    }
    public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> implements Filterable {
        List<ContactlistModel> contactlistModel;
        private List<ContactlistModel> listFilterContact;

        public ContactListAdapter(List<ContactlistModel> contactlistModel) {
            this.contactlistModel = contactlistModel;
            this.listFilterContact = contactlistModel;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ContactListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.contact_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            ContactlistModel model = listFilterContact.get(position);
            holder.binding.tvName.setText(model.getContactName());
            holder.binding.tvNumber.setText(model.getContactNumber());
            holder.binding.BtnInvite.setBackgroundResource(R.drawable.round_gray_cornor_normal);
            holder.binding.BtnInvite.setTextColor(getResources().getColor(R.color.gray));
            holder.binding.BtnInvite.setOnClickListener(v -> {
//                prepareContactData(model.getContactName(), model.getContactNumber());
                notifyDataSetChanged();
                holder.binding.BtnInvite.setBackgroundResource(R.drawable.round_blue_cornor_normal);
                holder.binding.BtnInvite.setTextColor(getResources().getColor(R.color.white));
            });
        }

        @Override
        public int getItemCount() {
            return listFilterContact.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    final FilterResults filterResults = new FilterResults();
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        listFilterContact = contactlistModel;
                    } else {
                        List<ContactlistModel> filteredList = new ArrayList<>();
                        for (ContactlistModel row : contactlistModel) {
                            if (row.getContactName().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }
                        listFilterContact = filteredList;
                    }
                    filterResults.values = listFilterContact;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    if (listFilterContact.size() == 0) {
                        binding.llError.setVisibility(View.VISIBLE);
                        binding.rvContactList.setVisibility(View.GONE);
                    } else {
                        binding.llError.setVisibility(View.GONE);
                        binding.rvContactList.setVisibility(View.VISIBLE);
                        listFilterContact = (ArrayList<ContactlistModel>) filterResults.values;
                        notifyDataSetChanged();
                    }
                }
            };
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ContactListLayoutBinding binding;

            public MyViewHolder(ContactListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public class FavContactListAdapter extends RecyclerView.Adapter<FavContactListAdapter.MyViewHolder> {
        List<FavContactlistModel> favcontactlistModel;

        public FavContactListAdapter(List<FavContactlistModel> contactlistModel) {
            this.favcontactlistModel = contactlistModel;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            FavouriteContactListLayoutBinding v = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , R.layout.favourite_contact_list_layout, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.binding.tvName.setText(favcontactlistModel.get(position).getContactName());
            holder.binding.tvNumber.setText(favcontactlistModel.get(position).getContactNumber());
            holder.binding.cvMainLayout.setOnClickListener(v -> {
                notifyDataSetChanged();
//                prepareContactData(favcontactlistModel.get(position).getContactName(), favcontactlistModel.get(position).getContactNumber());
            });
        }

        @Override
        public int getItemCount() {
            return favcontactlistModel.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            FavouriteContactListLayoutBinding binding;

            public MyViewHolder(FavouriteContactListLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }


}*/
