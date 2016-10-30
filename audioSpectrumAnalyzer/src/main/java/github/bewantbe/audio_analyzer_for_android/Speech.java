package github.bewantbe.audio_analyzer_for_android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public TextToSpeech textToSpeech;
    private static final int NAO_ACHOU = 0;
    private static final int ACHOU = 1;
    private List<String> linhasAchadas = new ArrayList<>();
    private Activity activity;

    private SpeechRecognizer getSpeechRecognizer() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity);
            speechRecognizer.setRecognitionListener(recognitionListener);
        }

        return speechRecognizer;
    }

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
//                    Intent installIntent = new Intent();
//                    installIntent.setAction(
//                            TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
//                    activity.startActivity(installIntent);

//                    final Locale myLocale = new Locale("pt", "BR");
//                    if (textToSpeech.isLanguageAvailable(myLocale) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
//                    textToSpeech.setLanguage(myLocale);
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
        //startSpeechRecognition();

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

    public void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        getSpeechRecognizer().startListening(intent);
    }

    private RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
            String message;
            boolean restart = true;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "Audio recording error";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "Client side error";
                    restart = false;
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "Insufficient permissions";
                    restart = false;
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "Network error";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "Network timeout";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "No match";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RecognitionService busy";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "error from server";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "No speech input";
                    break;
                default:
                    message = "Not recognised";
                    break;
            }

            if (restart) {
                stopSpeechRecognition();
                startSpeechRecognition();
            }
        }

        @Override
        public void onResults(Bundle results) {
            // Restart new dictation cycle
            startSpeechRecognition();
            int achou = NAO_ACHOU;
            // Return to the container activity dictation results
            final List<String> stringList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (stringList != null) {
//                stringBuilder.setLength(0);
//                for (int i = 0, size = stringList.size(); i < size; i++) {
//                    stringBuilder.append(stringList.get(i));
//                    stringBuilder.append(" : ");
//                }


                for (String palavraDita : stringList) {
                    List<String> linhasAchadasAux = new ArrayList<>();
                    String linhaDita = palavraDita;
                    linhasAchadas = getLinhaUsuario(linhaDita);
                    if (!linhasAchadas.isEmpty()) {
                        break;
                    }
//                    List<String> linhasAchadasAux = new ArrayList<>();
//                    String linhaDita = palavraDita;
//                    debugMessage.setText(linhaDita);
//                    linhasAchadas=getLinhaUsuario(linhaDita);
//                    if(){
//
//                    }
                }
                mensagens();
//                String linhaDita = stringBuilder.toString();
//                debugMessage.setText(linhaDita);
//                getLinhaUsuario(linhaDita);

//                }else{
//                    // informar que existem mais de uma linha
//                    textToSpeech.speak("mais de uma linha encontrada: ", TextToSpeech.QUEUE_FLUSH, null, null);
//                    for(String linhaComMais :linhasAchadas){
//                        textToSpeech.speak(linhaComMais, TextToSpeech.QUEUE_FLUSH, null, null);
//                    }
//                }

            }
        }

        private List<String> getLinhaUsuario (String linhaDita){


            //if(linhaDita.contains(FRASE_CHAVE)){
            List<String> linhasAchadas = new ArrayList<>();

            String[] parts = linhaDita.split(" ");
            for(String part : parts){

                if(part.equals("sair")){

                    linhasAchadas.add("sair");
                    return linhasAchadas;
                }
                for (String linha : linhasBus){
                    //boolean contains = linha.contains(part);
                    boolean contains = linha.matches(".*\\b"+part+"\\b.*");
                    if(contains){
                        linhasAchadas.add(linha);
                        return linhasAchadas;
                    }
                }
            }

            return linhasAchadas;
        }

        // }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };

    private void mensagens(){
        HashMap<String, String> myHashAlarm = new HashMap();
        textToSpeech.setOnUtteranceCompletedListener(this);
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
                "acabo a porra da mensagem");
        if(linhasAchadas.isEmpty()){
            //Informou ao usuario que nao achou nenhuma linha
            textToSpeech.speak("Nenhuma linha achada, tentar novamente", TextToSpeech.QUEUE_ADD, myHashAlarm);
        }else {
            //Informar ao usuario que achou a linha
            if(linhasAchadas.get(0).equals("sair")){
                textToSpeech.speak("Fechando aplicativo", TextToSpeech.QUEUE_ADD, myHashAlarm);
                // kill();
            }else{
                textToSpeech.speak("linha" + linhasAchadas.get(0) + "encontrádá, monitorando", TextToSpeech.QUEUE_ADD,myHashAlarm);
            }
        }
    }

    @Override
    public void onUtteranceCompleted(String uttId) {
        if (uttId.equals("acabo a porra da mensagem")) {
            String teste=" ";
        }
    }



    public void stopSpeechRecognition() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }


}