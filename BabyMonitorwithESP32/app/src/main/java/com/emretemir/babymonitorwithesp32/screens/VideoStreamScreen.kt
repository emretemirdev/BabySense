package com.emretemir.babymonitorwithesp32.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emretemir.babymonitorwithesp32.R

@Composable
fun VideoStreamScreen(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .requiredWidth(width = 375.dp)
            .requiredHeight(height = 336.dp)
            .padding(horizontal = 20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(height = 160.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(weight = 0.5f)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(color = Color(0xff12283d))
                        .padding(start = 10.dp,
                            end = 10.dp,
                            top = 12.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeight(height = 125.dp)
                            .padding(horizontal = 2.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top),
                            modifier = Modifier
                                .fillMaxWidth()
                                .requiredHeight(height = 77.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .requiredSize(size = 32.dp)
                                    .clip(shape = RoundedCornerShape(5.dp))
                                    .background(color = Color.White.copy(alpha = 0.16f))
                                    .padding(top = 4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.livingroom1),
                                    contentDescription = "living-room 1",
                                    modifier = Modifier
                                        .requiredSize(size = 24.dp))
                            }
                            Text(
                                text = "Living Room",
                                color = Color.White,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(align = Alignment.CenterVertically))
                            Text(
                                text = "3 device",
                                color = Color.White.copy(alpha = 0.7f),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(align = Alignment.CenterVertically))
                        }
                        Box(
                            modifier = Modifier
                                .requiredWidth(width = 56.dp)
                                .requiredHeight(height = 32.dp)
                        ) {
                            Text(
                                text = "On",
                                color = Color.White,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium),
                                modifier = Modifier
                                    .align(alignment = Alignment.TopStart)
                                    .offset(x = 9.dp,
                                        y = 0.dp)
                                    .fillMaxHeight()
                                    .wrapContentHeight(align = Alignment.CenterVertically))
                            Box(
                                modifier = Modifier
                                    .align(alignment = Alignment.TopCenter)
                                    .offset(x = 0.dp,
                                        y = 0.dp)
                                    .fillMaxHeight()
                                    .requiredWidth(width = 56.dp)
                                    .clip(shape = RoundedCornerShape(16.dp))
                                    .border(border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                                        shape = RoundedCornerShape(16.dp)))
                            Box(
                                modifier = Modifier
                                    .align(alignment = Alignment.TopStart)
                                    .offset(x = 28.dp,
                                        y = 0.dp)
                                    .fillMaxHeight()
                                    .requiredWidth(width = 24.dp)
                                    .clip(shape = CircleShape)
                                    .background(color = Color.White))
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(weight = 0.5f)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(color = Color.White)
                        .padding(start = 10.dp,
                            end = 10.dp,
                            top = 12.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeight(height = 125.dp)
                            .padding(horizontal = 2.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top),
                            modifier = Modifier
                                .fillMaxWidth()
                                .requiredHeight(height = 77.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .requiredSize(size = 32.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .align(alignment = Alignment.TopCenter)
                                        .offset(x = 0.dp,
                                            y = 0.dp)
                                        .fillMaxHeight()
                                        .requiredWidth(width = 32.dp)
                                        .clip(shape = RoundedCornerShape(5.dp))
                                        .background(color = Color(0xff3c81b5).copy(alpha = 0.16f)))
                                Image(
                                    painter = painterResource(id = R.drawable.bath1),
                                    contentDescription = "bath 1",
                                    colorFilter = ColorFilter.tint(Color(0xff12283d)),
                                    modifier = Modifier
                                        .align(alignment = Alignment.Center)
                                        .offset(x = 0.dp,
                                            y = 0.dp)
                                        .requiredSize(size = 24.dp))
                            }
                            Text(
                                text = "Bathroom",
                                color = Color(0xff262626),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(align = Alignment.CenterVertically))
                            Text(
                                text = "3 device",
                                color = Color(0xff8d8d8d),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(align = Alignment.CenterVertically))
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .requiredHeight(height = 32.dp)
                        ) {
                            Text(
                                text = "Off",
                                color = Color(0xff8d8d8d),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium),
                                modifier = Modifier
                                    .align(alignment = Alignment.TopStart)
                                    .offset(x = 32.dp,
                                        y = 0.dp)
                                    .fillMaxHeight()
                                    .requiredWidth(width = 104.dp)
                                    .wrapContentHeight(align = Alignment.CenterVertically))
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .requiredWidth(width = 56.dp)
                                    .clip(shape = RoundedCornerShape(16.dp))
                                    .border(border = BorderStroke(1.dp, Color(0xff3c81b5).copy(alpha = 0.5f)),
                                        shape = RoundedCornerShape(16.dp)))
                            Box(
                                modifier = Modifier
                                    .align(alignment = Alignment.TopStart)
                                    .offset(x = 4.dp,
                                        y = 0.dp)
                                    .fillMaxHeight()
                                    .requiredWidth(width = 24.dp)
                                    .clip(shape = CircleShape)
                                    .background(color = Color.LightGray))
                        }
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(height = 160.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(weight = 0.5f)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(color = Color.White)
                        .padding(start = 10.dp,
                            end = 10.dp,
                            top = 12.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeight(height = 125.dp)
                            .padding(horizontal = 2.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top),
                            modifier = Modifier
                                .fillMaxWidth()
                                .requiredHeight(height = 77.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .requiredSize(size = 32.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .align(alignment = Alignment.TopCenter)
                                        .offset(x = 0.dp,
                                            y = 0.dp)
                                        .fillMaxHeight()
                                        .requiredWidth(width = 32.dp)
                                        .clip(shape = RoundedCornerShape(5.dp))
                                        .background(color = Color(0xff3c81b5).copy(alpha = 0.16f)))
                                Image(
                                    painter = painterResource(id = R.drawable.restaurant1),
                                    contentDescription = "restaurant 1",
                                    modifier = Modifier
                                        .align(alignment = Alignment.Center)
                                        .offset(x = 0.dp,
                                            y = 0.dp)
                                        .requiredSize(size = 24.dp))
                            }
                            Text(
                                text = "Dining Room",
                                color = Color(0xff262626),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(align = Alignment.CenterVertically))
                            Text(
                                text = "3 device",
                                color = Color(0xff8d8d8d),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(align = Alignment.CenterVertically))
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .requiredHeight(height = 32.dp)
                        ) {
                            Text(
                                text = "Off",
                                color = Color(0xff8d8d8d),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium),
                                modifier = Modifier
                                    .align(alignment = Alignment.TopStart)
                                    .offset(x = 32.dp,
                                        y = 0.dp)
                                    .fillMaxHeight()
                                    .requiredWidth(width = 104.dp)
                                    .wrapContentHeight(align = Alignment.CenterVertically))
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .requiredWidth(width = 56.dp)
                                    .clip(shape = RoundedCornerShape(16.dp))
                                    .border(border = BorderStroke(1.dp, Color(0xff3c81b5).copy(alpha = 0.5f)),
                                        shape = RoundedCornerShape(16.dp)))
                            Box(
                                modifier = Modifier
                                    .align(alignment = Alignment.TopStart)
                                    .offset(x = 4.dp,
                                        y = 0.dp)
                                    .fillMaxHeight()
                                    .requiredWidth(width = 24.dp)
                                    .clip(shape = CircleShape)
                                    .background(color = Color.LightGray))
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(weight = 0.5f)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(color = Color.White)
                        .padding(start = 10.dp,
                            end = 10.dp,
                            top = 12.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeight(height = 125.dp)
                            .padding(horizontal = 2.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top),
                            modifier = Modifier
                                .fillMaxWidth()
                                .requiredHeight(height = 77.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .requiredSize(size = 32.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .align(alignment = Alignment.TopCenter)
                                        .offset(x = 0.dp,
                                            y = 0.dp)
                                        .fillMaxHeight()
                                        .requiredWidth(width = 32.dp)
                                        .clip(shape = RoundedCornerShape(5.dp))
                                        .background(color = Color(0xff3c81b5).copy(alpha = 0.16f)))
                                Image(
                                    painter = painterResource(id = R.drawable.bed1),
                                    contentDescription = "bed 1",
                                    modifier = Modifier
                                        .align(alignment = Alignment.Center)
                                        .offset(x = 0.dp,
                                            y = 0.dp)
                                        .requiredSize(size = 24.dp))
                            }
                            Text(
                                text = "Bedroom",
                                color = Color(0xff262626),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(align = Alignment.CenterVertically))
                            Text(
                                text = "3 device",
                                color = Color(0xff8d8d8d),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(align = Alignment.CenterVertically))
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .requiredHeight(height = 32.dp)
                        ) {
                            Text(
                                text = "Off",
                                color = Color(0xff8d8d8d),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium),
                                modifier = Modifier
                                    .align(alignment = Alignment.TopStart)
                                    .offset(x = 32.dp,
                                        y = 0.dp)
                                    .fillMaxHeight()
                                    .requiredWidth(width = 104.dp)
                                    .wrapContentHeight(align = Alignment.CenterVertically))
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .requiredWidth(width = 56.dp)
                                    .clip(shape = RoundedCornerShape(16.dp))
                                    .border(border = BorderStroke(1.dp, Color(0xff3c81b5).copy(alpha = 0.5f)),
                                        shape = RoundedCornerShape(16.dp)))
                            Box(
                                modifier = Modifier
                                    .align(alignment = Alignment.TopStart)
                                    .offset(x = 4.dp,
                                        y = 0.dp)
                                    .fillMaxHeight()
                                    .requiredWidth(width = 24.dp)
                                    .clip(shape = CircleShape)
                                    .background(color = Color.LightGray))
                        }
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 375, heightDp = 336)
@Composable
private fun CodiaTemporaryLayerFramePreview() {
    CodiaTemporaryLayerFrame(Modifier)
}