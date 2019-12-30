package com.omega.dottech2k20.Models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MetaDataViewModel(application: Application) : AndroidViewModel(application) {


	lateinit var mFaqLiveData: MutableLiveData<List<FAQ>>
	val TAG = this.javaClass.simpleName
	val mFirestore = FirebaseFirestore.getInstance()
	val FAQ_DOC_NAME = "FAQ"
	val META_DATA_COLLECTION_NAME = "MetaData"

	fun getFAQs(): LiveData<List<FAQ>> {
		if (!::mFaqLiveData.isInitialized) {
			mFaqLiveData = MutableLiveData()
			val query = mFirestore.collection(META_DATA_COLLECTION_NAME).document(FAQ_DOC_NAME)
			query.get().addOnCompleteListener {
				if (it.isSuccessful) {
					val result = it.result
					result?.let { res ->
						val data = res.data
						if (data != null) {

							val faqs = data["faqs"] as List<HashMap<String, String>>
							val listOfFaqs = mutableListOf<FAQ>()
							for (faq in faqs) {
								val question = faq["question"]
								val answer = faq["answer"]
								if (question != null && answer != null) {
									listOfFaqs.add(FAQ(question, answer))
								}
							}
							mFaqLiveData.value = listOfFaqs
						}
					}
				}
			}
		}
		return mFaqLiveData
	}

	fun requestQuery(uid: String, query: String) {
		val queryMap = hashMapOf<String, String>()
		queryMap[uid] = query

		val firestoreQuery = mFirestore.collection("MetaData").document(FAQ_DOC_NAME)
		firestoreQuery.update(FieldPath.of("pendingFaqs"), FieldValue.arrayUnion(queryMap))
			.addOnCompleteListener {
				if (it.isSuccessful) {
					Log.i(TAG, "FAQ request successfully placed : $queryMap")
				}
			}
	}
}