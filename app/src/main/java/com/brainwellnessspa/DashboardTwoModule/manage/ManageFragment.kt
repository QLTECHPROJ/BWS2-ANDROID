package com.brainwellnessspa.DashboardTwoModule.manage;

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.DashboardTwoModule.fragmentAudio.ViewAllAudioFragment
import com.brainwellnessspa.DashboardModule.Activities.DashboardActivity.audioClick
import com.brainwellnessspa.DashboardTwoModule.Model.HomeDataModel
import com.brainwellnessspa.DashboardTwoModule.MyPlayerActivity
import com.brainwellnessspa.DashboardTwoModule.fragmentPlaylist.MainPlaylistFragment
import com.brainwellnessspa.DashboardTwoModule.fragmentPlaylist.MyPlaylistListingActivity

import com.brainwellnessspa.R;
import com.brainwellnessspa.Services.GlobalInitExoPlayer.player
import com.brainwellnessspa.Utility.APINewClient
import com.brainwellnessspa.Utility.CONSTANTS
import com.brainwellnessspa.databinding.*
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

public class ManageFragment: Fragment() {
    lateinit var binding:FragmentManageBinding
    lateinit var ctx: Context
    lateinit var act: Activity
    lateinit var audioAdapter: AudioAdapter
    lateinit var playlistAdapter: PlaylistAdapter
    var CoUserID: String? = ""
    var USERID: String? = ""
    var homelistModel: HomeDataModel = HomeDataModel()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage, container, false)
        val view:View = binding.root
        ctx = requireActivity()
        act = requireActivity()
        val shared = ctx.getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, AppCompatActivity.MODE_PRIVATE)
        USERID = shared.getString(CONSTANTS.PREFE_ACCESS_UserID, "")
        CoUserID = shared.getString(CONSTANTS.PREFE_ACCESS_CoUserID, "")
        binding.rvMainPlayList.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMainAudioList.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        return view
    }

    override fun onResume() {
        prepareData()
        super.onResume()
    }

    fun callMainPlayer(position: Int, view: String?, listModel: List<HomeDataModel.ResponseData.Audio.Detail>, ctx: Context, act: Activity) {
        val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE)
        val AudioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        val MyPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PayerPlaylistId, "")
        val PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "")
        var PlayerPosition:Int = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
        if ((AudioPlayerFlag.equals("MainAudioList", ignoreCase = true) ||
                        AudioPlayerFlag.equals("ViewAllAudioList", ignoreCase = true)) && MyPlaylist.equals(view, ignoreCase = true)) {
            if(PlayFrom.equals(view, true)){
                if (player != null) {
                    if (position != PlayerPosition) {
                        player.seekTo(position, 0)
                        player.playWhenReady = true
                        val sharedxx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE)
                        val editor = sharedxx.edit()
                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                        editor.apply()
                    }
                    callMyPlayer(ctx,act)
                }else{
                    callPlayer(position,view,listModel,ctx,act)
                }
            }else{
                callPlayer(position,view,listModel,ctx,act)
            }
        }else{
            callPlayer(position,view,listModel,ctx,act)
        }
    }

    private fun callMyPlayer(ctx: Context, act: Activity) {
        val i = Intent(ctx, MyPlayerActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        ctx.startActivity(i)
        act.overridePendingTransition(0, 0)
    }

    private fun callPlayer(position: Int, view: String?, listModel: List<HomeDataModel.ResponseData.Audio.Detail>, ctx: Context,act: Activity) {
        val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, AppCompatActivity.MODE_PRIVATE)
        val editor = shared.edit()
        val gson = Gson()
        val json = gson.toJson(listModel)
        editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json)
        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
        editor.putString(CONSTANTS.PREF_KEY_PayerPlaylistId, "")
        editor.putString(CONSTANTS.PREF_KEY_PlayFrom, view)
        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "MainAudioList")
        editor.apply()
        audioClick = true
        callMyPlayer(ctx,act)
    }

    private fun prepareData() {
        if (BWSApplication.isNetworkConnected(ctx)) {
            BWSApplication.showProgressBar(binding.progressBar, binding.progressBarHolder,act)
            val listCall = APINewClient.getClient().getHomeData(CoUserID)
            listCall.enqueue(object : Callback<HomeDataModel?> {
                override fun onResponse(call: Call<HomeDataModel?>, response: Response<HomeDataModel?>) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)

                    val listModel = response.body()!!
                    homelistModel = response.body()!!
                    val measureRatio = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.38f, 0f)
                    binding.ivCreatePlaylist.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
                    binding.ivCreatePlaylist.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
                    binding.ivCreatePlaylist.scaleType = ImageView.ScaleType.FIT_XY
                    Glide.with(ctx).load(R.drawable.ic_create_playlist).thumbnail(0.05f)
                            .apply(RequestOptions.bitmapTransform(RoundedCorners(2))).priority(Priority.HIGH)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(binding.ivCreatePlaylist)
                    playlistAdapter = PlaylistAdapter(listModel.responseData!!.playlist[0], ctx, binding, act)
                    binding.rvMainPlayList.adapter = playlistAdapter

                    if (listModel.responseData!!.playlist[0].details!!.size > 4) {
                        binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        binding.tvViewAll.visibility = View.GONE
                    }
                    val fragmentManager1: FragmentManager = (ctx as FragmentActivity).supportFragmentManager
                    binding.tvViewAll.setOnClickListener {
                        val playlistFragment: Fragment = MainPlaylistFragment()
                        fragmentManager1.beginTransaction()
                                .replace(R.id.flContainer, playlistFragment)
                                .commit()
                        /*val bundle = Bundle()
                        if (listModel.responseData!!.playlist[0].view.equals("My Downloads", ignoreCase = true)) {
                            bundle.putString("MyDownloads", "1")
                        } else {
                            bundle.putString("MyDownloads", "0")
                        }
                        bundle.putString("GetLibraryID", listModel.responseData!!.playlist[0].getLibraryID)
                        bundle.putString("Name", listModel.responseData!!.playlist[0].view)
                        val viewAllAudioFragment: Fragment = ViewAllPlaylistFragment()
                        viewAllAudioFragment.arguments = bundle
                        fragmentManager1.beginTransaction()
                                .replace(R.id.flContainer, viewAllAudioFragment)
                                .commit()*/
                    }
                    audioAdapter = AudioAdapter(listModel.responseData!!.audio, ctx, binding, act, fragmentManager1)
                    binding.rvMainAudioList.adapter = audioAdapter

                }

                override fun onFailure(call: Call<HomeDataModel?>, t: Throwable) {
                    BWSApplication.hideProgressBar(binding.progressBar, binding.progressBarHolder, act)
                }
            })
        }
    }

    class AudioAdapter(private val listModel: List<HomeDataModel.ResponseData.Audio>, private val ctx: Context, var binding: FragmentManageBinding, val act: Activity, var fragmentManager1: FragmentManager) : RecyclerView.Adapter<AudioAdapter.MyViewHolder>() {

        inner class MyViewHolder(var binding: MainAudioLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: MainAudioLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.main_audio_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvViewAll.setOnClickListener { view ->
                val bundle = Bundle()
                bundle.putString("ID", listModel[position].homeAudioID)
                bundle.putString("Name", listModel[position].view)
                bundle.putString("Category", "")
                val viewAllAudioFragment: Fragment = ViewAllAudioFragment()
                viewAllAudioFragment.arguments = bundle
                fragmentManager1.beginTransaction()
                        .replace(R.id.flContainer, viewAllAudioFragment)
                        .commit()
            }

            if (listModel[position].details!!.isEmpty()) {
                holder.binding.llMainLayout.visibility = View.GONE
            } else {
                holder.binding.llMainLayout.visibility = View.VISIBLE
                holder.binding.tvTitle.text = listModel[position].view
                if (listModel[position].view.equals("My Downloads", ignoreCase = true)) {
                    val myDownloads: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = myDownloads
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    val myDownloadsAdapter = DownloadAdapter(listModel[position].details!!, ctx, binding, act, listModel[position].view)
                    holder.binding.rvMainAudio.adapter = myDownloadsAdapter
                    if (listModel[position].details != null &&
                            listModel[position].details!!.size > 4) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(ctx.getString(R.string.Library), ignoreCase = true)) {
                    val recommendedAdapter = LibraryAdapter(listModel[position].details!!, ctx, binding, act, listModel[position].view)
                    val recommended: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = recommended
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = recommendedAdapter
                    if (listModel[position].details != null &&
                            listModel[position].details!!.size > 4) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(ctx.getString(R.string.my_like), ignoreCase = true)) {
                    holder.binding.llMainLayout.visibility = View.GONE
                    /*RecentlyPlayedAdapter recentlyPlayedAdapter = new RecentlyPlayedAdapter(listModel.details!!, ctx);
                    RecyclerView.LayoutManager recentlyPlayed = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
                    holder.binding.rvMainAudio.setLayoutManager(recentlyPlayed);
                    holder.binding.rvMainAudio.setItemAnimator(new DefaultItemAnimator());
                    holder.binding.rvMainAudio.setAdapter(recentlyPlayedAdapter);*/
                } else if (listModel[position].view.equals(ctx.getString(R.string.recently_played), ignoreCase = true)) {
                    val recentlyPlayedAdapter = RecentlyPlayedAdapter(listModel[position].details!!, ctx, binding, act, listModel[position].view)
                    val recentlyPlayed: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = recentlyPlayed
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = recentlyPlayedAdapter
                    if (listModel[position].details!!.size > 6) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(ctx.getString(R.string.get_inspired), ignoreCase = true)) {
                    val recommendedAdapter = RecommendedAdapter(listModel[position].details!!, ctx, binding, act, listModel[position].view)
                    val inspired: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = inspired
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = recommendedAdapter
                    if (listModel[position].details!!.size > 4) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(ctx.getString(R.string.recommended_audio), ignoreCase = true)) {
                    val recommendedAdapter = RecommendedAdapter(listModel[position].details!!, ctx, binding, act, listModel[position].view)
                    val inspired: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = inspired
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = recommendedAdapter
                    if (listModel[position].details!!.size > 4) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(ctx.getString(R.string.popular_audio), ignoreCase = true)) {
                    val popularPlayedAdapter = PopularPlayedAdapter(listModel[position].details!!, ctx, binding, act, listModel[position].view)
                    val recentlyPlayed: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = recentlyPlayed
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = popularPlayedAdapter
                    if (listModel[position].details!! != null &&
                            listModel[position].details!!.size > 6) {
                        holder.binding.tvViewAll.visibility = View.VISIBLE
                    } else {
                        holder.binding.tvViewAll.visibility = View.GONE
                    }
                } else if (listModel[position].view.equals(ctx.getString(R.string.top_categories), ignoreCase = true)) {
                    holder.binding.tvViewAll.visibility = View.GONE
                    val topCategoriesAdapter = TopCategoriesAdapter(listModel[position].details!!, ctx, binding, act,
                            listModel[position].homeAudioID.toString(), listModel[position].view, fragmentManager1)
                    val topCategories: RecyclerView.LayoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
                    holder.binding.rvMainAudio.layoutManager = topCategories
                    holder.binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
                    holder.binding.rvMainAudio.adapter = topCategoriesAdapter
                }
            }
        }

        override fun getItemCount(): Int {
            return listModel!!.size
        }
    }

    class PlaylistAdapter(private val listModel: HomeDataModel.ResponseData.Play, private val ctx: Context, var binding: FragmentManageBinding, val act: Activity) : RecyclerView.Adapter<PlaylistAdapter.MyViewHolder>() {

        inner class MyViewHolder(var binding: PlaylistCustomLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PlaylistCustomLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.playlist_custom_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val measureRatio = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.38f, 0f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.tvAddToPlaylist.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            val measureRatio1 = BWSApplication.measureRatio(ctx, 0f, 1f, 1f, 0.38f, 0f)
            holder.binding.rlMainLayout.layoutParams.height = (measureRatio1.height * measureRatio1.ratio).toInt()
            holder.binding.rlMainLayout.layoutParams.width = (measureRatio1.widthImg * measureRatio1.ratio).toInt()
            holder.binding.tvPlaylistName.setText(listModel!!.details!![position].playlistName)
            Glide.with(ctx).load(listModel.details!![position].playlistImage).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(32))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
//                if (IsLock.equals("1", ignoreCase = true)) {
//                    holder.binding.ivLock.visibility = View.VISIBLE
//                } else if (IsLock.equals("2", ignoreCase = true)) {
//                    holder.binding.ivLock.visibility = View.VISIBLE
//                } else if (IsLock.equals("0", ignoreCase = true) || IsLock.equals("", ignoreCase = true)) {
//                    holder.binding.ivLock.visibility = View.GONE
//                }
//            if (index == position) {
//                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
//            } else
            holder.binding.tvAddToPlaylist.visibility = View.GONE
            holder.binding.tvAddToPlaylist.text = "Add To Playlist"
//            holder.binding.rlMainLayout.setOnLongClickListener { v ->
//                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
//                index = position
//                notifyDataSetChanged()
//                true
//            }
            holder.binding.tvAddToPlaylist.setOnClickListener {
//                    if (IsLock.equals("1", ignoreCase = true)) {
//                        holder.binding.ivLock.visibility = View.VISIBLE
//                        val i = Intent(ctx, MembershipChangeActivity::class.java)
//                        i.putExtra("ComeFrom", "Plan")
//                        ctx.startActivity(i)
//                    } else if (IsLock.equals("2", ignoreCase = true)) {
//                        holder.binding.ivLock.visibility = View.VISIBLE
//                        val i = Intent(ctx, MembershipChangeActivity::class.java)
//                        i.putExtra("ComeFrom", "Plan")
//                        ctx.startActivity(i)
//                    } else if (IsLock.equals("0", ignoreCase = true) || IsLock.equals("", ignoreCase = true)) {
//                holder.binding.ivLock.visibility = View.GONE
//                val i = Intent(ctx, AddPlaylistActivity::class.java)
//                i.putExtra("AudioId", "")
//                i.putExtra("ScreenView", "Playlist Main Screen")
//                i.putExtra("PlaylistID", listModel.details!![position].playlistID)
//                i.putExtra("PlaylistName", listModel.details!![position].playlistName)
//                i.putExtra("PlaylistImage", listModel.details!![position].playlistImage)
//                i.putExtra("PlaylistType", listModel.details!![position].created)
//                i.putExtra("Liked", "0")
//                ctx.startActivity(i)
//                    }
            }
            holder.binding.rlMainLayout.setOnClickListener { view ->
                try {
                    val i = Intent(ctx, MyPlaylistListingActivity::class.java)
                    i.putExtra("New", "0")
                    i.putExtra("PlaylistID", listModel.details!![position].playlistID)
                    i.putExtra("PlaylistName", listModel.details!![position].playlistName)
                    i.putExtra("PlaylistImage", listModel.details!![position].playlistImage)
                    i.putExtra("PlaylistSource", "")
                    i.putExtra("MyDownloads", "0")
                    i.putExtra("ScreenView", "")
                    i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    ctx.startActivity(i)
                    act.overridePendingTransition(0, 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun getItemCount(): Int {
            return if(listModel.details!!.size<4)
                listModel.details!!.size
            else 4
        }
    }

    class RecommendedAdapter(private val listModel: List<HomeDataModel.ResponseData.Audio.Detail>, private val ctx: Context, var binding: FragmentManageBinding, val act: Activity, var view: String?) : RecyclerView.Adapter<RecommendedAdapter.MyViewHolder>() {

        inner class MyViewHolder(var binding: BigBoxLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: BigBoxLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.big_box_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.text = listModel[position].name
            val measureRatio = BWSApplication.measureRatio(ctx, 20f, 1f, 1f, 0.48f, 20f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            holder.binding.llMainLayout.setOnClickListener {
                ManageFragment().callMainPlayer(position, view, listModel,ctx,act)
            }
        }

        override fun getItemCount(): Int {
            return if (4 > listModel.size) {
                listModel.size
            } else {
                4
            }
        }
    }

    class LibraryAdapter(private val listModel: List<HomeDataModel.ResponseData.Audio.Detail>, private val ctx: Context, var binding: FragmentManageBinding, val act: Activity, var view: String?) : RecyclerView.Adapter<LibraryAdapter.MyViewHolder>() {

        inner class MyViewHolder(var binding: BigBoxLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: BigBoxLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.big_box_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.setText(listModel[position].name)
            val measureRatio = BWSApplication.measureRatio(ctx, 20f, 1f, 1f, 0.48f, 20f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            holder.binding.llMainLayout.setOnClickListener {
                 ManageFragment().callMainPlayer(position, view, listModel, ctx, act)
            }
        }

        override fun getItemCount(): Int {
            return if (4 > listModel.size) {
                listModel.size
            } else {
                4
            }
        }
    }

    class DownloadAdapter(private val listModel: List<HomeDataModel.ResponseData.Audio.Detail>, private val ctx: Context, var binding: FragmentManageBinding, val act: Activity, var view: String?) : RecyclerView.Adapter<DownloadAdapter.MyViewHolder>() {

        inner class MyViewHolder(var binding: BigBoxLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: BigBoxLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.big_box_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.setText(listModel[position].name)
            val measureRatio = BWSApplication.measureRatio(ctx, 20f, 1f, 1f, 0.48f, 20f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
        }

        override fun getItemCount(): Int {
            return if (4 > listModel.size) {
                listModel.size
            } else {
                4
            }
        }
    }

    class RecentlyPlayedAdapter(private val listModel: List<HomeDataModel.ResponseData.Audio.Detail>, private val ctx: Context, var binding: FragmentManageBinding, val act: Activity, var view: String?) : RecyclerView.Adapter<RecentlyPlayedAdapter.MyViewHolder>() {

        inner class MyViewHolder(var binding: SmallBoxLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SmallBoxLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.small_box_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.setText(listModel[position].name)
            val measureRatio = BWSApplication.measureRatio(ctx, 16f, 1f, 1f, 0.28f, 10f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            holder.binding.llMainLayout.setOnClickListener {
                 ManageFragment().callMainPlayer(position, view, listModel, ctx, act)
            }
        }

        override fun getItemCount(): Int {
            return if (4 > listModel.size) {
                listModel.size
            } else {
                4
            }
        }
    }

    class PopularPlayedAdapter(private val listModel: List<HomeDataModel.ResponseData.Audio.Detail>, private val ctx: Context, var binding: FragmentManageBinding, val act: Activity, var view: String?) : RecyclerView.Adapter<PopularPlayedAdapter.MyViewHolder>() {

        inner class MyViewHolder(var binding: SmallBoxLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: SmallBoxLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.small_box_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.setText(listModel[position].name)
            val measureRatio = BWSApplication.measureRatio(ctx, 16f, 1f, 1f, 0.28f, 10f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx).load(listModel[position].imageFile).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(28))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            holder.binding.llMainLayout.setOnClickListener {
                 ManageFragment().callMainPlayer(position, view, listModel, ctx, act)
            }
        }

        override fun getItemCount(): Int {
            return if (4 > listModel.size) {
                listModel.size
            } else {
                4
            }
        }
    }

    class TopCategoriesAdapter(private val listModel: List<HomeDataModel.ResponseData.Audio.Detail>, private val ctx: Context, var binding: FragmentManageBinding, val act: Activity, var homeView: String, var viewString: String?, var fragmentManager1: FragmentManager) : RecyclerView.Adapter<TopCategoriesAdapter.MyViewHolder>() {

        inner class MyViewHolder(var binding: RoundBoxLayoutBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: RoundBoxLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.round_box_layout, parent, false)
            return MyViewHolder(v)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.tvTitle.setText(listModel[position].name)
            /* MeasureRatio measureRatio = measureRatio(ctx, 16,
                1, 1, 0.52f, 10);
        holder.binding.ivRestaurantImage.getLayoutParams().height = (int) (measureRatio.getHeight() * measureRatio.getRatio());
        holder.binding.ivRestaurantImage.getLayoutParams().width = (int) (measureRatio.getWidthImg() * measureRatio.getRatio());

*/
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(ctx).load(listModel[position].catImage).thumbnail(0.05f)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(124))).priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)

            holder.binding.llMainLayout.setOnClickListener { view ->
                val bundle = Bundle()
                bundle.putString("ID", homeView)
                bundle.putString("Name", viewString)
                bundle.putString("Category", listModel[position].categoryName)
                val viewAllAudioFragment: Fragment = ViewAllAudioFragment()
                viewAllAudioFragment.arguments = bundle
                fragmentManager1.beginTransaction()
                        .replace(R.id.flContainer, viewAllAudioFragment)
                        .commit()
            }
        }

        override fun getItemCount(): Int {
            return if (4 > listModel.size) {
                listModel.size
            } else {
                4
            }
        }
    }
}