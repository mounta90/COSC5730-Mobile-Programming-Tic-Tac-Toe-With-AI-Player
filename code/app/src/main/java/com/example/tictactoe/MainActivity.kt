package com.example.tictactoe

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.tictactoe.databinding.ActivityMainBinding
import com.example.tictactoe.databinding.NewGameDialogBinding
import com.example.tictactoe.fragments.BlankFragment
import com.example.tictactoe.fragments.TieFragment
import com.example.tictactoe.fragments.WinnerFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var blankFragment: BlankFragment
    private lateinit var tieFragment: TieFragment
    private lateinit var winnerFragment: WinnerFragment

    private lateinit var newGameDialogBinding: NewGameDialogBinding
    private lateinit var newGameDialog: Dialog

    private lateinit var appViewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        blankFragment = BlankFragment()
        tieFragment = TieFragment()
        winnerFragment = WinnerFragment()

        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]

        // ---------------------------------------------------------------------
        // New Game Dialog Creation
        // ---------------------------------------------------------------------

        newGameDialog = Dialog(this)

        newGameDialogBinding = NewGameDialogBinding.inflate(layoutInflater)
        newGameDialog.setContentView(newGameDialogBinding.root)

        newGameDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        newGameDialog.setCancelable(false)

        // ---------------------------------------------------------------------

        // ---------------------------------------------------------------------
        // Set initial bottom half of app to a blank fragment...
        // ---------------------------------------------------------------------
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.flFragment, blankFragment)
            .commit()
        // ---------------------------------------------------------------------

        // ---------------------------------------------------------------------
        // New Game Dialog Functionality
        // ---------------------------------------------------------------------
        newGameDialogBinding.rgGameType.setOnCheckedChangeListener { _, _ ->

            if (newGameDialogBinding.rbHumanVsHumanGameType.isChecked) {
                newGameDialogBinding.rbShapeO.isEnabled = false
                newGameDialogBinding.rbShapeX.isEnabled = false
            } else {
                newGameDialogBinding.rbShapeO.isEnabled = true
                newGameDialogBinding.rbShapeX.isEnabled = true
            }

        }

        newGameDialogBinding.btnCancel.setOnClickListener {
            newGameDialog.cancel()
        }

        newGameDialogBinding.btnPlay.setOnClickListener {

            val isVersusAi = newGameDialogBinding.rbHumanVsAiGameType.isChecked

            binding.gameView.resetGame(isVersusAi)

            if (isVersusAi && newGameDialogBinding.rbShapeO.isChecked) {

                binding.gameView.letAiPlayRandomFirstMove()

            }

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.flFragment, blankFragment)
                .commit()

            newGameDialog.cancel()

        }
        // ---------------------------------------------------------------------

        binding.fabNewGame.setOnClickListener {
            newGameDialog.show()
        }

        appViewModel.isGameFinished.observe(this) { isGameFinished: Boolean ->

            if (isGameFinished) {

                // Depending on the game outcome, set a bottom fragment to communicate result...

                val winner: String? = appViewModel.winner.value
                if (winner == null) {

                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.flFragment, tieFragment)
                        .commit()

                } else {

                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.flFragment, winnerFragment)
                        .commit()

                }
            }
        }
    }
}