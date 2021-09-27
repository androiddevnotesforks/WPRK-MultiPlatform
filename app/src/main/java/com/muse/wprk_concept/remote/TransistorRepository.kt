package com.muse.wprk_concept.remote

import com.muse.wprk_concept.data.Transistor.Podcast
import com.muse.wprk_concept.utilities.Constants
import com.muse.wprk_concept.utilities.Resource
import java.lang.Exception
import javax.inject.Inject

class TransistorRepository @Inject constructor(
    private val api: TransistorApi
) {
    suspend fun getPodcasts(): Resource<Podcast> {
        val res = try {
            api.getPodcasts(API_KEY = Constants.TRANSISTOR_KEY)
        } catch (e: Exception) {
            return Resource.Error("Error")
        }
        return Resource.Success(res.collection)
    }

}
