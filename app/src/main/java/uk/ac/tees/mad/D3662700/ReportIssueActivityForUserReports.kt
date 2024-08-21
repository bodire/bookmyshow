package uk.ac.tees.mad.D3662700

import android.os.Bundle



import android.content.Intent
import android.net.Uri

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ReportIssueActivityForUserReports : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var problem by remember { mutableStateOf("") }

                var imageUri by remember { mutableStateOf<Uri?>(null) }
                var showDialog by remember { mutableStateOf(false) }
                val context = LocalContext.current

                val galleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    imageUri = uri
                }

                val cameraLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicture()
                ) { success ->
                    if (success) {
                        // The image was saved to the Uri we provided
                    }
                }

                Scaffold(
                    topBar = {
                        SmallTopAppBar(
                            title = { Text("Report Issue") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                                }
                            }
                        )
                    },
                    bottomBar = {
                        // Add your bottom navigation bar here
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = problem,
                            onValueChange = { problem = it },
                            label = { Text("Explain the problem") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))



                        Row {
                            Button(
                                onClick = {
                                    galleryLauncher.launch("image/*")
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Filled.Image, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Choose from Gallery")
                            }

                            Spacer(modifier = Modifier.width(16.dp))


                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (problem.isNotBlank()) {

                                    uploadIssue(problem, imageUri) {
                                        showDialog = true
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Send, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send Report")
                        }
                    }
                }


                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { },
                        title = { Text("Report Submitted") },
                        text = { Text("Your issue will be resolved shortly.") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDialog = false
                                    // Navigate back to Dashboard
                                    val intent = Intent(context, Dashboard::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                            ) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }
    }

    private fun uploadIssue(problem: String, imageUri: Uri?, onComplete: () -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val issueRef = storageRef.child("issues/${user.uid}/${UUID.randomUUID()}")

            val issueMap = hashMapOf(
                "problem" to problem,
                "timestamp" to System.currentTimeMillis(),
                "userId" to user.uid
            )

            if (imageUri != null) {
                issueRef.putFile(imageUri).addOnSuccessListener {
                    issueRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        issueMap["imageUrl"] = downloadUri.toString()
                        FirebaseFirestore.getInstance().collection("issues").add(issueMap)
                            .addOnSuccessListener {
                                onComplete()
                            }
                    }
                }
            } else {
                FirebaseFirestore.getInstance().collection("issues").add(issueMap)
                    .addOnSuccessListener {
                        onComplete()
                    }
            }
        }
    }
}


class ComposeFileProvider : FileProvider(
    R.xml.file_paths
) {
    companion object {
        fun getImageUri(context: android.content.Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory
            )
            val authority = context.packageName + ".fileprovider"
            return getUriForFile(
                context,
                authority,
                file
            )
        }
    }
}