package eu.nelthorim.santacruzcultural.views

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import eu.nelthorim.santacruzcultural.R
import eu.nelthorim.santacruzcultural.api.CulturalEvent

/**
 * EventView. Vista de lista de eventos culturales en una zona.
 */
class EventView : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_view)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var data: ArrayList<CulturalEvent> = ArrayList()

        try {
            data = intent.getParcelableArrayListExtra<CulturalEvent>("events")
        } catch (ex: NullPointerException) {
            // should never happen
        }

        viewManager = LinearLayoutManager(this)
        eventAdapter = EventAdapter(data as List<CulturalEvent>)

        // Se configura la RecyclerView con sus requerimientos
        recyclerView = findViewById<RecyclerView>(R.id.eventView).apply {
            setHasFixedSize(true) // para mejorar el rendimiento, no tiene que calcular posibilidades de cambio de tamaño
            layoutManager = viewManager
            adapter = eventAdapter
        }

    }

    /**
     * Implementa la funcionalidad de volver atrás en el flujo de actividades.
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        /**
         * Método Factory que crea un Intent para iniciar una actividad EventView.
         * @param context contexto del Intent
         * @param events ArrayList de CulturalEvents a mostrar en la lista de eventos
         */
        fun spawn(context: Context, events: ArrayList<CulturalEvent>): Intent {
            val newIntent = Intent(context, EventView::class.java)
            newIntent.putParcelableArrayListExtra("events", events)
            return newIntent
        }
    }

}
