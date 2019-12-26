package com.omega.dottech2k20.Models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

class SponsorsViewModel(application: Application) : AndroidViewModel(application) {

	lateinit var mSponsorsLiveData: MutableLiveData<List<Sponsor>>
	val mFirestore = FirebaseFirestore.getInstance()
	val TAG = "SponsorsViewModel"

	fun getSponsorsData(): LiveData<List<Sponsor>> {
		if (!::mSponsorsLiveData.isInitialized) {
			mSponsorsLiveData = MutableLiveData()

			val query = mFirestore.collection("Sponsors")

			query.get().addOnCompleteListener {
				if (it.isSuccessful) {
					val result = it.result
					if (result != null) {
						mSponsorsLiveData.value = result.toObjects(Sponsor::class.java)
						Log.d(TAG, "Fetching sponsors data successfull")
					}
				} else {
					Log.d(TAG, "Failed to fetch sponsors " + it.exception)
				}
			}
		}
		return mSponsorsLiveData
	}

}