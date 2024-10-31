package com.example.ourbook

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class BookAdapter (private var book: List<Book>, context: Context) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private val db: BookDatabaseHelper = BookDatabaseHelper(context)

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.txtNama)
        val foto: ImageView = itemView.findViewById(R.id.Photo)
        val updateButton: ImageView = itemView.findViewById(R.id.btnEdit)
        val deleteButton: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int = book.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = book[position]
        holder.nama.text = book.name

        if (book.image != null) {
            val bmp = BitmapFactory.decodeByteArray(book.image, 0, book.image.size)
            holder.foto.setImageBitmap(bmp)
        } else {
            holder.foto.setImageResource(R.drawable.baseline_person_24)
        }

        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateBook::class.java).apply {
                putExtra("book_id", book.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context).apply {
                setTitle("Delete Confirmation")
                setMessage("Are you sure?")

                setPositiveButton("Yes") { dialog, _ ->
                    db.deleteBook(book.id)
                    refreshData(db.getAllBooks())
                    Toast.makeText(holder.itemView.context, "Note Deleted", Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                }

                setNegativeButton("No") { dialog, _ ->
                    Toast.makeText(holder.itemView.context, "Delete Cancelled", Toast.LENGTH_SHORT)
                        .show()
                    dialog.dismiss()
                }
            }.show()
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, DetailBook::class.java).apply {
                putExtra("book_id", book.id)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    fun refreshData(newBooks : List<Book>) {
        book = newBooks
        notifyDataSetChanged()
    }
}
