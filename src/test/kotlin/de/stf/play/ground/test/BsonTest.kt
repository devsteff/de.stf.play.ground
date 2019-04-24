package de.stf.play.ground.test

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.internal.LinkedTreeMap
import org.junit.Assert.assertEquals
import org.junit.Test

class BsonTest {

    @Test
    fun test1() {
        val res= handleIt<Any>()
        assertEquals("Any",  LinkedTreeMap::class.java, res?.javaClass)
        println("${res?.javaClass}=$res")
    }

    @Test
    fun test2() {
        val res= handleIt<JsonObject>()
        assertEquals("JsonElement", JsonObject::class.java, res?.javaClass)
        println("${res?.javaClass}=$res")
    }

    @Test
    fun test3() {
        val res= handleIt<List<LinkedTreeMap<String, String>>>(true)
        assertEquals("String", ArrayList::class.java, res?.javaClass)
        println("${res?.javaClass}=$res")
    }

    companion object {
        const val JSON =
            "{\"response\":[],\"_links\":{\"dvagResource\":\"https://entwicklung.api.dvag/ki_api_v19/rest/v19/perso" +
                "nen/?fullname=s&personstatus=KUNDE&maxResults=10\",\"self\":\"http://10.101.0.44:8080/v1/ki/search/" +
                "\"},\"_request\":{\"queryString\":\"name=s&status=KUNDE\",\"requestUri\":\"/v1/ki/search/\",\"reque" +
                "stUrl\":\"http://10.101.0.44:8080/v1/ki/search/\",\"headers\":{\"advisor\":\"4101730\",\"accept-lan" +
                "guage\":\"en,de-DE;q=0.9,de;q=0.8,en-US;q=0.7\",\"host\":\"10.101.0.44:8080\",\"upgrade-insecure-re" +
                "quests\":\"1\",\"connection\":\"keep-alive\",\"accept-encoding\":\"gzip, deflate\",\"user-agent\":" +
                "\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73." +
                "0.3683.86 Safari/537.36\",\"accept\":\"text/html,application/xhtml+xml,application/xml;q=0.9,image" +
                "/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3\"},\"parameters\":{\"name\":\"s\",\"s" +
                "tatus\":\"KUNDE\"},\"method\":\"GET\"},\"_serverTime\":\"1553528282234\"}"
        const val JSONLIST = "[" +
            "{'a' = 1, 'b' = 'b', 'c' = 'c'}," +
            "{'a' = 2, 'bb' = 'bb', 'c' = 'cc'}," +
            "{'a' = 3, 'bbb' = 'bbb', 'c' = 'ccc'}," +
            "{'a' = 6, 'b' = 'bbbbbbb', 'c' = 'cccccc'}" +
            "]"
        val gson = GsonBuilder().setPrettyPrinting().create()
        val parser = JsonParser()

        internal inline fun <reified T> handleIt(list: Boolean = false) =
            when {
                list -> {
                    println("list ${T::class.javaClass.name}")
                    gson.fromJson(JSONLIST, kotlin.Array<T>::class.java).toList()
                }
                T::class == JsonObject::class -> {
                    println("JsonElement ${T::class.javaClass.name}")
                    parser.parse(JSON)
                }
                else -> {
                    println("else ${T::class.javaClass.name}")
                    gson.fromJson(JSON, T::class.java)
                }
            }
    }
}

