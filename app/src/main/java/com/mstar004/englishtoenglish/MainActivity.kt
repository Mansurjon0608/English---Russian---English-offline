package com.mstar004.englishtoenglish

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.mstar004.englishtoenglish.databinding.AboutDialogBinding
import com.mstar004.englishtoenglish.databinding.ActivityMainBinding
import com.mstar004.englishtoenglish.databinding.FeedbackDialogBinding
import uz.ollohberdi.utils.LanguageShared
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var engRus: Translator
    private lateinit var rusEng: Translator
    private var language = 0
    private lateinit var mTTsFrom: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        downloadResource()
        checkLanguage()
        clickPlaySound()
        replaceLanguage()
        deleteText()
        copyTextOnClick()
        navigationViewMenu()
        binding.fromEditText.addTextChangedListener {
            translate(language, binding.fromEditText.text.toString())
        }


    }

    private fun feedbackDialog() {
        val dialog = AlertDialog.Builder(this).create()
        val item = FeedbackDialogBinding.inflate(layoutInflater)
        dialog.setView(item.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

    }

    private fun aboutDialog() {
        val dialog = AlertDialog.Builder(this).create()
        val item = AboutDialogBinding.inflate(layoutInflater)
        dialog.setView(item.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

    }

    private fun navigationViewMenu() {
        binding.menuImage.setOnClickListener {
            binding.drawableRoot.open()
            binding.navigationView.setNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menuAbout -> {
                        aboutDialog()
                    }
                    R.id.menuContactme -> {
                        feedbackDialog()
                    }
                }
                true
            }
        }
    }

    private fun copyTextOnClick() {
        binding.copyTheTranslatetext.setOnClickListener {
            copyText(binding.translateEditText.text.toString())
        }
        binding.copyTheoriginalText.setOnClickListener {
            copyText(binding.fromEditText.text.toString())
        }
    }

    @SuppressLint("ServiceCast")
    private fun copyText(copyText: String) {
        val clipboard =
            getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", copyText)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
    }

    private fun deleteText() {
        binding.clearOriginal.setOnClickListener {
            binding.fromEditText.text.clear()
        }
        binding.clearTrans.setOnClickListener {
            binding.translateEditText.text.clear()
        }
    }

    private fun replaceLanguage() {
        binding.cardTranslateReplace.setOnClickListener {
            binding.fromEditText.text.clear()
            binding.translateEditText.text.clear()
            when (language) {
                0 -> {
                    language = 1
                    LanguageShared.init(this)
                    LanguageShared.language = 1
                }
                1 -> {
                    language = 0
                    LanguageShared.init(this)
                    LanguageShared.language = 0
                }
            }
            checkLanguage()
        }
    }

    private fun clickPlaySound() {
        binding.soundPlayFrom.setOnClickListener {
            speakOriginal(language, binding.fromEditText.text.toString())
        }
        binding.soundPlayTranslate.setOnClickListener {
            speakTranslate(language, binding.translateEditText.text.toString())
        }
    }

    private fun speakTranslate(language: Int, text: String) {
        Toast.makeText(this, "speakTranslate", Toast.LENGTH_SHORT).show()
        when (language) {
            1 -> {

                mTTsFrom = TextToSpeech(this) { status ->
                    mTTsFrom.language = Locale.getDefault()
                    if (status == TextToSpeech.SUCCESS) {
                        mTTsFrom.language = Locale("en")
                        val pitch = 0.8f
                        val speed = 1.1f
                        mTTsFrom.setPitch(pitch)
                        mTTsFrom.setSpeechRate(speed)
                        mTTsFrom.speak(text, TextToSpeech.QUEUE_FLUSH, null)
                    }
                }
            }
            0 -> {
                mTTsFrom = TextToSpeech(this) {
                    mTTsFrom.language = Locale.getDefault()
                    if (it == TextToSpeech.SUCCESS) {
                        mTTsFrom.language = Locale("ru")
                    }
                    val pitch = 0.8f
                    val speed = 1.1f
                    mTTsFrom.setPitch(pitch)
                    mTTsFrom.setSpeechRate(speed)
                    mTTsFrom.speak(text, TextToSpeech.QUEUE_FLUSH, null)

                }
            }

        }
    }

    private fun speakOriginal(language: Int, text: String) {
        Toast.makeText(this, "speakOriginal", Toast.LENGTH_SHORT).show()
        when (language) {
            0 -> {

                mTTsFrom = TextToSpeech(this) { status ->
                    mTTsFrom.language = Locale.getDefault()
                    if (status == TextToSpeech.SUCCESS) {
                        mTTsFrom.language = Locale("en")
                        val pitch = 0.8f
                        val speed = 1.1f
                        mTTsFrom.setPitch(pitch)
                        mTTsFrom.setSpeechRate(speed)
                        mTTsFrom.speak(text, TextToSpeech.QUEUE_FLUSH, null)
                    }
                }
            }
            1 -> {
                mTTsFrom = TextToSpeech(this) {
                    mTTsFrom.language = Locale.getDefault()
                    if (it == TextToSpeech.SUCCESS) {
                        if (mTTsFrom.isLanguageAvailable(Locale(Locale.getDefault().language))
                            == TextToSpeech.LANG_AVAILABLE
                        ) {
                            mTTsFrom.language = Locale(Locale.getDefault().language)
                        } else {
                            mTTsFrom.language = Locale("ru")
                        }
                        val pitch = 0.8f
                        val speed = 1.1f
                        mTTsFrom.setPitch(pitch)
                        mTTsFrom.setSpeechRate(speed)
                        mTTsFrom.speak(text, TextToSpeech.QUEUE_FLUSH, null)

                    }
                }
            }
        }

    }

    private fun checkLanguage() {
        LanguageShared.init(this)
        language = LanguageShared.language

        when (language) {
            0 -> {
                binding.fromEditText.setHint(R.string.Text)
                binding.translateEditText.setHint(R.string.Translate)
            }
            1 -> {
                binding.fromEditText.setHint(R.string.TextRussian)
                binding.translateEditText.setHint(R.string.TranslateRussian)
            }
        }
    }

    private fun translate(i: Int, text: String) {
        when (i) {
            0 -> {
                engRus.translate(text).addOnSuccessListener {
                    binding.translateEditText.setText(it)
                }
            }
            1 -> {
                rusEng.translate(text).addOnSuccessListener {
                    binding.translateEditText.setText(it)
                }
            }
        }

    }

    private fun downloadResource() {
        val option1 = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.RUSSIAN)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        rusEng = Translation.getClient(option1)
        val option2 = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.RUSSIAN)
            .build()
        engRus = Translation.getClient(option2)
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "Downloading resources, please connect the internet..."
        pDialog.setCancelable(false)
        pDialog.show()
        engRus.downloadModelIfNeeded().addOnSuccessListener {
            rusEng.downloadModelIfNeeded().addOnSuccessListener {
                pDialog.cancel()
            }
        }
    }
}