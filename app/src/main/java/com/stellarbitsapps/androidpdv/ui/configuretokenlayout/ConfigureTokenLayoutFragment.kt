package com.stellarbitsapps.androidpdv.ui.configuretokenlayout

import android.Manifest
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stellarbitsapps.androidpdv.R

// Storage Permissions
const val REQUEST_EXTERNAL_STORAGE = 1
val PERMISSIONS_STORAGE = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

class ConfigureTokenLayoutFragment : Fragment() {

    companion object {
        fun newInstance() = ConfigureTokenLayoutFragment()
    }

    private lateinit var viewModel: ConfigureTokenLayoutViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_configure_token_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConfigureTokenLayoutViewModel::class.java)
        // TODO: Use the ViewModel
    }

    // TODO Get and verify permissions
//    val path = Environment.getExternalStorageDirectory().absolutePath + "/PDV/img_small.jpg"
//    val file = File(path)
//
//    // Check if we have write permission
//    val permission = ActivityCompat.checkSelfPermission(
//        requireActivity(),
//        Manifest.permission.WRITE_EXTERNAL_STORAGE
//    )
//
//    if (permission != PackageManager.PERMISSION_GRANTED) {
//        // We don't have permission so prompt the user
//        ActivityCompat.requestPermissions(
//            requireActivity(),
//            PERMISSIONS_STORAGE,
//            REQUEST_EXTERNAL_STORAGE
//        )
//    } else {
//        if (file.exists()) {
//            val b = BitmapFactory.decodeStream(FileInputStream(path))
//            printHelper.printBitmap(b, 2, 80)
//        }
//    }

}