package com.remziakgoz.artbookkotlinhomework.ui.dashboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.android.material.snackbar.Snackbar
import com.remziakgoz.artbookkotlinhomework.MainActivity
import com.remziakgoz.artbookkotlinhomework.R
import com.remziakgoz.artbookkotlinhomework.adapter.ArtAdapter
import com.remziakgoz.artbookkotlinhomework.databinding.FragmentDashboardBinding
import com.remziakgoz.artbookkotlinhomework.model.Art
import com.remziakgoz.artbookkotlinhomework.roomdb.ArtDao
import com.remziakgoz.artbookkotlinhomework.roomdb.ArtDatabase
import com.remziakgoz.artbookkotlinhomework.ui.home.HomeFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private lateinit var db : ArtDatabase
    private lateinit var artDao: ArtDao
    val compositeDisposable = CompositeDisposable()
    private var artNameInput : String? = null
    private var artistNameInput : String? = null
    private var yearInput : String? = null
    var selectedBitmap : Bitmap? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var artList: ArrayList<Art>
    var art: Art? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(requireContext(),ArtDatabase::class.java, "Arts").build()

        artDao = db.artDao()


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        artList = ArrayList()


        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val info = arguments?.getString("info")
        val selectedId = arguments?.getInt("id") ?: return root

        // Coroutine içinde veri çekme
        lifecycleScope.launch {
            art = artDao.getArtById(selectedId)
            art?.let {
                if (info == "old") {
                    binding.artNameText.setText(it.artName)
                    binding.artistNameText.setText(it.artistName)
                    binding.yearText.setText(it.year)

                    val bitmap = BitmapFactory.decodeByteArray(it.imageUri, 0, it.imageUri.size)
                    binding.imageView.setImageBitmap(bitmap)
                }
                // Diğer durumlar için ek işlemler
                binding.saveButton.visibility = View.GONE
                binding.deleteButton.visibility = View.VISIBLE
                binding.imageView.isEnabled = false
                binding.artNameText.isEnabled = false
                binding.artistNameText.isEnabled = false
                binding.yearText.isEnabled = false

            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.deleteButton.visibility = View.GONE



        binding.saveButton.setOnClickListener {
            save()
        }

        binding.deleteButton.setOnClickListener {

            delete()

        }

        binding.imageView.setOnClickListener {
            selectImage()
        }

        registerLauncher()


    }


    private fun makeSmallerBitmap(image : Bitmap, maximumSize: Int) : Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1) {
            //landscape
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()

        } else {
            //portrait
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()

        }


        return Bitmap.createScaledBitmap(image, width,height,true)
    }


    fun save() {

        if (selectedBitmap != null) {
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)

            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()


            artNameInput = binding.artNameText.text.toString()
            artistNameInput = binding.artistNameText.text.toString()
            yearInput = binding.yearText.text.toString()

            if (artNameInput.isNullOrEmpty() || artistNameInput.isNullOrEmpty() || yearInput.isNullOrEmpty() || selectedBitmap == null) {
                Toast.makeText(context,"Please enter all values and select an image",Toast.LENGTH_LONG).show()
            } else {
                val art = Art(artNameInput!!,artistNameInput!!,yearInput!!,byteArray)
                compositeDisposable.add(
                    artDao.insert(art)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleSaveSuccess)
                )
            }


        }


    }
//    private fun loadArtData() {
//        compositeDisposable.add(
//            artDao.getAll()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { artListFromDb ->
//                    artList.clear()
//                    artList.addAll(artListFromDb)
//                }
//        )
//    }


    private fun handleSaveSuccess() {
//        loadArtData() // Verileri yükle
        val navController = findNavController()
        navController.navigate(R.id.action_navigation_dashboard_to_navigation_home) // Geçiş yap
    }


    fun delete() {

        art?.let {
            compositeDisposable.add(
                artDao.delete(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleSaveSuccess)
            )
        }

    }

    fun selectImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //Android 33+ -> READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
                    //rationale
                    Snackbar.make(binding.root,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                        //request
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }).show()

                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            //Android 32- -> READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    //rationale
                    Snackbar.make(binding.root,"Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                        //request
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()

                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }


    }



    private fun registerLauncher() {

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    try {
                        val inputStream = requireContext().contentResolver.openInputStream(uri)
                        selectedBitmap = BitmapFactory.decodeStream(inputStream)
                        binding.imageView.setImageBitmap(selectedBitmap)

                        if (Build.VERSION.SDK_INT >= 28) {
                            // Android 9 (API 28) ve üstü
                            val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                        } else {
                            // Android 8 (API 27) ve altı
                            selectedBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                        }
                        binding.imageView.setImageBitmap(selectedBitmap)

                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
                        }
                    }

                }

            }



        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            } else {
                //permission denied
                Toast.makeText(context,"Permission needed!",Toast.LENGTH_LONG).show()
            }
        }
    }







    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        compositeDisposable.clear()
    }
}