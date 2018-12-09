package eu.nelthorim.santacruzcultural

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import eu.nelthorim.santacruzcultural.api.API
import eu.nelthorim.santacruzcultural.api.CulturalEvent
import eu.nelthorim.santacruzcultural.db.Database
import eu.nelthorim.santacruzcultural.views.EventDetailsView
import eu.nelthorim.santacruzcultural.views.EventView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * Mapa. Actividad principal de SantaCruzCultural.
 */
class Mapa : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var api: API;
    private lateinit var cluster : ClusterManager<CulturalEvent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        api = API(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val sctfe = LatLng(28.463824, -16.251825)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sctfe))

        setupClusterer()

        // Se obtienen los datos en una coroutina (hilo secundario)
        GlobalScope.launch {
            api.getData()
            // Se crean los marcadores en el hilo de interfaz a continuación (principal)
            runOnUiThread {
                createEvents()
            }
        }
    }

    /**
     * Configura el gestor de clústeres de marcadores
     */
    fun setupClusterer() {
        cluster = ClusterManager(this, mMap);

        mMap.setOnCameraIdleListener(cluster)
        mMap.setOnMarkerClickListener(cluster)

        // Crea un EventView con todos los eventos debajo del clúster
        cluster.setOnClusterClickListener {
            val intent = EventView.spawn(this, it.items as ArrayList<CulturalEvent>)
            startActivity(intent)
            true
        }

        // Crea un EventDetailsView para un evento individual
        cluster.setOnClusterItemClickListener {
            val intent = EventDetailsView.spawn(this, it)
            startActivity(intent)
            true
        }

    }

    /**
     * Crea los marcadores de eventos en el clúster
     */
    fun createEvents() {
        for (i in 0 until api.data!!.length()) {
            val event = CulturalEvent.from(api.data!!.getJSONObject(i))
            if (event != null)
                cluster.addItem(event)
        }
    }

    /**
     * Listener de botón que obtiene y abre la ventana de marcadores
     * @param view Botón pulsado
     */
    fun openBookmarks(view: View) {
        val db = Database.getDB(this)
        val cursor = db.rawQuery("select * from Bookmarks", null)
        val eventList = ArrayList<CulturalEvent>()

        while (cursor.moveToNext()) {
            eventList.add(CulturalEvent.from(JSONObject(cursor.getString(cursor.getColumnIndex("json"))))!!)
        }
        cursor.close()

        val intent = EventView.spawn(this, eventList)
        startActivity(intent)
    }

}
