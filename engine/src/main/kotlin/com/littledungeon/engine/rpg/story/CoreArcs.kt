package com.littledungeon.engine.rpg.story

/**
 * The stories of Book One. Arc one is always the first the player sees; the others come from chapter two on, so every
 * adventure after the first can be a different story. The rest of Book One (Ruby, the lost baby dragon, the Ink Shadow)
 * follows in later work.
 */
object CoreArcs {
    /**
     * "The Missing Pages": the Great Storybook is losing its pages, and Baron Grumblewick has been
     * taking them. The way in is through Inkwell Cellars, where his imp guards the silver quill.
     */
    val missingPages = Arc(
        id = "missing_pages",
        summary = "The pages of the Great Storybook are missing, and Baron Grumblewick took them. His gate is sealed with sticky black ink.",
        clues = listOf(Clue("knows_baron_lonely", "We found out that the Baron is lonely. Maybe he just wants somebody to listen.")),
        title = "The Missing Pages",
        lairId = "lair_manor",
        keyDungeonId = "inkwell_cellars",
        keyItemId = "silver_quill",
        bossId = "baron_grumblewick",
        setup = listOf(
            "The Great Storybook of Whisperwood is losing its pages! One by one, the stories are vanishing: bedtime tales, bakery recipes, even the song of the Old Stone Bridge.",
            "<elder>Hello, little adventurer. I am Professor Hoot, and I keep the Storybook. The pages are gone, and I know who took them. Baron Grumblewick, from the crooked manor.",
            "<pet>The grumpy man who never smiles? Why would he want stories?",
            "<elder>Nobody knows. But his gate is sealed with sticky black ink. Only his own silver quill can write it open, and his imp guards it in the Inkwell Cellars.",
        ),
        sealed = "The manor gate is sealed with sticky black ink! <pet>We need the Baron's silver quill. The Professor said it is in the Inkwell Cellars.",
        keyFound = "<pet>The silver quill! Now we can write the gate open. Let's go to the manor!",
        gateOpens = "You write one big word with the silver quill, and the ink on the gate runs away like a scared spider. The gate swings open.",
        ask = "What will you do about the Baron?",
        fightLabel = "Challenge the Baron",
        peaceLabel = "Listen to the Baron",
        peaceSteps = listOf(
            PeaceStep(
                "letters", "<baron>If you want to hear my story, you must write it down. Find me the right letter.", "<baron>Yes, that is the letter! Nobody has written my words down in years.",
                skippedBy = "knows_baron_lonely",
                skipNote = "<baron>You already know why I stopped writing. Nobody has ever understood me so quickly. We can skip the first page.",
            ),
            PeaceStep("pattern", "<baron>Every story has a pattern, like a song. Which one comes next?", "<baron>Round and round, just like my days. You understand."),
            PeaceStep("colors", "<baron>And a story needs color. I only ever had gray ink. Find me the right color.", "<baron>Oh, so bright! I had forgotten what color looks like."),
        ),
        returnSetup = listOf(
            "Another page of the Great Storybook has gone missing! The ink is spreading again.",
            "<elder>Little adventurer, you are back! The pages are drifting off once more, and I fear it is the Baron's ink. Please, go and see what has happened.",
        ),
        friendMeeting = "The manor door is open, and the Baron waits inside with his hat in his hands. <baron>You came back. My ink got away from me again, and the pages ran off in every direction. I am so sorry. Will you help me write them home? <pet>He is asking us for help, not for a fight!",
        friendSteps = listOf(
            PeaceStep("letters", "<baron>Help me write the page home. Find the letter that goes first.", "<baron>Yes! That is the letter. The page is nearly home."),
            PeaceStep("pattern", "<baron>Pages follow patterns, just like days. Which one comes next?", "<baron>Round and round. Thank you, my friend."),
            PeaceStep("colors", "<baron>And now some color, to cheer the page up. Find the right color.", "<baron>Oh, so bright! The page is whole again."),
        ),
        friendEnd = "Every page flutters home, and the Baron writes you into the very first line of the book, in gold. <baron>Whatever else happens, I will always be your friend. <pet>He is smiling again!",
        rivalMeeting = "The Baron slams the door of his study and glares. <baron>You again! The one who beat me. This time my ink is on my side, and I will not be so easily beaten.",
        moments = listOf(
            Moment("mossbrook", "The shelf in the village library is empty. Only a sticky black fingerprint is left. <pet>Somebody took the whole shelf of fairy tales!"),
            Moment("pennywhistle", "A poster is stuck to the market wall. It says, WANTED: ALL STORIES. Signed, B. G. <pet>B. G.? That must be Baron Grumblewick!"),
            Moment(
                "hermit_hill",
                "A single page is stuck in the grass, covered in ink. The only words you can read say, Once upon a time, there was a man who just wanted somebody to listen. <pet>That sounds so sad.",
                effects = listOf(Effect.SetFlag("knows_baron_lonely")),
            ),
            Moment("lantern_hollow", "Every lantern in town is gray with sticky ink, and Lumi is scrubbing as hard as she can. <pet>The Baron's ink is everywhere!"),
            Moment("whispering_falls", "The waterfall runs black for a moment, then clear again, as if somebody upstream washed a very inky pen. <pet>We must be getting close."),
            Moment(
                "fishers_dock",
                "A bottle bobs against the dock with a note inside. It says, Dear anybody. Is anyone there? I wrote you a story. From B. <pet>Oh. He does sound lonely.",
                effects = listOf(Effect.SetFlag("knows_baron_lonely")),
            ),
            Moment("windy_pass", "The wind blows a page right into your hands. It is blank except for one word at the top: Please. <pet>Who is that for?"),
            Moment("inkwell_cellars", "Barrels of ink line the walls, and little black handprints cover every one. <pet>An imp lives here. Be careful!"),
        ),
        variants = listOf(
            Variant(
                "jealous_baron",
                "A tall thin man glares from behind a mountain of pages. <baron>Everybody loves your adventures. Nobody has ever told one about me.",
                "The Baron's top hat flies off, and every page flutters home. He slinks away, muttering. <baron>I only wanted to be somebody's hero. <pet>Maybe one day he will be.",
                "You listen to the Baron's whole sad story. <baron>Would you... write me in? Just a small part? <narrator>You tell him there is room for everyone in a story, and a brand new page appears with his name at the top, in gold. <pet>He is crying happy tears!",
            ),
            Variant(
                "inky_baron",
                "The Baron stands very still, covered in swirling black ink. His eyes are blank. <baron>Mine. All mine. Mine. <pet>Something is wrong with him. The ink is controlling him!",
                "The ink splashes off him in every direction, and the Baron blinks. <baron>What happened? I feel as if I have been asleep for years. <narrator>He gives back every page, and he is too embarrassed to look at anybody. <pet>It was not his fault!",
                "You speak to the Baron softly, and a little ink falls away with every kind word. <baron>Thank you. I think I was lost in my own ink for a long time. <narrator>He hands you every page, and then he plants a tiny garden outside the manor, just to have something that grows. <pet>I like him now.",
            ),
            Variant(
                "lonely_baron",
                "Inside, a tall thin man sits at a huge desk, buried in pages. <baron>You came. Everyone always comes to take my stories away. Nobody ever wants to hear them.",
                "The Baron's top hat tumbles off, and every page flutters up and flies home to the Storybook. The Baron sniffs, picks up his hat, and shuffles away to write all by himself. <pet>He looks so sad. Maybe next time we can be friends.",
                "You sit and listen to the Baron's whole story, and it is sad and funny and very long. <baron>Nobody ever heard it to the end. Take your pages home. And, if you like, I will come and read to the children on Fridays. <pet>He smiled! He really smiled!",
            ),
        ),
    )

    /**
     * "The Dragon Who Didn't Want to Fight": shiny things are vanishing, and the whole kingdom blames the dragon of
     * Dragon's Peak. The dragon only ever wanted a friend. Its cave hides in a cloud that only the Cloud Whistle can blow
     * away, and the Mossy Golem keeps the whistle in Gloomwood Mine. Wren is the one person who knows the truth.
     */
    val dragonPeak = Arc(
        id = "dragon_peak",
        summary = "Shiny things are vanishing, and everybody blames the dragon of Dragon's Peak. But is the dragon really mean?",
        clues = listOf(Clue("knows_dragon_kind", "Wren told us a secret: the dragon kept her safe in the snow. The dragon is kind!")),
        title = "The Dragon Who Didn't Want to Fight",
        lairId = "lair_peak",
        keyDungeonId = "gloomwood_mine",
        keyItemId = "cloud_whistle",
        bossId = "big_dragon",
        minChapter = 2,
        setup = listOf(
            "Shiny things are vanishing all over Whisperwood! Mayor Tilly's golden bell is gone. Zig's silver spoons are gone. Even Finn's lucky fishing hook is gone.",
            "<elder>Little adventurer, everybody says the dragon of Dragon's Peak took them. And now a page of the Storybook has torn itself out, the page about the dragon.",
            "<pet>A big dragon? I am a dragon too! Maybe it is not mean at all.",
            "<elder>Maybe not. A storybook should tell the truth. The dragon's cave is hidden inside a cloud, and only the Cloud Whistle can blow the cloud away. The Mossy Golem keeps it, deep in Gloomwood Mine.",
        ),
        returnSetup = listOf(
            "Shiny things are going missing again, and people are whispering about the dragon.",
            "<elder>You are back, little adventurer! The dragon's page has torn itself out once more. Please, go and find out what is really happening up on the peak.",
        ),
        sealed = "A thick gray cloud sits on the mountain, and you cannot see the cave at all. <pet>We need the Cloud Whistle to blow it away. The Professor said it is in Gloomwood Mine.",
        keyFound = "<pet>The Cloud Whistle! It is shaped like a little dragon. Let's take it to Dragon's Peak!",
        gateOpens = "You blow the Cloud Whistle. It sings a long, high note, and the cloud rolls away like a blanket. There is the dragon's cave, glittering inside.",
        ask = "What will you do about the dragon?",
        fightLabel = "Challenge the dragon",
        peaceLabel = "Talk to the dragon",
        peaceSteps = listOf(
            PeaceStep(
                "count", "<dragon>If you want to talk, help me count my treasure first. I like to know how many gems I have.", "<dragon>That is right. You count well, little one.",
                skippedBy = "knows_dragon_kind",
                skipNote = "<dragon>You already know I am not a bad dragon? Nobody ever knew that before. Then we do not need to count. Come in.",
            ),
            PeaceStep("colors", "<dragon>Every treasure has an owner. Help me find the right color, so it goes back home.", "<dragon>Yes, that one! It belongs to the bakery."),
            PeaceStep("numbers", "<dragon>The last treasure has a number painted on it. Tell me which number it is, and I will fly it home myself.", "<dragon>Oh! I have never given anything back before. It feels warm, right here."),
        ),
        friendMeeting = "The cloud has gone already, and the dragon is waiting at the mouth of the cave, holding something very carefully in its claws. <dragon>My friend! I found more shiny things on the mountain, and I want to give them back the right way, this time. Will you help me? <pet>The dragon is asking for help, not for a fight!",
        friendSteps = listOf(
            PeaceStep("count", "<dragon>First, let us count the gems, so none get lost on the way.", "<dragon>Good counting! Nothing will be lost."),
            PeaceStep("colors", "<dragon>Now find the right color, so I know whose door to knock on.", "<dragon>That door! I will knock very gently."),
            PeaceStep("numbers", "<dragon>And this one has a number on it. Which number is it?", "<dragon>Then that is the last one. Thank you, my friend."),
        ),
        friendEnd = "The dragon flies over the whole kingdom with you on its back, and everyone waves as the treasures float down to their owners. <dragon>A friend is the shiniest thing I ever found. <pet>I am flying! I am really flying!",
        rivalMeeting = "The cloud rolls away, and the dragon is already standing in the mouth of the cave, puffing smoke. <dragon>You again. The one who beat me. This time I am ready for you.",
        moments = listOf(
            Moment("mossbrook", "The town bell tower is empty. Mayor Tilly's golden bell is gone, and there is a big scorch mark on the floor. <pet>A scorch mark? That does look like a dragon."),
            Moment("pennywhistle", "A sign hangs on Zig's stall. It says, NO SPOONS TODAY. A DRAGON TOOK THEM. <pet>Did anybody see it happen, though?"),
            Moment(
                "hermit_hill",
                "There is a letter pinned to Hazel's little round door. It is from the Baron. It says, The dragon wrote back to me once. It signed its letter, Your friend, if you want one. <pet>The dragon writes letters? That is so nice!",
                needs = listOf(Cond.Flag("friend:baron_grumblewick")),
                effects = listOf(Effect.SetFlag("knows_dragon_kind")),
            ),
            Moment(
                "fishers_dock",
                "Finn's boat is tied up at the dock, but a big silver claw mark is scratched on the side. <pet>Finn's lucky hook was in that boat. The dragon must have been here!",
                needs = listOf(Cond.Flag("finn_boat")),
            ),
            Moment("gloomwood_mine", "Deep in the mine, something long and silver hums in the dark, like a song nobody has played in years. <pet>That must be the Cloud Whistle!"),
            Moment("windy_pass", "Up on the pass, a single golden scale is caught between two rocks. It is warm when you touch it. <pet>That is a dragon scale. We are close now."),
        ),
        variants = listOf(
            Variant(
                "lonely_dragon",
                "Inside the cave is a mountain of shiny things, and on top of it sits a great red dragon, all alone. <dragon>Go away. Everybody is scared of me. Even the shiny things only shine back at me.",
                "The dragon tumbles down its mountain of treasure, and every shiny thing rolls out of the cave. <dragon>Take them. I only wanted something that shined back at me. <pet>It looks so sad. Maybe next time we can be friends.",
                "You sit down next to the dragon and tell it about your adventures. The dragon laughs, and its laugh is warm, like a fireplace. <dragon>Nobody ever talked to me before. Here, take everything back. I would much rather have a friend. <pet>It is smiling! A dragon smile!",
            ),
            Variant(
                "collector_dragon",
                "The dragon is polishing a silver spoon, very proudly. <dragon>Hello! Have you come to see my presents? People leave them out for me. Spoons on tables, bells in towers. They must love me very much.",
                "The dragon puffs out a sad little cloud of smoke. <dragon>They were not presents? Then I am a thief. I am so sorry. <narrator>It gives back every single thing, and hides its face under a wing. <pet>It did not know! It really did not know.",
                "You explain, very kindly, that the spoons and bells belong to people. The dragon goes pink all over. <dragon>Oh! Oh, no. Then I will give them back, and I will say sorry to everybody. <narrator>And the next day, the dragon leaves a present on every doorstep: a shiny pebble from its mountain. <pet>Now it gives presents too!",
            ),
            Variant(
                "cold_dragon",
                "The cave is cold, and the dragon is curled up tight, shivering. <dragon>My fire went out. I gathered every shiny thing in the kingdom to catch the sunlight and keep me warm. It did not work.",
                "The dragon sneezes, and a tiny flame pops out. <dragon>Oh! My fire! I do not need the shiny things now. Take them home. <narrator>It is still a little grumpy, but it is warm again. <pet>A warm dragon is a happier dragon.",
                "You help the dragon tell a joke, and it laughs so hard that its fire comes back, big and bright and cozy. <dragon>A good laugh is better than any treasure! Take everything home, and come and warm up here whenever you like. <pet>The best fireplace in the whole kingdom!",
            ),
        ),
    )

    /**
     * "The Night of a Thousand Lanterns": the Festival of Lights is tonight, and the Bat King has been taking Lantern
     * Hollow's lanterns, because his bats are scared of the dark too. The Bat Belfry is too dark to climb without the
     * Starlight Jar, which the Spider Queen keeps in the Spider Caves. Pip, the littlest bat, ran away to the lights.
     */
    val thousandLanterns = Arc(
        id = "thousand_lanterns",
        summary = "The lanterns of Lantern Hollow are going missing, and the bats are carrying them away to the Bat Belfry.",
        clues = listOf(Clue("knows_bats_scared", "We found out that the bats are scared of the dark. That is why they take the lanterns!")),
        title = "The Night of a Thousand Lanterns",
        lairId = "lair_belfry",
        keyDungeonId = "spider_caves",
        keyItemId = "starlight_jar",
        bossId = "bat_king",
        minChapter = 2,
        setup = listOf(
            "Tonight is the Festival of Lights in Lantern Hollow! But every night, more lanterns go missing, and now there are hardly any left.",
            "<elder>Little adventurer, without lanterns there is no festival, and without the festival, the Storybook's page about it is fading away. Lumi saw bats carrying the lanterns north, to the old Bat Belfry.",
            "<pet>Bats? Why would bats want lanterns?",
            "<elder>That is what we must find out. But the belfry stairs are pitch dark. You will need the Starlight Jar, and the Spider Queen keeps it in the Spider Caves.",
        ),
        returnSetup = listOf(
            "It is festival time in Lantern Hollow again, and the lanterns are going missing again!",
            "<elder>You are back, just in time! The bats are flying off with the lanterns once more. Please, go and find out why.",
        ),
        sealed = "The belfry door is open, but inside it is so dark that you cannot see the first step. <pet>We need the Starlight Jar. The Professor said the Spider Queen has it, in the Spider Caves.",
        keyFound = "<pet>The Starlight Jar! It is full of tiny twinkling lights. Now we can climb the belfry!",
        gateOpens = "You hold up the Starlight Jar, and soft silver light fills the stairs. Up and up you climb, round and round, all the way to the top.",
        ask = "What will you do about the Bat King?",
        fightLabel = "Chase the bats away",
        peaceLabel = "Listen to the bats",
        peaceSteps = listOf(
            PeaceStep(
                "colors", "<critter>If you want to help us, find the right color. We sort our lanterns by color.", "<critter>Yes! That one is the prettiest.",
                skippedBy = "knows_bats_scared",
                skipNote = "<critter>You already know that we are scared of the dark? Then you understand us. No sorting needed.",
            ),
            PeaceStep("count", "<critter>Every bat gets one glow gem for its own little lantern. Count the gems with me.", "<critter>That is right. Not one bat will be left in the dark."),
            PeaceStep("pattern", "<critter>And a festival needs lanterns hung in a pattern. Which one comes next?", "<critter>Oh, how lovely. Just like the stars."),
        ),
        friendMeeting = "The belfry is already glowing, and the Bat King hangs from the bell rope, waving a tiny lantern. <critter>Our friend is back! Some of my littlest bats are still scared of the dark. Will you help me make them brave, before the festival? <pet>He is asking for help!",
        friendSteps = listOf(
            PeaceStep("count", "<critter>Count the glow gems with me, so no little bat is forgotten.", "<critter>Every bat is here. Thank you."),
            PeaceStep("colors", "<critter>Now pick the right color, for the bravest little lantern of all.", "<critter>That is the one. Look how it glows!"),
            PeaceStep("pattern", "<critter>And last, the pattern for our flying dance. Which one comes next?", "<critter>Wing, wing, twirl! We are ready."),
        ),
        friendEnd = "The bats fly down to Lantern Hollow in a long, glowing line, and they help Lumi light every lantern in town, all thousand of them. <critter>We are not scared anymore. Not one bit. <pet>It is the brightest festival ever!",
        rivalMeeting = "The Bat King flaps up into the rafters and hisses. <critter>You again! The one who chased us out last time. These lanterns are ours now, and you will not take them.",
        moments = listOf(
            Moment("lantern_hollow", "Hardly any lanterns are left in Lantern Hollow, and the festival flags droop in the dark. <pet>It is so gloomy. Poor Lumi."),
            Moment(
                "lantern_hollow",
                "Lumi runs up and gives you a hug. <child>My friend! I saved my very best lantern for you. Please bring the others home. <pet>We will, Lumi!",
                needs = listOf(Cond.Flag("lumi_gift")),
                effects = listOf(Effect.Heal(99)),
            ),
            Moment("hermit_hill", "Hazel is looking up at the sky through a long brass telescope. You look too, and you see a line of tiny lanterns flying north. <pet>The bats! They are going to the belfry!"),
            Moment(
                "windy_pass",
                "Bess waves from a rock. <sneak>Hey, friend! My little brother saw the bats last night. They were not being naughty. They were shivering. <pet>Shivering? Maybe they are scared!",
                needs = listOf(Cond.Flag("bess_befriended")),
                effects = listOf(Effect.SetFlag("knows_bats_scared")),
            ),
            Moment("spider_caves", "Far inside the cave, something twinkles like a jar full of stars. <pet>That must be the Starlight Jar!"),
            Moment("gloomwood_mine", "A little lantern lies on the floor of the mine, still glowing. A bat must have dropped it on the way north. <pet>We are on the right trail."),
            Moment(
                "inkwell_cellars", "A note is pinned to an ink barrel. It says, Dear bats. You may borrow my candles. From B. <pet>The Baron is helping the bats? He really has changed.",
                needs = listOf(Cond.Flag("friend:baron_grumblewick")),
            ),
        ),
        variants = listOf(
            Variant(
                "scared_bats",
                "At the top of the belfry, a thousand lanterns hang from the beams, and a thousand little bats huddle under them. A big bat wearing a tiny crown spreads his wings. <critter>Stay back! These lights are ours. We are, um, not scared of the dark. Not at all.",
                "The bats scatter into the night, and the lanterns float gently back down to Lantern Hollow. <critter>Fine! Keep your lights. We will just sit in the dark, all alone. <narrator>But that night, the town leaves one lantern burning on the hill, just for the bats. <pet>That was kind.",
                "You tell the Bat King that everybody is scared of the dark sometimes, even you. <critter>Even heroes? <narrator>Lumi climbs up the stairs behind you with a basket of glow lanterns, one for every bat. <critter>Our own lights! Then we will give back yours, and we will come to the festival! <pet>Bats at the festival! Hooray!",
            ),
            Variant(
                "showoff_bat_king",
                "The Bat King is standing in a pile of lanterns, admiring himself in the glow. <critter>Look at me! The shiniest king in the kingdom! Nobody ever looked at me when I was just a bat in the dark.",
                "The Bat King's crown slips over his eyes, and he tumbles into the bell with a bong. <critter>Ow. All right, all right, take them back. <narrator>He sulks in the rafters, but he peeks out when the festival begins. <pet>He wants to see the lights too!",
                "You tell the Bat King that he does not need a thousand lanterns to be noticed. Everybody will see him if he leads the festival dance. <critter>Me? Lead the dance? <narrator>That night, the Bat King swoops over Lantern Hollow at the front of the parade, with one little lantern on his crown. <pet>He is the star of the festival!",
            ),
            Variant(
                "sleepless_bats",
                "The belfry is full of lanterns, and full of very tired bats with very big yawns. <critter>The new owl in the woods hoots all night, and the little ones are scared. The lanterns help them sleep.",
                "The bats flap away grumbling, and the lanterns go home to Lantern Hollow. <critter>Fine. We will just stay awake all day instead. <narrator>Professor Hoot hears about it, and promises to hoot more quietly. <pet>That will help them sleep.",
                "You promise to ask the owl to hoot softly, and you sing the little bats a lullaby until every one of them is asleep. <critter>Shh. They are sleeping. Take the lanterns home, and thank you. <narrator>Now there is a quiet hour in the woods every night, and everybody sleeps better. <pet>Even me. Yawn.",
            ),
        ),
    )

    val all = listOf(missingPages, dragonPeak, thousandLanterns)
}
