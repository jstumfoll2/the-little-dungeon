package com.littledungeon.engine.rpg.story

/**
 * One thing to do on a quest. It is the step the hero is on until everything in [until] is true. [at] is the place it
 * happens, so the quest book can say where to go.
 */
data class QuestStep(
    val says: String,
    val until: List<Cond>,
    val at: String? = null,
    /** What the hero must have found out before the book may say [says]: whom they have met or been told about. */
    val knows: List<Cond> = emptyList(),
    /** Said instead of [says] until [knows] holds, so the book never names somebody the hero has not heard of. */
    val guess: String? = null,
) {
    constructor(says: String, at: String?, vararg until: Cond) : this(says, until.toList(), at)

    init {
        require(knows.isEmpty() || guess != null) { "a step that waits to be known needs a guess: $says" }
    }

    /** This step, said as [guess] until [knows] holds. */
    fun guessUntil(knows: Cond, guess: String) = copy(knows = listOf(knows), guess = guess)
}

/** How a quest looks in the book while its step is only a guess: [title] and the picture of [itemId], not the giver. */
data class Secret(val title: String, val itemId: String)

/**
 * Something a person asked the hero to do. A quest is data, read from what the world already remembers: it is in the
 * quest book once [starts] holds, the step shown is the first whose [QuestStep.until] does not hold yet, and it is
 * finished once [done] holds. Nothing here changes the world: the person's own dialog does that. [giver] is the person
 * whose picture the quest wears; [title] is a few words for grown-ups reading along (the child hears the steps). The
 * book only tells what the hero has found out: a step names a person or place once the hero has met or heard of them.
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
    /** When set, the giver stays a secret while the step shown is a guess (decision #66). */
    val secret: Secret? = null,
)

/** Something the hero has found out about the story, said in the quest book once [flag] is remembered. */
data class Clue(val flag: String, val says: String)
