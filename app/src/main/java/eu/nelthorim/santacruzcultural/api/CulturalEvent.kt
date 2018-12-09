package eu.nelthorim.santacruzcultural.api

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * CulturalEvent. Dataclase que representa un evento cultural en Santa Cruz de Tenerife.
 * @property name El nombre de la actividad
 * @property latlong La localización geográfica del evento
 * @property description La descripción el evento
 * @property startDate La fecha de inicio del evento
 * @property endDate La fecha de finalización del evento
 * @property imageURL La URL de la imagen representativa del evento
 * @property rawJSON El JSON crudo que representa el evento
 */
data class CulturalEvent(val name: String, val latlong: LatLng, var description: String,
                         var startDate: LocalDateTime, var endDate: LocalDateTime, var imageURL: String,
                         var rawJSON: JSONObject) : ClusterItem, Parcelable {

    // Necesarios para implementar ClusterItem
    override fun getSnippet(): String = description
    override fun getTitle(): String = name
    override fun getPosition(): LatLng = latlong

    /**
     * Constructor alternativo que crea un CulturalEvent desde un Parcel de Android
     * @param parcel Parcel que contiene los datos de un evento cultural
     */
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readParcelable(LatLng::class.java.classLoader)!!,
            parcel.readString()!!,
            parcel.readSerializable() as LocalDateTime,
            parcel.readSerializable() as LocalDateTime,
            parcel.readString()!!,
            JSONObject(parcel.readString()!!)
    )

    /**
     * Escribe los datos de un evento cultural a un Parcel
     * @param parcel un Parcel vacío a rellenar
     * @param flags ignorado
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeParcelable(latlong, flags)
        parcel.writeString(description)
        parcel.writeSerializable(startDate)
        parcel.writeSerializable(endDate)
        parcel.writeString(imageURL)
        parcel.writeString(rawJSON.toString())
    }

    /**
     * Ignorado, necesario para implementar Parcelable
     */
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CulturalEvent> {
        /**
         * Método Factory para crear un evento Cultural desde un objeto JSON
         * @param eventObject un CulturalEvent si el JSON es válido, null si no
         */
        fun from(eventObject: JSONObject): CulturalEvent? {
            val name = eventObject["nombre"] as String
            val latlong: LatLng?

            // Algunos eventos tienen localización NULL, los ignoramos
            try {
                latlong = LatLng(eventObject.getDouble("latitud"), eventObject.getDouble("longitud"))
            } catch (ex: JSONException) {
                return null
            }

            val description= eventObject["descripcion"] as String

            // Los formatos de fecha y hora son extraños (hora viene en segundos)
            val startDay = eventObject["fecha_inicio"].toString()
            val startHour = String.format("%02d", eventObject["hora_inicio"] as Int / 3600) + String.format("%02d", (eventObject["hora_inicio"] as Int % 3600) / 60)
            val startDate = LocalDateTime.parse(startDay + startHour,
                    DateTimeFormatter.ofPattern("yyyyMMddHHmm"))

            val endDay = eventObject["fecha_fin"].toString()
            val endHour = String.format("%02d", eventObject["hora_fin"] as Int / 3600) + String.format("%02d", (eventObject["hora_fin"] as Int % 3600) / 60)
            val endDate = LocalDateTime.parse(endDay+endHour,
                    DateTimeFormatter.ofPattern("yyyyMMddHHmm"))

            val imageURL = eventObject["imagen"] as String

            return CulturalEvent(name, latlong, description, startDate, endDate, imageURL, eventObject)
        }

        /**
         * Método Factory para crear un CulturalEvent desde un Parcel. Necesario para implementar Parcel.
         * @param parcel un Parcel con los datos de un evento cultural
         */
        override fun createFromParcel(parcel: Parcel): CulturalEvent {
            return CulturalEvent(parcel)
        }

        /**
         * Método Factory que construye un Array vacío de CulturalEvents a ser rellenado por Android
         * @param size el tamaño del Array
         */
        override fun newArray(size: Int): Array<CulturalEvent?> {
            return arrayOfNulls(size)
        }
    }
}