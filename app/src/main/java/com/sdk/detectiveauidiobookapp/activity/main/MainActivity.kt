package com.sdk.detectiveauidiobookapp.activity.main

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.sdk.detectiveauidiobookapp.R
import com.sdk.detectiveauidiobookapp.adapters.SwipeSongAdapter
import com.sdk.detectiveauidiobookapp.data.model.Story
import com.sdk.detectiveauidiobookapp.databinding.AboutLayoutBinding
import com.sdk.detectiveauidiobookapp.databinding.ActivityMainBinding
import com.sdk.detectiveauidiobookapp.exoplayer.isPlaying
import com.sdk.detectiveauidiobookapp.exoplayer.toSong
import com.sdk.detectiveauidiobookapp.util.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager

    private var curPlayingSong: Story? = null

    private var playbackState: PlaybackStateCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeToObservers()

        initViews()
    }

    private fun initViews() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment)!!

        binding.vpSong.adapter = swipeSongAdapter

        binding.vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (playbackState?.isPlaying == true) {
                    mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position])
                } else {
                    curPlayingSong = swipeSongAdapter.songs[position]
                }
            }
        })

        binding.ivPlayPause.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }

        swipeSongAdapter.setOnItemClickListener {
            navHostFragment.findNavController().navigate(
                R.id.globalActionToSongFragment
            )
        }

        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.songFragment -> hideBottomBar()
                R.id.homeFragment -> showBottomBar()
                else -> showBottomBar()
            }
        }
        binding.navView.setNavigationItemSelectedListener(this)
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.START)
        }
    }

    private fun hideBottomBar() {
        binding.ivCurSongImage.isVisible = false
        binding.vpSong.isVisible = false
        binding.ivPlayPause.isVisible = false
    }

    private fun showBottomBar() {
        binding.ivCurSongImage.isVisible = true
        binding.vpSong.isVisible = true
        binding.ivPlayPause.isVisible = true
    }

    private fun switchViewPagerToCurrentSong(song: Story) {
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
        if (newItemIndex != -1) {
            binding.vpSong.currentItem = newItemIndex
            curPlayingSong = song
        }
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(this) {
            it?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        result.data?.let { songs ->
                            swipeSongAdapter.songs = songs
                            if (songs.isNotEmpty()) {
                                glide.load((curPlayingSong ?: songs[0]).imageUrl)
                                    .into(binding.ivCurSongImage)
                            }
                            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
                        }
                    }
                    Status.ERROR -> Unit
                    Status.LOADING -> Unit
                }
            }
        }
        mainViewModel.curPlayingSong.observe(this) {
            if (it == null) return@observe

            curPlayingSong = it.toSong()
            glide.load(curPlayingSong?.imageUrl).into(binding.ivCurSongImage)
            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
        }
        mainViewModel.playbackState.observe(this) {
            playbackState = it
            binding.ivPlayPause.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.ic_pause_white else R.drawable.ic_play_white
            )
        }
        mainViewModel.isConnected.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.ERROR -> Snackbar.make(
                        binding.rootLayout,
                        result.message ?: getString(R.string.error),
                        Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }
            }
        }
        mainViewModel.networkError.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.ERROR -> Snackbar.make(
                        binding.rootLayout,
                        result.message ?: getString(R.string.error),
                        Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_other -> gotoCom(Constants.MY_APPS)
            R.id.nav_rate -> gotoPlayStore(packageName)
            R.id.nav_telegram -> gotoCom(Constants.USER_NAME)
            R.id.nav_channel -> gotoCom(Constants.CHANNEL_NAME)
            R.id.nav_share -> shareThisApp(packageName)
            R.id.nav_about -> {
                showAlertDialog()
            }
        }
        return true
    }

    private fun showAlertDialog() {
        binding.drawerLayout.closeDrawer(Gravity.START)
        val view: View = LayoutInflater.from(this).inflate(R.layout.about_layout, null)
        val dialog = AlertDialog.Builder(this).create()
        val bn = AboutLayoutBinding.bind(view)
        dialog.setView(view)
        bn.btnOk.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(Gravity.START)) {
            binding.drawerLayout.closeDrawer(Gravity.START)
        } else {
            super.onBackPressed()
        }
    }
}