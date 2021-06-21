package com.brainwellnessspa.dashboardModule.manage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication
import com.brainwellnessspa.dashboardModule.models.SegmentPlaylist
import com.brainwellnessspa.dashboardModule.models.ViewAllPlayListModel
import com.brainwellnessspa.downloadModule.activities.DownloadPlaylistActivity
import com.brainwellnessspa.R
import com.brainwellnessspa.dashboardModule.activities.AddPlaylistActivity
import com.brainwellnessspa.databinding.FragmentViewAllPlaylistBinding
import com.brainwellnessspa.databinding.PlaylistViewAllLayoutBinding
import com.brainwellnessspa.roomDataBase.DownloadPlaylistDetailsUnique
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ViewAllPlaylistFragment : Fragment() {
    lateinit var binding: FragmentViewAllPlaylistBinding
    var getLibraryId: String? = null
    var name: String? = null
    var coUserId: String? = null
    var userId: String? = null
    var userName: String? = null
    var audioFlag: String? = null
    var myDownloads: String? = null
    var screenView: String? = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_view_all_playlist, container, false)
        val shared1 = requireActivity()
            .getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        userName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "")
        val shared =
            requireActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
        audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
        BWSApplication.DB = BWSApplication.getAudioDataBase(activity)
        if (arguments != null) {
            getLibraryId = requireArguments().getString("GetLibraryID")
            name = requireArguments().getString("Name")
            myDownloads = requireArguments().getString("MyDownloads")
        }
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                callBack()
                return@setOnKeyListener true
            }
            false
        }
        binding.llBack.setOnClickListener { callBack() }
        val manager = GridLayoutManager(activity, 2)
        binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
        binding.rvMainAudio.layoutManager = manager
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun getAllMedia() {
        BWSApplication.DB.taskDao()
            .getAllPlaylist1(coUserId)
            .observe(
                requireActivity(),
                { audioList: List<DownloadPlaylistDetailsUnique> ->
                    binding.tvTitle.text = "My Downloads"
                    screenView = "My Downloads"
                    val listModelList = ArrayList<ViewAllPlayListModel.ResponseData.Detail>()
                    for (i in audioList.indices) {
                        val detail = ViewAllPlayListModel.ResponseData.Detail()
                        detail.totalAudio = audioList[i].totalAudio
                        detail.totalhour = audioList[i].totalhour
                        detail.totalminute = audioList[i].totalminute
                        detail.playlistID = audioList[i].playlistID
                        detail.playlistDesc = audioList[i].playlistDesc
                        detail.playlistMastercat = audioList[i].playlistMastercat
                        detail.playlistSubcat = audioList[i].playlistSubcat
                        detail.playlistName = audioList[i].playlistName
                        detail.playlistImage = audioList[i].playlistImage
                        //                detail.setPlaylistImageDetails("");
                        detail.created = audioList[i].created
                        listModelList.add(detail)
                    }
                    val p = Properties()
                    val section = ArrayList<SegmentPlaylist>()
                    for (i in audioList.indices) {
                        val e =
                            SegmentPlaylist()
                        e.playlistId = audioList[i].playlistID
                        e.playlistName = audioList[i].playlistName
                        e.playlistType = audioList[i].created
                        e.playlistDuration = (audioList[i].totalhour
                                + "h "
                                + audioList[i].totalminute
                                + "m")
                        e.audioCount = audioList[i].totalAudio
                        section.add(e)
                    }
                    p.putValue("userId", coUserId)
                    val gson = Gson()
                    p.putValue("playlists", gson.toJson(section))
                    p.putValue("section", screenView)
                    BWSApplication.addToSegment(
                        "View All Playlist Screen Viewed",
                        p,
                        CONSTANTS.screen
                    )
                    val adapter = PlaylistAdapter(listModelList)
                    binding.rvMainAudio.adapter = adapter
                })
    }

    private fun callBack() {
        val audioFragment: Fragment = MainPlaylistFragment()
        val fragmentManager1 = requireActivity().supportFragmentManager
        fragmentManager1.beginTransaction().replace(R.id.flContainer, audioFragment).commit()
    }

    override fun onResume() {
        if (myDownloads.equals("1", ignoreCase = true)) {
            getAllMedia()
        } else {
            prepareData()
        }
        super.onResume()
    }

    private fun prepareData() {
        /*     try {
        SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        audioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
        SharedPreferences shared2 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
        String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
        Gson gson1 = new Gson();
        Type type1 = new TypeToken<List<String>>() {
        }.getType();
        List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
        if (!IsLock.equalsIgnoreCase("0") && (audioFlag.equalsIgnoreCase("MainAudioList")
                || audioFlag.equalsIgnoreCase("ViewAllAudioList"))) {
            String audioID = "";
            SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = shared.getString(CONSTANTS.PREF_KEY_PlayerAudioList, String.valueOf(gson));
            Type type = new TypeToken<ArrayList<MainPlayModel>>() {
            }.getType();
            ArrayList<MainPlayModel> arrayList = gson.fromJson(json, type);

            if (arrayList.get(0).getAudioFile().equalsIgnoreCase("")) {
                arrayList.remove(0);
            }
            audioID = arrayList.get(0).getID();

            if (UnlockAudioList.contains(audioID)) {
            } else {
                SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                SharedPreferences.Editor editorr = sharedm.edit();
                editorr.remove(CONSTANTS.PREF_KEY_MainAudioList);
                editorr.remove(CONSTANTS.PREF_KEY_PlayerAudioList);
                editorr.remove(CONSTANTS.PREF_KEY_PlayerPosition);
                editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
                editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
                editorr.remove(CONSTANTS.PREF_KEY_AudioPlayerFlag);
                editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
                editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
                editorr.clear();
                editorr.commit();
                callNewPlayerRelease();
            }
        } else if (!IsLock.equalsIgnoreCase("0") && !audioFlag.equalsIgnoreCase("AppointmentDetailList")) {
            SharedPreferences sharedm = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
            SharedPreferences.Editor editorr = sharedm.edit();
            editorr.remove(CONSTANTS.PREF_KEY_MainAudioList);
            editorr.remove(CONSTANTS.PREF_KEY_PlayerAudioList);
            editorr.remove(CONSTANTS.PREF_KEY_PlayerPosition);
            editorr.remove(CONSTANTS.PREF_KEY_queuePlay);
            editorr.remove(CONSTANTS.PREF_KEY_audioPlay);
            editorr.remove(CONSTANTS.PREF_KEY_AudioPlayerFlag);
            editorr.remove(CONSTANTS.PREF_KEY_PlaylistId);
            editorr.remove(CONSTANTS.PREF_KEY_myPlaylist);
            editorr.clear();
            editorr.commit();
            callNewPlayerRelease();

        }
        SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
        audioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
        if (!audioFlag.equalsIgnoreCase("0")) {

            Fragment fragment = new MiniPlayerFragment();
            FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .add(R.id.flContainer, fragment)
                    .commit();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 6, 4, 280);
            binding.llSpace.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 6, 4, 50);
            binding.llSpace.setLayoutParams(params);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }*/
        if (BWSApplication.isNetworkConnected(activity)) {
            try {
                BWSApplication.showProgressBar(
                    binding.progressBar, binding.progressBarHolder, activity
                )
                val listCall = APINewClient.getClient().getViewAllPlayLists(coUserId, getLibraryId)
                listCall.enqueue(
                    object : Callback<ViewAllPlayListModel?> {
                        override fun onResponse(
                            call: Call<ViewAllPlayListModel?>,
                            response: Response<ViewAllPlayListModel?>
                        ) {
                            try {
                                val listModel = response.body()
                                if (listModel != null) {
                                    if (listModel
                                            .responseCode
                                            .equals(
                                                getString(R.string.ResponseCodesuccess),
                                                ignoreCase = true
                                            )
                                    ) {
                                        BWSApplication.hideProgressBar(
                                            binding.progressBar,
                                            binding.progressBarHolder,
                                            activity
                                        )
                                        binding.tvTitle.text = listModel.responseData!!.view
                                        screenView = listModel.responseData!!.view
                                        val p = Properties()
                                        val section = ArrayList<SegmentPlaylist>()
                                        for (i in listModel.responseData!!.details!!.indices) {
                                            val e =
                                                SegmentPlaylist()
                                            e.playlistId =
                                                listModel.responseData!!.details!![i].playlistID
                                            e.playlistName =
                                                listModel.responseData!!.details!![i].playlistName
                                            e.playlistType =
                                                listModel.responseData!!.details!![i].created
                                            e.playlistDuration =
                                                (listModel.responseData!!.details!![i].totalhour
                                                        + "h "
                                                        + listModel.responseData!!.details!![i].totalminute
                                                        + "m")
                                            e.audioCount =
                                                listModel.responseData!!.details!![i].totalAudio
                                            section.add(e)
                                        }
                                        p.putValue("userId", coUserId)
                                        val gson = Gson()
                                        p.putValue("playlists", gson.toJson(section))
                                        p.putValue("section", screenView)
                                        BWSApplication.addToSegment(
                                            "View All Playlist Screen Viewed", p, CONSTANTS.screen
                                        )
                                        val adapter = PlaylistAdapter(
                                            listModel.responseData!!.details!!
                                        )
                                        binding.rvMainAudio.adapter = adapter
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(
                            call: Call<ViewAllPlayListModel?>, t: Throwable
                        ) {
                            BWSApplication.hideProgressBar(
                                binding.progressBar, binding.progressBarHolder, activity
                            )
                        }
                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            BWSApplication.showToast(getString(R.string.no_server_found), activity)
        }
    }

    inner class PlaylistAdapter(private val listModelList: List<ViewAllPlayListModel.ResponseData.Detail>) :
        RecyclerView.Adapter<PlaylistAdapter.MyViewHolder>() {
        var index = -1
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: PlaylistViewAllLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.playlist_view_all_layout,
                parent,
                false
            )
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val measureRatio = BWSApplication.measureRatio(activity, 0f, 1f, 1f, 0.44f, 0f)
            holder.binding.ivRestaurantImage.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.tvAddToPlaylist.layoutParams.height =
                (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.width =
                (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.tvPlaylistName.text = listModelList[position].playlistName
            val measureRatio1 = BWSApplication.measureRatio(activity, 0f, 1f, 1f, 0.44f, 0f)
            holder.binding.rlMainLayout.layoutParams.height =
                (measureRatio1.height * measureRatio1.ratio).toInt()
            holder.binding.rlMainLayout.layoutParams.width =
                (measureRatio1.widthImg * measureRatio1.ratio).toInt()
            Glide.with(requireActivity())
                .load(listModelList[position].playlistImage)
                .thumbnail(0.05f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(42)))
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .into(holder.binding.ivRestaurantImage)

            //            if (IsLock.equalsIgnoreCase("1")) {
            //                holder.binding.ivLock.setVisibility(View.VISIBLE);
            //            } else if (IsLock.equalsIgnoreCase("2")) {
            //                holder.binding.ivLock.setVisibility(View.VISIBLE);
            //            } else if (IsLock.equalsIgnoreCase("0") || IsLock.equalsIgnoreCase("")) {
            holder.binding.ivLock.visibility = View.GONE
            //            }
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
                holder.binding.ivLock.visibility = View.GONE
                val i = Intent(activity, AddPlaylistActivity::class.java)
                i.putExtra("AudioId", "")
                i.putExtra("ScreenView", "Playlist View All Screen")
                i.putExtra("PlaylistID", listModelList[position].playlistID)
                i.putExtra("PlaylistName", listModelList[position].playlistName)
                i.putExtra("PlaylistImage", listModelList[position].playlistImage)
                i.putExtra("PlaylistType", listModelList[position].created)
                i.putExtra("Liked", "0")
                startActivity(i)
            }
            holder.binding.rlMainLayout.setOnClickListener {
                //                if (IsLock.equalsIgnoreCase("1")) {
                //                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                //                    Intent i = new Intent(getActivity(),
                // MembershipChangeActivity.class);
                //                    i.putExtra("ComeFrom", "Plan");
                //                    startActivity(i);
                //                } else if (IsLock.equalsIgnoreCase("2")) {
                //                    holder.binding.ivLock.setVisibility(View.VISIBLE);
                //                    BWSApplication.showToast(getString(R.string.reactive_plan),
                // getActivity());
                //                } else if (IsLock.equalsIgnoreCase("0") ||
                // IsLock.equalsIgnoreCase("")) {
                holder.binding.ivLock.visibility = View.GONE
                if (myDownloads.equals("1", ignoreCase = true)) {
                    //                            getMedia(listModelList.get(position).getPlaylistID());
                    val i = Intent(activity, DownloadPlaylistActivity::class.java)
                    i.putExtra("New", "0")
                    i.putExtra("PlaylistID", listModelList[position].playlistID)
                    i.putExtra("PlaylistName", listModelList[position].playlistName)
                    i.putExtra("PlaylistImage", listModelList[position].playlistImage)
                    i.putExtra("PlaylistImageDetails", "")
                    i.putExtra("TotalAudio", listModelList[position].totalAudio)
                    i.putExtra("Totalhour", listModelList[position].totalhour)
                    i.putExtra("Totalminute", listModelList[position].totalminute)
                    i.putExtra("MyDownloads", "1")
                    requireActivity().startActivity(i)
                } else {
                    GetPlaylistLibraryID = getLibraryId
                    val i = Intent(activity, MyPlaylistListingActivity::class.java)
                    i.putExtra("New", "0")
                    i.putExtra("PlaylistID", listModelList[position].playlistID)
                    i.putExtra("PlaylistName", listModelList[position].playlistName)
                    i.putExtra("PlaylistImage", listModelList[position].playlistImage)
                    i.putExtra("MyDownloads", myDownloads)
                    i.putExtra("ScreenView", screenView)
                    i.putExtra("PlaylistType", listModelList[position].created)
                    i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(i)
                }
            }
        }

        override fun getItemCount(): Int {
            return listModelList.size
        }

        inner class MyViewHolder(var binding: PlaylistViewAllLayoutBinding) :
            RecyclerView.ViewHolder(
                binding.root
            )
    }

    companion object {
        var GetPlaylistLibraryID: String? = ""
    }
}