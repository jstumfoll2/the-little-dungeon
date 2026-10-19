package com.littledungeon.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.littledungeon.R
import com.littledungeon.engine.model.Speech
import com.littledungeon.engine.model.Voice
import com.littledungeon.engine.rpg.run.BagEntry
import com.littledungeon.engine.rpg.run.QuestBook
import com.littledungeon.engine.rpg.run.QuestEntry
import com.littledungeon.ui.art.Art
import com.littledungeon.ui.art.Character
import com.littledungeon.ui.art.Mood
import com.littledungeon.ui.art.Picto
import com.littledungeon.ui.art.PictoIcon
import com.littledungeon.ui.art.Rigs
import kotlinx.coroutines.launch

/** Which page of the quest book is open. */
private enum class BookPage { QUESTS, BAG }

/**
 * The quest book (issue #23), opened from the corner at a choice or on the map: the story so far, the favors people asked
 * for and where to go next on one page, and everything in the bag on the other. The child can't read, so a page is read
 * out when it opens and every picture says what it is when tapped; the words under the pictures are for grown-ups reading
 * along. Looking in the book changes nothing in the story.
 */
@Composable
fun QuestBookScreen(book: QuestBook, close: () -> Unit) {
    val narrator = LocalNarrator.current
    val sfx = LocalSfx.current
    val scope = rememberCoroutineScope()
    var page by remember { mutableStateOf(BookPage.QUESTS) }
    fun pageSaid() = if (page == BookPage.QUESTS) book.questsSaid else book.bagSaid
    LaunchedEffect(page) { narrator.speak(pageSaid()) }
    val say: (List<Speech>) -> Unit = { lines ->
        sfx.play("tap")
        scope.launch { narrator.speakAloud(lines) }
    }
    BoxWithConstraints(Modifier.fillMaxSize().background(Color(0x99000000)).clickable(NoRipple, null) {}) {
        val h = maxHeight
        Row(
            Modifier.align(Alignment.Center).fillMaxWidth(0.8f).fillMaxHeight(0.9f)
                .shadow(8.dp, RoundedCornerShape(24.dp)).background(Palette.Paper, RoundedCornerShape(24.dp))
                .border(4.dp, Palette.PaperEdge, RoundedCornerShape(24.dp)).padding(12.dp),
        ) {
            // The tabs: the storybook for the quests, the treasure for the bag.
            Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Tab(R.drawable.art_book, page == BookPage.QUESTS, h * 0.2f) { page = BookPage.QUESTS }
                Tab(R.drawable.art_treasure, page == BookPage.BAG, h * 0.2f) { page = BookPage.BAG }
            }
            Box(Modifier.weight(1f).fillMaxHeight().padding(start = 12.dp)) {
                when (page) {
                    BookPage.QUESTS -> QuestPage(book, h, say)
                    BookPage.BAG -> BagPage(book.bag, h, say)
                }
            }
        }
        RoundButton(Picto.LISTEN, Palette.Sky, h * 0.14f, Modifier.align(Alignment.TopStart).padding(10.dp)) {
            scope.launch { narrator.speakAloud(pageSaid()) }
        }
        RoundButton(Picto.CHECK, Palette.Go, h * 0.16f, Modifier.align(Alignment.BottomEnd).padding(10.dp)) {
            narrator.stop()
            close()
        }
    }
}

@Composable
private fun Tab(picture: Int, open: Boolean, size: Dp, onClick: () -> Unit) {
    Box(
        Modifier.size(size).clip(RoundedCornerShape(20.dp))
            .background(if (open) Palette.Gold.copy(alpha = 0.5f) else Color(0x22C9A46A))
            .border(if (open) 4.dp else 0.dp, Palette.PaperEdge, RoundedCornerShape(20.dp))
            .clickable(NoRipple, null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(painterResource(picture), null, Modifier.size(size * 0.8f).alpha(if (open) 1f else 0.6f))
    }
}

/** The story first, then each favor still open, what was finished in this adventure, and the baby dragon's next stop. */
@Composable
private fun QuestPage(book: QuestBook, h: Dp, say: (List<Speech>) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        QuestRow(book.story, h, say)
        book.quests.forEach { QuestRow(it, h, say) }
        book.finished.forEach { QuestRow(it, h, say) }
        Line(h, Voice.caption(book.tip), null, { say(book.tip) }) {
            Character(Rigs.babyDragon, Mood.HAPPY, Modifier.fillMaxSize(), animate = false)
        }
    }
}

@Composable
private fun QuestRow(entry: QuestEntry, h: Dp, say: (List<Speech>) -> Unit) {
    Line(h, Voice.caption(entry.said), entry.title, { say(entry.said) }, done = entry.done) {
        val giver = entry.giver
        val item = entry.itemId
        when {
            giver != null -> Character(Rigs.byArt("npc_$giver"), Mood.CALM, Modifier.fillMaxSize(), animate = false)
            item != null -> Image(painterResource(Art.item(item)), null, Modifier.fillMaxSize())
        }
    }
}

/** One line of the quest page: a picture, then a title and the words for grown-ups. Tapping anywhere on it reads it out. */
@Composable
private fun Line(h: Dp, words: String, title: String?, onClick: () -> Unit, done: Boolean = false, picture: @Composable () -> Unit) {
    val fontSize = with(LocalDensity.current) { (h * 0.04f).toSp() }
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0x22C9A46A))
            .clickable(NoRipple, null, onClick = onClick).padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(Modifier.size(h * 0.16f)) {
            picture()
            if (done) PictoIcon(Picto.CHECK, Palette.Right, Modifier.align(Alignment.BottomEnd).size(h * 0.07f))
        }
        Column(Modifier.weight(1f)) {
            if (title != null) Text(title, color = Palette.Ink, fontWeight = FontWeight.Black, fontSize = fontSize, maxLines = 1)
            Text(words, color = Palette.Ink, fontSize = fontSize * 0.85f, maxLines = 3, lineHeight = fontSize)
        }
    }
}

/** Everything carried, worn things first with a gold edge. Each picture says what it is, and how many there are. */
@Composable
private fun BagPage(bag: List<BagEntry>, h: Dp, say: (List<Speech>) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val perRow = 5
        val tile = minOf(h * 0.26f, maxWidth / (perRow * 1.1f))
        val fontSize = with(LocalDensity.current) { (tile * 0.12f).toSp() }
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(tile * 0.1f)) {
            bag.chunked(perRow).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(tile * 0.1f)) {
                    row.forEach { entry ->
                        Column(
                            Modifier.size(tile, tile * 1.1f).clip(RoundedCornerShape(16.dp)).background(Color(0x22C9A46A))
                                .border(if (entry.worn) 4.dp else 0.dp, Palette.Gold, RoundedCornerShape(16.dp))
                                .clickable(NoRipple, null) { say(entry.said) },
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(Modifier.size(tile * 0.72f)) {
                                Image(painterResource(Art.item(entry.itemId)), null, Modifier.fillMaxSize())
                                if (entry.count > 1) {
                                    Text(
                                        "x${entry.count}", color = Color.White, fontWeight = FontWeight.Black, fontSize = fontSize * 1.3f,
                                        modifier = Modifier.align(Alignment.BottomEnd).background(Color(0xCC2A1C10), RoundedCornerShape(50)).padding(horizontal = 6.dp),
                                    )
                                }
                            }
                            Text(entry.name, color = Palette.Ink, fontWeight = FontWeight.Bold, fontSize = fontSize, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}
