package com.example.caissemanager.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.caissemanager.domain.model.Caisse
import com.example.caissemanager.utils.StaticFunction


@Composable
fun CaisseItem(
    selectedCaisse: Caisse?,
    caisse: Caisse,
    color: Color,
    deviseSuffix: String
) {
    var selectedCaisse1 = selectedCaisse
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.onBackground
                ), shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                selectedCaisse1 = caisse

            }


    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(end = 12.dp, start = 4.dp)
        ) {

            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color = color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.BottomCenter

                ) {

                    Text(
                        modifier = Modifier


                            .align(Alignment.Center),
                        text = if (caisse.descriptionCaisse.isBlank()) "O" else caisse.descriptionCaisse[0].toString()
                            .toUpperCase(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        "${caisse.montant}  $deviseSuffix",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W900
                    )
                    Text(
                        caisse.compte,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,


                        )

                }
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .align(Alignment.CenterEnd),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    caisse.codeCaisse,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    StaticFunction.formatTimestampToRelativeTime(
                        caisse.date
                    ), style = MaterialTheme.typography.bodySmall
                )

            }

        }
    }
    Spacer(Modifier.height(8.dp))
}