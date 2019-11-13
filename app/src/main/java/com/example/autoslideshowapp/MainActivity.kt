package com.example.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.content.ContentUris
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val PERMISSIONS_REQUEST_CODE = 100

    private var mTimer: Timer? = null

    private var mHandler = Handler() //

    val list = ArrayList<Uri>()//配列リスト・Uri内データ

    public var increment = 0
    public var max_increment = 0
    public var counter = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button1.setOnClickListener(this)
        button2.setOnClickListener(this)


        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
                button1.isEnabled = false
                button2.isEnabled = false

            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }


        button.setOnClickListener {

            // 再生・停止・切り替え

            counter += 1
            if (counter > 1) {
                counter = 0
            }

            if (counter == 0) {
                button.text = "再生"
                if (mTimer != null) {
                    mTimer!!.cancel()
                    mTimer = null
                }
                button1.isEnabled = true//真
                button2.isEnabled = true

            } else if (counter == 1) {
                button.text = "停止"
                if (mTimer == null) {
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mHandler.post {
                                showImage()
                                increment += 1
                                if (increment > max_increment) {
                                    increment = 0
                                }
                            }
                        }
                    }, 2500, 3000)//初動2500ミリ秒、ループ間隔3000ミリ秒
                }
                button1.isEnabled = false//偽
                button2.isEnabled = false
            }
            Log.d("counter", counter.toString() + "/" + button.text)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

        //private として宣言すると、その宣言を含むファイルの中でのみ見えます
     private fun getContentsInfo() { // 画像の情報を取得する
         val resolver = contentResolver
         val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)

         )

         cursor!!.moveToFirst()
            // indexからID取得し、IDから画像URI取得する
         var fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
         var id = cursor.getLong(fieldIndex)
         var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)


         do {
             fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
             id = cursor.getLong(fieldIndex)
             imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

             list.add(imageUri)

             imageView.setImageURI(imageUri)

         } while (cursor.moveToNext())

         imageView.setImageURI(list[increment])
         max_increment = list.size - 1
         Log.d("lists", max_increment.toString())

         for (i in list.indices) {
                Log.d("ANDROIDs", i.toString() + "," + list[i].toString())
         }
         Log.d("increments", increment.toString() + "/" + max_increment.toString())

          cursor.close()
     }

        //画面表示
     private fun showImage() {
        imageView.setImageURI(list[increment])
        max_increment = list.size - 1
         Log.d("lists", max_increment.toString())

     }

     override fun onClick(v: View) {
        if(v.id == R.id.button1) {
            increment += 1
            if (increment > max_increment) {
                    increment = 0
            }
                showImage()

        } else if(v.id == R.id.button2) {
                increment -= 1
                if (increment < 0) {
                    increment = max_increment
                }
                showImage()
        }
     }
}


