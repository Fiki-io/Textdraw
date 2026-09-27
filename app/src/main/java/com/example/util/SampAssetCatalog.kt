package com.example.util

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class SampSpriteItem(
    val library: String,
    val textureName: String,
    val fullTag: String,
    val imageUrl: String
)

data class SampSkinItem(
    val id: Int,
    val name: String
)

data class SampVehicleItem(
    val id: Int,
    val name: String,
    val category: String
)

object SampAssetCatalog {

    private var spritesCache: List<SampSpriteItem>? = null
    private var spritePathMap: Map<String, String>? = null
    private var skinsCache: List<SampSkinItem>? = null
    private var vehiclesCache: List<SampVehicleItem>? = null

    private fun getOrLoadPathMap(context: Context): Map<String, String> {
        if (spritePathMap != null) return spritePathMap!!
        val map = mutableMapOf<String, String>()
        try {
            val jsonStr = context.assets.open("samp_sprites_map.json").bufferedReader().use { it.readText() }
            val jsonObj = JSONObject(jsonStr)
            val keys = jsonObj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                map[k.lowercase()] = jsonObj.getString(k)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        spritePathMap = map
        return map
    }

    fun getSpriteImageUrl(spriteTag: String): String {
        val clean = spriteTag.trim()
        val parts = clean.split(":")
        if (parts.size != 2) return ""
        val lib = parts[0].trim()
        val tex = parts[1].trim()

        val mappedPath = spritePathMap?.get(clean.lowercase())
        val path = mappedPath ?: run {
            val folder = if (lib.equals("LD_LOADSCS", ignoreCase = true)) "LOADSCS" else lib
            "$folder/$tex.png"
        }
        return "https://raw.githubusercontent.com/openmultiplayer/wiki/master/static/images/sprites/$path"
    }

    fun get3DModelImageUrl(modelId: Int): String {
        return when {
            modelId in 400..611 -> {
                "https://raw.githubusercontent.com/openmultiplayer/wiki/master/static/images/vehiclePictures/Vehicle_$modelId.jpg"
            }
            modelId in 0..311 -> {
                "https://raw.githubusercontent.com/openmultiplayer/wiki/master/static/images/skins/$modelId.png"
            }
            else -> {
                "https://raw.githubusercontent.com/openmultiplayer/wiki/master/static/images/skins/0.png"
            }
        }
    }

    fun getSprites(context: Context): List<SampSpriteItem> {
        if (spritesCache != null) return spritesCache!!
        val pathMap = getOrLoadPathMap(context)
        val list = mutableListOf<SampSpriteItem>()
        try {
            val jsonStr = context.assets.open("samp_sprites.json").bufferedReader().use { it.readText() }
            val jsonObj = JSONObject(jsonStr)
            val keys = jsonObj.keys()
            while (keys.hasNext()) {
                val lib = keys.next()
                val arr = jsonObj.getJSONArray(lib)
                for (i in 0 until arr.length()) {
                    val full = arr.getString(i)
                    val tex = full.substringAfter(":")
                    val path = pathMap[full.lowercase()] ?: run {
                        val folder = if (lib.equals("LD_LOADSCS", ignoreCase = true)) "LOADSCS" else lib
                        "$folder/$tex.png"
                    }
                    val url = "https://raw.githubusercontent.com/openmultiplayer/wiki/master/static/images/sprites/$path"
                    list.add(
                        SampSpriteItem(
                            library = lib,
                            textureName = tex,
                            fullTag = full,
                            imageUrl = url
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        spritesCache = list
        return list
    }

    fun getSkins(context: Context): List<SampSkinItem> {
        if (skinsCache != null) return skinsCache!!
        val list = mutableListOf<SampSkinItem>()
        try {
            val jsonStr = context.assets.open("samp_skins.json").bufferedReader().use { it.readText() }
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(SampSkinItem(id = obj.getInt("id"), name = obj.getString("name")))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        skinsCache = list
        return list
    }

    fun getVehicles(context: Context): List<SampVehicleItem> {
        if (vehiclesCache != null) return vehiclesCache!!
        val list = mutableListOf<SampVehicleItem>()
        try {
            val jsonStr = context.assets.open("samp_vehicles.json").bufferedReader().use { it.readText() }
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    SampVehicleItem(
                        id = obj.getInt("id"),
                        name = obj.getString("name"),
                        category = obj.optString("category", "General")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        vehiclesCache = list
        return list
    }
}
