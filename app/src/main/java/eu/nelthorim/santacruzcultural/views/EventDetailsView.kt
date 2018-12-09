package eu.nelthorim.santacruzcultural.views

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import eu.nelthorim.santacruzcultural.R
import eu.nelthorim.santacruzcultural.api.CulturalEvent
import eu.nelthorim.santacruzcultural.db.Database
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL
import java.time.format.DateTimeFormatter

/**
 * EventDetailsView. Muestra los detalles de un único evento cultural.
 */
class EventDetailsView : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvDesc: TextView
    private lateinit var imImage: ImageView
    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView

    private lateinit var event: CulturalEvent

    val dateFormat = DateTimeFormatter.ofPattern("eeee dd/MM/yyyy HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details_view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        tvName = findViewById(R.id.name)
        tvDesc = findViewById(R.id.description)
        imImage = findViewById(R.id.image)
        tvStartDate = findViewById(R.id.startDate)
        tvEndDate = findViewById(R.id.endDate);

        event = intent.extras?.getParcelable<CulturalEvent>("event")!!
        tvName.text = event.name
        tvDesc.text = Html.fromHtml(event.description, Html.FROM_HTML_MODE_COMPACT)
        tvStartDate.text = event.startDate.format(dateFormat)
        tvEndDate.text = event.endDate.format(dateFormat)

        // Se obtiene la imagen de internet en un hilo secundario y se reemplaza
        // el placeholder en el hilo principal posteriormente
        GlobalScope.launch {
            val imageRequest = downloadImage(event.imageURL)
            val image = imageRequest.get()
            runOnUiThread {
                imImage.setImageBitmap(image)
            }
        }

    }

    /**
     * Permite volver a atrás en el flujo de actividades
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Descarga una imagen desde internet, de manera asíncrona
     * @param url una String representando una URL de Internet conteniendo una imagen
     * @return un RequestFuture que contendrá un Bitmap una vez se haya descargado por un hilo secundario
     */
    fun downloadImage(url: String): RequestFuture<Bitmap> {
        val queue = Volley.newRequestQueue(this)
        val future = RequestFuture.newFuture<Bitmap>()
        // Se requiere reemplazar HTTP por HTTPS en las URLs
        val request = ImageRequest(url.replace("http:", "https:"),
                future, 0, 0, null, null, future)
        queue.add(request)
        return future
    }

    /**
     * Listener de un botón que añade un favorito a la base de datos de favoritos
     * @param view el botón pulsado
     */
    fun addBookmark(view: View) {
        val db = Database.getDB(this)
        val existent = db.query("Bookmarks", null, "json=?", arrayOf(event.rawJSON.toString()), null, null, null)

        // Si el evento está en favoritos, se elimina. Si no, se añade.
        if (existent.count > 0) {
            db.delete("Bookmarks", "json=?", arrayOf(event.rawJSON.toString()))
            Toast.makeText(this, "Evento eliminado de Favoritos", Toast.LENGTH_SHORT).show()
            finish() // Se cierra la vista al eliminar el evento de la vista
        } else {
            val record = ContentValues()
            record.put("json", event.rawJSON.toString())
            db.insert("Bookmarks", null, record)
            Toast.makeText(this, "Evento añadido a Favoritos", Toast.LENGTH_SHORT).show()
        }

        existent.close()
    }

    companion object {
        /**
         * Método Factory para obtener un Intent de esta Activity
         * @param context contexto para crear el intent
         * @param event evento para mostrar en una instancia de EventDetailsView
         */
        fun spawn(context: Context, event: CulturalEvent): Intent {
            val intent = Intent(context, EventDetailsView::class.java)
            intent.putExtra("event", event)
            return intent
        }
    }
}
