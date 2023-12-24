/*
 * Copyright (c) 2022 Kodeco Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 * 
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.yourcompany.android.jetreddit.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.yourcompany.android.jetreddit.R
import com.yourcompany.android.jetreddit.components.ImagePost
import com.yourcompany.android.jetreddit.components.JoinedToast
import com.yourcompany.android.jetreddit.components.TextPost
import com.yourcompany.android.jetreddit.domain.model.PostModel
import com.yourcompany.android.jetreddit.domain.model.PostType
import com.yourcompany.android.jetreddit.viewmodel.MainViewModel
import com.yourcompany.android.jetreddit.views.TrendingTopicView
import java.util.*
import kotlin.concurrent.schedule

@Composable
fun HomeScreen(viewModel: MainViewModel) {
  val posts: List<PostModel> by viewModel.allPosts.observeAsState(listOf())

  val homeScreenItems = mapHomeScreenItem(posts)

  var isToastVisible by remember { mutableStateOf(false) }

  val onJoinClickAction: (Boolean) -> Unit = { joined ->
    isToastVisible = joined
    if (isToastVisible) {
      Timer().schedule(3000) {
        isToastVisible = false
      }
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(modifier = Modifier.background(color = MaterialTheme.colors.secondary)) {
      items(
        items = homeScreenItems
      ) {item ->

        if(item.type == HomeScreenItemType.TRENDING) {
          TrendingTopics(
            trendingTopics = trendingItems,
            modifier = Modifier.padding(
              top = 16.dp,
              bottom = 6.dp
            )
          )
        } else if (item.postModel != null) {
          val post = item.postModel
          if (post.type == PostType.TEXT) {
            TextPost(post, onJoinButtonClick = onJoinClickAction)
          } else {
            ImagePost(post, onJoinButtonClick = onJoinClickAction)
          }
          Spacer(modifier = Modifier.height(6.dp))
        }


      }
    }

    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 16.dp)
    ) {
      JoinedToast(visible = isToastVisible)
    }
  }
}

private data class HomeScreenItem(
  val type: HomeScreenItemType,
  val postModel: PostModel? = null
)

private enum class HomeScreenItemType {
  TRENDING,
  POST
}

private data class TrendingTopicModel(
  val text: String,
  @DrawableRes val imageRes: Int = 0
)

@Composable
private fun TrendingTopic(model: TrendingTopicModel) {
  AndroidView({ context ->
      TrendingTopicView(context).apply{
        text = model.text
        image = model.imageRes
      }
  })
}

@Composable
private fun TrendingTopics(
  trendingTopics: List<TrendingTopicModel>,
  modifier: Modifier = Modifier
) {
  Card(
    shape = MaterialTheme.shapes.large,
    modifier = modifier
  ) {
    Column(Modifier.padding(vertical = 8.dp)) {
      Row(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          modifier = Modifier.size(18.dp),
          imageVector = Icons.Filled.Star,
          tint = Color.Blue,
          contentDescription = "Star icon"
        )
        Spacer(Modifier.width(4.dp))
        Text(
          text = "Trending today",
          fontWeight = FontWeight.Bold,
          color = Color.Black
        )
      }
      Spacer(Modifier.height(8.dp))
      LazyRow(
        contentPadding = PaddingValues(
          start = 16.dp,
          top = 8.dp,
          end = 16.dp
        )
      ){
        itemsIndexed(
          items = trendingTopics,
          itemContent = { index, trendingModel ->
            TrendingTopic(trendingModel)
            if(index != trendingTopics.lastIndex){
              Spacer(Modifier.width(8.dp))
            }
          }
        )
      }
    }
  }
}

private fun mapHomeScreenItem(
  posts: List<PostModel>
): List<HomeScreenItem> {
  val homeScreenItems = mutableListOf<HomeScreenItem>()
  homeScreenItems.add(HomeScreenItem(HomeScreenItemType.TRENDING))
  posts.forEach { post ->
    homeScreenItems.add(HomeScreenItem(HomeScreenItemType.POST, post))
  }
  return homeScreenItems
}

private val trendingItems = listOf(
  TrendingTopicModel(
    "Compose Tutorial",
    R.drawable.jetpack_composer
  ),
  TrendingTopicModel(
    "Compose Animations",
    R.drawable.jetpack_compose_animations
  ),
  TrendingTopicModel(
    "Compose Migration",
    R.drawable.compose_migration_crop
  ),
  TrendingTopicModel(
    "DataStore Tutorial",
    R.drawable.data_storage
  ),
  TrendingTopicModel(
    "Android Animations",
    R.drawable.android_animations
  ),
  TrendingTopicModel(
    "Deep Links in Android",
    R.drawable.deeplinking
  )
)

@Preview
@Composable
private fun TrendingTopicPreview(){
  TrendingTopic(
    TrendingTopicModel(
    "Compose Animations",
      R.drawable.jetpack_compose_animations
    )
  )
}

@Preview
@Composable
private fun TrendingTopicsPreview() {
  TrendingTopics(trendingTopics = trendingItems)
}