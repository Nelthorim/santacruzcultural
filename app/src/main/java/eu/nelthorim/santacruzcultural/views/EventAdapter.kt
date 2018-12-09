package eu.nelthorim.santacruzcultural.views

import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import eu.nelthorim.santacruzcultural.R
import eu.nelthorim.santacruzcultural.api.CulturalEvent
import java.time.format.DateTimeFormatter

/**
 * EventAdapter. Adaptador de eventos culturales a un RecyclerView
 * @property data lista de objetos CulturalEvent a mostrar en la vista
 */
class EventAdapter(private val data: List<CulturalEvent>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    /**
     * EventViewHolder. Inicializa la vista de cada evento cultural en un RecyclerView, añadiendo
     * un listener a cada una.
     * @property view Vista a la que se va a acoplar el evento
     */
    inner class EventViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        init {
            // Al hacer click en cada evento, se abre una vista de detalles
            view.setOnClickListener {
                val intent = EventDetailsView.spawn(view.context, data[adapterPosition])
                view.context.startActivity(intent)
            }
        }
    }

    /**
     * Callback que se llama para vincular cada vista con su evento. Se configuran los datos a mostrar
     * @param holder un objeto de EventViewHolder
     * @param position la posición del evento en la lista de eventos a vincular
     */
    override fun onBindViewHolder(holder: EventAdapter.EventViewHolder, position: Int) {
        val tvName = holder.view.findViewById<TextView>(R.id.listName)
        val tvDesc = holder.view.findViewById<TextView>(R.id.listDesc)
        val tvStartDate = holder.view.findViewById<TextView>(R.id.listStartDate)
        val tvEndDate = holder.view.findViewById<TextView>(R.id.listEndDate)

        val dateFormat = DateTimeFormatter.ofPattern("eeee dd/MM/yyyy HH:mm")

        tvName.text = data[position].name
        tvDesc.text = "${Html.fromHtml(data[position].description, Html.FROM_HTML_MODE_COMPACT).split(".")[0]}..."
        tvStartDate.text = Html.fromHtml("<b>Inicio: </b>${data[position].startDate.format(dateFormat)}",
                Html.FROM_HTML_MODE_COMPACT)
        tvEndDate.text = Html.fromHtml("<b>Fin: </b>${data[position].endDate.format(dateFormat)}",
                Html.FROM_HTML_MODE_COMPACT)

    }

    /**
     * Callback que se llama cuando se crea cada ViewHolder
     * @param parent grupo de vistas que contendrá la vista a crear
     * @param viewType ignorado
     * @return un EventAdapter.EventViewHolder para el evento correspondiente
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapter.EventViewHolder {
        // Se obtiene el Layout a usar por ID y se crea un Holder con él
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.event_view, parent, false)
        return EventViewHolder(view)
    }

    /**
     * Devuelve el número de elementos vinculados al Adapter
     * @return el número de eventos que se van a mostrar en la vista
     */
    override fun getItemCount(): Int = data.size

}