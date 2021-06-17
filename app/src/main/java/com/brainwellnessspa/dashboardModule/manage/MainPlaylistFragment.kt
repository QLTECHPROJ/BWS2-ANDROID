package com.brainwellnessspa.dashboardModule.manage

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.BWSApplication.DB
import com.brainwellnessspa.DownloadModule.Fragments.AudioDownloadsFragment
import com.brainwellnessspa.R

import com.brainwellnessspa.roomDataBase.DownloadPlaylistDetailsUnique
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.dashboardModule.activities.AddPlaylistActivity
import com.brainwellnessspa.dashboardModule.models.CreateNewPlaylistModel
import com.brainwellnessspa.dashboardModule.models.MainPlaylistLibraryModel
import com.brainwellnessspa.databinding.FragmentPlaylistBinding
import com.brainwellnessspa.databinding.MainPlaylistLayoutBinding
import com.brainwellnessspa.databinding.PlaylistCustomLayoutBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainPlaylistFragment : Fragment() {
    lateinit var binding: FragmentPlaylistBinding
    var CoUserID: String? = ""
    var USERID: String? = ""
    var Check: String? = ""
    var AudioFlag: String? = ""
    var adapter: MainPlayListAdapter? = null
    var listModelGloble: ArrayList<MainPlaylistLibraryModel.ResponseData>? = null
    var PlaylistSource = ""
    var playlistAdapter: PlaylistAdapter? = null
    var ComeScreenMyPlaylist = 0
    lateinit var ctx: Context
    lateinit var act: Activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false)
        val view = binding.root
        ctx = requireActivity()
        act = requireActivity()
        val shared =
            ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
        AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        AudioDownloadsFragment.comefromDownload = "0"
        if (arguments != null) {
            Check = requireArguments().getString("Check")
        }

        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                callBack()
                return@setOnKeyListener true
            }
            false
        }

        binding.llBack.setOnClickListener { callBack() }
        val measureRatio = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.38f, 0f)
        binding.ivCreatePlaylist.layoutParams.height =
            (measureRatio.height * measureRatio.ratio).toInt()
        binding.ivCreatePlaylist.layoutParams.width =
            (measureRatio.widthImg * measureRatio.ratio).toInt()
        binding.ivCreatePlaylist.scaleType = ImageView.ScaleType.FIT_XY
        Glide.with(requireActivity()).load(R.drawable.ic_create_playlist)
            .thumbnail(0.05f)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
            .into(binding.ivCreatePlaylist)
        val manager: RecyclerView.LayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvMainPlayList.layoutManager = manager
        binding.rvMainPlayList.itemAnimator = DefaultItemAnimator()
        prepareData("onCreateView")
        return view
    }

    private fun callMyPlaylistsFragment(
        s: String,
        id: String,
        name: String,
        playlistImage: String,
        MyDownloads: String,
        ScreenView: String,
        act: Activity,
        ctx: Context
    ) {
//        try {
        val i = Intent(ctx, MyPlaylistListingActivity::class.java)
        i.putExtra("New", s)
        i.putExtra("PlaylistID", id)
        i.putExtra("PlaylistName", name)
        i.putExtra("PlaylistImage", playlistImage)
        i.putExtra("PlaylistSource", PlaylistSource)
        i.putExtra("MyDownloads", MyDownloads)
        i.putExtra("ScreenView", ScreenView)
        i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        ctx.startActivity(i)
        act.overridePendingTransition(0, 0)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    private fun prepareData(comeFrom: String) {
        if (BWSApplication.isNetworkConnected(activity)) {
            BWSApplication.showProgressBar(
                binding.progressBar,
                binding.progressBarHolder,
                activity
            )
            val listCall = APINewClient.getClient().getMainPlayLists(CoUserID)
            listCall.enqueue(object : Callback<MainPlaylistLibraryModel?> {
                override fun onResponse(
                    call: Call<MainPlaylistLibraryModel?>,
                    response: Response<MainPlaylistLibraryModel?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            BWSApplication.hideProgressBar(
                                binding.progressBar,
                                binding.progressBarHolder,
                                activity
                            )
                            val listModel = response.body()
                            binding.rlCreatePlaylist.visibility = View.VISIBLE
                            listModelGloble = listModel!!.responseData
                            //                            adapter = new MainPlayListAdapter();
//                            binding.rvMainPlayList.setAdapter(adapter);

                            //                            adapter = new MainPlayListAdapter();
//                            binding.rvMainPlayList.setAdapter(adapter);
                            GetPlaylistDetail(listModel.responseData!!)
                            val section = ArrayList<String>()
                            for (i in listModel.responseData!!.indices) {
                                section.add(listModel.responseData!![i].view!!)
                            }

                            if (comeFrom.equals("onResume", ignoreCase = true)) {
                                val p = Properties()
                                p.putValue("userId", CoUserID)
                                val gson: Gson
                                val gsonBuilder = GsonBuilder()
                                gson = gsonBuilder.create()
                                p.putValue("sections", gson.toJson(section))
                                BWSApplication.addToSegment(
                                    "Playlist Screen Viewed",
                                    p,
                                    CONSTANTS.screen
                                )
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<MainPlaylistLibraryModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(
                        binding.progressBar,
                        binding.progressBarHolder,
                        activity
                    )
                }
            })
        } else {
            val responseData = ArrayList<MainPlaylistLibraryModel.ResponseData>()
            val details = ArrayList<MainPlaylistLibraryModel.ResponseData.Detail>()
            val listModel = MainPlaylistLibraryModel.ResponseData()
            listModel.getLibraryID = "2"
            listModel.details = details
            listModel.userId = CoUserID
            listModel.view = "My Downloads"
            responseData.add(listModel)
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    private fun GetPlaylistDetail(responseData: ArrayList<MainPlaylistLibraryModel.ResponseData>) {
        DB.taskDao()
            .getAllPlaylist1(CoUserID)
        .observe(
                requireActivity(),
                { audioList: List<DownloadPlaylistDetailsUnique> ->
                    val details = ArrayList<MainPlaylistLibraryModel.ResponseData.Detail>()
                    if (audioList.isNotEmpty()) {
                        for (i in audioList.indices) {
                            val detail = MainPlaylistLibraryModel.ResponseData.Detail()
                            detail.totalAudio = audioList[i].totalAudio
                            detail.totalhour = audioList[i].totalhour
                            detail.totalminute = audioList[i].totalminute
                            detail.playlistID = audioList[i].playlistID
                            detail.playlistDesc = audioList[i].playlistDesc
                            detail.playlistMastercat = audioList[i].playlistMastercat
                            detail.playlistSubcat = audioList[i].playlistSubcat
                            detail.playlistName = audioList[i].playlistName
                            detail.playlistImage = audioList[i].playlistImage
//                            detail.playlistImageDetails = audioList[i].playlistImageDetails
                            detail.playlistID = audioList[i].playlistID
                            detail.created = audioList[i].created
                            details.add(detail)
                        }
                        for (i in responseData.indices) {
                            if (responseData[i].view.equals("My Downloads", ignoreCase = true)) {
                                responseData[i].details = details
                            }
                        }
                        adapter = MainPlayListAdapter(
                            ctx,
                            binding,
                            act,
                            responseData,
                            CoUserID,
                            playlistAdapter
                        )
                        binding.rvMainPlayList.adapter = adapter
                    } else {
                        if (BWSApplication.isNetworkConnected(activity)) {
                            adapter = MainPlayListAdapter(
                                ctx,
                                binding,
                                act,
                                responseData,
                                CoUserID,
                                playlistAdapter
                            )
                            binding.rvMainPlayList.adapter = adapter
                        }
                    }
                })
    }

    private fun callBack() {
        val fragmentManager1: FragmentManager = (ctx as FragmentActivity).supportFragmentManager
        val audioFragment: Fragment = ManageFragment()
        fragmentManager1.beginTransaction()
            .replace(R.id.flContainer, audioFragment)
            .commit()
    }

    override fun onResume() {
        prepareData("onResume")
        super.onResume()
    }

    class MainPlayListAdapter(
        var ctx: Context,
        var binding: FragmentPlaylistBinding,
        var act: Activity,
        var listModel: ArrayList<MainPlaylistLibraryModel.ResponseData>,
        var CoUserID: String?,
        var playlistAdapter: PlaylistAdapter?
    ) : RecyclerView.Adapter<MainPlayListAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MainPlaylistLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.main_playlist_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            if (listModel[position].details != null &&
                listModel[position].details!!.size > 6
            ) {
                holder.binding.tvViewAll.visibility = (View.VISIBLE)
            } else {
                holder.binding.tvViewAll.visibility = (View.GONE)
            }
            holder.binding.tvViewAll.setOnClickListener {
                val viewAllPlaylistFragment: Fragment =
                    ViewAllPlaylistFragment()
                val fragmentManager1: FragmentManager =
                    (ctx as FragmentActivity).supportFragmentManager
                fragmentManager1.beginTransaction()
                    .replace(R.id.flContainer, viewAllPlaylistFragment)
                    .commit()
                val bundle = Bundle()
                if (listModel[position].view.equals("My Downloads", ignoreCase = true)) {
                    bundle.putString("MyDownloads", "1")
                } else {
                    bundle.putString("MyDownloads", "0")
                }
                bundle.putString("GetLibraryID", listModel[position].getLibraryID)
                bundle.putString("Name", listModel[position].view)
                viewAllPlaylistFragment.arguments = bundle
            }
            binding.rlCreatePlaylist.setOnClickListener {
                val dialog = Dialog(ctx)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.create_palylist)
                dialog.window!!.setBackgroundDrawable(
                    ColorDrawable(
                        ContextCompat.getColor(
                            ctx,
                            R.color.blue_transparent
                        )
                    )
                )
                dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                val edtCreate = dialog.findViewById<EditText>(R.id.edtCreate)
                val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
                val btnSendCode = dialog.findViewById<Button>(R.id.btnSendCode)
                edtCreate.clearFocus()
                val popupTextWatcher: TextWatcher = object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        val number = edtCreate.text.toString().trim { it <= ' ' }
                        if (number.isNotEmpty()) {
                            btnSendCode.isEnabled = true
                            btnSendCode.setTextColor(ContextCompat.getColor(ctx,R.color.black))
                            btnSendCode.setBackgroundResource(R.drawable.white_round_cornor)
                        } else {
                            btnSendCode.isEnabled = false
                            btnSendCode.setTextColor(ContextCompat.getColor(ctx,R.color.white))
                            btnSendCode.setBackgroundResource(R.drawable.gray_round_cornor)
                        }
                    }

                    override fun afterTextChanged(s: Editable) {}
                }
                edtCreate.addTextChangedListener(popupTextWatcher)
                dialog.setOnKeyListener { _: DialogInterface?, keyCode: Int, _: KeyEvent? ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss()
                        return@setOnKeyListener true
                    }
                    false
                }
                btnSendCode.setOnClickListener {
                    if (BWSApplication.isNetworkConnected(ctx)) {
                        BWSApplication.showProgressBar(
                            binding.progressBar,
                            binding.progressBarHolder,
                            act
                        )
                        val listCall = APINewClient.getClient()
                            .getCreatePlaylist(CoUserID, edtCreate.text.toString())
                        listCall.enqueue(object : Callback<CreateNewPlaylistModel?> {
                            override fun onResponse(
                                call: Call<CreateNewPlaylistModel?>,
                                response: Response<CreateNewPlaylistModel?>
                            ) {
                                try {
                                    BWSApplication.hideProgressBar(
                                        binding.progressBar,
                                        binding.progressBarHolder,
                                        act
                                    )
                                    if (response.isSuccessful) {
                                        val listModel = response.body()
                                        if (listModel!!.responseData!!.iscreate.equals(
                                                "0",
                                                ignoreCase = true
                                            )
                                        ) {
                                            BWSApplication.showToast(listModel.responseMessage, act)
//                                            dialog.dismiss()
                                        } else if (listModel.responseData!!.iscreate.equals(
                                                "1",
                                                ignoreCase = true
                                            ) ||
                                            listModel.responseData!!.iscreate.equals(
                                                "",
                                                ignoreCase = true
                                            )
                                        ) {
                                            MainPlaylistFragment().callMyPlaylistsFragment(
                                                "1",
                                                listModel.responseData!!.playlistID.toString(),
                                                listModel.responseData!!.playlistName.toString(),
                                                "",
                                                "0",
                                                "Your Created",
                                                act,
                                                ctx
                                            )
                                            dialog.dismiss()
                                        }
                                    }
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }

                            override fun onFailure(
                                call: Call<CreateNewPlaylistModel?>,
                                t: Throwable
                            ) {
                                BWSApplication.hideProgressBar(
                                    binding.progressBar,
                                    binding.progressBarHolder,
                                    act
                                )
                            }
                        })
                    } else {
                        BWSApplication.showToast(ctx.getString(R.string.no_server_found), act)
                    }
                }
                tvCancel.setOnClickListener { dialog.dismiss() }
                dialog.show()
                dialog.setCancelable(false)
            }
            val manager: RecyclerView.LayoutManager =
                LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
            holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
            holder.binding.rvMainAudio.layoutManager = manager
            if (listModel[position].details!!.size == 0) {
                holder.binding.llMainLayout.visibility = View.GONE
            } else {
                holder.binding.llMainLayout.visibility = View.VISIBLE
                holder.binding.tvTitle.text = listModel[position].view
                when {
                    listModel[position].view.equals(
                        ctx.getString(R.string.your_created),
                        ignoreCase = true
                    ) -> {
                        playlistAdapter = PlaylistAdapter(
                            listModel[position].details!!,
                            ctx,
                            act,
                            "0",
                            listModel[position].view!!
                        )
                        holder.binding.rvMainAudio.adapter = playlistAdapter
                    }
                    listModel[position].view.equals("My Downloads", ignoreCase = true) -> {
                        playlistAdapter = PlaylistAdapter(
                            listModel[position].details!!,
                            ctx,
                            act,
                            "1",
                            listModel[position].view!!
                        )
                        holder.binding.rvMainAudio.adapter = playlistAdapter
                    }
                    listModel[position].view.equals(
                        ctx.getString(R.string.Recommended_Playlist),
                        ignoreCase = true
                    ) -> {
                        playlistAdapter = PlaylistAdapter(
                            listModel[position].details!!, ctx,
                            act, "0", listModel[position].view!!
                        )
                        holder.binding.rvMainAudio.adapter = playlistAdapter
                    }
                    listModel[position].view.equals(
                        ctx.getString(R.string.populars),
                        ignoreCase = true
                    ) -> {
                        playlistAdapter = PlaylistAdapter(
                            listModel[position].details!!, ctx,
                            act, "0", listModel[position].view!!
                        )
                        holder.binding.rvMainAudio.adapter = playlistAdapter
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return listModel.size
        }

        inner class MyViewHolder(var binding: MainPlaylistLayoutBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    class PlaylistAdapter(
        private val listModel: ArrayList<MainPlaylistLibraryModel.ResponseData.Detail>,
        var ctx: Context,
        var act: Activity,
        var MyDownloads: String,
        var screenView: String
    ) : RecyclerView.Adapter<PlaylistAdapter.MyViewHolder>() {
        var index = -1
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PlaylistCustomLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.playlist_custom_layout, parent, false
            )
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val measureRatio = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.38f, 0f)
            holder.binding.ivRestaurantImage.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.tvAddToPlaylist.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            val measureRatio1 = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.38f, 0f)
            holder.binding.rlMainLayout.layoutParams.height =
                (measureRatio1.height * measureRatio1.ratio).toInt()
            holder.binding.rlMainLayout.layoutParams.width =
                (measureRatio1.widthImg * measureRatio1.ratio).toInt()
            holder.binding.tvPlaylistName.text = listModel[position].playlistName
            Glide.with(ctx).load(listModel[position].playlistImage).thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(42))).priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false)
                .into(holder.binding.ivRestaurantImage)

            if (index == position) {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
            } else holder.binding.tvAddToPlaylist.visibility = View.GONE

            holder.binding.tvAddToPlaylist.text = "Add To Playlist"

            holder.binding.rlMainLayout.setOnLongClickListener {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
                index = position
                notifyDataSetChanged()
                true
            }

            holder.binding.tvAddToPlaylist.setOnClickListener {
                val i = Intent(ctx, AddPlaylistActivity::class.java)
                i.putExtra("AudioId", "")
                i.putExtra("ScreenView", "Playlist View All Screen")
                i.putExtra("PlaylistID", listModel[position].playlistID)
                i.putExtra("PlaylistName", "")
                i.putExtra("PlaylistImage", "")
                i.putExtra("PlaylistType", "")
                i.putExtra("Liked", "0")
                ctx.startActivity(i)
            }

            holder.binding.rlMainLayout.setOnClickListener {
                if (MyDownloads.equals("1", ignoreCase = true)) {
                    MainPlaylistFragment().callMyPlaylistsFragment(
                        "0",
                        listModel[position].playlistID!!,
                        listModel[position].playlistName!!,
                        listModel[position].playlistImage!!,
                        MyDownloads,
                        "Downloaded Playlists",
                        act,
                        ctx
                    )
                } else {
                    MainPlaylistFragment().callMyPlaylistsFragment(
                        "0", listModel[position].playlistID!!, listModel[position].playlistName!!,
                        listModel[position].playlistImage!!, MyDownloads, screenView, act, ctx
                    )
                }
            }
        }

        override fun getItemCount(): Int {
            return if (6 > listModel.size) {
                listModel.size
            } else {
                6
            }
        }

        inner class MyViewHolder(var binding: PlaylistCustomLayoutBinding) :
            RecyclerView.ViewHolder(binding.root)
    }
}