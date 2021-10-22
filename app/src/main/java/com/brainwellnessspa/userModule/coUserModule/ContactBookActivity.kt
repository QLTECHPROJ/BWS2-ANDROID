package com.brainwellnessspa.userModule.coUserModule

import android.Manifest
import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.R
import com.brainwellnessspa.databinding.ActivityContactBookBinding
import com.brainwellnessspa.databinding.ContactListLayoutBinding
import com.brainwellnessspa.databinding.FavouriteContactListLayoutBinding
import com.brainwellnessspa.referralModule.models.ContactlistModel
import com.brainwellnessspa.referralModule.models.FavContactlistModel
import com.brainwellnessspa.services.GlobalInitExoPlayer.Companion.relesePlayer
import com.brainwellnessspa.userModule.models.SetInviteUserModel

import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ContactBookActivity : AppCompatActivity() {
    lateinit var binding: ActivityContactBookBinding
    lateinit var ctx: Context
    var userPromoCode: String? = null
    var referLink: String? = null
    var userId: String? = null
    var mainAccountID: String? = null
    lateinit var activity: Activity
    lateinit var searchEditText: EditText
    var contactListAdapter: ContactListAdapter? = null
    var favContactListAdapter: FavContactListAdapter? = null
    private var userList: MutableList<ContactlistModel> = ArrayList()
    var favUserList: MutableList<FavContactlistModel> = ArrayList()
    var p: Properties = Properties()
    var stackStatus = 0
    var myBackPress = false
    private var numStarted = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_book)
        ctx = this@ContactBookActivity
        activity = this@ContactBookActivity
        val shared = getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        mainAccountID = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        userId = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        binding.rvFavContactList.layoutManager = mLayoutManager
        binding.rvFavContactList.itemAnimator = DefaultItemAnimator()
        binding.rvContactList.setHasFixedSize(true)
        val mListLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        binding.rvContactList.layoutManager = mListLayoutManager
        binding.rvContactList.itemAnimator = DefaultItemAnimator()
        withoutSearch()

        binding.llBack.setOnClickListener {
            IsFirstClick = "0"
            myBackPress = true
            val i = Intent(applicationContext, AddCouserActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(i)
            finish()
        }

        p = Properties()
        p.putValue("userId", userId)
        p.putValue("referLink", referLink)
        p.putValue("userReferCode", userPromoCode)
        addToSegment("Invite Friends Screen Viewed", p, CONSTANTS.screen)
        binding.searchView.onActionViewExpanded()
        searchEditText = binding.searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(ContextCompat.getColor(activity, R.color.dark_blue_gray))
        searchEditText.setHintTextColor(ContextCompat.getColor(activity, R.color.gray))
        val closeButton = binding.searchView.findViewById<ImageView>(R.id.search_close_btn)
        binding.searchView.clearFocus()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(search: String): Boolean {
                binding.searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(search: String): Boolean {
                try {
                    p = Properties()
                    p.putValue("userId", userId)
                    p.putValue("searchKeyword", search)
                    addToSegment("Contact Searched", p, CONSTANTS.track)
                    contactListAdapter!!.filter.filter(search)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return false
            }
        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(AppLifecycleCallback())
        }
        closeButton.setOnClickListener {
            binding.searchView.clearFocus()
            searchEditText.setText("")
            binding.searchView.setQuery("", false)
            binding.llError.visibility = View.GONE
            binding.rvContactList.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        binding.searchView.clearFocus()
        searchEditText.setText("")
        binding.searchView.setQuery("", false)
        super.onResume()
    }

    override fun onBackPressed() {
        IsFirstClick = "0"
        myBackPress = true
        val i = Intent(applicationContext, AddCouserActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(i)
        finish()
    }

    private fun withoutSearch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS), MY_PERMISSIONS_REQUEST_READ_CONTACTS)
            } else {
                showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                Handler(Looper.getMainLooper()).postDelayed({
                    val projection = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER /*, ContactsContract.CommonDataKinds.Phone.PHOTO_URI*/)
                    val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
                    val cur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, "starred=?", arrayOf("1"), ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
                    var lastPhoneName: String? = " "
                    if (phones != null) {
                        if (phones.count > 0) {
                            while (phones.moveToNext()) {
                                val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                                if (!name.equals(lastPhoneName, ignoreCase = true)) {
                                    lastPhoneName = name
                                    val user = ContactlistModel()
                                    user.contactName = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                                    user.contactNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                    userList.add(user)
                                }
                            }
                        }
                    }
                    phones!!.close()
                    contactListAdapter = ContactListAdapter(userList)
                    binding.rvContactList.adapter = contactListAdapter
                    var lastPhoneNameFav: String? = " "
                    if (cur != null) {
                        if (cur.count > 0) {
                            while (cur.moveToNext()) {
                                val name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                                if (!name.equals(lastPhoneNameFav, ignoreCase = true)) {
                                    lastPhoneNameFav = name
                                    val user = FavContactlistModel()
                                    user.contactName = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                                    user.contactNumber = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                    favUserList.add(user)
                                }
                            }
                        }
                    }
                    if (favUserList.size == 0) {
                        binding.tvFavorites.visibility = View.GONE
                        binding.rvFavContactList.visibility = View.GONE
                    } else {
                        binding.tvFavorites.visibility = View.VISIBLE
                        binding.rvFavContactList.visibility = View.VISIBLE
                        favContactListAdapter = FavContactListAdapter(favUserList)
                        binding.rvFavContactList.adapter = favContactListAdapter
                    }
                    cur!!.close()
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }, (2 * 1000).toLong())
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    withoutSearch()
                } else {
                    myBackPress = true
                    val buildermain = AlertDialog.Builder(ctx)
                    buildermain.setMessage("Please Allow Contact Permission")
                    buildermain.setCancelable(true)
                    buildermain.setPositiveButton(getString(R.string.ok)) { dialogs: DialogInterface, _: Int ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                        dialogs.dismiss()
                    }
                    val alert11 = buildermain.create()
                    alert11.window!!.setBackgroundDrawableResource(R.drawable.dialog_bg)
                    alert11.show()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alert11.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, R.color.blue))
                    }
                }
                return
            }
        }
    }

    inner class ContactListAdapter(var contactlistModel: List<ContactlistModel>) : RecyclerView.Adapter<ContactListAdapter.MyViewHolder>(), Filterable {
        private var listFilterContact: List<ContactlistModel>
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: ContactListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.contact_list_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val model = listFilterContact[position]
            holder.binding.tvName.text = model.contactName
            holder.binding.tvNumber.text = model.contactNumber
            holder.binding.BtnInvite.setBackgroundResource(R.drawable.round_gray_cornor_normal)
            holder.binding.BtnInvite.setTextColor(ContextCompat.getColor(activity, R.color.gray))
            holder.binding.BtnInvite.setOnClickListener {
                myBackPress = true
                notifyDataSetChanged()
                setContactInvite(model.contactName.toString(), model.contactNumber.toString(), holder.binding.BtnInvite, "0")
            }
        }

        override fun getItemCount(): Int {
            return listFilterContact.size
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(charSequence: CharSequence): FilterResults {
                    val filterResults = FilterResults()
                    val charString = charSequence.toString()
                    listFilterContact = if (charString.isEmpty()) {
                        contactlistModel
                    } else {
                        val filteredList: MutableList<ContactlistModel> = ArrayList()
                        for (row in contactlistModel) {
                            if (row.contactName!!.toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row)
                            }
                        }
                        filteredList
                    }
                    filterResults.values = listFilterContact
                    return filterResults
                }

                override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                    if (listFilterContact.isEmpty()) {
                        binding.llError.visibility = View.VISIBLE
                        binding.rvContactList.visibility = View.GONE
                    } else {
                        binding.llError.visibility = View.GONE
                        binding.rvContactList.visibility = View.VISIBLE
                        listFilterContact = filterResults.values as ArrayList<ContactlistModel>
                        notifyDataSetChanged()
                    }
                }
            }
        }

        inner class MyViewHolder(var binding: ContactListLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        init {
            listFilterContact = contactlistModel
        }
    }

    inner class FavContactListAdapter(private var favcontactlistModel: List<FavContactlistModel>) : RecyclerView.Adapter<FavContactListAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: FavouriteContactListLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.favourite_contact_list_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvName.text = favcontactlistModel[position].contactName
            holder.binding.tvNumber.text = favcontactlistModel[position].contactNumber
            holder.binding.cvMainLayout.setOnClickListener {
                myBackPress = true
                notifyDataSetChanged()
                setContactInvite(favcontactlistModel[position].contactName.toString(), favcontactlistModel[position].contactNumber.toString(), null, "1")
            }
        }

        override fun getItemCount(): Int {
            return favcontactlistModel.size
        }

        inner class MyViewHolder(var binding: FavouriteContactListLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }

    private fun setContactInvite(contactName: String, contactNumber: String, BtnInvite: Button?, isFav: String) {
        var number: String = contactNumber
        number = number.replaceAfter("\\D", "")
        val firstTwoChars: String = number.substring(0, 2)
        /*if (number.length > 10 && firstTwoChars.equals("+91", ignoreCase = true)) {
            number = number.substring(2, number.length)
        }else*/

        number = if (number.length > 10 && firstTwoChars.equals("91", ignoreCase = true)) {
            number.substring(2, number.length)
        } else if (number.length > 9 && firstTwoChars.equals("61", ignoreCase = true)) {
            number.substring(2, number.length)
        } else number
        if (isNetworkConnected(activity)) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, activity)
            val listCall: Call<SetInviteUserModel> = APINewClient.client.getSetInviteUser(userId, contactName, number)
            listCall.enqueue(object : Callback<SetInviteUserModel> {
                override fun onResponse(call: Call<SetInviteUserModel>, response: Response<SetInviteUserModel>) {
                    try {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                        val listModel: SetInviteUserModel = response.body()!!
                        when {
                            listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true) -> {
                                Log.e("contactNumber", number)
                                p = Properties()
                                p.putValue("userId", userId)
                                p.putValue("referLink", referLink)
                                p.putValue("userReferCode", userPromoCode)
                                p.putValue("contactName", contactName)
                                p.putValue("contactNumber", number)
                                addToSegment("Invite Friend Clicked", p, CONSTANTS.track)
                                if (BtnInvite != null) {
                                    if (isFav.equals("0", ignoreCase = true)) {
                                        BtnInvite.setBackgroundResource(R.drawable.round_blue_cornor_normal)
                                        BtnInvite.setBackgroundResource(R.drawable.round_blue_cornor_normal)
                                        BtnInvite.setTextColor(ContextCompat.getColor(activity, R.color.white))
                                    }
                                }
                                val referLink = listModel.responseData?.inviteLink
                                val uri = Uri.parse("smsto:$number")
                                val smsIntent = Intent(Intent.ACTION_SENDTO, uri)
                                // smsIntent.setData(uri);
                                smsIntent.putExtra("sms_body", "Hey, I am loving using the Brain Wellness App. You can develop yourself in the comfort of your home while you sleep and gain access to over 75 audio programs helping you to live inspired and improve your mental wellbeing. I would like to invite you to try it. Sign up using the link and get 14 days free trial \n$referLink")
                                startActivity(smsIntent)
                                finish()

                            }
                            listModel.responseCode.equals(getString(R.string.ResponseCodeDeleted), ignoreCase = true) -> {
                                callDelete403(activity, listModel.responseMessage)
                            }
                            else -> {
                                showToast(listModel.responseMessage, activity)
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<SetInviteUserModel>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, activity)
                }
            })
        } else {
            showToast(getString(R.string.no_server_found), activity)
        }

    }

    internal inner class AppLifecycleCallback : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {
            if (numStarted == 0) {
                stackStatus = 1
                Log.e("APPLICATION", "APP IN FOREGROUND")
                //app went to foreground
            }
            numStarted++
        }

        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {
            numStarted--
            if (numStarted == 0) {
                if (!myBackPress) {
                    Log.e("APPLICATION", "Back press false")
                    stackStatus = 2
                } else {
                    myBackPress = true
                    stackStatus = 1
                    Log.e("APPLICATION", "back press true ")
                }
                Log.e("APPLICATION", "App is in BACKGROUND")
                // app went to background
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {
            if (numStarted == 0 && stackStatus == 2) {
                Log.e("Destroy", "Activity Destroyed")
                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notificationId)
                relesePlayer(applicationContext)
            } else {
                Log.e("Destroy", "Activity go in main activity")
            }
        }
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 90
    }
}