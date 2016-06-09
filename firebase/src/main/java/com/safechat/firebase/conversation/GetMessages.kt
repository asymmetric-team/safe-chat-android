package com.safechat.firebase.conversation

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.safechat.conversation.Message
import rx.Observable

fun getPreviousMessagesWithUid(otherUid: String): Observable<List<Message>> {
    return Observable.create { subscriber ->
        subscriber.onStart()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseDatabase
                .getInstance()
                .reference
                .child("conversations")
                .child(min(uid, otherUid))
                .child(max(uid, otherUid))
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                    }

                    override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                    }

                    override fun onChildAdded(data: DataSnapshot?, p1: String?) {
                        if (data == null) {
                            subscriber.onNext(emptyList())
                            subscriber.onCompleted()
                        } else {
                            val value = data.getValue(object : GenericTypeIndicator<GetMessage>() {})
                            if (value == null) {
                                subscriber.onNext(emptyList())
                            } else {
                                subscriber.onNext(listOf(Message(value.message!!, value.sender == uid)))
                            }
                        }
                    }

                    override fun onChildRemoved(p0: DataSnapshot?) {
                    }

                    override fun onCancelled(error: DatabaseError) {
                        subscriber.onError(error.toException())
                    }
                })
    }
}

private class GetMessage {
    var message: String? = null
    var sender: String? = null
}