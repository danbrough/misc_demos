package danbroid.exoservice.content.rnz

import danbroid.exoservice.content.MediaItemBuilder
import danbroid.exoservice.content.MediaItemDSL
import danbroid.exoservice.content.URI_CONTENT_PREFIX
import danbroid.exoservice.content.URI_MEDIA_PREFIX
import danbroid.media.domain.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


const val URI_RNZ_CONTENT_PREFIX = "$URI_CONTENT_PREFIX/rnz"
const val URI_RNZ_MEDIA_PREFIX = "$URI_MEDIA_PREFIX/rnz"
const val URL_RNZ = "https://www.rnz.co.nz"
const val URL_RNZ_NEWS = "https://www.rnz.co.nz/news"
const val NEWS_IMAGE = "https://www.rnz.co.nz/brand-images/rnz-news.jpg"
const val URI_RNZ_PROGRAMME_PREFIX = "$URI_RNZ_MEDIA_PREFIX/programme"
const val URI_RNZ_PODCASTS = "$URI_RNZ_CONTENT_PREFIX/podcasts"
const val URI_RNZ_NEWS = "$URI_RNZ_MEDIA_PREFIX/newsbulletin"

/*@MediaItemDSL
fun MediaItemBuilder.rnzWebsite(block: MediaItemBuilder.() -> Unit={}) =
  menu {
    mediaID = URL_RNZ
    deepLink = URL_RNZ
    titleID = R.string.title_rnz_website
    subTitleID = R.string.subtitle_rnz_website
    imageURI = "https://www.rnz.co.nz/brand-images/rnz-national.jpg"
    block()
  }*/


@MediaItemDSL
fun MediaItemBuilder.rnzNews() = menu {
  mediaID = URI_RNZ_NEWS
  title = "RNZ News"
  subtitle = "Latest news bulletin"
  isPlayable = true
  imageURI = "https://www.rnz.co.nz/brand-images/rnz-news.jpg"
  //website = URL_RNZ_NEWS
  liveItem = flow {
    emit(RNZUtils(context).loadRNZNews())
  }.flowOn(Dispatchers.IO)
}
//menuItem(RNZNewsBuilder(context), block)

/*
@MediaItemDSL
fun MediaItemBuilder.rnzPodcasts(block: RNZPodcastsBuilder.() -> Unit = {}) =
  menuItem(RNZPodcastsBuilder(context), block)


@MediaItemDSL
fun MediaItemBuilder.rnzNational32(block: (MediaItemBuilder.() -> Unit)={}) =
  menu {
    mediaID = "$URI_RNZ_MEDIA_PREFIX/national32"
    titleID = R.string.title_national_radio
    subTitleID = R.string.subtitle_national_radio_32
    playable = true
    imageURI = "https://www.rnz.co.nz/brand-images/rnz-national.jpg"
    uri = "http://radionz-ice.streamguys.com/national"
    block()
  }


@MediaItemDSL
fun MediaItemBuilder.rnzNational64(block: (MediaItemBuilder.() -> Unit)={}) =
  menu {
    mediaID = "$URI_RNZ_MEDIA_PREFIX/national64"
    titleID = R.string.title_national_radio
    subTitleID = R.string.subtitle_national_radio_64
    playable = true
    imageURI = "https://www.rnz.co.nz/brand-images/rnz-national.jpg"
    uri = "http://radionz-ice.streamguys.com/national.mp3"
    block()
  }

@MediaItemDSL
fun MediaItemBuilder.rnzNational128(block: (MediaItemBuilder.() -> Unit)={}) =
  menu {
    mediaID = "$URI_RNZ_MEDIA_PREFIX/national128"
    titleID = R.string.title_national_radio
    subTitleID = R.string.subtitle_national_radio_128
    playable = true
    imageURI = "https://www.rnz.co.nz/brand-images/rnz-national.jpg"
    uri = "http://radionz-ice.streamguys.com/National_aac128"
    block()
  }

@MediaItemDSL
fun MediaItemBuilder.rnzInternational32(block: (MediaItemBuilder.() -> Unit)={}) =
  menu {
    mediaID = "$URI_RNZ_MEDIA_PREFIX/international32"
    titleID = R.string.title_rnz_international
    subTitleID = R.string.subtitle_rnz_international32
    playable = true
    imageURI = "https://www.rnz.co.nz/brand-images/rnz-international.jpg"
    uri = "http://radionz-ice.streamguys.com/international"
    block()
  }

@MediaItemDSL
fun MediaItemBuilder.rnzInternational64(block: (MediaItemBuilder.() -> Unit)={}) =
  menu {
    mediaID = "$URI_RNZ_MEDIA_PREFIX/international64"
    titleID = R.string.title_rnz_international
    subTitleID = R.string.subtitle_rnz_international64
    playable = true
    imageURI = "https://www.rnz.co.nz/brand-images/rnz-international.jpg"
    uri = "http://radionz-ice.streamguys.com/international.mp3"
    block()
  }



@MediaItemDSL
fun MediaItemBuilder.rnzConcert32(block: (MediaItemBuilder.() -> Unit)={}) =
  menu {
    mediaID = "$URI_RNZ_MEDIA_PREFIX/concert32"
    titleID = R.string.title_rnz_concert
    subTitleID = R.string.subtitle_rnz_concert32
    playable = true
    imageURI = "https://www.rnz.co.nz/brand-images/rnz-concert.jpg"
    uri = "http://radionz-ice.streamguys.com/concert"
    block()
  }

@MediaItemDSL
fun MediaItemBuilder.rnzConcert64(block: (MediaItemBuilder.() -> Unit)={}) =
  menu {
    mediaID = "$URI_RNZ_MEDIA_PREFIX/concert64"
    titleID = R.string.title_rnz_concert
    subTitleID = R.string.subtitle_rnz_concert64
    playable = true
    imageURI = "https://www.rnz.co.nz/brand-images/rnz-concert.jpg"
    uri = "http://radionz-ice.streamguys.com/concert.mp3"
    block()
  }

@MediaItemDSL
fun MediaItemBuilder.rnzConcert128(block: (MediaItemBuilder.() -> Unit)={}) =
  menu {
    mediaID = "$URI_RNZ_MEDIA_PREFIX/concert128"
    titleID = R.string.title_rnz_concert
    subTitleID = R.string.subtitle_rnz_concert128
    playable = true
    imageURI = "https://www.rnz.co.nz/brand-images/rnz-concert.jpg"
    uri = "http://radionz-ice.streamguys.com/Concert_aac128"
    block()
  }*/
