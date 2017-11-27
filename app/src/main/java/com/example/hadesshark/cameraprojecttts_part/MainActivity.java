package com.example.hadesshark.cameraprojecttts_part;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    public static final int REQ_SPEECH_TO_TEXT = 0;
    public static final int REQ_TTS_DATA_CHECK = 1;

    private ImageButton ImBtnMic;
    private TextToSpeech tts;
    private TextView tvShow;

    private String option;

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.ERROR) {
            showToast(R.string.text_TextToSpeechError);
            return;
        }

        int available = tts.isLanguageAvailable(Locale.getDefault());

        if (available == TextToSpeech.LANG_NOT_SUPPORTED) {
            showToast(R.string.text_LanguageNotSupported);
            return;
        }

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, REQ_TTS_DATA_CHECK);
    }

    private void showToast(int textId) {
        Toast.makeText(this, textId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_TTS_DATA_CHECK:
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL) {
                    Intent installIntent = new Intent();
                    installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installIntent);
                }
                break;
            case REQ_SPEECH_TO_TEXT:
                if (resultCode == RESULT_OK) {
                    List<String> list = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    option = list.get(0);
                    tvShow.setText(option);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvShow = MainActivity.this.findViewById(R.id.tvShow);

        ImBtnMic = MainActivity.this.findViewById(R.id.ImBtnMic);
        ImBtnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                startActivityForResult(intent, REQ_SPEECH_TO_TEXT);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.shutdown();
        }
    }
}
