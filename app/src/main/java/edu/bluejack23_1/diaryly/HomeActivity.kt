package edu.bluejack23_1.diaryly

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.bluejack23_1.diaryly.journal.JournalFragment
import edu.bluejack23_1.diaryly.moods.MoodsFragment
import edu.bluejack23_1.diaryly.profile.ProfileFragment
import edu.bluejack23_1.diaryly.search.SearchFragment
import java.util.Calendar

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottomNavigationView = findViewById(R.id.bottomNavbar)

        bottomNavigationView.setOnItemSelectedListener { menuitem ->
            when (menuitem.itemId) {
                R.id.menu_moods -> {
                    replaceFragment(MoodsFragment())
                    true
                }
                R.id.menu_journal -> {
                    replaceFragment(JournalFragment())
                    true
                }
                R.id.menu_search -> {
                    replaceFragment(SearchFragment())
                    true
                }
                R.id.menu_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }

        }

        // Replace the fragment with the moods fragment on startup.
        replaceFragment(MoodsFragment())

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance()
//        calendar.set(Calendar.HOUR_OF_DAY, 20) // 8 PM
//        calendar.set(Calendar.MINUTE, 0)
//        calendar.set(Calendar.SECOND, 5)
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1) // 2 minutes from now


        // Schedule the alarm to trigger at 8 PM every day
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }


}