package com.joybrata.notepad.presentation

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.joybrata.notepad.R
import com.joybrata.notepad.databinding.ActivityMainBinding
import com.joybrata.notepad.domain.model.Note
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.launch
/**
 * Created by Joybrata Paul on 10/08/2024
 **/
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: NotesViewModel by viewModels()
    private val noteAdapter = NoteAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        setupRecyclerView()
        setupInput()
        observeState()
        observeEvents()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        binding.list.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        attachSwipeHandler()
    }

    private fun setupInput() {
        binding.button.setOnClickListener {
            val content = binding.textinput.text.toString()
            viewModel.addNote(content)
            binding.textinput.text.clear()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    noteAdapter.submitList(state.notes)
                }
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is NotesUiEvent.ShowMessage ->
                            Snackbar.make(binding.list, event.message, Snackbar.LENGTH_LONG).show()

                        is NotesUiEvent.NoteDeleted ->
                            Snackbar.make(binding.list, "Item removed", Snackbar.LENGTH_LONG)
                                .setAction("Undo") { viewModel.restoreNote(event.note) }
                                .show()
                    }
                }
            }
        }
    }

    private fun attachSwipeHandler() {
        val callback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                if (position == RecyclerView.NO_POSITION) return
                val note = noteAdapter.noteAt(position)
                when (direction) {
                    ItemTouchHelper.LEFT -> viewModel.deleteNote(note)
                    ItemTouchHelper.RIGHT -> {
                        // Re-bind so the swiped row snaps back before showing the dialog.
                        noteAdapter.notifyItemChanged(position)
                        showEditDialog(note)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
                    .addSwipeLeftBackgroundColor(
                        ContextCompat.getColor(this@MainActivity, R.color.blue)
                    )
                    .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .addSwipeRightBackgroundColor(
                        ContextCompat.getColor(this@MainActivity, R.color.yellow)
                    )
                    .addSwipeRightActionIcon(R.drawable.baseline_edit_24)
                    .addSwipeRightLabel("Edit")
                    .setSwipeRightLabelColor(Color.WHITE)
                    .create()
                    .decorate()

                super.onChildDraw(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(binding.list)
    }

    private fun showEditDialog(note: Note) {
        val input = EditText(this).apply { setText(note.content) }
        AlertDialog.Builder(this)
            .setTitle("Edit Item")
            .setView(input)
            .setPositiveButton("Update") { _, _ ->
                viewModel.updateNote(note, input.text.toString())
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }
}
