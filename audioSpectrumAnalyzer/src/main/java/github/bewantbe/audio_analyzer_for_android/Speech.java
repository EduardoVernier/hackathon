package github.bewantbe.audio_analyzer_for_android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Speech implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {

    private static final int REQUEST_PERMISSIONS = 2;
    private static final int MY_DATA_CHECK_CODE = 1;
    private TextToSpeech mTts;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private SpeechRecognizer speechRecognizer;
    private StringBuilder stringBuilder = new StringBuilder();
    private List<String> linhasBus = new ArrayList<>();
    private static final String FRASE_CHAVE = "Olá aplicativo";
    private TextToSpeech textToSpeech;
    private static final int NAO_ACHOU = 0;
    private static final int ACHOU = 1;
    private List<String> linhasAchadas = new ArrayList<>();
    private Activity activity;

    public Speech(final Activity activity) {

        this.activity = activity;

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            textToSpeech = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.ERROR) {
                        // cry
                        return;
                    }
                    textToSpeech.setSpeechRate(0.75f);
                    Intent installIntent = new Intent();
                    installIntent.setAction(
                            TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    activity.startActivity(installIntent);

                    final Locale myLocale = new Locale("pt", "BR");
                        textToSpeech.setLanguage(myLocale);
//                    if (textToSpeech.isLanguageAvailable(myLocale) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
//                    } else {
//                        textToSpeech.setLanguage(Locale.US);
//                    }

                    linhasBus.add("110 - Restinga nova via tristeza");
                    linhasBus.add("149 - Icarai");
                    linhasBus.add("266 - Vila nova");
                    comecar();
                }
            });
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSIONS);
        }

        startTextToSpeech();
    }

    private void startTextToSpeech() {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        activity.startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    }


    private void comecar() {
        HashMap<String, String> myHashAlarm = new HashMap();
        textToSpeech.setOnUtteranceCompletedListener(this);
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
                "acabo a porra da mensagem");
        textToSpeech.speak("Bem vindo ao bus sonar. Fale o número da linha ou aguarde um ônibus chegar. Se quiser fechar o aplicativo fale sair", TextToSpeech.QUEUE_ADD, myHashAlarm);
    }

    @Override
    public void onUtteranceCompleted(String uttId) {
        if (uttId.equals("acabo a porra da mensagem")) {
            //startSpeechRecognition();
        }
    }

    public void announceBus(String busName) {
        HashMap<String, String> myHashAlarm = new HashMap();
        textToSpeech.setOnUtteranceCompletedListener(this);
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
                "acabo a porra da mensagem");
        textToSpeech.speak("O ônibus " + busName + " está chegando.", TextToSpeech.QUEUE_ADD, myHashAlarm);
    }

    @Override
    public void onInit(int status) {

    }
}