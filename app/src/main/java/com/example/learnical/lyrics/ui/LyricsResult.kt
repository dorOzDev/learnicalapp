package com.example.learnical.lyrics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import com.example.learnical.lyrics.network.LyricsResponse

@Composable
fun LyricsResult(data: LyricsResponse) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

        Text("song name:")
        Text(data.songName)

        Text(
            buildAnnotatedString {
                append("Url ")
                withLink(
                    LinkAnnotation.Url(
                        data.url,
                        TextLinkStyles(style = SpanStyle(color = Color.Blue))
                    )
                ) {
                    append("song url")
                }
            }
        )

        Text("Lyric:")
        Text(data.lyric)
    }
}