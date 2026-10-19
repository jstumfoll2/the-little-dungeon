package com.littledungeon.engine.rpg

import com.littledungeon.engine.model.Voice
import com.littledungeon.engine.rpg.content.Content
import com.littledungeon.engine.rpg.hero.Hero
import com.littledungeon.engine.rpg.items.Slot
import com.littledungeon.engine.rpg.learn.SkillBook
import com.littledungeon.engine.rpg.run.Beat
import com.littledungeon.engine.rpg.run.Journey
import com.littledungeon.engine.rpg.run.Reply
import com.littledungeon.engine.rpg.run.questBook
import com.littledungeon.engine.rpg.story.Cond
import com.littledungeon.engine.rpg.story.Effect
import com.littledungeon.engine.rpg.world.WorldMemory
import com.littledungeon.engine.util.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The quest book (issue #23): the story so far, open quests, where to go next, and the bag, all said out loud. */
class QuestBookTest {
    private fun journey(hero: Hero = Hero(), world: WorldMemory = WorldMemory(), story: String = "missing_pages") =
        Journey(3, hero, SkillBook(), world, Clock { 0L }, storyId = story)

    private fun text(j: Journey) = Voice.caption(j.questBook().questsSaid)

    @Test
    fun `a new adventure tells the story, says nobody has asked for help yet, and points the way`() {
        val j = journey()
        val said = text(j)
        assertTrue("This is the story of The Missing Pages." in said, said)
        assertTrue("We need the Silver Quill. It is in Inkwell Cellars." in said, said)
        assertTrue("Nobody has asked us for help yet." in said, said)
        assertTrue("Let's go to" in said, said)
        assertEquals("silver_quill", j.questBook().story.itemId)
    }

    @Test
    fun `a quest moves on as the world changes, and is cheered for once it is done`() {
        val j = journey()
        j.setFlag("quest_recipe")
        assertEquals(listOf("bun_recipe"), j.questBook().quests.map { it.id })
        assertTrue("A sneaky fox took it" in text(j), text(j))
        j.setFlag("met:rascal_fox")
        assertTrue("Rascal the Fox has it" in text(j), text(j))
        j.setFlag("recipe_got")
        assertTrue("Bring it back to Baker Bun" in text(j), text(j))
        j.setFlag("bun_recipe_returned")
        assertTrue("Maybe the mayor of Mossbrook" in text(j), text(j))
        j.setFlag("met:mayor_tilly")
        assertTrue("Tell Mayor Tilly" in text(j), text(j))
        j.setFlag("tilly_thanked")
        val book = j.questBook()
        assertTrue(book.quests.isEmpty())
        assertEquals(listOf("bun_recipe"), book.finished.map { it.id })
        assertTrue("Quest done! Baker Bun has her recipe back" in text(j), text(j))
    }

    @Test
    fun `a quest finished in an earlier adventure is not cheered for again`() {
        val j = journey(world = WorldMemory(flags = setOf("quest_recipe", "recipe_got", "bun_recipe_returned", "tilly_thanked")))
        assertTrue(j.questBook().finished.isEmpty())
        assertTrue(j.questBook().quests.isEmpty())
    }

    @Test
    fun `the story moves on once the key is found, and clues found along the way are told`() {
        val j = journey()
        j.hero = j.hero.give("silver_quill")
        j.setFlag("knows_baron_lonely")
        val said = text(j)
        assertTrue("We have the Silver Quill! Now we can go to Grumblewick Manor." in said, said)
        assertTrue("the Baron is lonely" in said, said)
    }

    @Test
    fun `the bag shows what is worn and carried, with how many`() {
        val hero = Hero(bag = mapOf("berry" to 3, "rope" to 1), worn = mapOf(Slot.HEAD to "chicken_hat"))
        val bag = journey(hero).questBook().bag
        assertEquals(listOf("chicken_hat", "berry", "rope"), bag.map { it.itemId })
        assertTrue(bag[0].worn)
        assertEquals("A juicy berry. It heals a little. You have three.", Voice.caption(bag[1].said))
        assertTrue("You are wearing it." in Voice.caption(bag[0].said))
        assertTrue("Your bag is empty." in Voice.caption(journey().questBook().bagSaid))
    }

    @Test
    fun `looking in the book changes nothing about the journey`() {
        val a = journey()
        val b = journey()
        repeat(12) {
            a.questBook()
            val reply = when (val beat = a.beat) {
                is Beat.Ask -> Reply.Solved(1, 0, 1000)
                is Beat.Roll -> Reply.Rolled(false, 1)
                is Beat.Choose, is Beat.Travel -> Reply.Picked(0)
                else -> Reply.Next
            }
            a.reply(reply)
            b.reply(reply)
        }
        assertEquals(b.beat, a.beat)
        assertEquals(b.hero, a.hero)
        assertEquals(b.world, a.world)
    }

    @Test
    fun `every quest names real people, places and flags the world can set`() {
        val set = buildSet {
            for (npc in Content.npcs) for (n in npc.nodes) {
                (n.effects + n.options.flatMap { it.effects }).filterIsInstance<Effect.SetFlag>().forEach { add(it.name) }
            }
            for (arc in Content.arcs) arc.moments.flatMap { it.effects }.filterIsInstance<Effect.SetFlag>().forEach { add(it.name) }
        }
        fun flags(c: Cond): List<String> = when (c) {
            is Cond.Flag -> listOf(c.name)
            is Cond.NoFlag -> listOf(c.name)
            is Cond.Not -> flags(c.cond)
            is Cond.AnyOf -> c.conds.flatMap { flags(it) }
            else -> emptyList()
        }
        val places = Content.locations.map { it.id }.toSet()
        for (q in Content.quests) {
            assertTrue(Content.npc(q.giver) != null, "${q.id}: no person ${q.giver}")
            for (step in q.steps) assertTrue(step.at == null || step.at in places, "${q.id}: no place ${step.at}")
            val conds = q.starts + q.done + q.steps.flatMap { it.until + it.knows }
            // Meeting somebody is remembered by the engine itself, not by dialog.
            for (f in conds.flatMap { flags(it) }.filterNot { it.startsWith("met:") }) assertTrue(f in set, "${q.id}: nothing sets the flag $f")
            for (f in conds.flatMap { flags(it) }.filter { it.startsWith("met:") }) {
                assertTrue(Content.npc(f.removePrefix("met:")) != null, "${q.id}: nobody to meet for $f")
            }
        }
        for (arc in Content.arcs) {
            assertTrue(arc.summary.isNotBlank(), "${arc.id} has no summary for the quest book")
            for (clue in arc.clues) assertTrue(clue.flag in set, "${arc.id}: nothing sets the flag ${clue.flag}")
        }
    }

    @Test
    fun `magic beans bought before meeting anybody who wants them are a mystery, not a spoiler`() {
        val j = journey(Hero(bag = mapOf("magic_beans" to 1)))
        val beans = j.questBook().quests.single { it.id == "ribbit_beans" }
        val said = Voice.caption(beans.said)
        assertTrue("We have magic beans! What are they for?" in said, said)
        assertTrue("Ribbit" !in said && "Fishers' Dock" !in said, said)
        assertTrue("Ribbit" !in beans.title, beans.title)
        assertEquals(null, beans.giver)
        assertEquals("magic_beans", beans.itemId)

        j.setFlag("met:sir_ribbit")
        val known = j.questBook().quests.single { it.id == "ribbit_beans" }
        assertTrue("Bring them to Sir Ribbit, at Fishers' Dock." in Voice.caption(known.said))
        assertEquals("sir_ribbit", known.giver)
    }

    @Test
    fun `a quest waits until the hero has heard of the people in it`() {
        // Befriending Bess before Captain Hob asked: Hob is a stranger, so there is nobody to tell yet.
        assertTrue(journey().apply { setFlag("bess_befriended") }.questBook().quests.isEmpty())
        // The story found out the bats are scared, but Pip has not been met.
        assertTrue(journey(story = "thousand_lanterns").apply { setFlag("knows_bats_scared") }.questBook().quests.isEmpty())
        // Grumble said he would take the boat back to Finn, and nothing more.
        val boat = journey().apply { setFlag("grumble_befriended") }.questBook().quests.single()
        assertEquals("Grumble is our friend now! He is taking the boat back to somebody called Finn.", Voice.caption(boat.said))
    }
}
