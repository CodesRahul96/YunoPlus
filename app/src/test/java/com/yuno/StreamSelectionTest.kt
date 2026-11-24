package com.yuno

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.schabi.newpipe.extractor.services.youtube.ItagItem
import org.schabi.newpipe.extractor.services.youtube.dashmanifestcreators.YoutubeProgressiveDashManifestCreator

class StreamSelectionTest {
    @Test
    fun test4kItagsExist() {
        val itag4k = ItagItem.getItag(313)
        assertNotNull(itag4k)
        assertEquals("2160p", itag4k!!.resolutionString)

        val itag1440 = ItagItem.getItag(271)
        assertNotNull(itag1440)
        assertEquals("1440p", itag1440!!.resolutionString)

        val itag4k60 = ItagItem.getItag(315)
        assertNotNull(itag4k60)
        assertEquals("2160p60", itag4k60!!.resolutionString)
    }

    @Test
    fun testDashManifestCreationFor4k() {
        val itag4k = ItagItem.getItag(313)!!
        itag4k.codec = "vp9"
        itag4k.bitrate = 15_000_000
        itag4k.width = 3840
        itag4k.height = 2160
        val xml = YoutubeProgressiveDashManifestCreator.fromProgressiveStreamingUrl(
            "https://rr1---sn-example.googlevideo.com/videoplayback?clen=123456789&dur=300.000",
            itag4k,
            300,
        )
        assertNotNull(xml)
        assertTrue(xml.contains("2160") || xml.contains("vp9"))
    }
}
