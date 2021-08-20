package com.brainwellnessspa.dashboardModule.enhance

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brainwellnessspa.BWSApplication.isDisclaimer
import com.brainwellnessspa.R
import com.brainwellnessspa.BWSApplication.*
import com.brainwellnessspa.dashboardModule.activities.MyPlayerActivity
import com.brainwellnessspa.dashboardModule.models.HomeScreenModel.ResponseData.DisclaimerAudio
import com.brainwellnessspa.dashboardModule.models.SegmentAudio
import com.brainwellnessspa.dashboardModule.models.ViewAllAudioListModel
import com.brainwellnessspa.databinding.AudiolistCustomLayoutBinding
import com.brainwellnessspa.databinding.FragmentViewAllAudioBinding
import com.brainwellnessspa.roomDataBase.DownloadAudioDetails
import com.brainwellnessspa.roomDataBase.DownloadAudioDetailsUniq
import com.brainwellnessspa.services.GlobalInitExoPlayer
import com.brainwellnessspa.userModule.signupLogin.SignInActivity
import com.brainwellnessspa.utility.APINewClient
import com.brainwellnessspa.utility.CONSTANTS
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.segment.analytics.Properties
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ViewAllAudioFragment : Fragment() {
    lateinit var binding: FragmentViewAllAudioBinding
    var id: String? = null
    var name: String? = null
    var userId: String? = null
    var category: String? = null
    var coUserId: String? = null
    var userName: String? = null
    var audioList: List<DownloadAudioDetails>? = null
    lateinit var ctx: Context
    lateinit var act: Activity
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        ctx = requireActivity()
        act = requireActivity()
        val shared1 = requireActivity().getSharedPreferences(CONSTANTS.PREFE_ACCESS_SIGNIN_COUSER, Context.MODE_PRIVATE)
        userId = shared1.getString(CONSTANTS.PREFE_ACCESS_mainAccountID, "")
        coUserId = shared1.getString(CONSTANTS.PREFE_ACCESS_UserId, "")
        userName = shared1.getString(CONSTANTS.PREFE_ACCESS_NAME, "")
        if (arguments != null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_all_audio, container, false)
            id = requireArguments().getString("ID")
            name = requireArguments().getString("Name")
            category = requireArguments().getString("Category")
        }
        DB = getAudioDataBase(ctx)
        val view = binding.root
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
        val manager = GridLayoutManager(getActivity(), 2)
        binding.rvMainAudio.itemAnimator = DefaultItemAnimator()
        binding.rvMainAudio.layoutManager = manager
        return view
    }

    private fun callBack() {
        val audioFragment: Fragment = ManageFragment()
        val fragmentManager1 = requireActivity().supportFragmentManager
        fragmentManager1.beginTransaction().replace(R.id.flContainer, audioFragment).commit()
        val bundle = Bundle()
        audioFragment.arguments = bundle
    }

    override fun onResume() {
        //        refreshData();
        if (name.equals("My Downloads", ignoreCase = true)) {
            audioList = ArrayList()
            callObserverMethod()
        } else {
            prepareData()
        }
        super.onResume()
    }

    private fun callObserverMethod() {
        DB.taskDao()?.geAllDataz("", coUserId)?.observe(requireActivity(), { audioList: List<DownloadAudioDetailsUniq> ->
            //            refreshData();
            binding.tvTitle.text = name
            val listModelList = ArrayList<ViewAllAudioListModel.ResponseData.Detail>()
            for (i in audioList.indices) {
                val mainPlayModel = ViewAllAudioListModel.ResponseData.Detail()
                mainPlayModel.iD = audioList[i].ID
                mainPlayModel.name = audioList[i].Name
                mainPlayModel.audioFile = audioList[i].AudioFile
                mainPlayModel.audioDirection = audioList[i].AudioDirection
                mainPlayModel.audiomastercat = audioList[i].Audiomastercat
                mainPlayModel.audioSubCategory = audioList[i].AudioSubCategory
                mainPlayModel.imageFile = audioList[i].ImageFile
                mainPlayModel.audioDuration = audioList[i].AudioDuration
                listModelList.add(mainPlayModel)
            }
            val adapter = AudiolistAdapter(listModelList)
            binding.rvMainAudio.adapter = adapter
        })
    }

    private fun prepareData() {
        //        refreshData();
        if (isNetworkConnected(getActivity())) {
            showProgressBar(binding.progressBar, binding.progressBarHolder, getActivity())
            val listCall = APINewClient.client.getViewAllAudioLists(coUserId, id, category)
            listCall.enqueue(object : Callback<ViewAllAudioListModel?> {
                override fun onResponse(call: Call<ViewAllAudioListModel?>, response: Response<ViewAllAudioListModel?>) {
                    try {
                        hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity())
                        val listModel = response.body()
                        if (listModel != null) {
                            if (listModel.responseCode.equals(getString(R.string.ResponseCodesuccess), ignoreCase = true)) {
                                if (category.equals("", ignoreCase = true)) {
                                    binding.tvTitle.text = listModel.responseData?.view
                                } else {
                                    binding.tvTitle.text = category
                                }
                                val section = ArrayList<SegmentAudio>()
                                for (i in listModel.responseData?.details?.indices!!) {
                                    val e = SegmentAudio()
                                    e.audioId = listModel.responseData?.details?.get(i)?.iD
                                    e.audioName = listModel.responseData?.details?.get(i)?.name
                                    e.masterCategory = listModel.responseData?.details?.get(i)?.audiomastercat
                                    e.subCategory = listModel.responseData?.details?.get(i)?.audioSubCategory
                                    e.audioDuration = listModel.responseData?.details?.get(i)?.audioDirection
                                    section.add(e)
                                }
                                val p = Properties()
                                val gson = Gson()
                                p.putValue("audios", gson.toJson(section))
                                if (name.equals(getString(R.string.top_categories), ignoreCase = true)) {
                                    p.putValue("categoryName", category)
                                }
                                p.putValue("source", name)
                                addToSegment("Audio ViewAll Screen Viewed", p, CONSTANTS.screen)
                                val adapter = AudiolistAdapter(listModel.responseData?.details)
                                binding.rvMainAudio.adapter = adapter
                            } else if (listModel.responseCode.equals(ctx.getString(R.string.ResponseCodeDeleted), ignoreCase = true)) {
                                deleteCall(act)
                                showToast(listModel.responseMessage, act)
                                val i = Intent(act, SignInActivity::class.java)
                                i.putExtra("mobileNo", "")
                                i.putExtra("countryCode", "")
                                i.putExtra("name", "")
                                i.putExtra("email", "")
                                i.putExtra("countryShortName", "")
                                startActivity(i)
                                act.finish()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ViewAllAudioListModel?>, t: Throwable) {
                    hideProgressBar(binding.progressBar, binding.progressBarHolder, getActivity())
                }
            })
        } else {
            showToast(getString(R.string.no_server_found), getActivity())
        }
    }

    /*    private void refreshData() {
      try {
          GlobalInitExoPlayer globalInitExoPlayer = new GlobalInitExoPlayer();
          globalInitExoPlayer.UpdateMiniPlayer(getActivity());
          SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
          AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
          if (!AudioFlag.equalsIgnoreCase("0")) {
              openOnlyFragment();
              LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
              params.setMargins(4, 6, 4, 260);
              binding.llSpace.setLayoutParams(params);
          } else {
              LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
              params.setMargins(4, 6, 4, 50);
              binding.llSpace.setLayoutParams(params);
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
  }*/
    inner class AudiolistAdapter(private val listModelList: ArrayList<ViewAllAudioListModel.ResponseData.Detail>?) : RecyclerView.Adapter<AudiolistAdapter.MyViewHolder>() {
        var index = -1
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v: AudiolistCustomLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.audiolist_custom_layout, parent, false)
            return MyViewHolder(v)
        }

        @SuppressLint("SetTextI18n") override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val measureRatio = measureRatio(getActivity(), 0f, 1f, 1f, 0.46f, 0f)
            holder.binding.ivRestaurantImage.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.ivRestaurantImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.binding.tvAddToPlaylist.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.tvAddToPlaylist.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.rlMainLayout.layoutParams.height = (measureRatio.height * measureRatio.ratio).toInt()
            holder.binding.rlMainLayout.layoutParams.width = (measureRatio.widthImg * measureRatio.ratio).toInt()
            holder.binding.tvAudioName.text = listModelList?.get(position)?.name
            Glide.with(requireActivity()).load(listModelList?.get(position)?.imageFile).thumbnail(0.05f).apply(RequestOptions.bitmapTransform(RoundedCorners(38))).priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false).into(holder.binding.ivRestaurantImage)
            holder.binding.llMore.visibility = View.VISIBLE
            holder.binding.llMore.setOnClickListener {
                if (IsLock.equals("1")) {
                    callEnhanceActivity(ctx, act)
                } else if (IsLock.equals("0")) {
                    callAudioDetails(listModelList?.get(position)?.iD, ctx, getActivity(), coUserId, "viewAllAudioList", ArrayList(), listModelList, ArrayList(), ArrayList(), position)
                }
            }
            if (listModelList!![position].isPlay.equals("0")) {
                holder.binding.ivLock.visibility = View.VISIBLE
            } else {
                holder.binding.ivLock.visibility = View.GONE
            }

            if (index == position) {
                holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
            } else holder.binding.tvAddToPlaylist.visibility = View.GONE

            holder.binding.tvAddToPlaylist.text = "Add To Playlist"

            holder.binding.rlMainLayout.setOnLongClickListener {
                if (listModelList[position].isPlay.equals("0")) {
                    callEnhanceActivity(ctx,act)
                } else {
                    holder.binding.tvAddToPlaylist.visibility = View.VISIBLE
                    index = position
                    notifyDataSetChanged()
                }
                true
            }
            holder.binding.tvAddToPlaylist.setOnClickListener {
                val p = Properties()
                p.putValue("audioId", listModelList[position].iD)
                p.putValue("audioName", listModelList[position].name)
                p.putValue("audioDescription", "")
                p.putValue("directions", listModelList[position].audioDirection)
                p.putValue("masterCategory", listModelList[position].audiomastercat)
                p.putValue("subCategory", listModelList[position].audioSubCategory)
                p.putValue("audioDuration", listModelList[position].audioDuration)
                p.putValue("position", GlobalInitExoPlayer.GetCurrentAudioPosition())
                p.putValue("audioType", "Streaming")
                p.putValue("source","Audio View All Screen")
                p.putValue("audioService", appStatus(ctx))
                p.putValue("bitRate", "")
                p.putValue("sound", hundredVolume.toString())
                addToSegment("Add To Playlist Clicked", p, CONSTANTS.track)
                val i = Intent(getActivity(), AddPlaylistActivity::class.java)
                i.putExtra("AudioId", listModelList[position].iD)
                i.putExtra("ScreenView", "Audio View All Screen")
                i.putExtra("PlaylistID", "")
                i.putExtra("PlaylistName", "")
                i.putExtra("PlaylistImage", "")
                i.putExtra("PlaylistType", "")
                i.putExtra("Liked", "0")
                startActivity(i)
            }
            holder.binding.rlMainLayout.setOnClickListener {
                if (listModelList[position].isPlay.equals("0")) {
                callEnhanceActivity(ctx,act)
            } else {
                callMainTransFrag(position)
            }
            }
        }

        private fun callMainTransFrag(position: Int) {
            try {
                val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                val myPlaylist = shared1.getString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
                //                String PlayFrom = shared1.getString(CONSTANTS.PREF_KEY_PlayFrom, "");
                val playerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                if (name.equals("My Downloads", ignoreCase = true)) {
                    if (isNetworkConnected(ctx)) {
                        if (audioPlayerFlag.equals("DownloadListAudio", ignoreCase = true)) {
                            if (isDisclaimer == 1) {
                                if (player != null) {
                                    if (!player.playWhenReady) {
                                        player.playWhenReady = true
                                    }
                                } else {
                                    audioClick = true
                                }
                                callMyPlayer()
                                showToast("The audio shall start playing after the disclaimer", act)
                            } else {
                                if (player != null) {
                                    if (position != playerPosition) {
                                        player.seekTo(position, 0)
                                        player.playWhenReady = true
                                        val sharedxx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                                        val editor = sharedxx.edit()
                                        editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                                        editor.apply()
                                    }
                                    callMyPlayer()
                                } else {
                                    if (listModelList != null) {
                                        callPlayer(position, listModelList, true)
                                    }
                                }
                            }
                        } else {
                            val listModelList2 = ArrayList<ViewAllAudioListModel.ResponseData.Detail>()
                            if (listModelList != null) {
                                listModelList2.addAll(listModelList)
                            }
                            val gson = Gson()
                            val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
                            val isPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
                            val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                            val type = object : TypeToken<DisclaimerAudio?>() {}.type
                            val arrayList = gson.fromJson<DisclaimerAudio>(disclimerJson, type)
                            val mainPlayModel = ViewAllAudioListModel.ResponseData.Detail()
                            mainPlayModel.iD = arrayList.id
                            mainPlayModel.name = arrayList.name
                            mainPlayModel.audioFile = arrayList.audioFile
                            mainPlayModel.audioDirection = arrayList.audioDirection
                            mainPlayModel.audiomastercat = arrayList.audiomastercat
                            mainPlayModel.audioSubCategory = arrayList.audioSubCategory
                            mainPlayModel.imageFile = arrayList.imageFile
                            mainPlayModel.audioDuration = arrayList.audioDuration
                            var audioc = true
                            if (isDisclaimer == 1) {
                                if (player != null) {
                                    player.playWhenReady = true
                                    audioc = false
                                    listModelList2.add(mainPlayModel)
                                } else {
                                    isDisclaimer = 0
                                    if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                                        audioc = true
                                        listModelList2.add(mainPlayModel)
                                    }
                                }
                            } else {
                                isDisclaimer = 0
                                if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                                    audioc = true
                                    listModelList2.add(mainPlayModel)
                                }
                            }
                            callPlayer(position, listModelList2, audioc)
                        }
                    } else {
                        getMedia(position)
                    }
                } else if (name.equals(getString(R.string.top_categories), ignoreCase = true)) {
                    val catName = shared1.getString(CONSTANTS.PREF_KEY_Cat_Name, "")
                    if (catName.equals(category, ignoreCase = true)) {
                        if (isDisclaimer == 1) {
                            if (player != null) {
                                if (!player.playWhenReady) {
                                    player.playWhenReady = true
                                }
                            } else {
                                audioClick = true
                            }
                            callMyPlayer()
                            showToast("The audio shall start playing after the disclaimer", act)
                        } else {
                            if (player != null) {
                                if (position != playerPosition) {
                                    player.seekTo(position, 0)
                                    player.playWhenReady = true
                                    val sharedxx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                                    val editor = sharedxx.edit()
                                    editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                                    editor.apply()
                                }
                                callMyPlayer()
                            } else {
                                if (listModelList != null) {
                                    callPlayer(position, listModelList, true)
                                }
                            }
                        }
                    } else {
                        val listModelList2 = ArrayList<ViewAllAudioListModel.ResponseData.Detail>()
                        if (listModelList != null) {
                            listModelList2.addAll(listModelList)
                        }
                        val gson = Gson()
                        val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
                        val isPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
                        val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                        val type = object : TypeToken<DisclaimerAudio?>() {}.type
                        val arrayList = gson.fromJson<DisclaimerAudio>(disclimerJson, type)
                        val mainPlayModel = ViewAllAudioListModel.ResponseData.Detail()
                        mainPlayModel.iD = arrayList.id
                        mainPlayModel.name = arrayList.name
                        mainPlayModel.audioFile = arrayList.audioFile
                        mainPlayModel.audioDirection = arrayList.audioDirection
                        mainPlayModel.audiomastercat = arrayList.audiomastercat
                        mainPlayModel.audioSubCategory = arrayList.audioSubCategory
                        mainPlayModel.imageFile = arrayList.imageFile
                        mainPlayModel.audioDuration = arrayList.audioDuration
                        var audioc = true
                        if (isDisclaimer == 1) {
                            if (player != null) {
                                player.playWhenReady = true
                                audioc = false
                                listModelList2.add(mainPlayModel)
                            } else {
                                isDisclaimer = 0
                                if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                                    audioc = true
                                    listModelList2.add(mainPlayModel)
                                }
                            }
                        } else {
                            isDisclaimer = 0
                            if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                                audioc = true
                                listModelList2.add(mainPlayModel)
                            }
                        }
                        callPlayer(position, listModelList2, audioc)
                    }
                } else {
                    if ((audioPlayerFlag.equals("MainAudioList", ignoreCase = true) || audioPlayerFlag.equals("ViewAllAudioList", ignoreCase = true)) && myPlaylist.equals(name, ignoreCase = true)) {
                        if (isDisclaimer == 1) {
                            if (player != null) {
                                if (!player.playWhenReady) {
                                    player.playWhenReady = true
                                }
                            } else {
                                audioClick = true
                            }
                            callMyPlayer()
                            showToast("The audio shall start playing after the disclaimer", act)
                        } else {
                            if (player != null) {
                                if (position != playerPosition) {
                                    player.seekTo(position, 0)
                                    player.playWhenReady = true
                                    val sharedxx = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                                    val editor = sharedxx.edit()
                                    editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
                                    editor.apply()
                                }
                                callMyPlayer()
                            } else {
                                if (listModelList != null) {
                                    callPlayer(0, listModelList, true)
                                }
                            }
                        }
                    } else {
                        val listModelList2 = ArrayList<ViewAllAudioListModel.ResponseData.Detail>()
                        if (listModelList != null) {
                            listModelList2.addAll(listModelList)
                        }
                        val gson = Gson()
                        val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
                        val isPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
                        val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                        val type = object : TypeToken<DisclaimerAudio?>() {}.type
                        val arrayList = gson.fromJson<DisclaimerAudio>(disclimerJson, type)
                        val mainPlayModel = ViewAllAudioListModel.ResponseData.Detail()
                        mainPlayModel.iD = arrayList.id
                        mainPlayModel.name = arrayList.name
                        mainPlayModel.audioFile = arrayList.audioFile
                        mainPlayModel.audioDirection = arrayList.audioDirection
                        mainPlayModel.audiomastercat = arrayList.audiomastercat
                        mainPlayModel.audioSubCategory = arrayList.audioSubCategory
                        mainPlayModel.imageFile = arrayList.imageFile
                        mainPlayModel.audioDuration = arrayList.audioDuration
                        var audioc = true
                        if (isDisclaimer == 1) {
                            if (player != null) {
                                player.playWhenReady = true
                                audioc = false
                                listModelList2.add(mainPlayModel)
                            } else {
                                isDisclaimer = 0
                                if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                                    audioc = true
                                    listModelList2.add(mainPlayModel)
                                }
                            }
                        } else {
                            isDisclaimer = 0
                            if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                                audioc = true
                                listModelList2.add(mainPlayModel)
                            }
                        }
                        callPlayer(position, listModelList2, audioc)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun getMedia(position: Int) {
            DB.taskDao()?.geAllLiveDataBYDownloaded("Complete", coUserId)?.observe((ctx as LifecycleOwner?)!!, { audioList: List<String?>? ->
                val downloadAudioDetailsList = audioList
                var pos = 0
                val shared1 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
                val audioPlayerFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0")
                val playerPosition = shared1.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0)
                if (audioPlayerFlag.equals("DownloadListAudio", ignoreCase = true)) {
                    if (isDisclaimer == 1) {
                        if (player != null) {
                            if (!player.playWhenReady) {
                                player.playWhenReady = true
                            }
                        } else {
                            audioClick = true
                            miniPlayer = 1
                        }
                        callMyPlayer()
                        showToast("The audio shall start playing after the disclaimer", act)
                    } else {
                        val listModelList2 = ArrayList<ViewAllAudioListModel.ResponseData.Detail>()
                        for (i in listModelList?.indices!!) {
                            if (downloadAudioDetailsList!!.contains(listModelList[i].name)) {
                                listModelList2.add(listModelList[i])
                            }
                        }
                        if (position != playerPosition) {
                            if (downloadAudioDetailsList!!.contains(listModelList[position].name)) {
                                pos = position
                                callPlayer(pos, listModelList2, true)
                            } else {
                                //                                pos = 0;
                                showToast(ctx.getString(R.string.no_server_found), act)
                            }
                        }
                        if (listModelList2.size == 0) {
                            //                                callTransFrag(pos, listModelList2, true);
                            showToast(ctx.getString(R.string.no_server_found), act)
                        }
                    }
                } else {
                    val listModelList2 = ArrayList<ViewAllAudioListModel.ResponseData.Detail>()
                    for (i in listModelList?.indices!!) {
                        if (downloadAudioDetailsList!!.contains(listModelList[i].name)) {
                            listModelList2.add(listModelList[i])
                        }
                    }
                    if (downloadAudioDetailsList!!.contains(listModelList[position].name)) {
                        pos = position
                        val gson = Gson()
                        val shared12 = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE)
                        val isPlayDisclimer = shared12.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0")
                        val disclimerJson = shared12.getString(CONSTANTS.PREF_KEY_Disclimer, gson.toString())
                        val type = object : TypeToken<DisclaimerAudio?>() {}.type
                        val arrayList = gson.fromJson<DisclaimerAudio>(disclimerJson, type)
                        val mainPlayModel = ViewAllAudioListModel.ResponseData.Detail()
                        mainPlayModel.iD = arrayList.id
                        mainPlayModel.name = arrayList.name
                        mainPlayModel.audioFile = arrayList.audioFile
                        mainPlayModel.audioDirection = arrayList.audioDirection
                        mainPlayModel.audiomastercat = arrayList.audiomastercat
                        mainPlayModel.audioSubCategory = arrayList.audioSubCategory
                        mainPlayModel.imageFile = arrayList.imageFile
                        mainPlayModel.audioDuration = arrayList.audioDuration
                        var audioc = true
                        if (isDisclaimer == 1) {
                            if (player != null) {
                                player.playWhenReady = true
                                audioc = false
                                listModelList2.add(pos, mainPlayModel)
                            } else {
                                isDisclaimer = 0
                                if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                                    audioc = true
                                    listModelList2.add(pos, mainPlayModel)
                                }
                            }
                        } else {
                            isDisclaimer = 0
                            if (isPlayDisclimer.equals("1", ignoreCase = true)) {
                                audioc = true
                                listModelList2.add(pos, mainPlayModel)
                            }
                        }
                        if (listModelList2.size != 0) {
                            if (!listModelList2[pos].audioFile.equals("", ignoreCase = true)) {
                                if (listModelList2.size != 0) {
                                    callPlayer(pos, listModelList2, audioc)
                                } else {
                                    showToast(ctx.getString(R.string.no_server_found), act)
                                }
                            } else if (listModelList2[pos].audioFile.equals("", ignoreCase = true) && listModelList2.size > 1) {
                                callPlayer(pos, listModelList2, audioc)
                            } else {
                                showToast(ctx.getString(R.string.no_server_found), act)
                            }
                        } else {
                            showToast(ctx.getString(R.string.no_server_found), act)
                        }
                    } else {
                        showToast(ctx.getString(R.string.no_server_found), act)
                    }
                }
            })
        }

        private fun callMyPlayer() {
            val i = Intent(ctx, MyPlayerActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            ctx.startActivity(i)
            act.overridePendingTransition(0, 0)
        }

        private fun callPlayer(position1: Int, listModel: ArrayList<ViewAllAudioListModel.ResponseData.Detail>, audioc: Boolean) {
            if (audioc) {
                GlobalInitExoPlayer.callNewPlayerRelease()
            }
            var position = position1
            val shared = ctx.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE)
            val editor = shared.edit()
            val gson = Gson()
            val json: String
            when {
                name.equals("My Downloads", ignoreCase = true) -> {
                    val downloadAudioDetails = ArrayList<DownloadAudioDetails>()
                    for (i in listModelList?.indices!!) {
                        val mainPlayModel = DownloadAudioDetails()
                        mainPlayModel.ID = listModelList[i].iD!!
                        mainPlayModel.Name = listModelList[i].name!!
                        mainPlayModel.AudioFile = listModelList[i].audioFile!!
                        mainPlayModel.AudioDirection = listModelList[i].audioDirection!!
                        mainPlayModel.Audiomastercat = listModelList[i].audiomastercat!!
                        mainPlayModel.AudioSubCategory = listModelList[i].audioSubCategory!!
                        mainPlayModel.ImageFile = listModelList[i].imageFile!!
                        mainPlayModel.AudioDuration = listModelList[i].audioDuration!!
                        downloadAudioDetails.add(mainPlayModel)
                    }
                    editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "DownloadListAudio")
                    json = gson.toJson(downloadAudioDetails)
                }
                name.equals(getString(R.string.top_categories), ignoreCase = true) -> {

                    if(IsLock == "1") {
                        val listDetail = ArrayList<ViewAllAudioListModel.ResponseData.Detail>()
                        for (i in listModel.indices) {
                            if(listModel[i].isPlay.equals("1")) {
                                val mainPlayModel = ViewAllAudioListModel.ResponseData.Detail()
                                mainPlayModel.iD= listModel[i].iD!!
                                mainPlayModel.name = listModel[i].name!!
                                mainPlayModel.audioFile = listModel[i].audioFile!!
                                mainPlayModel.isPlay = listModel[i].isPlay!!
                                mainPlayModel.audioDirection = listModel[i].audioDirection!!
                                mainPlayModel.audiomastercat = listModel[i].audiomastercat!!
                                mainPlayModel.audioSubCategory = listModel[i].audioSubCategory!!
                                mainPlayModel.imageFile = listModel[i].imageFile!!
                                mainPlayModel.audioDuration = listModel[i].audioDuration!!
                                listDetail.add(mainPlayModel)
                            }
                        }
                        position = if (position < listDetail.size) {
                            position
                        } else {
                            0
                        }
                        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, getString(R.string.top_categories))
                        editor.putString(CONSTANTS.PREF_KEY_Cat_Name, category)
                        json = gson.toJson(listDetail)
                    }else{
                        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, getString(R.string.top_categories))
                        editor.putString(CONSTANTS.PREF_KEY_Cat_Name, category)
                        json = gson.toJson(listModel)
                    }
                }
                else -> {

                    if(IsLock == "1") {
                        val listDetail = ArrayList<ViewAllAudioListModel.ResponseData.Detail>()
                        for (i in listModel.indices) {
                            if(listModel[i].isPlay.equals("1")) {
                                val mainPlayModel = ViewAllAudioListModel.ResponseData.Detail()
                                mainPlayModel.iD= listModel[i].iD!!
                                mainPlayModel.name = listModel[i].name!!
                                mainPlayModel.audioFile = listModel[i].audioFile!!
                                mainPlayModel.isPlay = listModel[i].isPlay!!
                                mainPlayModel.audioDirection = listModel[i].audioDirection!!
                                mainPlayModel.audiomastercat = listModel[i].audiomastercat!!
                                mainPlayModel.audioSubCategory = listModel[i].audioSubCategory!!
                                mainPlayModel.imageFile = listModel[i].imageFile!!
                                mainPlayModel.audioDuration = listModel[i].audioDuration!!
                                listDetail.add(mainPlayModel)
                            }
                        }
                        position = if (position < listDetail.size) {
                            position
                        } else {
                            0
                        }
                        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "ViewAllAudioList")
                        json = gson.toJson(listDetail)
                    }else{
                        editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "ViewAllAudioList")
                        json = gson.toJson(listModel)
                    }
                }
            }
            editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json)
            editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position)
            editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistId, "")
            editor.putString(CONSTANTS.PREF_KEY_PlayerPlaylistName, "")
            editor.putString(CONSTANTS.PREF_KEY_PlayFrom, name)
            editor.apply()
            audioClick = audioc
            callMyPlayer()
        }

        override fun getItemCount(): Int {
            return listModelList?.size!!
        }

        inner class MyViewHolder(var binding: AudiolistCustomLayoutBinding) : RecyclerView.ViewHolder(binding.root)
    }
/*    private void callnewTrans(int position, ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList) {
          SharedPreferences shared = context.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
          boolean audioPlay = shared.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
          String AudioFlag = shared.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
          String MyPlaylist = shared.getString(CONSTANTS.PREF_KEY_myPlaylist, "");
          int positionSaved = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
          SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
          String IsPlayDisclimer = (shared1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0"));
          if (Name.equalsIgnoreCase("My Downloads")) {
              if (audioPlay && AudioFlag.equalsIgnoreCase("DownloadListAudio")) {
                  if (isDisclaimer == 1) {
                      if (player != null) {
                          if (!player.getPlayWhenReady()) {
                              player.setPlayWhenReady(true);
                          }
                      } else {
                          audioClick = true;
                          miniPlayer = 1;
                      }
                      Intent i = new Intent(getActivity(), AudioPlayerActivity.class);
                      i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                      getActivity().startActivity(i);
                      getActivity().overridePendingTransition(0, 0);
                      BWSApplication.showToast("The audio shall start playing after the disclaimer", context);
                  } else {
                      if (player != null) {
                          if (position != positionSaved) {
                              player.seekTo(position, 0);
                              player.setPlayWhenReady(true);
                              miniPlayer = 1;
                              SharedPreferences sharedxx = context.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                              SharedPreferences.Editor editor = sharedxx.edit();
                              editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                              editor.commit();
                          }
                          Intent i = new Intent(getActivity(), AudioPlayerActivity.class);
                          i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                          getActivity().startActivity(i);
                          getActivity().overridePendingTransition(0, 0);
                      } else {
                          callTransFrag(position, listModelList, true);
                      }
                  }
              } else {
                  ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
                  listModelList2.addAll(listModelList);
                  ViewAllAudioListModel.ResponseData.Detail mainPlayModel = new ViewAllAudioListModel.ResponseData.Detail();
                  mainPlayModel.setID("0");
                  mainPlayModel.setName("Disclaimer");
                  mainPlayModel.setAudioFile("");
                  mainPlayModel.setAudioDirection("The audio shall start playing after the disclaimer");
                  mainPlayModel.setAudiomastercat("");
                  mainPlayModel.setAudioSubCategory("");
                  mainPlayModel.setImageFile("");
                  mainPlayModel.setLike("");
                  mainPlayModel.setDownload("");
                  mainPlayModel.setAudioDuration("00:48");
                  boolean audioc = true;
                  if (isDisclaimer == 1) {
                      if (player != null) {
                          player.setPlayWhenReady(true);
                          audioc = false;
                          listModelList2.add(position, mainPlayModel);
                      } else {
                          isDisclaimer = 0;
                          if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                              audioc = true;
                              listModelList2.add(position, mainPlayModel);
                          }
                      }
                  } else {
                      isDisclaimer = 0;
                      if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                          audioc = true;
                          listModelList2.add(position, mainPlayModel);
                      }
                  }
                  callTransFrag(position, listModelList2, audioc);
              }
          } else {
              if (audioPlay && (AudioFlag.equalsIgnoreCase("MainAudioList") || AudioFlag.equalsIgnoreCase("ViewAllAudioList")) && MyPlaylist.equalsIgnoreCase(Name)) {
                  if (isDisclaimer == 1) {
                      if (player != null) {
                          if (!player.getPlayWhenReady()) {
                              player.setPlayWhenReady(true);
                          }
                      } else {
                          audioClick = true;
                          miniPlayer = 1;
                      }
                      Intent i = new Intent(getActivity(), AudioPlayerActivity.class);
                      i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                      getActivity().startActivity(i);
                      getActivity().overridePendingTransition(0, 0);
                      BWSApplication.showToast("The audio shall start playing after the disclaimer", context);
                  } else {
                      if (MyPlaylist.equalsIgnoreCase(getString(R.string.recently_played))) {
                          ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
                          if (!IsLock.equalsIgnoreCase("0")) {
                              SharedPreferences shared2 = context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                              String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
                              Gson gson1 = new Gson();
                              Type type1 = new TypeToken<List<String>>() {
                              }.getType();
                              List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
                              int size = listModelList.size();
                              for (int i = 0; i < size; i++) {
                                  if (UnlockAudioList.contains(listModelList.get(i).getID())) {
                                      listModelList2.add(listModelList.get(i));
                                  }
                              }
                              if (position < listModelList2.size()) {
                                  position = position;
                              } else {
                                  position = 0;
                              }
                          } else {
                              listModelList2.addAll(listModelList);
                          }
                          callTransFrag(position, listModelList2, true);
                      } else {
                          if (player != null) {
                              if (position != positionSaved) {
                                  player.seekTo(position, 0);
                                  player.setPlayWhenReady(true);
                                  miniPlayer = 1;
                                  SharedPreferences sharedxx = context.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                                  SharedPreferences.Editor editor = sharedxx.edit();
                                  editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                                  editor.commit();
                              }
                              Intent i = new Intent(getActivity(), AudioPlayerActivity.class);
                              i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                              getActivity().startActivity(i);
                              getActivity().overridePendingTransition(0, 0);
                          } else {
                              ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
                              if (!IsLock.equalsIgnoreCase("0")) {
                                  SharedPreferences shared2 = context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                                  String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
                                  Gson gson1 = new Gson();
                                  Type type1 = new TypeToken<List<String>>() {
                                  }.getType();
                                  List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
                                  int size = listModelList.size();
                                  for (int i = 0; i < size; i++) {
                                      if (UnlockAudioList.contains(listModelList.get(i).getID())) {
                                          listModelList2.add(listModelList.get(i));
                                      }
                                  }
                                  if (position < listModelList2.size()) {
                                      position = position;
                                  } else {
                                      position = 0;
                                  }
                              } else {
                                  listModelList2.addAll(listModelList);
                              }
                              callTransFrag(position, listModelList2, true);
                          }
                      }
                  }
              } else {
                  ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
                  if (!IsLock.equalsIgnoreCase("0")) {
                      SharedPreferences shared2 = context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                      String UnlockAudioLists = shared2.getString(CONSTANTS.PREF_KEY_UnLockAudiList, "");
                      Gson gson1 = new Gson();
                      Type type1 = new TypeToken<List<String>>() {
                      }.getType();
                      List<String> UnlockAudioList = gson1.fromJson(UnlockAudioLists, type1);
                      int size = listModelList.size();
                      for (int i = 0; i < size; i++) {
                          if (UnlockAudioList.contains(listModelList.get(i).getID())) {
                              listModelList2.add(listModelList.get(i));
                          }
                      }
                      if (position < listModelList2.size()) {
                          position = position;
                      } else {
                          position = 0;
                      }
                  } else {
                      listModelList2.addAll(listModelList);
                  }

                  ViewAllAudioListModel.ResponseData.Detail mainPlayModel = new ViewAllAudioListModel.ResponseData.Detail();
                  mainPlayModel.setID("0");
                  mainPlayModel.setName("Disclaimer");
                  mainPlayModel.setAudioFile("");
                  mainPlayModel.setAudioDirection("The audio shall start playing after the disclaimer");
                  mainPlayModel.setAudiomastercat("");
                  mainPlayModel.setAudioSubCategory("");
                  mainPlayModel.setImageFile("");
                  mainPlayModel.setLike("");
                  mainPlayModel.setDownload("");
                  mainPlayModel.setAudioDuration("00:48");
                  boolean audioc = true;
                  if (isDisclaimer == 1) {
                      if (player != null) {
                          player.setPlayWhenReady(true);
                          audioc = false;
                          listModelList2.add(position, mainPlayModel);
                      } else {
                          isDisclaimer = 0;
                          if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                              audioc = true;
                              listModelList2.add(position, mainPlayModel);
                          }
                      }
                  } else {
                      isDisclaimer = 0;
                      if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                          audioc = true;
                          listModelList2.add(position, mainPlayModel);
                      }
                  }
                  callTransFrag(position, listModelList2, audioc);
              }
          }
      }

      private void callTransFrag(int position, ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList, boolean audioc) {
          try {
              SharedPreferences shared = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
              SharedPreferences.Editor editor = shared.edit();
              Gson gson = new Gson();
              String json = "";
              ArrayList<ViewAllAudioListModel.ResponseData.Detail> listModelList2 = new ArrayList<>();
              ViewAllAudioListModel.ResponseData.Detail mainPlayModel = new ViewAllAudioListModel.ResponseData.Detail();
              if (Name.equalsIgnoreCase(getString(R.string.top_categories))) {
                  SharedPreferences shared1 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                  boolean audioPlay = shared1.getBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                  AudioFlag = shared1.getString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "0");
                  String catName = shared1.getString(CONSTANTS.PREF_KEY_Cat_Name, "");
                  int positionSaved = shared.getInt(CONSTANTS.PREF_KEY_PlayerPosition, 0);
                  SharedPreferences shared11 = getActivity().getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
                  String IsPlayDisclimer = (shared11.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0"));
                  if (audioPlay && AudioFlag.equalsIgnoreCase("TopCategories") && catName.equalsIgnoreCase(Category)) {
                      if (isDisclaimer == 1) {
                          if (player != null) {
                              if (!player.getPlayWhenReady()) {
                                  player.setPlayWhenReady(true);
                              }
                          } else {
                              BWSApplication.showToast("The audio shall start playing after the disclaimer", context);
                          }
                          openMyFragment(true);
                      } else {
                          listModelList2 = new ArrayList<>();
                          listModelList2.addAll(listModelList);
                          json = gson.toJson(listModelList2);
                          editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "TopCategories");
                          editor.putString(CONSTANTS.PREF_KEY_Cat_Name, Category);
                          editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
                          editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                          editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                          editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                          editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                          editor.putString(CONSTANTS.PREF_KEY_myPlaylist, Name);
                          editor.commit();
                          if (player != null) {
                              if (position != positionSaved) {
                                  player.seekTo(position, 0);
                                  player.setPlayWhenReady(true);
                                  miniPlayer = 1;
                                  SharedPreferences sharedxx = context.getSharedPreferences(CONSTANTS.PREF_KEY_PLAYER, Context.MODE_PRIVATE);
                                  SharedPreferences.Editor editord = sharedxx.edit();
                                  editord.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                                  editord.commit();
                              }
                              openOnlyFragment();
                          } else {
                              openMyFragment(true);
                          }
                      }
                  } else {
                      listModelList2 = new ArrayList<>();
                      listModelList2.addAll(listModelList);
                      mainPlayModel.setID("0");
                      mainPlayModel.setName("Disclaimer");
                      mainPlayModel.setAudioFile("");
                      mainPlayModel.setAudioDirection("The audio shall start playing after the disclaimer");
                      mainPlayModel.setAudiomastercat("");
                      mainPlayModel.setAudioSubCategory("");
                      mainPlayModel.setImageFile("");
                      mainPlayModel.setLike("");
                      mainPlayModel.setDownload("");
                      if (isDisclaimer == 1) {
                          if (player != null) {
                              player.setPlayWhenReady(true);
                              audioc = false;
                              listModelList2.add(position, mainPlayModel);
                          } else {
                              isDisclaimer = 0;
                              if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                                  audioc = true;
                                  listModelList2.add(position, mainPlayModel);
                              }
                          }
                      } else {
                          isDisclaimer = 0;
                          if (IsPlayDisclimer.equalsIgnoreCase("1")) {
                              audioc = true;
                              listModelList2.add(position, mainPlayModel);
                          }
                      }
                      json = gson.toJson(listModelList2);
                      editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "TopCategories");
                      editor.putString(CONSTANTS.PREF_KEY_Cat_Name, Category);
                      editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
                      editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                      editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                      editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                      editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                      editor.putString(CONSTANTS.PREF_KEY_myPlaylist, Name);
                      editor.commit();
                      openMyFragment(audioc);
                  }
              } else {
                  json = gson.toJson(listModelList);
                  if (Name.equalsIgnoreCase("My Downloads")) {
                      editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "DownloadListAudio");
                  } else {
                      editor.putString(CONSTANTS.PREF_KEY_AudioPlayerFlag, "ViewAllAudioList");
                  }
                  editor.putString(CONSTANTS.PREF_KEY_MainAudioList, json);
                  editor.putInt(CONSTANTS.PREF_KEY_PlayerPosition, position);
                  editor.putBoolean(CONSTANTS.PREF_KEY_queuePlay, false);
                  editor.putBoolean(CONSTANTS.PREF_KEY_audioPlay, true);
                  editor.putString(CONSTANTS.PREF_KEY_PlaylistId, "");
                  editor.putString(CONSTANTS.PREF_KEY_myPlaylist, Name);
                  editor.commit();
  //                openMyFragment();
                  miniPlayer = 1;
                  audioClick = true;
                  callNewPlayerRelease();

  //                SharedPreferences shared1 = context.getSharedPreferences(CONSTANTS.PREF_KEY_LOGIN, Context.MODE_PRIVATE);
  //                String IsPlayDisclimer = (shared1.getString(CONSTANTS.PREF_KEY_IsDisclimer, "0"));
  //                if(IsPlayDisclimer.equalsIgnoreCase("1")){
  //                    openOnlyFragment();
  //                }
                  Intent i = new Intent(getActivity(), AudioPlayerActivity.class);
                  i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                  getActivity().startActivity(i);
                  getActivity().overridePendingTransition(0, 0);
              }

          } catch (Exception e) {
              e.printStackTrace();
          }
      }

      private void openOnlyFragment() {

          Fragment fragment = new MiniPlayerFragment();
          FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
          fragmentManager1.beginTransaction()
                  .add(R.id.flContainer, fragment)
                  .commit();
      }

      private void openMyFragment(boolean audioc) {
          miniPlayer = 1;
          audioClick = audioc;
          if (audioc) {
              callNewPlayerRelease();
          }
          openOnlyFragment();
      }*/
}