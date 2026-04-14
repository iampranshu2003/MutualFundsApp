package com.example.mutualfundsapp.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(
    title: String,
    subtitle: String,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmptyStateIllustration(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (buttonText != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onButtonClick) {
                Text(buttonText)
            }
        }
    }
}

@Composable
private fun EmptyStateIllustration(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.08f
        val boxSize = Size(size.minDimension * 0.7f, size.minDimension * 0.5f)
        val topLeft = Offset(
            (size.width - boxSize.width) / 2f,
            (size.height - boxSize.height) / 2f
        )

        drawRoundRect(
            color = color.copy(alpha = 0.15f),
            topLeft = topLeft,
            size = boxSize,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
        )
        drawRoundRect(
            color = color.copy(alpha = 0.5f),
            topLeft = topLeft,
            size = boxSize,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
        )
        drawLine(
            color = color.copy(alpha = 0.6f),
            start = Offset(topLeft.x + boxSize.width * 0.2f, topLeft.y + boxSize.height * 0.35f),
            end = Offset(topLeft.x + boxSize.width * 0.8f, topLeft.y + boxSize.height * 0.35f),
            strokeWidth = strokeWidth * 0.6f
        )
        drawLine(
            color = color.copy(alpha = 0.4f),
            start = Offset(topLeft.x + boxSize.width * 0.2f, topLeft.y + boxSize.height * 0.6f),
            end = Offset(topLeft.x + boxSize.width * 0.7f, topLeft.y + boxSize.height * 0.6f),
            strokeWidth = strokeWidth * 0.6f
        )
    }
}
