package com.textfield.json.ottawastreetcameras.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.textfield.json.ottawastreetcameras.BuildConfig
import com.textfield.json.ottawastreetcameras.R


@Composable
fun AboutDialog(
    onLicences: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.app_name_long))
        },
        text = {
            Text(text = stringResource(R.string.version, BuildConfig.VERSION_NAME))
        },
        confirmButton = {
            Row {
                TextButton(onClick = {
                    onLicences()
                    onDismiss()
                }) {
                    Text(stringResource(R.string.licences))
                }
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.close))
                }
            }
        }
    )
}

@Preview
@Composable
private fun AboutDialogPreview() {
    AboutDialog()
}