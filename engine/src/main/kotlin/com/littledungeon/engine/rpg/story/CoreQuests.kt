package com.littledungeon.engine.rpg.story

import com.littledungeon.engine.rpg.story.Cond.*

/**
 * The favors the first people of Whisperwood ask (see the threads in [CoreNpcs]). Each one follows flags and items the
 * people's own dialog already sets, so a quest is only words: the quest book reads it, and nothing else changes.
 */
object CoreQuests {
    val recipe = Quest(
        "bun_recipe", "baker_bun", "Baker Bun's recipe",
        starts = listOf(Flag("quest_recipe")),
        steps = listOf(
            QuestStep("Baker Bun lost her recipe page. Rascal the Fox has it, at Pennywhistle Market.", "pennywhistle", Flag("recipe_got")),
            QuestStep("We have the recipe page! Bring it back to Baker Bun in Mossbrook.", "mossbrook", Flag("bun_recipe_returned")),
            QuestStep("Baker Bun is happy again. Tell Mayor Tilly the good news, in Mossbrook.", "mossbrook", Flag("tilly_thanked")),
        ),
        done = listOf(Flag("tilly_thanked")),
        doneSays = "Baker Bun has her recipe back, and Mayor Tilly is so proud of us!",
    )

    val bess = Quest(
        "hob_bess", "captain_hob", "Bandit Bess",
        starts = listOf(AnyOf(Flag("hob_asked"), Flag("bess_befriended"))),
        steps = listOf(
            QuestStep("Bandit Bess takes tolls at Windy Pass. Captain Hob says, try talking to her first.", "windy_pass", Flag("bess_befriended")),
            QuestStep("Bess is our friend now! Tell Captain Hob, in Lantern Hollow.", "lantern_hollow", Flag("hob_thanked")),
        ),
        done = listOf(Flag("hob_thanked")),
        doneSays = "Bandit Bess stopped taking tolls, and Captain Hob bowed to us!",
    )

    val boat = Quest(
        "finn_boat", "finn_fisher", "Finn's boat",
        starts = listOf(AnyOf(Flag("finn_asked"), Flag("grumble_befriended"))),
        steps = listOf(
            QuestStep("Finn's boat is with Grumble the Troll, at Old Stone Bridge. Ask him nicely.", "old_bridge", Flag("grumble_befriended")),
            QuestStep("Grumble is our friend now! Go and see Finn, at Fishers' Dock.", "fishers_dock", Flag("finn_boat")),
        ),
        done = listOf(Flag("finn_boat")),
        doneSays = "Finn has his boat back, and everybody can ride the river ferry!",
    )

    val beans = Quest(
        "ribbit_beans", "sir_ribbit", "Sir Ribbit's beans",
        starts = listOf(AnyOf(Flag("ribbit_asked"), HasItem("magic_beans"))),
        steps = listOf(
            QuestStep("Sir Ribbit needs magic beans. Rascal the Fox sells them, at Pennywhistle Market.", "pennywhistle", AnyOf(HasItem("magic_beans"), Flag("ribbit_beans"))),
            QuestStep("We have magic beans! Bring them to Sir Ribbit, at Fishers' Dock.", "fishers_dock", Flag("ribbit_beans")),
        ),
        done = listOf(Flag("ribbit_beans")),
        doneSays = "Sir Ribbit got his magic beans, and he passed his knight exam!",
    )

    val wren = Quest(
        "wren_dragon", "wren_girl", "Wren's secret",
        starts = listOf(ArcIs("dragon_peak"), Flag("wren_believed")),
        steps = listOf(
            QuestStep("Wren says the dragon is kind. Tell Brogan the Smith, at Pennywhistle Market.", "pennywhistle", Flag("brogan_brave")),
        ),
        done = listOf(Flag("brogan_brave")),
        doneSays = "Brogan the Smith is not scared of the dragon anymore!",
    )

    val pip = Quest(
        "pip_lantern", "pip_bat", "A light for Pip",
        starts = listOf(Flag("knows_bats_scared")),
        steps = listOf(
            QuestStep("Pip the little bat is scared of the dark. Bring Pip a lantern, in Lantern Hollow.", "lantern_hollow", Flag("pip_lit")),
        ),
        done = listOf(Flag("pip_lit")),
        doneSays = "Pip has a lantern of its own, and it is not scared anymore!",
    )

    val all = listOf(recipe, bess, boat, beans, wren, pip)
}
