package com.example.ourbook

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import java.io.ByteArrayOutputStream

class BookDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_NAME = "booksapp.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "allbooks"
        private const val ROW_ID = "id"
        private const val ROW_NAME = "name"
        private const val ROW_SURENAME = "surename"
        private const val ROW_EMAIL = "email"
        private const val ROW_ADDRESS = "address"
        private const val ROW_DATE = "date"
        private const val ROW_HP = "noHandphone"
        private const val ROW_IMG = "image"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME " +
                "($ROW_ID INTEGER PRIMARY KEY, $ROW_NAME TEXT, $ROW_SURENAME TEXT, $ROW_EMAIL TEXT, " +
                "$ROW_ADDRESS TEXT, $ROW_DATE TEXT, $ROW_HP TEXT, $ROW_IMG BLOB)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertBook(book : Book) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(ROW_NAME, book.name)
            put(ROW_SURENAME, book.surename)
            put(ROW_EMAIL, book.email)
            put(ROW_ADDRESS, book.address)
            put(ROW_DATE, book.date)
            put(ROW_HP, book.hp)
            put(ROW_IMG, book.image)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllBooks(): List<Book> {
        val booksList = mutableListOf<Book>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        while(cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(ROW_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(ROW_NAME))
            val surename = cursor.getString(cursor.getColumnIndexOrThrow(ROW_SURENAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(ROW_EMAIL))
            val address = cursor.getString(cursor.getColumnIndexOrThrow(ROW_ADDRESS))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(ROW_DATE))
            val hp = cursor.getString(cursor.getColumnIndexOrThrow(ROW_HP))
            val image = cursor.getBlob(cursor.getColumnIndexOrThrow(ROW_IMG))

            val book = Book(id, name, surename, email, address, date, hp, image)
            booksList.add(book)
        }
        cursor.close()
        db.close()
        return booksList
    }

    fun updateBook(book : Book) {
        val db = writableDatabase
        val values = ContentValues().apply{
            put(ROW_NAME, book.name)
            put(ROW_SURENAME, book.surename)
            put(ROW_EMAIL, book.email)
            put(ROW_ADDRESS, book.address)
            put(ROW_DATE, book.date)
            put(ROW_HP, book.hp)
            put(ROW_IMG, book.image)
        }
        val whereClause = "$ROW_ID = ?"
        val whereArgs = arrayOf(book.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getBookByID(bookId: Int): Book {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $ROW_ID = $bookId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(ROW_ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(ROW_NAME))
        val surename = cursor.getString(cursor.getColumnIndexOrThrow(ROW_SURENAME))
        val email = cursor.getString(cursor.getColumnIndexOrThrow(ROW_EMAIL))
        val address = cursor.getString(cursor.getColumnIndexOrThrow(ROW_ADDRESS))
        val date = cursor.getString(cursor.getColumnIndexOrThrow(ROW_DATE))
        val hp = cursor.getString(cursor.getColumnIndexOrThrow(ROW_HP))
        val image = cursor.getBlob(cursor.getColumnIndexOrThrow(ROW_IMG))

        cursor.close()
        db.close()
        return Book(id, name, surename, email, address, date, hp, image)
    }

    fun deleteBook(bookId: Int) {
        val db = writableDatabase
        val whereClause = "$ROW_ID = ?"
        val whereArgs = arrayOf(bookId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    fun ImageViewToByte(img: ImageView): ByteArray {
        val bitmap: Bitmap = if (img.drawable is BitmapDrawable) {
            (img.drawable as BitmapDrawable).bitmap
        } else {
            val vectorDrawable = img.drawable
            val width = vectorDrawable.intrinsicWidth
            val height = vectorDrawable.intrinsicHeight
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                val canvas = Canvas(this)
                vectorDrawable.setBounds(0, 0, width, height)
                vectorDrawable.draw(canvas)
            }
        }

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        return stream.toByteArray()
    }
}