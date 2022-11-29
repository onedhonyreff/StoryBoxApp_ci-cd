package com.fikri.submissionstoryappbpai.other_class

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.fikri.submissionstoryappbpai.R
import com.google.android.gms.maps.model.LatLng
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@ColorInt
fun Context.resolveColorAttr(@AttrRes colorAttr: Int): Int {
    val resolvedAttr =
        resolveThemeAttr(colorAttr)
    val colorRes = if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else resolvedAttr.data
    return ContextCompat.getColor(this, colorRes)
}

fun Context.resolveThemeAttr(@AttrRes attrRes: Int): TypedValue {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return typedValue
}

fun String.withDateFormat(
    type: Int = DateFormat.DEFAULT,
    pattern: String = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'"
): String {
    var result = this
    val format = SimpleDateFormat(pattern, Locale.US)
    val date = format.parse(this) as Date
    try {
        result = DateFormat.getDateInstance(type).format(date)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}

fun String.toDate(pattern: String = "yyyy-MM-dd HH:mm:ss.SSS"): Date {
    val format = SimpleDateFormat(pattern, Locale.US)
    return format.parse(this) as Date
}

fun getStringDate(dayDelta: Int = 0): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        var current = LocalDateTime.now()
        if (dayDelta != 0) {
            current = current.plusDays(dayDelta.toLong())
        }
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        current.format(formatter)
    } else {
        val current = Calendar.getInstance()
        if (dayDelta != 0) {
            current.add(Calendar.DAY_OF_YEAR, dayDelta)
        }
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        formatter.format(current.time)
    }
}

fun getDayDiff(date1: Date, date2: Date): Int {
    val diff: Long = date2.time - date1.time
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    return days.toInt()
}

fun dpToPx(context: Context, dp: Float): Float {
    return dp * context.resources.displayMetrics.density
}

private const val FILENAME_FORMAT = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun createFile(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    val outputDirectory = if (
        mediaDir != null && mediaDir.exists()
    ) mediaDir else application.filesDir

    return File(outputDirectory, "$timeStamp.jpg")
}

fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
    val matrix = Matrix()
    return if (isBackCamera) {
        matrix.postRotate(90f)
        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    } else {
        matrix.postRotate(-90f)
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f) // flip gambar
        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contextResolver: ContentResolver = context.contentResolver
    val myFile = createTempFile(context)

    val inputStream = contextResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

fun reduceFileImage(file: File, maxSize: Int = 500000): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > maxSize && compressQuality > 0)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun reverseGeocoding(geocoder: Geocoder, latLng: LatLng, default: String = ""): String {
    var addressName = default
    try {
        val list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (list != null && list.size != 0) {
            addressName = list[0].getAddressLine(0)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return addressName
}

fun CombinedLoadStates.decideOnState(
    itemCount: Int,
    showLoading: ((Boolean) -> Unit)? = null,
    adapterAnyLoadingStateLoading: ((Boolean) -> Unit)? = null,
    showEmptyState: ((Boolean) -> Unit)? = null,
    showError: ((String) -> Unit)? = null,
    showAnyError: ((String) -> Unit)? = null
) {
    showLoading?.invoke(refresh is LoadState.Loading)

    adapterAnyLoadingStateLoading?.invoke(
        refresh is LoadState.Loading ||
                prepend is LoadState.Loading ||
                append is LoadState.Loading ||
                source.refresh is LoadState.Loading ||
                source.prepend is LoadState.Loading ||
                source.append is LoadState.Loading
    )

    showEmptyState?.invoke(
        source.append.endOfPaginationReached && itemCount == 0
    )

    val errorState = refresh as? LoadState.Error

    errorState?.let { showError?.invoke(it.error.toString()) }

    val anyErrorState = source.append as? LoadState.Error
        ?: source.prepend as? LoadState.Error
        ?: source.refresh as? LoadState.Error
        ?: append as? LoadState.Error
        ?: prepend as? LoadState.Error
        ?: refresh as? LoadState.Error

    anyErrorState?.let { showAnyError?.invoke(it.error.toString()) }
}