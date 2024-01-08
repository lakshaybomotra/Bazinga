package com.lbdev.bazinga

import android.app.ActivityOptions
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lbdev.bazinga.ActorActivity.Companion.dpToPx
import com.lbdev.bazinga.adaptors.ActorMoviesAdaptor
import com.lbdev.bazinga.adaptors.RecentActorsAdaptor
import com.lbdev.bazinga.databinding.ActivityMainScreenBinding
import com.lbdev.bazinga.db.RecentBazingasDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

class MainScreen : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainScreenBinding
    lateinit var recentActorsAdaptor: RecentActorsAdaptor
    lateinit var rvRecent: RecyclerView
    val context: Context = this
    private lateinit var myDB: RecentBazingasDatabase
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in MainScreen.REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(
                    baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        val nameOfPhoto = UUID.randomUUID().toString()
        val isSavedSuccessfully =
            it?.let { it1 -> savePhotoToInternalStorage(nameOfPhoto, it1) }
        if (isSavedSuccessfully == true) {
            val activity = Intent(this, DetectActivity::class.java)
            activity.putExtra("bitmap", it)
            activity.putExtra("name", nameOfPhoto)
            startActivity(activity)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivityMainScreenBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        rvRecent = viewBinding.recentRecyclerView
        val params = rvRecent.layoutParams
        params.apply {
            width = context.resources.displayMetrics.widthPixels
            height = context.resources.displayMetrics.heightPixels
        }
        rvRecent.layoutParams = params
        rvRecent.layoutManager = GridLayoutManager(this, 2)
        rvRecent.addItemDecoration(SpacingItemDecoration(2, dpToPx(this, 8), true))
        rvRecent.setHasFixedSize(true)

        myDB = RecentBazingasDatabase.getDatabase(this)
        getRecentBazingas()
        if (!allPermissionsGranted()) {
            requestPermissions()
        }

        when (intent.getStringExtra("functionType")) {
            "function1" -> {
                if (allPermissionsGranted()) {
                    showCameraDialog()
                } else {
                    showPermissionDialog()
                }
            }
        }

        val cameraOpen = findViewById<View>(R.id.cameraOpen)

        cameraOpen.setOnClickListener {
            if (allPermissionsGranted()) {
                showCameraDialog()
            } else {
                showPermissionDialog()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getRecentBazingas()
    }

    private fun getRecentBazingas() {
        GlobalScope.launch {
            val bazingas = myDB.recentDao().getAllRecentBazingas()

            withContext(Dispatchers.Main) {
                if (bazingas.isNotEmpty()) {
                    viewBinding.recentShimmer.visibility = View.GONE
                    viewBinding.noSearch.visibility = View.GONE
                    viewBinding.recentRecyclerView.visibility = View.VISIBLE
                    recentActorsAdaptor = RecentActorsAdaptor(baseContext, bazingas)
                    rvRecent.adapter = recentActorsAdaptor
                    recentActorsAdaptor.setOnClickListener(
                        object : RecentActorsAdaptor.OnClickListener {
                            override fun onClick(position: Int, photoUrl: String ,id: Int) {
                                onItemGridClicked(photoUrl, id)
                            }
                        }
                    )
                } else {
                    viewBinding.recentShimmer.visibility = View.GONE
                    viewBinding.noSearch.visibility = View.VISIBLE
                    viewBinding.recentRecyclerView.visibility = View.GONE
                }
            }
        }
    }

    private fun onItemGridClicked(photoUrl: String, id: Int) {
        val intent = Intent(this, ActorActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("photoUrl", photoUrl)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun showCameraDialog() {
        val dialog = Dialog(this)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.setTitle("Choose option")
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setContentView(R.layout.image_dialog)
        dialog.setCanceledOnTouchOutside(false)
        val openGallery = dialog.findViewById<ImageView>(R.id.openGallery)
        val openCamera = dialog.findViewById<ImageView>(R.id.openCamera)

        openGallery.setOnClickListener {
            selectPhotoFromGallery.launch("image/*")
            dialog.dismiss()
        }

        openCamera.setOnClickListener {
            if (allPermissionsGranted()) {
                takePhoto.launch()
                dialog.dismiss()
            } else {
                showPermissionDialog()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    val selectPhotoFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) {

        if (it != null) {
            val photoUri = it
            try {
                val source: ImageDecoder.Source =
                    ImageDecoder.createSource(this.contentResolver, photoUri)
                val bitmap = ImageDecoder.decodeBitmap(source)

                val nameOfPhoto = UUID.randomUUID().toString()
                val isSavedSuccessfully =
                    bitmap.let { it1 -> savePhotoToInternalStorage(nameOfPhoto, it1) }
                if (isSavedSuccessfully) {
                    val activity = Intent(this, DetectActivity::class.java)
                    activity.putExtra("name", nameOfPhoto)
                    startActivity(activity)
                } else {
                    Toast.makeText(this, "not saved", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission required")
        builder.setMessage("Some permissions are needed to be allowed to use this app without any problems.")
        builder.setPositiveButton("Grant") { dialog, which ->
            dialog.cancel()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        builder.show().apply {
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.white))
            getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.white))
        }
    }

    private fun savePhotoToInternalStorage(filename: String, bmp: Bitmap): Boolean {
        return try {
            openFileOutput("$filename.png", MODE_PRIVATE).use { stream ->
                if (!bmp.compress(Bitmap.CompressFormat.PNG, 95, stream)) {
                    throw IOException("Couldn't save bitmap.")
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }


    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                android.Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}