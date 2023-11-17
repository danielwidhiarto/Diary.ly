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
import java.util.TimeZone

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

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))

//        //Test EveryMinute Show
//        // Set the initial trigger time to the current time plus 1 minute
//        calendar.add(Calendar.MINUTE, 1)
//        // Schedule the alarm to trigger every minute
//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            60 * 1000, // 60 seconds * 1000 milliseconds = 1 minute
//            pendingIntent
//        )

        //Scheduled Every 8PM
        // Set the trigger time to 8 PM
        calendar.set(Calendar.HOUR_OF_DAY, 20) // 8 PM
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        // Schedule the alarm to trigger every day at 8 PM
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