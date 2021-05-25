package com.example.workline

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.workline.adapters.MessageInChatAdapter
import com.example.workline.modelos.Message
import com.example.workline.modelos.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_configuration.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec


class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val dbrt = FirebaseDatabase.getInstance()
    private val mensajeriaRef = dbrt.getReference("Mensajeria")
    private val estadosRef = dbrt.getReference("Estados")
    private val listMessage = mutableListOf<Message>()
    private val adapter = MessageInChatAdapter(listMessage)
    private var myUserId: String = ""
    private var friendUserId: String = ""

    private var selectedPhotoUri: Uri? = null
    private var urlUpload: Uri? = null
    private var mStorage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        auth = Firebase.auth
        val bundle = intent.extras
        val idFriend = bundle?.getString("idFriend").toString()
        myUserId = auth.currentUser.uid
        friendUserId = idFriend

        getEstado(friendUserId)
        getUserChat(friendUserId)

       btnSendLocation.setOnClickListener {

            revisarPermisos()
        }

        btnSendPhoto.setOnClickListener {
            sendMessage()
        }

        btnSendMessage.setOnClickListener {
            val textMessage = editTextMessage.text.toString()
            val mensajoteEncriptadote = encrypt(textMessage, "662ede816988e58fb6d057d9d85605e0").toString()

            if(myUserId.toString().isNotEmpty() && friendUserId.toString().isNotEmpty() && mensajoteEncriptadote.isNotEmpty()) {
                db.collection("users").document(myUserId).get().addOnSuccessListener {
                    val emmiterName = it.get("name").toString() + " " + it.get("lastName").toString()


                    //Insertamos el mensaje en el usuario emisor
                    insertMessage(myUserId.toString(), friendUserId, mensajoteEncriptadote, myUserId.toString(), emmiterName, true, 0)
                    //Insertamos el mensaje en el usuario remitente
                    insertMessage(friendUserId, myUserId.toString(), mensajoteEncriptadote, myUserId.toString(), emmiterName, true, 0)
                }
            }
            editTextMessage.text.clear()
        }



        setEstado("Activo")

        rvChat.adapter = adapter
        getMessages(myUserId, friendUserId)
    }

    private fun insertMessage(me: String, friend: String, textMessage: String, emitterId: String, emitterName: String, encriptado: Boolean, typeMessage: Int) {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        db.collection("users").document(friend).get().addOnSuccessListener {
            val user = User(it.get("userName").toString(), it.get("email").toString(), it.get("name").toString(), it.get("lastName").toString(), it.get("carrera").toString(), it.get("image").toString())

            val message = Message(mensajeriaRef.push().key.toString(), textMessage, emitterId.toString(), currentDate, user.nombre + " " + user.lastName, emitterName, friend, user.image, typeMessage, encriptado)
            message.encripted = encriptado
            mensajeriaRef.child(me).child(friend).child(message.id).setValue(message)
            insertLastMessage(me, friend, message)
        }
    }

    private fun getMessages(userId: String ,friendId: String) {
        mensajeriaRef.child(userId).child(friendId).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listMessage.clear()
                for(snap in snapshot.children) {
                    if(snap.key.toString() != "lastMessage") {
                        val message: Message = snap.getValue(
                                Message::class.java
                        ) as Message
                        if(message.encripted){
                            var desencriptado = decryptWithAES("662ede816988e58fb6d057d9d85605e0", message.content).toString()
                            message.content = desencriptado
                        }
                        message.mine = message.emitter.equals(userId)
                        //message.mine = message.emitter == userId
                        listMessage.add(message)
                        Log.d("Success", message.toString())
                    }
                }
                if(listMessage.size > 0 && listMessage != null) {
                    adapter.notifyDataSetChanged()
                    rvChat.smoothScrollToPosition(listMessage.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        })
        adapter.notifyDataSetChanged()
    }

    private fun insertLastMessage(me: String, friend: String, lastMessage: Message) {
        mensajeriaRef.child(me).child("lastMessage").child(friend).setValue(lastMessage)
    }

    fun encrypt(strToEncrypt: String, secret_key: String): String? {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray

        try {
            keyBytes = secret_key.toByteArray(charset("UTF8"))
            val skey = SecretKeySpec(keyBytes, "AES")
            val input = strToEncrypt.toByteArray(charset("UTF8"))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(Cipher.ENCRYPT_MODE, skey)

                val cipherText = ByteArray(cipher.getOutputSize(input.size))
                var ctLength = cipher.update(
                        input, 0, input.size,
                        cipherText, 0
                )
                ctLength += cipher.doFinal(cipherText, ctLength)
                return String(
                        Base64.encode(cipherText)
                )
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

    private fun abrirMapa() {

        startActivityForResult(Intent(this,MapsActivity::class.java),1)
    }

    private fun revisarPermisos() {
        // Apartir de Android 6.0+ necesitamos pedir el permiso de ubicacion
        // directamente en tiempo de ejecucion de la app
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no tenemos permiso para la ubicacion
            // Solicitamos permiso
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        } else {
            // Ya se han concedido los permisos anteriormente
            abrirMapa()
        }
    }

    private fun getEstado(friend: String) {
        estadosRef.child(friend).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot != null) {
                    textViewUserEstado.text = "Estado: " + snapshot.value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        })
    }

    private fun getUserChat(friend: String) {
        db.collection("users").document(friend).get().addOnSuccessListener {
            if(it != null) {
                textViewUserChat.text = it.get("name").toString() + " " + it.get("lastName").toString()
            }
        }
    }

    private fun sendMessage() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            var boolDo:Boolean =  false
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED){
                //permission denied
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(permissions, 1001)
            }
            else{
                //permission already granted
                boolDo =  true

            }


            if(boolDo == true){
                pickImageFromGallery()
            }

        }

    }

    private fun pickImageFromGallery() {
        //Abrir la galería
        val intent  =  Intent()
        intent.setAction(Intent.ACTION_PICK);
        intent.type = "image/*"
        startActivityForResult(intent, 1000)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            1001 -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //PERMISO DENEGADO
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
                }
            }

            1 -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Se requiere aceptar el permiso", Toast.LENGTH_SHORT).show()
                        revisarPermisos()
                    } else {
                        Toast.makeText(this, "Permisio concedido", Toast.LENGTH_SHORT).show()
                        abrirMapa()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestcode: Int, resultcode: Int, data: Intent?) {
        super.onActivityResult(requestcode, resultcode, intent)

        if (resultcode == Activity.RESULT_OK) {

            if(requestcode == 1000) {
                selectedPhotoUri = data?.data
                saveImage()
            }

            if(requestcode == 1) {
                if(resultcode == RESULT_OK){
                    val direccionSeleccionada=data?.getStringExtra("ubicacion")?:""
                    findViewById<EditText>(R.id.editTextMessage).setText(direccionSeleccionada)

                }else{

                }
            }
        }
    }

    private fun saveImage() {

        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = mStorage.getReference("/chatImage/$filename")
        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("AddImage", "Success upload image from group: ${it.metadata?.path}")
            ref.downloadUrl.addOnSuccessListener {
                Log.d("AddImage", "File location: $it")
                urlUpload = it
                if(urlUpload != null) {
                    if(myUserId.isNotEmpty() && friendUserId.isNotEmpty()) {
                        db.collection("users").document(myUserId).get().addOnSuccessListener {
                            val emmiterName = it.get("name").toString() + " " + it.get("lastName").toString()


                            //Insertamos el mensaje en el usuario emisor
                            insertMessage(myUserId.toString(), friendUserId, urlUpload.toString(), myUserId.toString(), emmiterName, false, 1)
                            //Insertamos el mensaje en el usuario remitente
                            insertMessage(friendUserId, myUserId.toString(), urlUpload.toString(), myUserId.toString(), emmiterName, false, 1)
                        }
                    }
                }
            }
        }
    }

    private fun setEstado(estado: String) {
        estadosRef.child(auth.currentUser.uid).setValue(estado)
    }

    override fun onResume() {

        //ESTADO ACTIVO
        setEstado("Activo")

        super.onResume()
    }

    override fun onPause() {

        //ESTADO INACTIVO
        if(auth.currentUser != null) {
            setEstado("Inactivo")
        }

        super.onPause()
    }
}








