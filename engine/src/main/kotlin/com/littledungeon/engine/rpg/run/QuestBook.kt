package com.littledungeon.engine.rpg.run

import com.littledungeon.engine.model.Speech
import com.littledungeon.engine.rpg.content.Content
import com.littledungeon.engine.rpg.items.Item
import com.littledungeon.engine.rpg.learn.Words
import com.littledungeon.engine.rpg.story.Arc
import com.littledungeon.engine.rpg.story.Quest

/**
 * One entry on the quest page. [giver] is the person whose picture it wears (null for the main story, which shows
 * [itemId], the thing the story needs). [title] is a few words for grown-ups reading along; the child hears [said].
 */
data class QuestEntry(
    val id: String,
    val title: String,
    val said: List<Speech>,
    val giver: String? = null,
    val itemId: String? = null,
    val done: Boolean = false,
)

/** One thing in the bag, or worn. Tapping it says [said]. */
data class BagEntry(val itemId: String, val name: String, val count: Int, val worn: Boolean, val said: List<Speech>)

/**
 * The quest book (issue #23): everything the hero carries, the story so far, the favors people asked for, and where to go
 * next. It is only read from the journey: opening it is not a [Command] and changes nothing, so a replay never sees it.
 * Everything in it is spoken, because the child can't read; [QuestBookLines] holds the words so the voice catalog can
 * list them all.
 */
data class QuestBook(
    val story: QuestEntry,
    val quests: List<QuestEntry>,
    /** Quests finished in this adventure. */
    val finished: List<QuestEntry>,
    val tip: List<Speech>,
    val bag: List<BagEntry>,
) {
    /** Read when the quest page opens: the story, every open quest, what was finished, and where to go next. */
    val questsSaid: List<Speech>
        get() = story.said + (if (quests.isEmpty()) Speech.of(QuestBookLines.noQuests()) else quests.flatMap { it.said }) +
            finished.flatMap { it.said } + tip

    /** Read when the bag page opens. */
    val bagSaid: List<Speech> get() = Speech.of(if (bag.isEmpty()) QuestBookLines.bagEmpty() else QuestBookLines.bagIntro())
}

/** The quest book as it stands now. */
fun Journey.questBook(): QuestBook {
    val story = QuestEntry(arc.id, arc.title, Speech.of(storySoFar().joinToString(" ")), itemId = arc.keyItemId)
    val open = Content.quests.filter { holds(it.starts) && !holds(it.done) }
    val finished = Content.quests.filter { it.id !in questsDoneBefore && holds(it.done) }
    return QuestBook(
        story,
        open.map { q -> QuestEntry(q.id, q.title, Speech.of(stepNow(q)), giver = q.giver) },
        finished.map { q -> QuestEntry(q.id, q.title, Speech.of(QuestBookLines.finished(q)), giver = q.giver, done = true) },
        Speech.of(tip()),
        bagEntries(),
    )
}

/** The ids of quests already finished when this adventure began, so the book only cheers for new ones. */
internal fun Journey.questsDone(): Set<String> = Content.quests.filter { holds(it.done) }.map { it.id }.toSet()

private fun Journey.storySoFar(): List<String> = buildList {
    add(QuestBookLines.storyIs(arc.title))
    if (arc.summary.isNotBlank()) add(arc.summary)
    arc.clues.filter { hasFlag(it.flag) }.forEach { add(it.says) }
    add(QuestBookLines.storyStep(arc, hero.has(arc.keyItemId)))
}

private fun Journey.stepNow(q: Quest): String = (q.steps.firstOrNull { !holds(it.until) } ?: q.steps.last()).says

/** The baby dragon points the way: the next place down the road the story marks, toward where the story goes next. */
private fun Journey.tip(): String {
    val goal = goal()
    if (goal.id == here) return QuestBookLines.tipHere(goal.name)
    val road = markedRoad(roadsHere()) ?: return QuestBookLines.tipMap()
    val next = kingdom.location(road.other(here))
    return QuestBookLines.tip(next.name, goal.name)
}

private fun Journey.bagEntries(): List<BagEntry> {
    val worn = hero.worn.values.mapNotNull { Content.item(it) }.map { entry(it, 1, worn = true) }
    // In the order the content lists them, so the same things are always in the same places.
    val carried = Content.items.filter { hero.has(it.id) }.map { entry(it, hero.count(it.id), worn = false) }
    return worn + carried
}

private fun entry(item: Item, count: Int, worn: Boolean) = BagEntry(item.id, item.name, count, worn, Speech.of(QuestBookLines.item(item, count, worn)))

/** Every word of the quest book. Each takes only content and small numbers, so [all] can list every sentence. */
object QuestBookLines {
    fun storyIs(title: String) = "This is the story of $title."

    fun storyStep(arc: Arc, hasKey: Boolean): String {
        val item = Content.item(arc.keyItemId)?.name ?: "key"
        return if (hasKey) {
            "We have the $item! Now we can go to ${place(arc.lairId)}."
        } else {
            "We need the $item. It is in ${place(arc.keyDungeonId)}."
        }
    }

    fun noQuests() = "Nobody has asked us for help yet. Talk to the people in the towns!"

    fun finished(q: Quest) = "Quest done! ${q.doneSays}"

    fun tip(next: String, goal: String) =
        if (next == goal) "<pet>Let's go to ${lower(goal)} next!" else "<pet>Let's go to ${lower(next)} next. It is on the way to ${lower(goal)}."

    fun tipHere(goal: String) = "<pet>We made it to ${lower(goal)}!"

    fun tipMap() = "<pet>Let's look at the map and pick a road."

    fun bagIntro() = "Here is everything in your bag. Tap a picture to hear what it is."

    fun bagEmpty() = "Your bag is empty. Find treasure, or buy things in the shops!"

    fun item(item: Item, count: Int, worn: Boolean) = buildString {
        append(item.blurb)
        if (worn) append(" You are wearing it.")
        if (count > MAX_COUNT) append(" You have lots and lots.") else if (count > 1) append(" You have ${Words.number(count)}.")
    }

    /** Every sentence the quest book can say, for the voice catalog. */
    fun all(): List<String> = buildList {
        add(noQuests()); add(bagIntro()); add(bagEmpty()); add(tipMap())
        for (arc in Content.arcs) {
            add(storyIs(arc.title))
            add(arc.summary)
            arc.clues.forEach { add(it.says) }
            add(storyStep(arc, false))
            add(storyStep(arc, true))
        }
        for (q in Content.quests) {
            q.steps.forEach { add(it.says) }
            add(finished(q))
        }
        val names = Content.locations.map { it.name }
        for (goal in names) {
            add(tipHere(goal))
            for (next in names) add(tip(next, goal))
        }
        for (item in Content.items) for (worn in listOf(false, true)) for (count in 1..MAX_COUNT + 1) add(item(item, count, worn))
    }.filter { it.isNotBlank() }

    /** The most of one thing said as a number; more is "lots and lots". */
    private const val MAX_COUNT = 99

    private fun place(id: String) = lower(Content.kingdom.location(id).name)

    /** "The Spider Caves" reads as "the Spider Caves" in the middle of a sentence. */
    private fun lower(name: String) = if (name.startsWith("The ")) "the " + name.removePrefix("The ") else name
}
