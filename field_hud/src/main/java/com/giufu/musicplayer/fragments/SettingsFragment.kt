package com.giufu.musicplayer.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.giufu.musicplayer.R
import com.giufu.musicplayer.menu.MenuActivity
import kotlin.properties.Delegates

class SettingsFragment : BaseFragment() {
    private var textView: TextView? = null
    private var footer: TextView? = null
    private var timestamp: TextView? = null
    private lateinit var units: String
    private lateinit var gateway: String
    private var port by Delegates.notNull<Int>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.main_layout, container, false)
        textView = TextView(context)
        textView!!.textSize = BODY_TEXT_SIZE.toFloat()
        textView!!.typeface = Typeface.create(
            getString(R.string.thin_font),
            Typeface.NORMAL
        )
        val bodyLayout = view.findViewById<FrameLayout>(R.id.body_layout)
        bodyLayout.addView(textView)
        footer = view.findViewById(R.id.footer)
        timestamp = view.findViewById(R.id.timestamp)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        updateText()
        return view
    }

    private fun updateText() {
        units = sharedPreferences.getString("units","imperial")
        gateway = sharedPreferences.getString("gateway","")
        port = sharedPreferences.getInt("port", 8080)
        textView!!.text = "Current settings:\nUnits: $units" +
                "\nPhone ip: $gateway\nPhone port: $port"
    }

    companion object {
        private const val BODY_TEXT_SIZE = 40
        fun newInstance(menu: Int?): SettingsFragment {
            val myFragment = SettingsFragment()
            val args = Bundle()
            if (menu != null) {
                args.putInt(MENU_KEY, menu)
            }
            myFragment.arguments = args
            return myFragment
        }
    }

    override fun onSingleTapUp() {
        //super.onSingleTapUp();
        if (arguments != null) {
            val menu = requireArguments().getInt(MENU_KEY, MENU_DEFAULT_VALUE)
            if (menu != MENU_DEFAULT_VALUE) {
                val intent = Intent(
                    activity,
                    MenuActivity::class.java
                )
                intent.putExtra(MENU_KEY, menu)
                startActivityForResult(intent, REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val id = data.getIntExtra(MenuActivity.EXTRA_MENU_ITEM_ID_KEY, MenuActivity.EXTRA_MENU_ITEM_DEFAULT_VALUE)
            when (id) {
                R.id.action_units -> {
                    if (units == "metric"){
                        sharedPreferences.edit().putString("units","imperial").apply()
                    }
                    else{
                        sharedPreferences.edit().putString("units","metric").apply()
                    }
                }
            }
            updateText()
        }
    }
}