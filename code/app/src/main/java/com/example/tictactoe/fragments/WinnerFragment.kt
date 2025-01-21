package com.example.tictactoe.fragments


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tictactoe.AppViewModel
import com.example.tictactoe.R
import com.example.tictactoe.databinding.FragmentWinnerBinding

class WinnerFragment : Fragment(R.layout.fragment_winner) {
    private var fragmentBinding: FragmentWinnerBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentWinnerBinding.bind(view)

        // "fragmentBinding" gets declared to "binding" memory reference (I think?)
        fragmentBinding = binding

        val viewModelOwner = requireActivity()
        val appViewModel: AppViewModel = ViewModelProvider(viewModelOwner)[AppViewModel::class.java]

        val winner = appViewModel.winner.value
        val loser = appViewModel.loser.value

        binding.tvWinner.text = getString(R.string.tv_winner_text, winner)
        binding.tvLoser.text = getString(R.string.tv_loser_text, loser)

    }

    override fun onDestroyView() {
        // I believe the variables "fragmentBinding" and "binding" share the same...
        // memory location due to Kotlin's way of assigning object variables through...
        // reference. So assigning null to "fragmentBinding" also assigns null...
        // to "binding". This allows us to clean up memory used by "binding".
        fragmentBinding = null
        super.onDestroyView()
    }
}
