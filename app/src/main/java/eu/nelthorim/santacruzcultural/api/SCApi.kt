package eu.nelthorim.santacruzcultural.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject

/**
 * AGENDA. Dirección URL de la Agenda Cultural del Ayuntamiento de S/C de Tenerife.
 */
private const val AGENDA = "https://www.santacruzdetenerife.es/opendata/dataset/f38a3086-8d4c-4c6a-943e-629056b66f01/resource/95a65af3-5f11-4041-a418-45afcc5e7686/download/agendacultural.json";

/**
 * API. Representa una llamada a la API del Ayuntamiento de Santa Cruz para obtener datos.
 * @param context contexto de ejecución de Android
 * @param address dirección de la API a llamar, por defecto es la agenda cultural
 * @property data Array de objetos JSON representando eventos culturales, obtenido de forma asíncrona
 * @property future Futuro que obtendrá los datos de la api
 */
class API(context: Context, address: String = AGENDA) {
    var data: JSONArray? = null
    val future: RequestFuture<JSONObject>

    init {
        // Se inicia una cola de ejecución de Volley y se añade la petición,
        // vinculada a un Futuro que recibirá los datos
        val queue = Volley.newRequestQueue(context)
        future = RequestFuture.newFuture<JSONObject>()
        val request = JsonObjectRequest(Request.Method.GET, address, null, future, future)
        queue.add(request)
    }

    /**
     * Corutina que obtiene los datos de la API de manera asíncrona.
     */
    suspend fun getData() = coroutineScope {
        // Fuerza al Futuro a recibir los datos en un hilo secundario
        data = future.get().getJSONArray("docs")
    }

}
