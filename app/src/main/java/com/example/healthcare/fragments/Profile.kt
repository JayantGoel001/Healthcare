package com.example.healthcare.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.healthcare.R
import de.hdodenhof.circleimageview.CircleImageView

class Profile : Fragment() {
    private var  ProfileImage: CircleImageView?=null
    private val PICK_IMAGE=1234
    private var imageUri: Uri?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ProfileImage=view.findViewById(R.id.Profile_Image)
        if(ProfileImage!=null && imageUri!=null)
        {
            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver , imageUri)
            ProfileImage!!.setImageBitmap(bitmap)
        }
        ProfileImage!!.setOnClickListener{
            val gallery: Intent = Intent()
            gallery.type = "image/*"
            gallery.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data!!

            try {

                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver , imageUri)
                ProfileImage!!.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}

