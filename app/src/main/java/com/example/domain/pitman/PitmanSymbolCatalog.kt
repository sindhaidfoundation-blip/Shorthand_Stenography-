package com.example.domain.pitman

import com.example.domain.model.PitmanSymbolEntry
import com.example.domain.model.StenoCategory
import com.example.domain.model.StenoPosition
import com.example.domain.model.StenoStrokeSpec
import com.example.domain.model.SymbolExerciseGroup

object PitmanSymbolCatalog {

    val entries: List<PitmanSymbolEntry> = listOf(
        // Straight Strokes (Explodents & Ray)
        PitmanSymbolEntry(
            id = "sym_p",
            group = SymbolExerciseGroup.STRAIGHT_STROKES,
            phoneticCue = "/p/ as in pay, petition",
            directionHint = "Light downward stroke sloped ~60 deg left-to-right",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "P (Pee)",
                strokeFormula = "Straight • Light Down 60°",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Light Straight Explodent (Voiceless Bilabial)",
                ruleNote = "Written lightly downward at 60 degrees from vertical. Alone in 1st Pos = party; 2nd Pos = principle; 3rd Pos = put.",
                legalDefinition = "Foundational consonant stem for petition, party, parliament, and public.",
                glyphKey = "sym_p"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_b",
            group = SymbolExerciseGroup.STRAIGHT_STROKES,
            phoneticCue = "/b/ as in be, bail",
            directionHint = "Heavy downward stroke sloped ~60 deg left-to-right",
            isHeavy = true,
            spec = StenoStrokeSpec(
                term = "B (Bee)",
                strokeFormula = "Straight • Heavy Down 60°",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Heavy Straight Explodent (Voiced Pair of Pee)",
                ruleNote = "Matches Pee's angle exactly but written with firm heavy pressure. Alone in 2nd position represents 'be'; above the line = 'by'.",
                legalDefinition = "Core consonant stem for bail, bench, burden of proof, and benefit.",
                glyphKey = "sym_b"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_t",
            group = SymbolExerciseGroup.STRAIGHT_STROKES,
            phoneticCue = "/t/ as in take, tribunal",
            directionHint = "Light vertical stroke drawn strictly downward (90°)",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "T (Tee)",
                strokeFormula = "Straight • Light Vertical 90°",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Light Vertical Explodent (Voiceless Alveolar)",
                ruleNote = "Strict perpendicular downward stroke. Alone above line = 'at'; on line = 'it'; through line = 'out'. Halving any stroke adds T/D.",
                legalDefinition = "Forms tribunal, trial, testimony, and P-T petitioner intersection.",
                glyphKey = "sym_t"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_d",
            group = SymbolExerciseGroup.STRAIGHT_STROKES,
            phoneticCue = "/d/ as in do, decree",
            directionHint = "Heavy vertical stroke drawn strictly downward (90°)",
            isHeavy = true,
            spec = StenoStrokeSpec(
                term = "D (Dee)",
                strokeFormula = "Straight • Heavy Vertical 90°",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Heavy Vertical Explodent (Voiced Pair of Tee)",
                ruleNote = "Heavy perpendicular downward stroke. Alone above line = 'had'; on the line = 'do'; through the line = 'different/difference'.",
                legalDefinition = "Core stem for decree, doubt, discretion, and due process.",
                glyphKey = "sym_d"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_ch",
            group = SymbolExerciseGroup.STRAIGHT_STROKES,
            phoneticCue = "/tʃ/ as in charge, charter",
            directionHint = "Light downward stroke sloped ~60° right-to-left",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Ch (Chay)",
                strokeFormula = "Straight • Light Down 120°",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Light Downward Affricate (Always Downward)",
                ruleNote = "Slopes sharply downward from top-right to bottom-left (60° from horizontal — steeper than upward Ray's 30°). Alone on line = 'which'.",
                legalDefinition = "Used in charge, chief justice, chamber, and constitutional charter.",
                glyphKey = "sym_ch"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_j",
            group = SymbolExerciseGroup.STRAIGHT_STROKES,
            phoneticCue = "/dʒ/ as in judge, justice",
            directionHint = "Heavy downward stroke sloped ~60° right-to-left",
            isHeavy = true,
            spec = StenoStrokeSpec(
                term = "J (Jay)",
                strokeFormula = "Straight • Heavy Down 120°",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Heavy Downward Affricate (Voiced Pair of Chay)",
                ruleNote = "Heavy stroke parallel to Chay. Double Jay (J-J) is the classic shorthand contraction for 'judgment'; J-S stands for 'justice'.",
                legalDefinition = "Essential stroke for judge, judgment, jurisdiction, and justice.",
                glyphKey = "sym_j"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_k",
            group = SymbolExerciseGroup.STRAIGHT_STROKES,
            phoneticCue = "/k/ as in court, counsel",
            directionHint = "Light horizontal stroke drawn strictly left-to-right (0°)",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "K (Kay)",
                strokeFormula = "Straight • Light Horizontal 0°",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Light Horizontal Explodent (Velar)",
                ruleNote = "Written horizontally from left to right. Alone above line = 'can'; on the line = 'come'. Frequently intersects stems to denote 'Court' or 'Counsel'.",
                legalDefinition = "Primary intersection symbol for High Court, Supreme Court, and counsel.",
                glyphKey = "sym_k"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_g",
            group = SymbolExerciseGroup.STRAIGHT_STROKES,
            phoneticCue = "/ɡ/ as in government, grant",
            directionHint = "Heavy horizontal stroke drawn strictly left-to-right (0°)",
            isHeavy = true,
            spec = StenoStrokeSpec(
                term = "G (Gay)",
                strokeFormula = "Straight • Heavy Horizontal 0°",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Heavy Horizontal Explodent (Voiced Pair of Kay)",
                ruleNote = "Heavy horizontal left-to-right stroke. Alone above line = 'go/ago'; on the line = 'give/given'. Forms G-V contraction for 'government'.",
                legalDefinition = "Key consonant for government, governance, guarantee, and grant.",
                glyphKey = "sym_g"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_ray",
            group = SymbolExerciseGroup.STRAIGHT_STROKES,
            phoneticCue = "/r/ initial or before downstrokes (right, reform)",
            directionHint = "Light upward stroke sloped ~30° bottom-left to top-right",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "R (Upward Ray)",
                strokeFormula = "Straight • Light Up 30°",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Upward Shallow Consonant (Distinct from Chay by 30° Angle)",
                ruleNote = "Always written upward at a shallow 30 degrees above horizontal. Used when R precedes another downward stroke or when a vowel follows final R.",
                legalDefinition = "Forms right, reform, record, respondent, and appellate review.",
                glyphKey = "sym_ray"
            )
        ),

        // Curved Strokes (Continuants, Nasals, Liquids, Coalescents, Aspirate)
        PitmanSymbolEntry(
            id = "sym_f",
            group = SymbolExerciseGroup.CURVED_STROKES,
            phoneticCue = "/f/ as in fact, fiscal",
            directionHint = "Light downward convex curve sloping left-to-right",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "F (Ef)",
                strokeFormula = "Curve • Light Down",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Light Curved Continuant (Quarter-Circle Left Edge)",
                ruleNote = "Shallow downward curve bending outward to the left. Alone above line = 'if'; on the line = 'for'.",
                legalDefinition = "Used in fact, fiscal, forum, and fundamental rights.",
                glyphKey = "sym_f"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_v",
            group = SymbolExerciseGroup.CURVED_STROKES,
            phoneticCue = "/v/ as in verdict, vested",
            directionHint = "Heavy downward convex curve sloping left-to-right",
            isHeavy = true,
            spec = StenoStrokeSpec(
                term = "V (Vee)",
                strokeFormula = "Curve • Heavy Down",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Heavy Curved Continuant (Voiced Pair of Ef)",
                ruleNote = "Same downward curve as Ef shaded heavily in the middle. Alone on the line = 'have'; above line = 'very'.",
                legalDefinition = "Represents verdict, vested right, ultra vires, and evidence.",
                glyphKey = "sym_v"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_th",
            group = SymbolExerciseGroup.CURVED_STROKES,
            phoneticCue = "/θ/ as in think, authority",
            directionHint = "Light vertical-curved stroke bending outward to the left",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Th (Ith)",
                strokeFormula = "Curve • Light Vertical",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Voiceless Dental Fricative Curve",
                ruleNote = "Drawn downward as a vertical shallow C-curve. Alone above line = 'thank/thanked'; on line = 'think'.",
                legalDefinition = "Appears in authority, third party, and constitutional oath.",
                glyphKey = "sym_th"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_dh",
            group = SymbolExerciseGroup.CURVED_STROKES,
            phoneticCue = "/ð/ as in that, they",
            directionHint = "Heavy vertical-curved stroke bending outward to the left",
            isHeavy = true,
            spec = StenoStrokeSpec(
                term = "Th (Thee)",
                strokeFormula = "Curve • Heavy Vertical",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Voiced Dental Fricative Curve",
                ruleNote = "Heavy vertical C-curve. One of the highest-frequency grammalogues: 1st Pos = 'that'; 2nd Pos = 'they/them'; 3rd Pos = 'within/without'.",
                legalDefinition = "High-frequency connective grammalogue across every legal sentence.",
                glyphKey = "sym_dh"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_s",
            group = SymbolExerciseGroup.CURVED_STROKES,
            phoneticCue = "/s/ as in suit, statute",
            directionHint = "Light vertical-curved stroke bending outward to the right",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "S (Es)",
                strokeFormula = "Curve • Light Right-Arc",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Full S-Stroke (Used When Vowel Precedes Initial S or Alone)",
                ruleNote = "Drawn downward bending right (reverse of Ith). Use full stroke Es instead of the small circle when initial S is preceded by a vowel (e.g., ask, estate) or followed by two vowels.",
                legalDefinition = "Used in suit, estate, assessee, and so/us grammalogues.",
                glyphKey = "sym_s"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_z",
            group = SymbolExerciseGroup.CURVED_STROKES,
            phoneticCue = "/z/ as in zone, was",
            directionHint = "Heavy vertical-curved stroke bending outward to the right",
            isHeavy = true,
            spec = StenoStrokeSpec(
                term = "Z (Zee)",
                strokeFormula = "Curve • Heavy Right-Arc",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Heavy Voiced Sibilant Curve",
                ruleNote = "Matches Es curve with heavy shading. Alone on the line = 'was'; 3rd Pos = 'whose'.",
                legalDefinition = "Forms zoning, zero-rated, was, and whose.",
                glyphKey = "sym_z"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_sh",
            group = SymbolExerciseGroup.CURVED_STROKES,
            phoneticCue = "/ʃ/ as in shall, issue",
            directionHint = "Light downward curve sloped right-to-left",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Sh (Ish)",
                strokeFormula = "Curve • Light Down-Left",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Light Palatal Sibilant (Downward / Upward When Joined)",
                ruleNote = "Written downward alone (2nd Pos = 'shall'); may be written upward ('Shee') when joining smoothly after D, F, or V.",
                legalDefinition = "Mandatory statutory verb 'shall' and core stem for issue and legislation.",
                glyphKey = "sym_sh"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_m",
            group = SymbolExerciseGroup.CURVED_STROKES,
            phoneticCue = "/m/ as in merit, monetary",
            directionHint = "Light horizontal arch curving upward (like a hill)",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "M (Em)",
                strokeFormula = "Curve • Light Arch Horizontal",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Horizontal Nasal Arch (Light)",
                ruleNote = "Arches smoothly above the chord left-to-right. Alone above line = 'me/my'; on line = 'may/him'. Heavy shaded Em adds P/B ('Emp/Emb').",
                legalDefinition = "Represents merit, monetary, mandate, and amendment.",
                glyphKey = "sym_m"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_n",
            group = SymbolExerciseGroup.CURVED_STROKES,
            phoneticCue = "/n/ as in notice, natural",
            directionHint = "Light horizontal cup curving downward (like a bowl)",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "N (En)",
                strokeFormula = "Curve • Light Bowl Horizontal",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Horizontal Nasal Cup (Light)",
                ruleNote = "Curves downward beneath the chord left-to-right. Alone above line = 'in/any'; on line = 'no/know'; through line = 'own'.",
                legalDefinition = "Core stroke for notice, natural justice, and enactment.",
                glyphKey = "sym_n"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_ng",
            group = SymbolExerciseGroup.CURVED_STROKES,
            phoneticCue = "/ŋ/ as in hearing, ruling",
            directionHint = "Heavy horizontal cup curving downward",
            isHeavy = true,
            spec = StenoStrokeSpec(
                term = "Ng (Ing)",
                strokeFormula = "Curve • Heavy Bowl Horizontal",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Heavy Nasal Cup (Velar Nasal)",
                ruleNote = "Shaded version of En. Alone above line = 'English'; on line = 'thing'; 3rd Pos = 'language/owing'.",
                legalDefinition = "Represents verbal '-ing' endings such as hearing, filing, and ruling.",
                glyphKey = "sym_ng"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_l",
            group = SymbolExerciseGroup.CURVED_STROKES,
            phoneticCue = "/l/ as in law, legislation",
            directionHint = "Light curve usually drawn upward from lower-left to upper-right",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "L (El)",
                strokeFormula = "Curve • Light Upward Arc",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Liquid Consonant (Standard Upward Arc)",
                ruleNote = "Sweeps upward at ~30°. Alone on the line = 'Lord'; above line = 'will' (in phrasing) or upward El-Sh for legislation.",
                legalDefinition = "Primary stem for law, learned counsel, legislation, and appeal.",
                glyphKey = "sym_l"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_ar",
            group = SymbolExerciseGroup.CURVED_STROKES,
            phoneticCue = "/r/ after vowel (our, order, court)",
            directionHint = "Light shallow curve drawn downward from upper-right to lower-left",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Ar (Downward R)",
                strokeFormula = "Curve • Light Downward Arc",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Downward R Curve (Initial Vowel Before R or Final R)",
                ruleNote = "Starts with a clean curved downward sweep. Used when a vowel precedes initial R (order, arbitrary) or when R ends a word without a following vowel.",
                legalDefinition = "Forms order, article, arbitral award, and error.",
                glyphKey = "sym_ar"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_w",
            group = SymbolExerciseGroup.CURVED_STROKES,
            phoneticCue = "/w/ as in writ, witness",
            directionHint = "Upward Ray stroke preceded by a small initial hook",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "W (Way)",
                strokeFormula = "Hooked Upstroke • Light Up 30°",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Coalescent W Stroke (Hooked Upward 30°)",
                ruleNote = "An initial hook on the left of upward 30° stroke. Alone on the line = 'we/way'. Abbreviated to a semicircle before K, G, M, R.",
                legalDefinition = "Represents writ, witness, waiver, and well-settled principle.",
                glyphKey = "sym_w"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_y",
            group = SymbolExerciseGroup.CURVED_STROKES,
            phoneticCue = "/j/ as in year, your",
            directionHint = "Upward Ray stroke preceded by a small right-side hook",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Y (Yay)",
                strokeFormula = "Under-Hooked • Light Up 30°",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Palatal Coalescent Stroke",
                ruleNote = "Initial hook opens downward beneath upward 30° stem. Alone on the line = 'your'; above line = 'year'.",
                legalDefinition = "Common in appellate openings ('Your Lordships', 'last year').",
                glyphKey = "sym_y"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_h",
            group = SymbolExerciseGroup.CURVED_STROKES,
            phoneticCue = "/h/ as in High Court, hearing",
            directionHint = "Small circle attached cleanly to upward 30° stem (Hay)",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "H (Upward Hay)",
                strokeFormula = "Circled Upstroke • Light Up 30°",
                position = StenoPosition.FIRST,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Aspirate Stem (Upward Hay / Downward Hay)",
                ruleNote = "Upward Hay begins with a small clockwise circle below the stem. In 1st Pos intersected by Kay, it forms the universal 'High Court' outline.",
                legalDefinition = "Used in High Court, hearing, habeas corpus, and holding.",
                glyphKey = "sym_h"
            )
        ),

        // Circles, Loops & Hooks
        PitmanSymbolEntry(
            id = "sym_circle_s",
            group = SymbolExerciseGroup.CIRCLES_HOOKS,
            phoneticCue = "Initial, medial, or final /s/ or /z/",
            directionHint = "Small crisp circle turned inside curves or left/upper side of straights",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Small S-Circle",
                strokeFormula = "o + Stem (Initial / Medial / Final)",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Abbreviated Sibilant Circle (Anti-Clockwise on Straight Stems)",
                ruleNote = "Replaces full Es whenever S is not preceded by an initial vowel or followed by a final vowel. Read first when initial, last when final (including plurals).",
                legalDefinition = "Forms plurals (rights, courts, reforms) and prefixes (state, stay, statute).",
                glyphKey = "sym_circle_s"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_circle_ses",
            group = SymbolExerciseGroup.CIRCLES_HOOKS,
            phoneticCue = "Medial/final /sɛs/, /sɪs/, or initial /sw/",
            directionHint = "Double-sized large circle attached to stem",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Large Ses / Sw-Circle",
                strokeFormula = "O + Stem (Large Double Circle)",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Double-Size Circle Rule (Initial SW / Medial-Final SES)",
                ruleNote = "Initially represents 'sw-' (swift, swear); medially or finally represents 'ses/sis/ces' with an intervening vowel (process, basis, cases).",
                legalDefinition = "Essential for due process, cases, recesses, and statutory basis.",
                glyphKey = "sym_circle_ses"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_loop_st",
            group = SymbolExerciseGroup.CIRCLES_HOOKS,
            phoneticCue = "Initial/final /st/ or /zd/ (state, must, fast)",
            directionHint = "Elongated oval loop covering half the length of the stem",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Stee Loop (-st)",
                strokeFormula = "Half-Length Oval Loop on Stem",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Half-Length St Loop (Never Crosses Stroke End)",
                ruleNote = "Extends roughly half the stem's length on the S-circle side. Represents initial st- (state, stay) or final -st/-zd (constituted, dismissed, just).",
                legalDefinition = "Found in Constitution, state, statute, dismissed, and manifest.",
                glyphKey = "sym_loop_st"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_loop_str",
            group = SymbolExerciseGroup.CIRCLES_HOOKS,
            phoneticCue = "Medial/final /stər/ (minister, register, master)",
            directionHint = "Large elongated loop extending two-thirds of the stem",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Ster Loop (-str)",
                strokeFormula = "2/3-Length Large Loop",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Two-Thirds Length Final/Medial Loop",
                ruleNote = "Cannot be written initially; used medially or finally for '-ster' (minister, magistrate's register, disaster). Accepts a final S-circle.",
                legalDefinition = "Rapidly writes minister, register, master, and administer.",
                glyphKey = "sym_loop_str"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_hook_r",
            group = SymbolExerciseGroup.CIRCLES_HOOKS,
            phoneticCue = "Blended /r/ (Pr, Tr, Kr, Fr, Dr)",
            directionHint = "Small initial hook on the right-hand / clockwise side of straight stems",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Initial R-Hook (Pr / Tr / Kr)",
                strokeFormula = "Right-Side Initial Hook + Stem",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Double-Consonant Series (Right Motion Like Right Hand)",
                ruleNote = "Small clockwise hook at the start blends consonant + R into one pen stroke (principle, trial, decree, progress).",
                legalDefinition = "Forms principle, tribunal, decree, prosecution, and prerogative.",
                glyphKey = "sym_hook_r"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_hook_l",
            group = SymbolExerciseGroup.CIRCLES_HOOKS,
            phoneticCue = "Blended /l/ (Pl, Tl, Kl, Fl, Bl)",
            directionHint = "Small initial hook on the left-hand / anti-clockwise side of straight stems",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Initial L-Hook (Pl / Kl / Fl)",
                strokeFormula = "Left-Side Initial Hook + Stem",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Double-Consonant Series (Left Motion Like Left Hand)",
                ruleNote = "Small anti-clockwise hook at the start blends consonant + L into one stroke (plea, clause, claim, public, compliance).",
                legalDefinition = "Forms plea, plaintiff, clause, public interest, and legislation.",
                glyphKey = "sym_hook_l"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_hook_n",
            group = SymbolExerciseGroup.CIRCLES_HOOKS,
            phoneticCue = "Final /n/ (within, government, inflation)",
            directionHint = "Small final hook on the anti-clockwise / inside side of stem",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Final N-Hook (-n)",
                strokeFormula = "Stem + Small Final Hook",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Final Hook Rule (Anti-Clockwise on Straight Stems, Inside Curves)",
                ruleNote = "Terminates a stroke with a small crisp hook to add N (opinion, plan, sustain, Pakistan). Halving an N-hooked stroke adds T or D (event, grant).",
                legalDefinition = "Closes opinion, Constitution, Pakistan, and petition.",
                glyphKey = "sym_hook_n"
            )
        ),
        PitmanSymbolEntry(
            id = "sym_hook_shun",
            group = SymbolExerciseGroup.CIRCLES_HOOKS,
            phoneticCue = "Final /ʃən/ (-tion, -sion, -cian)",
            directionHint = "Large final hook (twice the size of N/F hooks)",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Final -tion Hook",
                strokeFormula = "Stem + Large Final Hook",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Large Final Hook (Opposite Vowel Side or Inside Curve)",
                ruleNote = "Double the size of a standard N/F hook, representing -tion/-sion/-cian at high speed without extra strokes.",
                legalDefinition = "Terminates jurisdiction, constitution, discretion, legislation, and inflation.",
                glyphKey = "sym_hook_shun"
            )
        ),

        // Core Grammalogues
        PitmanSymbolEntry(
            id = "gram_the",
            group = SymbolExerciseGroup.GRAMMALOGUES,
            phoneticCue = "Word-sign for 'the' (standalone dot or attached tick)",
            directionHint = "Heavy dot on the baseline, or light slanting tick joined to preceding word",
            isHeavy = true,
            spec = StenoStrokeSpec(
                term = "Grammalogue: the",
                strokeFormula = "Heavy Dot (2nd Pos) / Phrased Tick",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Standalone Heavy Dot + Sloping Phrase Tick",
                ruleNote = "Written alone as a firm dot on the baseline (2nd Pos). In phrasing, joined downward or upward at 45° to words like 'of the', 'in the', 'to the'.",
                legalDefinition = "Highest-frequency English grammalogue in courtroom and newspaper prose.",
                glyphKey = "gram_the"
            )
        ),
        PitmanSymbolEntry(
            id = "gram_of",
            group = SymbolExerciseGroup.GRAMMALOGUES,
            phoneticCue = "Word-signs for 'of' (1st Pos) and 'to' (2nd Pos)",
            directionHint = "Light short downward 60° tick placed in 1st position (above line)",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Grammalogue: of / to",
                strokeFormula = "Short Light Tick (1st Pos 'of' / 2nd Pos 'to')",
                position = StenoPosition.FIRST,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Short Slope-Tick Position Contrast",
                ruleNote = "A short light downward tick sloping 60° rightwards: above the line = 'of'; on the line = 'to'. Opposite slope gives 'on/should' vs 'and'.",
                legalDefinition = "Core preposition grammalogues powering legal and editorial phrasing.",
                glyphKey = "gram_of"
            )
        ),
        PitmanSymbolEntry(
            id = "gram_should",
            group = SymbolExerciseGroup.GRAMMALOGUES,
            phoneticCue = "Word-sign for 'should' (upward light tick)",
            directionHint = "Short light upward 30° tick resting on the baseline",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Grammalogue: should / and",
                strokeFormula = "Short Light Upward Tick (2nd Pos 'should')",
                position = StenoPosition.SECOND,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "Upward 30° Grammalogue Tick",
                ruleNote = "Drawn upward on the line for 'should' (and 'and' when above the line). Never confused with downward 'of/to' thanks to its upward writing direction.",
                legalDefinition = "Frequent normative modal in Dawn editorials and appellate directives.",
                glyphKey = "gram_should"
            )
        ),
        PitmanSymbolEntry(
            id = "gram_and",
            group = SymbolExerciseGroup.GRAMMALOGUES,
            phoneticCue = "Word-sign for 'and' (1st Pos upward tick)",
            directionHint = "Short light upward 30° tick placed above the line (1st Pos)",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Grammalogue: and",
                strokeFormula = "Short Light Upward Tick (1st Pos)",
                position = StenoPosition.FIRST,
                category = StenoCategory.GRAMMALOGUE,
                theoryPrinciple = "1st-Position Upward Tick Logogram",
                ruleNote = "Written lightly upward above the line (1st Pos) for 'and', keeping pen travel minimal between noun pairs in newspaper and legal dictation.",
                legalDefinition = "Connects statutory phrases ('principles and rules', 'fiscal and monetary').",
                glyphKey = "gram_and"
            )
        ),

        // Dawn Editorial Contractions
        PitmanSymbolEntry(
            id = "dawn_government",
            group = SymbolExerciseGroup.DAWN_EDITORIAL,
            phoneticCue = "/ˈɡʌvərnmənt/ (Dawn Editorial staple)",
            directionHint = "Heavy horizontal Gay + heavy Vee with final N-hook",
            isHeavy = true,
            spec = StenoStrokeSpec(
                term = "government",
                strokeFormula = "G-V-N (Heavy Contraction)",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Standard Pitman Contraction (Omitted -er-m-t)",
                ruleNote = "Heavy Gay (on the line) flows into heavy downward Vee with a medial/final hook; syllables '-ern-ment' are omitted by settled Pitman convention.",
                legalDefinition = "The executive authority of the federation or a province under the Constitution.",
                glyphKey = "dawn_government"
            )
        ),
        PitmanSymbolEntry(
            id = "dawn_parliament",
            group = SymbolExerciseGroup.DAWN_EDITORIAL,
            phoneticCue = "/ˈpɑːrləmənt/ (parliament / parliamentary)",
            directionHint = "1st-Pos Per-hook + upward El + M halved for T",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "parliament",
                strokeFormula = "P-R-L-M-N-T",
                position = StenoPosition.FIRST,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "1st-Position Pr-Hook + Abbreviated Em-Nt",
                ruleNote = "Per-hook starts above the line (1st Pos broad 'ah'), joins upward El and abbreviated Em-En halved for T.",
                legalDefinition = "The Majlis-e-Shoora (National Assembly and Senate) exercising federal legislative power.",
                glyphKey = "dawn_parliament"
            )
        ),
        PitmanSymbolEntry(
            id = "dawn_economy",
            group = SymbolExerciseGroup.DAWN_EDITORIAL,
            phoneticCue = "/ɪˈkɒnəmi/ (economy / economic)",
            directionHint = "1st-Pos Kay + En + Em with light vowel indicator",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "economy",
                strokeFormula = "K-N-M (1st Pos)",
                position = StenoPosition.FIRST,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Horizontal K-N-M Cadence Outline",
                ruleNote = "Kay-En-Em written briskly in 1st position for the stressed 'o' sound; adding final K produces 'economic'.",
                legalDefinition = "National system of fiscal production, taxation, trade, and monetary stability.",
                glyphKey = "dawn_economy"
            )
        ),
        PitmanSymbolEntry(
            id = "dawn_inflation",
            group = SymbolExerciseGroup.DAWN_EDITORIAL,
            phoneticCue = "/ɪnˈfleɪʃən/ (inflation / inflationary)",
            directionHint = "En + Fl-hook + large final -tion hook on 2nd line",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "inflation",
                strokeFormula = "N-F-L-Sh-N",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Initial En + Fl-Hook + Large -tion Hook",
                ruleNote = "Light horizontal En curves into initial Fl-hook (Ef with L-hook) resting on the baseline and closes with a large right-side -tion hook.",
                legalDefinition = "Sustained increase in consumer price levels eroding real purchasing power.",
                glyphKey = "dawn_inflation"
            )
        ),
        PitmanSymbolEntry(
            id = "dawn_accountability",
            group = SymbolExerciseGroup.DAWN_EDITORIAL,
            phoneticCue = "/əˌkaʊntəˈbɪləti/ (editorial governance term)",
            directionHint = "Halved Kay-N + disjoined Bee for '-bility'",
            isHeavy = true,
            spec = StenoStrokeSpec(
                term = "accountability",
                strokeFormula = "K-N-T + Disjoined B (-bility)",
                position = StenoPosition.THIRD,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Halved K-N-T + Disjoined B (-bility Suffix Rule)",
                ruleNote = "Halved Kay with N-hook writes 'account', followed by a closely disjoined heavy Bee beside it to represent '-ability/-ibility'.",
                legalDefinition = "Institutional obligation of public officials to justify decisions and spending.",
                glyphKey = "dawn_accountability"
            )
        ),
        PitmanSymbolEntry(
            id = "dawn_legislation",
            group = SymbolExerciseGroup.DAWN_EDITORIAL,
            phoneticCue = "/ˌlɛdʒɪsˈleɪʃən/ (statutory & editorial term)",
            directionHint = "Upward El + Jay + S-circle + upward El + -tion hook",
            isHeavy = true,
            spec = StenoStrokeSpec(
                term = "legislation",
                strokeFormula = "L-J-S-L-Sh-N",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Medial Circle + Upward El + -tion Hook",
                ruleNote = "Upward El flows into heavy Jay, turns a crisp S-circle into upward El, and finishes with a large -tion hook.",
                legalDefinition = "Enacted statutory law passed by Parliament or a Provincial Assembly.",
                glyphKey = "dawn_legislation"
            )
        ),
        PitmanSymbolEntry(
            id = "dawn_reform",
            group = SymbolExerciseGroup.DAWN_EDITORIAL,
            phoneticCue = "/rɪˈfɔːrm/ (reform / reforms)",
            directionHint = "Upward Ray + Ef + Ar + Em in 1st position",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "reform",
                strokeFormula = "R-F-R-M",
                position = StenoPosition.FIRST,
                category = StenoCategory.HALVING,
                theoryPrinciple = "Upward Ray + Ef-Ar-Em Outline",
                ruleNote = "Upward Ray joins downward Ef, Ar, and horizontal Em in 1st position; adding a final S-circle converts instantly to 'reforms'.",
                legalDefinition = "Structural policy or institutional improvement in fiscal, judicial, or civil administration.",
                glyphKey = "dawn_reform"
            )
        ),

        // Signature Legal Outlines
        PitmanSymbolEntry(
            id = "legal_learned_counsel",
            group = SymbolExerciseGroup.LEGAL_OUTLINES,
            phoneticCue = "Appellate courtroom intersection",
            directionHint = "Halved L-R-N-D intersected by K-N-S-L",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "learned counsel",
                strokeFormula = "L-R-N-D + K-N-S-L",
                position = StenoPosition.SECOND,
                category = StenoCategory.INTERSECTION,
                theoryPrinciple = "Halving Principle + Judicial Intersection",
                ruleNote = "Upward El-Ar-En halved to add -D, intersected across the stem by Kay-En-S-El (counsel).",
                legalDefinition = "Honorific appellate designation for an advocate appearing before the Court.",
                glyphKey = "learned_counsel"
            )
        ),
        PitmanSymbolEntry(
            id = "legal_jurisdiction",
            group = SymbolExerciseGroup.LEGAL_OUTLINES,
            phoneticCue = "3rd-Position Jay through the baseline",
            directionHint = "Heavy Jay through line + S-circle + K + -tion hook",
            isHeavy = true,
            spec = StenoStrokeSpec(
                term = "jurisdiction",
                strokeFormula = "J-R-S-D-K-Sh-N",
                position = StenoPosition.THIRD,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "3rd-Position Placement + Medial Circle + -tion Hook",
                ruleNote = "Heavy downward Jay passes through the ruled line (3rd Pos for 'oo'), joining Ar, S-circle, Kay, and final -tion hook.",
                legalDefinition = "Lawful authority conferred upon a court or tribunal to hear and adjudicate a dispute.",
                glyphKey = "jurisdiction"
            )
        ),
        PitmanSymbolEntry(
            id = "legal_constitution",
            group = SymbolExerciseGroup.LEGAL_OUTLINES,
            phoneticCue = "Omitted 'con-' dot + medial Stee loop",
            directionHint = "Horizontal Kay + Stee loop + halved Tee + -tion hook",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "Constitution",
                strokeFormula = "K-N-S-T-Sh-N",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Medial Stee Loop + Special Contraction",
                ruleNote = "Initial 'con-' dot is omitted; Kay joins a crisp Stee loop and final -tion hook on the baseline.",
                legalDefinition = "The supreme law of the Islamic Republic of Pakistan establishing Fundamental Rights.",
                glyphKey = "constitution"
            )
        ),
        PitmanSymbolEntry(
            id = "legal_high_court",
            group = SymbolExerciseGroup.LEGAL_OUTLINES,
            phoneticCue = "1st-Position H intersected by K",
            directionHint = "Upward circled Hay above line intersected by horizontal Kay",
            isHeavy = false,
            spec = StenoStrokeSpec(
                term = "learned High Court",
                strokeFormula = "H-K",
                position = StenoPosition.FIRST,
                category = StenoCategory.INTERSECTION,
                theoryPrinciple = "1st-Position Courtroom Intersection (H-K)",
                ruleNote = "Upward Hay placed in 1st position for diphthong 'i', intersected perpendicularly by Kay (Court).",
                legalDefinition = "Principal constitutional and appellate court of a province exercising writ jurisdiction.",
                glyphKey = "high_court"
            )
        )
    )

    fun entriesFor(group: SymbolExerciseGroup, query: String = ""): List<PitmanSymbolEntry> {
        val q = query.trim()
        return entries.filter { entry ->
            val matchesGroup = group == SymbolExerciseGroup.ALL || entry.group == group
            val matchesQuery = q.isEmpty() ||
                entry.spec.term.contains(q, ignoreCase = true) ||
                entry.spec.strokeFormula.contains(q, ignoreCase = true) ||
                entry.phoneticCue.contains(q, ignoreCase = true) ||
                entry.spec.theoryPrinciple.contains(q, ignoreCase = true)
            matchesGroup && matchesQuery
        }
    }

    fun drillOptionsFor(target: PitmanSymbolEntry, seedIndex: Int): List<PitmanSymbolEntry> {
        val sameGroup = entries.filter { it.id != target.id && it.group == target.group }
        val otherPool = entries.filter { it.id != target.id && it.group != target.group }
        val distractors = (sameGroup + otherPool).take(3)
        val combined = (listOf(target) + distractors).toMutableList()
        val shift = seedIndex % combined.size
        return combined.drop(shift) + combined.take(shift)
    }
}
