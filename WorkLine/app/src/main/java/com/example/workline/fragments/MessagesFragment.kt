package com.example.workline.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workline.ChatActivity
import com.example.workline.HomeActivity
import com.example.workline.R
import com.example.workline.adapters.MessageAdapter
import com.example.workline.adapters.MessageInChatAdapter
import com.example.workline.modelos.Message
import com.example.workline.modelos.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_messages.*
import kotlinx.android.synthetic.main.fragment_messages.view.*
import java.security.Security
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec
import org.bouncycastle.util.encoders.Base64
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import org.bouncycastle.jce.provider.BouncyCastleProvider

class MessagesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val dbrt = FirebaseDatabase.getInstance()
    private val chatsRef = dbrt.getReference("Mensajeria")
    private lateinit var rootView: View

    var messages = arrayListOf<Message>(
            Message("0", "Hola", "Edson Lugo", "19/06/2021"),
            Message("1", "Hola", "Edson Lugo", "19/06/2021"))

    private val listLastMessageChat = mutableListOf<Message>()
    private var adapter = MessageAdapter(activity, listLastMessageChat)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_messages, container, false)
        adapter = MessageAdapter(activity, listLastMessageChat)
        rootView.rvMessage.adapter = adapter

        getChats()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val act = activity
        if (act != null) {
            act.title = "Mensajes"
        }
    }

    private fun getChats() {
        auth = Firebase.auth
        chatsRef.child(auth.currentUser.uid).child("lastMessage").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listLastMessageChat.clear()
                if(snapshot.exists()) {
                    listLastMessageChat.clear()
                    for(snap in snapshot.children) {
                        val message: Message = snap.getValue(
                                Message::class.java
                        ) as Message

                        if(message.encripted){
                            var desencriptado = decryptWithAES("662ede816988e58fb6d057d9d85605e0", message.content).toString()
                            message.content = desencriptado
                        }

                        listLastMessageChat.add(message)
                        Log.d("Success", "Listo last message")
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        })
    }

    fun decryptWithAES(key: String, strToDecrypt: String?): String? {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray

        try {
            keyBytes = key.toByteArray(charset("UTF8"))
            val skey = SecretKeySpec(keyBytes, "AES")
            val input = org.bouncycastle.util.encoders.Base64
                .decode(strToDecrypt?.trim { it <= ' ' }?.toByteArray(charset("UTF8")))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(Cipher.DECRYPT_MODE, skey)

                val plainText = ByteArray(cipher.getOutputSize(input.size))
                var ptLength = cipher.update(input, 0, input.size, plainText, 0)
                ptLength += cipher.doFinal(plainText, ptLength)
                val decryptedString = String(plainText)
                return decryptedString.trim { it <= ' ' }
            }
        } catch (uee: UnsupportedEncodingException) {
            uee.printStackTrace()
        } catch (ibse: IllegalBlockSizeException) {
            ibse.printStackTrace()
        } catch (bpe: BadPaddingException) {
            bpe.printStackTrace()
        } catch (ike: InvalidKeyException) {
            ike.printStackTrace()
        } catch (nspe: NoSuchPaddingException) {
            nspe.printStackTrace()
        } catch (nsae: NoSuchAlgorithmException) {
            nsae.printStackTrace()
        } catch (e: ShortBufferException) {
            e.printStackTrace()
        }

        return null
    }
}