package com.googlium.googliumnotes

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.googlium.googliumnotes.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val note = arguments?.getSerializable("note") as? Note
        note?.let { currentNote ->
            binding.textDetailTitle.text = currentNote.title
            binding.textDetailContent.text = currentNote.content
            if (currentNote.imageUri != null) {
                binding.imageDetail.setImageURI(Uri.parse(currentNote.imageUri))
                binding.imageDetail.visibility = View.VISIBLE
            } else {
                binding.imageDetail.visibility = View.GONE
            }

            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_detail, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.action_edit -> {
                            val bundle = Bundle().apply {
                                putSerializable("note", currentNote)
                            }
                            findNavController().navigate(R.id.SecondFragment, bundle)
                            true
                        }
                        R.id.action_delete -> {
                            NoteRepository.deleteNote(requireContext(), currentNote.id)
                            findNavController().navigateUp()
                            true
                        }
                        else -> false
                    }
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
