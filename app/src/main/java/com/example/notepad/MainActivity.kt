package com.example.notepad

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class MainActivity : AppCompatActivity() {

    private lateinit var item: EditText
    private lateinit var button: Button
    lateinit var list: RecyclerView

    var listItems = ArrayList<String>()

    var fileHelper = FileHelper()

    private var deleteIcon: Drawable? = null
    private var editIcon: Drawable? = null
    private val background = ColorDrawable()
    private lateinit var itemAdapter: ItemAdapter

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.baseline_delete_24)
        editIcon = ContextCompat.getDrawable(this, R.drawable.baseline_edit_24)
        item = findViewById(R.id.textinput)
        button = findViewById(R.id.button)
        list = findViewById(R.id.list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listItems = fileHelper.readData(this)
        val recyclerView: RecyclerView = findViewById(R.id.list)
        itemAdapter = ItemAdapter(listItems)
        recyclerView.adapter = itemAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        button.setOnClickListener {
            val itemName = item.text.toString().trim()
            if (itemName.isNotEmpty()) {
                listItems.add(itemName)
                item.text.clear()
                fileHelper.writeData(listItems, applicationContext)
                itemAdapter.notifyItemInserted(listItems.size - 1)
            } else {
                Toast.makeText(this, "Please enter a valid text", Toast.LENGTH_LONG).show()
            }
        }

        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val removedItem = listItems[position]
                        listItems.removeAt(position)
                        fileHelper.writeData(listItems, applicationContext)
                        itemAdapter.notifyItemRemoved(position)

                        val undoMessage = Snackbar.make(list, "Item removed", Snackbar.LENGTH_LONG)
                        undoMessage.setAction("Undo") {
                            listItems.add(position, removedItem)
                            itemAdapter.notifyItemInserted(position)
                        }
                        undoMessage.show()
                    }
                    ItemTouchHelper.RIGHT -> {
                        showEditDialog(position)
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
                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.blue))
                    .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.yellow))
                    .addSwipeRightActionIcon(R.drawable.baseline_edit_24)
                    .addSwipeRightLabel("Edit")
                    .setSwipeRightLabelColor(Color.WHITE)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .create()
                    .decorate()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                background.draw(c)
                deleteIcon!!.draw(c)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun showEditDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Item")

        val input = EditText(this)
        input.setText(listItems[position])
        builder.setView(input)

        builder.setPositiveButton("Update") { _, _ ->
            val editedText = input.text.toString().trim()
            if (editedText.isNotEmpty()) {
                listItems[position] = editedText
                fileHelper.writeData(listItems, applicationContext)
                itemAdapter.notifyItemChanged(position)
            } else {
                Toast.makeText(this, "Please enter a valid text", Toast.LENGTH_LONG).show()
                itemAdapter.notifyItemChanged(position)
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
            itemAdapter.notifyItemChanged(position)
        }

        builder.show()
    }

    inner class ItemAdapter(private val items: ArrayList<String>) :
        RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.textView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val item = items.getOrNull(position)
            holder.textView.text = item ?: ""
        }

        override fun getItemCount() = items.size
    }
}