package com.googlium.googliumnotes

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.googlium.googliumnotes.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private var editingNote: Note? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.imageNote.setImageURI(it)
            binding.imageNote.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editingNote = arguments?.getSerializable("note") as? Note
        editingNote?.let { note ->
            binding.editNoteTitle.setText(note.title)
            binding.editNoteContent.setText(note.content)
            if (note.imageUri != null) {
                selectedImageUri = Uri.parse(note.imageUri)
                binding.imageNote.setImageURI(selectedImageUri)
                binding.imageNote.visibility = View.VISIBLE
            }
            binding.buttonSave.text = "Update Note"
        }

        binding.buttonUploadImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.buttonSave.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val title = binding.editNoteTitle.text.toString()
        val content = binding.editNoteContent.text.toString()

        if (title.isBlank() && content.isBlank()) {
            return
        }

        val note = editingNote?.copy(
            title = title.ifBlank { "Untitled" },
            content = content,
            imageUri = selectedImageUri?.toString()
        ) ?: Note(
            title = title.ifBlank { "Untitled" },
            content = content,
            imageUri = selectedImageUri?.toString()
        )

        if (editingNote != null) {
            NoteRepository.updateNote(requireContext(), note)
        } else {
            NoteRepository.addNote(requireContext(), note)
        }
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
