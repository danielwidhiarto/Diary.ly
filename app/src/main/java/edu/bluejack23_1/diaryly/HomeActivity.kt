package edu.bluejack23_1.diaryly

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.bluejack23_1.diaryly.journal.JournalFragment
import edu.bluejack23_1.diaryly.moods.MoodsFragment
import edu.bluejack23_1.diaryly.profile.ProfileFragment
import edu.bluejack23_1.diaryly.search.SearchFragment

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
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }
}