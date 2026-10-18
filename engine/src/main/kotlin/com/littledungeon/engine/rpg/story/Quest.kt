package com.littledungeon.engine.rpg.story

/**
 * One thing to do on a quest. It is the step the hero is on until everything in [until] is true. [at] is the place it
 * happens, so the quest book can say where to go.
 */
data class QuestStep(val says: String, val until: List<Cond>, val at: String? = null) {
    constructor(says: String, at: String?, vararg until: Cond) : this(says, until.toList(), at)
}

/**
 * Something a person asked the hero to do. A quest is data, read from what the world already remembers: it is in the
 * quest book once [starts] holds, the step shown is the first whose [QuestStep.until] does not hold yet, and it is
 * finished once [done] holds. Nothing here changes the world: the person's own dialog does that. [giver] is the person
 * whose picture the quest wears; [title] is a few words for grown-ups reading along (the child hears the steps).
 */
data class Quest(
    val id: String,
    val giver: String,
    val title: String,
    val starts: List<Cond>,
    val steps: List<QuestStep>,
    val done: List<Cond>,
    /** Said in the quest book when the quest was finished in this adventure. */
    val doneSays: String,
)

/** Something the hero has found out about the story, said in the quest book once [flag] is remembered. */
data class Clue(val flag: String, val says: String)
