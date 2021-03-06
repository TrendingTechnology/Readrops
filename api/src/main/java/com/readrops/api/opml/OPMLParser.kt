package com.readrops.api.opml

import android.content.Context
import android.net.Uri
import com.readrops.api.opml.model.OPML
import com.readrops.api.utils.LibUtils
import io.reactivex.Completable
import io.reactivex.Single
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import java.io.OutputStream

class OPMLParser {

    companion object {
        @JvmStatic
        fun read(uri: Uri, context: Context): Single<OPML> {
            return Single.create { emitter ->
                val fileString = LibUtils.fileToString(uri, context)
                val serializer: Serializer = Persister()

                val opml: OPML = serializer.read(OPML::class.java, fileString)

                emitter.onSuccess(opml)
            }
        }

        @JvmStatic
        fun write(opml: OPML, outputStream: OutputStream): Completable {
            return Completable.create { emitter ->
                val serializer: Serializer = Persister()
                serializer.write(opml, outputStream)

                emitter.onComplete()
            }
        }
    }


}