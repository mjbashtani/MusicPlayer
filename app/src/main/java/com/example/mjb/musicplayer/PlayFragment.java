package com.example.mjb.musicplayer;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mjb.musicplayer.model.Album;
import com.example.mjb.musicplayer.model.Artist;
import com.example.mjb.musicplayer.model.Music;
import com.example.mjb.todo.utils.PictureUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class PlayFragment extends Fragment {
   private CircleImageView mCoverArt;
   private Button playButton,nextButton,previousButton,repeatButton,repeatAllButton,shuffleButton;
   private SeekBar timeSeekBar;
   private   TextView elapsedTextView,remainingTextView,nameTextField,artistTextFiled,nextTextView;
   private MediaPlayer mMediaPlayer;
   private int totalTime;
   private List<Music> playingMusicList;
   private List<Music> OrginalMusicList;
   private Album mAlbum;
   private Artist mArtist;
   private Music mCurrentMusic;
   private int number;
   private Boolean ShuffleMode = false;

    public Boolean getShuffleMode() {
        return ShuffleMode;
    }

    @SuppressLint("NewApi")
    public void setShuffleMode(Boolean shuffleMode) {
        if(shuffleMode){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                shuffleButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
            }

            Collections.shuffle(playingMusicList);
            number = playingMusicList.indexOf(mCurrentMusic);
            setNextMusicLabel();


        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                shuffleButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            }

            playingMusicList =  new ArrayList<>(OrginalMusicList);
            number = playingMusicList.indexOf(mCurrentMusic);
            setNextMusicLabel();

        }


        ShuffleMode = shuffleMode;
    }

    public static PlayFragment newInstance(ArrayList<Music> musicList, int number) {

        Bundle args = new Bundle();
        args.putSerializable("musiclisted",  musicList);
        args.putInt("number",number);
        PlayFragment fragment = new PlayFragment();
        fragment.setArguments(args);
        return fragment;
    }




    public PlayFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        mAlbum = (Album) getArguments().getSerializable("albumseted");
        mArtist = (Artist) getArguments().getSerializable("artistseted");

        if(playingMusicList == null)
            OrginalMusicList = (ArrayList<Music>) getArguments().getSerializable("musiclisted");
        playingMusicList = new ArrayList<>(OrginalMusicList);


        playButton = view.findViewById(R.id.playbtn);
        nextButton = view.findViewById(R.id.next_button);
        previousButton = view.findViewById(R.id.previous_button);
        mCoverArt = view.findViewById(R.id.playing_imageview);
        timeSeekBar = view.findViewById(R.id.positionBar);
        elapsedTextView = view.findViewById(R.id.elapsTimeLabel2);
        remainingTextView = view.findViewById(R.id.remainingTimeLabel2);
        nameTextField = view.findViewById(R.id.musicname_playing);
        shuffleButton = view.findViewById(R.id.shuffle_btn);
        artistTextFiled = view.findViewById(R.id.artistname_playing);
        number = getArguments().getInt("number");
        nextTextView = view.findViewById(R.id.next_textview);
        repeatButton = view.findViewById(R.id.repeat_button);
        repeatAllButton = view.findViewById(R.id.refresh_button);
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeMusicTo(number);
            }
        });
        repeatAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeMusicTo(0);
            }
        });
       shuffleButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               setShuffleMode(!getShuffleMode());
           }
       });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number == playingMusicList.size()-1)
                    number = -1;

                ChangeMusicTo(number+1);
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number != 0)
                    number = number -1;
                ChangeMusicTo((number)% playingMusicList.size());
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mMediaPlayer.isPlaying())
                    mMediaPlayer.start();
                else
                    mMediaPlayer.pause();
                detectPlayButton();


            }
        });
        ChangeMusicTo(number);

        timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                    mMediaPlayer.seekTo(progress);
                    timeSeekBar.setProgress(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mMediaPlayer != null){
                    try {
                        Message message = new Message();
                        message.what = mMediaPlayer.getCurrentPosition();
                        mHandler.sendMessage(message);
                        Thread.sleep(1000);


                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }

                }

            }
        });
        thread.start();




        return view;
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int currentPositon = msg.what;
            timeSeekBar.setProgress(currentPositon);
            String elapsedTime = createTimeLabel(currentPositon);
            elapsedTextView.setText(elapsedTime);
            String remainingTime = createTimeLabel(totalTime-currentPositon);
            remainingTextView.setText("- "+remainingTime);
            if(currentPositon == totalTime) {
                mMediaPlayer.seekTo(0);
                mMediaPlayer.start();
            }

        }
    };

public String createTimeLabel(int time){

    String timelabel = "";
    int min = time/1000/60;
    int sec =  time/1000%60;

    timelabel = min + ":";

    if (sec<10)
        timelabel += "0";
    timelabel += sec;
return timelabel;

}

    @Override
    public void onPause() {
    mMediaPlayer.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
    mMediaPlayer.release();
        super.onDestroy();
    }

    @Override
    public void onResume() {
   detectPlayButton();
        super.onResume();
    }
    private void ChangeMusicTo(int Number){

   if(mMediaPlayer != null)
       mMediaPlayer.stop();
        number = Number;
        mCurrentMusic = playingMusicList.get(number);
        mMediaPlayer = MediaPlayer.create(getActivity(),Uri.fromFile(new File(mCurrentMusic.getPath())));

        artistTextFiled.setText(" -"+mCurrentMusic.getArtist());
        nameTextField.setText(mCurrentMusic.getTitle());
        mMediaPlayer.seekTo(0);
        mMediaPlayer.isLooping();
        totalTime = mMediaPlayer.getDuration();
        timeSeekBar.setMax(totalTime);
        if(mCurrentMusic.getCoverPath() != null)
        mCoverArt.setImageBitmap(PictureUtils.getScaledBitmap(mCurrentMusic.getCoverPath(),300,300));
        else
            mCoverArt.setImageResource(R.drawable.music);
        setNextMusicLabel();

        mMediaPlayer.start();
    detectPlayButton();

    }

    private void setNextMusicLabel() {
        nextTextView.setText(playingMusicList.get(number != playingMusicList.size()-1 ? number+1 : 0).getTitle());
    }

    private void detectPlayButton(){
       if(mMediaPlayer.isPlaying())
           playButton.setBackgroundResource(R.drawable.pause);
       else
           playButton.setBackgroundResource(R.drawable.play);

   }
}
