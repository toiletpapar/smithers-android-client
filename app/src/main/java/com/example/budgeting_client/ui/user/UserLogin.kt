package com.example.budgeting_client.ui.user

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.budgeting_client.R
import com.example.budgeting_client.models.AuthUser
import com.example.budgeting_client.models.AuthUserErrors
import com.example.budgeting_client.utils.AppErrors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserLogin(
    onLoginClick: (authUser: AuthUser) -> Unit,
    errors: AppErrors<AuthUserErrors>?
) {
    // State
    var authUser by rememberSaveable { mutableStateOf(AuthUser(username = "", password = "")) }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    // Render
    Surface() {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val modifier = Modifier.fillMaxWidth(0.8f)

            Text(
                text = "Smithers",
                style = MaterialTheme.typography.titleLarge,
            )
            if (errors?.hasOneOfError(listOf(AuthUserErrors.UNAUTHORIZED)) !== null) {
                Surface() {
                    // TODO: Error message could look better
                    errors.createErrorComposable(listOf(AuthUserErrors.UNAUTHORIZED))?.invoke()
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            TextField(
                modifier = modifier,
                value = authUser.username,
                onValueChange = { authUser = authUser.copy(username = it.trim()) },
                label = { Text(stringResource(id = R.string.username_title)) },
                singleLine = true,
                isError = errors?.hasOneOfError(listOf(AuthUserErrors.EMPTY_USERNAME)) ?: false,
                supportingText = errors?.createErrorComposable(listOf(AuthUserErrors.EMPTY_USERNAME))
            )
            Spacer(modifier = Modifier.size(16.dp))
            TextField(
                modifier = modifier,
                value = authUser.password,
                onValueChange = { authUser = authUser.copy(password = it.trim()) },
                label = { Text(stringResource(id = R.string.password_title)) },
                singleLine = true,
                visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordHidden = !passwordHidden }) {
                        val visibilityIcon = if (passwordHidden)
                            ImageVector.vectorResource(id = R.drawable.visibility)
                        else
                            ImageVector.vectorResource(id = R.drawable.visibility_off)
                        Icon(imageVector = visibilityIcon, contentDescription = null)
                    }
                },
                isError = errors?.hasOneOfError(listOf(AuthUserErrors.EMPTY_PASSWORD)) ?: false,
                supportingText = errors?.createErrorComposable(listOf(AuthUserErrors.EMPTY_PASSWORD))
            )
            Spacer(modifier = Modifier.size(16.dp))
            Button(
                modifier = modifier,
                onClick = { onLoginClick(authUser) },
                contentPadding = ButtonDefaults.TextButtonContentPadding
            ) {
                Text("Login")
            }
        }
    }
}