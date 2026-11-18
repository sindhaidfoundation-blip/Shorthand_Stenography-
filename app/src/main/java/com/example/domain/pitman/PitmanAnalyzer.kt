package com.example.domain.pitman

import com.example.domain.model.PassageHighlight
import com.example.domain.model.StenoAnalysis
import com.example.domain.model.StenoCategory
import com.example.domain.model.StenoPosition
import com.example.domain.model.StenoStrokeSpec
import kotlin.math.ceil
import kotlin.math.max

object PitmanAnalyzer {

    private data class LexiconRule(
        val aliases: List<String>,
        val spec: StenoStrokeSpec
    )

    private val legalRules: List<LexiconRule> = listOf(
        LexiconRule(
            aliases = listOf("learned counsel"),
            spec = StenoStrokeSpec(
                term = "learned counsel",
                strokeFormula = "L-R-N-D + K-N-S-L",
                position = StenoPosition.SECOND,
                category = StenoCategory.INTERSECTION,
                theoryPrinciple = "Halving Principle + Judicial Intersection",
                ruleNote = "Upward El-Ar-En is halved to add -D (learned), then intersected across the stem by Kay-En-S-El (counsel) to eliminate pen-lift gaps at courtroom speeds.",
                legalDefinition = "Honorific appellate designation for an advocate or barrister appearing before the Court to argue a party's case.",
                glyphKey = "learned_counsel"
            )
        ),
        LexiconRule(
            aliases = listOf("learned high court", "high court"),
            spec = StenoStrokeSpec(
                term = "learned High Court",
                strokeFormula = "H-K",
                position = StenoPosition.FIRST,
                category = StenoCategory.INTERSECTION,
                theoryPrinciple = "1st-Position Courtroom Intersection (H-K)",
                ruleNote = "Upward Hay (High) is placed in 1st position for the diphthong 'i', intersected perpendicularly by Kay (Court) as a fixed judicial abbreviation.",
                legalDefinition = "The principal constitutional and appellate court of a province exercising writ (Art. 199), revisional, and appellate jurisdiction.",
                glyphKey = "high_court"
            )
        ),
        LexiconRule(
            aliases = listOf("supreme court"),
            spec = StenoStrokeSpec(
                term = "Supreme Court",
                strokeFormula = "S-P-R-M + K-R-T",
                position = StenoPosition.SECOND,
                category = StenoCategory.INTERSECTION,
                theoryPrinciple = "Double-Consonant Phrase + Halved Intersection",
                ruleNote = "Initial S-circle attaches to Per-Em (Supreme), intersected by Kay-Ar halved for T (Court) on the baseline.",
                legalDefinition = "The apex constitutional court of Pakistan whose decisions on questions of law bind all subordinate courts under Article 189.",
                glyphKey = "supreme_court"
            )
        ),
        LexiconRule(
            aliases = listOf("petitioner's", "petitioner", "petitioners"),
            spec = StenoStrokeSpec(
                term = "petitioner",
                strokeFormula = "P-T",
                position = StenoPosition.SECOND,
                category = StenoCategory.INTERSECTION,
                theoryPrinciple = "Abbreviated Legal Contraction (Omitted -tion)",
                ruleNote = "Light downward Pee intersected by Tee (P-T) represents 'petition/petitioner' in rapid legal reporting; the medial -tion hook is omitted by convention.",
                legalDefinition = "The party who initiates a constitutional writ, civil revision, or special leave petition seeking judicial relief from the Court.",
                glyphKey = "petitioner"
            )
        ),
        LexiconRule(
            aliases = listOf("jurisdiction", "jurisdictional"),
            spec = StenoStrokeSpec(
                term = "jurisdiction",
                strokeFormula = "J-R-S-D-K-Sh-N",
                position = StenoPosition.THIRD,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "3rd-Position Vowel Placement + Medial Circle + -tion Hook",
                ruleNote = "Heavy downward Jay passes through the ruled line (3rd Pos for 'oo'), flowing into Ar, medial S-circle, Kay, and a large right-side -tion hook.",
                legalDefinition = "The lawful authority conferred upon a court or statutory tribunal to hear, try, and adjudicate a dispute or grant relief.",
                glyphKey = "jurisdiction"
            )
        ),
        LexiconRule(
            aliases = listOf("constitution", "constitutional"),
            spec = StenoStrokeSpec(
                term = "Constitution",
                strokeFormula = "K-N-S-T-Sh-N",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Medial Stee Loop + Special Contraction",
                ruleNote = "Omission of initial 'con-' dot in special contraction; Kay joins a crisp Stee loop and final -tion hook resting on the 2nd-position baseline.",
                legalDefinition = "The supreme fundamental law of the Islamic Republic of Pakistan establishing state organs, federalism, and Fundamental Rights.",
                glyphKey = "constitution"
            )
        ),
        LexiconRule(
            aliases = listOf("well-settled principle", "well settled principle"),
            spec = StenoStrokeSpec(
                term = "well-settled principle",
                strokeFormula = "W-L-S-T-L-D + P-R-N-S-P-L",
                position = StenoPosition.SECOND,
                category = StenoCategory.PHRASING,
                theoryPrinciple = "Joined Appellate Phrasing + Pr-Hook Contraction",
                ruleNote = "Way-El-S-Tee-El halved for D joins directly without lifting the pen into the standard Per-En-S-Pee-El contraction for 'principle'.",
                legalDefinition = "An authoritative legal rule established by consistent judicial precedent that lower forums are bound to follow.",
                glyphKey = "well_settled"
            )
        ),
        LexiconRule(
            aliases = listOf("principles of natural justice", "natural justice"),
            spec = StenoStrokeSpec(
                term = "principles of natural justice",
                strokeFormula = "P-R-S-P-L-Z + N-Ch-R-L + J-S",
                position = StenoPosition.SECOND,
                category = StenoCategory.PHRASING,
                theoryPrinciple = "Omission of 'of' + Juxtaposed Legal Phrase",
                ruleNote = "The connective 'of' is omitted by phrasing 'principles' directly into En-Cher-El (natural) and Jay-Es (justice) on the 2nd-position line.",
                legalDefinition = "Core rules of procedural fairness: no one shall be a judge in their own cause (nemo judex) and both sides must be heard (audi alteram partem).",
                glyphKey = "natural_justice"
            )
        ),
        LexiconRule(
            aliases = listOf("fundamental rights", "fundamental right"),
            spec = StenoStrokeSpec(
                term = "fundamental rights",
                strokeFormula = "F-N-D-M-N-T-L + R-T-S",
                position = StenoPosition.FIRST,
                category = StenoCategory.PHRASING,
                theoryPrinciple = "1st-Position Halving + Phrased Plural Circle",
                ruleNote = "Ef with En-hook is halved for D and abbreviated before upward Ray halved for T with a final S-circle in 1st position (diphthong 'i' in rights).",
                legalDefinition = "Inalienable constitutional protections (Articles 8–28) enforceable directly against state action by the High Courts and Supreme Court.",
                glyphKey = "fundamental_rights"
            )
        ),
        LexiconRule(
            aliases = listOf("due process"),
            spec = StenoStrokeSpec(
                term = "due process",
                strokeFormula = "D + P-R-S-S",
                position = StenoPosition.THIRD,
                category = StenoCategory.PHRASING,
                theoryPrinciple = "3rd-Position Diphthong + Large Ses-Circle",
                ruleNote = "Heavy Dee through the line (3rd Pos for 'ew') links straight into Per-hook and a large final Ses-circle representing '-cess'.",
                legalDefinition = "Constitutional guarantee under Article 10-A requiring fair notice, impartial adjudication, and adherence to established legal procedure.",
                glyphKey = "due_process"
            )
        ),
        LexiconRule(
            aliases = listOf("revision petition", "constitutional petition"),
            spec = StenoStrokeSpec(
                term = "revision petition",
                strokeFormula = "R-V-Sh-N + P-T",
                position = StenoPosition.SECOND,
                category = StenoCategory.INTERSECTION,
                theoryPrinciple = "-tion Hook + P-T Legal Intersection",
                ruleNote = "Ray-Vee ends with a -tion hook and is immediately intersected by the P-T contraction to mark a formal court filing.",
                legalDefinition = "A statutory application invoking supervisory review of a subordinate court's order for jurisdictional error or material irregularity.",
                glyphKey = "petitioner"
            )
        ),
        LexiconRule(
            aliases = listOf("statutory tribunals", "tribunals", "tribunal"),
            spec = StenoStrokeSpec(
                term = "tribunal",
                strokeFormula = "T-R-B-N-L",
                position = StenoPosition.FIRST,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Initial Tr-Hook + Final En-Hook & Upward El",
                ruleNote = "Tee with R-hook flows into heavy Bee, medial En-hook, and upward El, with 1st-position placement for the stressed diphthong.",
                legalDefinition = "A specialized quasi-judicial body created by statute to adjudicate regulatory, tax, service, or administrative disputes.",
                glyphKey = "tribunal"
            )
        ),
        LexiconRule(
            aliases = listOf("discretion", "discretionary"),
            spec = StenoStrokeSpec(
                term = "discretion",
                strokeFormula = "D-S-K-R-Sh-N",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Medial S-Circle + Kr-Hook + -tion Hook",
                ruleNote = "Heavy Dee joins S-circle turned inside the Kr-hook curve, finishing with a large -tion hook on the opposite side of the stroke.",
                legalDefinition = "Power conferred on a court or official to choose among lawful courses of action based on sound judicial reason rather than whim.",
                glyphKey = "discretion"
            )
        ),
        LexiconRule(
            aliases = listOf("adjudication", "adjudicate"),
            spec = StenoStrokeSpec(
                term = "adjudication",
                strokeFormula = "J-D-K-Sh-N",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Unvocalized Legal Outline + Final -tion Hook",
                ruleNote = "Initial unaccented 'ad-' is dropped in high-speed reporting; heavy Jay-Dee-Kay terminates with a crisp -tion hook.",
                legalDefinition = "The formal judicial process of resolving a legal controversy and pronouncing a binding judgment or decree.",
                glyphKey = "jurisdiction"
            )
        ),
        LexiconRule(
            aliases = listOf("impugned judgment", "judgment"),
            spec = StenoStrokeSpec(
                term = "impugned judgment",
                strokeFormula = "M-P-G-N-D + J-J-M-N-T",
                position = StenoPosition.SECOND,
                category = StenoCategory.PHRASING,
                theoryPrinciple = "Halved M-P-G-N-D + Double-Jay Contraction",
                ruleNote = "Em-Pee-Gay-En halved for D joins smoothly into Pitman's iconic Jay-Jay contraction (judgment) on the line.",
                legalDefinition = "The contested judicial decision or order of a lower court challenged before an appellate or revisional forum.",
                glyphKey = "judgment"
            )
        ),
        LexiconRule(
            aliases = listOf("benefit of doubt"),
            spec = StenoStrokeSpec(
                term = "benefit of doubt",
                strokeFormula = "B-N-F-T + D-T",
                position = StenoPosition.SECOND,
                category = StenoCategory.PHRASING,
                theoryPrinciple = "Omitted 'of' + Halved Heavy Dee",
                ruleNote = "Bee-En-Ef-Tee links directly to heavy Dee halved for T (doubt), omitting 'of' as a standard criminal-trial phraseogram.",
                legalDefinition = "Mandatory criminal-law rule requiring acquittal whenever a single reasonable doubt dents the prosecution's case.",
                glyphKey = "benefit_doubt"
            )
        ),
        LexiconRule(
            aliases = listOf("prosecution"),
            spec = StenoStrokeSpec(
                term = "prosecution",
                strokeFormula = "P-R-S-K-Sh-N",
                position = StenoPosition.FIRST,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Pr-Hook + Medial S-Circle + -tion Hook",
                ruleNote = "Per-hook descends from 1st position, turns an S-circle into horizontal Kay, and closes with a right-side -tion hook.",
                legalDefinition = "The State or complainant party conducting criminal proceedings against an accused person before a trial court.",
                glyphKey = "prosecution"
            )
        ),
        LexiconRule(
            aliases = listOf("specific performance"),
            spec = StenoStrokeSpec(
                term = "specific performance",
                strokeFormula = "S-P-S-F-K + P-R-F",
                position = StenoPosition.SECOND,
                category = StenoCategory.INTERSECTION,
                theoryPrinciple = "S-P-S-F-K Contraction + Per-Ef Intersection",
                ruleNote = "S-Pee-S-Ef-Kay is intersected by Per-Ef (performance) for rapid equity and civil contract dictation.",
                legalDefinition = "An equitable decree under the Specific Relief Act compelling a party to fulfill the exact terms of a valid contract.",
                glyphKey = "specific_performance"
            )
        ),
        LexiconRule(
            aliases = listOf("locus poenitentiae"),
            spec = StenoStrokeSpec(
                term = "locus poenitentiae",
                strokeFormula = "L-K-S + P-N-T-N-Sh",
                position = StenoPosition.SECOND,
                category = StenoCategory.PHRASING,
                theoryPrinciple = "Phonetic Latin Phrasing + Halved Penultimate",
                ruleNote = "Written phonetically by sound rather than spelling: El-Kay-Es followed by Pee-En-Tee halved with final Ish-vowel.",
                legalDefinition = "Doctrine permitting an authority to withdraw an order before it takes effect, but barring rescission once decisive steps create vested rights.",
                glyphKey = "natural_justice"
            )
        ),
        LexiconRule(
            aliases = listOf("arbitral award", "arbitration"),
            spec = StenoStrokeSpec(
                term = "arbitral award",
                strokeFormula = "R-B-T-R-L + W-R-D",
                position = StenoPosition.FIRST,
                category = StenoCategory.INTERSECTION,
                theoryPrinciple = "Doubling Principle (-trl) + Halved W-R-D",
                ruleNote = "Downward Ar-Bee-Tee is doubled in length to express '-tral/-ter', paired with halved Way-Ar for 'award'.",
                legalDefinition = "The binding final decision rendered by an arbitral tribunal resolving a commercial or contractual dispute.",
                glyphKey = "arbitral_award"
            )
        ),
        LexiconRule(
            aliases = listOf("respondent", "appellant"),
            spec = StenoStrokeSpec(
                term = "respondent",
                strokeFormula = "R-S-P-N-D-N-T",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Upward Ray + S-Pee + Halved En-D-En-T",
                ruleNote = "Upward Ray with S-circle joins Pee and abbreviated halved En-D-En-T to distinguish appellant/respondent cleanly.",
                legalDefinition = "The opposing party who answers an appeal, writ, or revision petition filed before an appellate court.",
                glyphKey = "petitioner"
            )
        ),
        LexiconRule(
            aliases = listOf("islamic republic of pakistan", "pakistan"),
            spec = StenoStrokeSpec(
                term = "Pakistan",
                strokeFormula = "P-K-S-T-N",
                position = StenoPosition.FIRST,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Proper-Noun Double Underline + En-Hook",
                ruleNote = "Pee-Kay-S-Tee with final En-hook placed in 1st position; marked by two short ticks underneath to denote a proper noun.",
                legalDefinition = "The Islamic Republic of Pakistan, governed by the 1973 Constitution and a federal parliamentary judicial structure.",
                glyphKey = "constitution"
            )
        ),
        LexiconRule(
            aliases = listOf("governments", "government"),
            spec = StenoStrokeSpec(
                term = "government",
                strokeFormula = "G-V-N (Contraction)",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Standard Pitman Contraction (Omitted -ern-ment)",
                ruleNote = "Heavy horizontal Gay flows into heavy downward Vee with final N-hook; syllables '-ern-ment' are omitted by settled Pitman rule.",
                legalDefinition = "Executive administration of the state or province accountable to parliament and constitutional law.",
                glyphKey = "dawn_government"
            )
        ),
        LexiconRule(
            aliases = listOf("parliamentary", "parliament"),
            spec = StenoStrokeSpec(
                term = "parliament",
                strokeFormula = "P-R-L-M-N-T",
                position = StenoPosition.FIRST,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "1st-Position Pr-Hook + Abbreviated Em-Nt",
                ruleNote = "Per-hook starts above the line (1st Pos 'ah'), joins upward El and abbreviated Em-En halved for T.",
                legalDefinition = "The federal legislative assembly exercising law-making, budgetary, and oversight powers.",
                glyphKey = "dawn_parliament"
            )
        ),
        LexiconRule(
            aliases = listOf("macroeconomic", "economic", "economy"),
            spec = StenoStrokeSpec(
                term = "economy",
                strokeFormula = "K-N-M (1st Pos)",
                position = StenoPosition.FIRST,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "1st-Position Horizontal K-N-M Contraction",
                ruleNote = "Kay-En-Em written briskly in 1st position for the stressed 'o' vowel; adding final Kay distinguishes 'economic'.",
                legalDefinition = "National system of fiscal revenue, monetary stability, production, and trade.",
                glyphKey = "dawn_economy"
            )
        ),
        LexiconRule(
            aliases = listOf("inflationary", "inflation"),
            spec = StenoStrokeSpec(
                term = "inflation",
                strokeFormula = "N-F-L-Sh-N",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Initial En + Fl-Hook + Final -tion Hook",
                ruleNote = "Horizontal En joins initial Fl-hook (Ef with L-hook) on the baseline and terminates with a large right-side -tion hook.",
                legalDefinition = "Sustained rise in consumer prices eroding household purchasing power and monetary stability.",
                glyphKey = "dawn_inflation"
            )
        ),
        LexiconRule(
            aliases = listOf("accountability"),
            spec = StenoStrokeSpec(
                term = "accountability",
                strokeFormula = "K-N-T + Disjoined B (-bility)",
                position = StenoPosition.THIRD,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Halved K-N-T + Disjoined B (-bility Suffix)",
                ruleNote = "Halved Kay with N-hook writes 'account', followed closely by a disjoined heavy Bee beside it for '-bility'.",
                legalDefinition = "Constitutional and public obligation to explain executive conduct and fiscal spending transparently.",
                glyphKey = "dawn_accountability"
            )
        ),
        LexiconRule(
            aliases = listOf("legislation", "legislative"),
            spec = StenoStrokeSpec(
                term = "legislation",
                strokeFormula = "L-J-S-L-Sh-N",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Medial S-Circle + Upward El + -tion Hook",
                ruleNote = "Upward El connects to heavy Jay, turns a medial S-circle into upward El, and finishes with a large -tion hook.",
                legalDefinition = "Statutes and enactments debated and passed by Parliament or a Provincial Assembly.",
                glyphKey = "dawn_legislation"
            )
        ),
        LexiconRule(
            aliases = listOf("monetary policy"),
            spec = StenoStrokeSpec(
                term = "monetary policy",
                strokeFormula = "M-N-T-R + P-L-S",
                position = StenoPosition.SECOND,
                category = StenoCategory.PHRASING,
                theoryPrinciple = "Doubled En-Tr + Joined P-L-S Phrase",
                ruleNote = "Em-En doubled for '-ter' (monetary) joins directly into Pee-El-Es (policy) for rapid economic reporting.",
                legalDefinition = "Central bank management of policy interest rates, liquidity, and price stability.",
                glyphKey = "dawn_economy"
            )
        ),
        LexiconRule(
            aliases = listOf("fiscal deficit", "fiscal"),
            spec = StenoStrokeSpec(
                term = "fiscal deficit",
                strokeFormula = "F-S-K-L + D-F-S-T",
                position = StenoPosition.THIRD,
                category = StenoCategory.PHRASING,
                theoryPrinciple = "Medial Circle + Halved F-S-T Contraction",
                ruleNote = "Ef-S-Kay-El pairs with heavy Dee-Ef-S-Tee halved for final T to record budget gaps cleanly at 120+ WPM.",
                legalDefinition = "Shortfall between total government expenditure and non-borrowed state revenue.",
                glyphKey = "dawn_economy"
            )
        ),
        LexiconRule(
            aliases = listOf("sustainable", "unsustainable"),
            spec = StenoStrokeSpec(
                term = "sustainable",
                strokeFormula = "S-S-T-N-B-L",
                position = StenoPosition.SECOND,
                category = StenoCategory.CONTRACTION,
                theoryPrinciple = "Initial Stee/Ses Loop + N-Hook + Bl-Hook",
                ruleNote = "Large initial Ses/Stee joins Tee-En and Bee with L-hook cleanly along the baseline.",
                legalDefinition = "Fiscal, environmental, or institutional policy durable over the long term without crisis.",
                glyphKey = "dawn_reform"
            )
        ),
        LexiconRule(
            aliases = listOf("infrastructure"),
            spec = StenoStrokeSpec(
                term = "infrastructure",
                strokeFormula = "N-F-R-S-T-R-K-Ch-R",
                position = StenoPosition.FIRST,
                category = StenoCategory.HALVING,
                theoryPrinciple = "Doubling Principle (-tr / -ture)",
                ruleNote = "En-Ef-Ar-Stee-Kay followed by doubled Chay for '-ture' compresses a five-syllable policy word into one fluid outline.",
                legalDefinition = "Physical and digital national backbone: energy grids, irrigation canals, highways, and courts.",
                glyphKey = "dawn_legislation"
            )
        ),
        LexiconRule(
            aliases = listOf("public interest"),
            spec = StenoStrokeSpec(
                term = "public interest",
                strokeFormula = "P-B + N-T-R-S-T",
                position = StenoPosition.SECOND,
                category = StenoCategory.PHRASING,
                theoryPrinciple = "P-B Contraction + Halved Stee Phrase",
                ruleNote = "Standard P-B contraction (public) joins straight into En-Tee-Ray-Stee (interest) in one continuous motion.",
                legalDefinition = "General welfare of citizens guiding Article 184(3) jurisdiction and Dawn editorial policy.",
                glyphKey = "dawn_parliament"
            )
        ),
        LexiconRule(
            aliases = listOf("democratic"),
            spec = StenoStrokeSpec(
                term = "democratic",
                strokeFormula = "D-M-K-R-T-K",
                position = StenoPosition.SECOND,
                category = StenoCategory.HALVING,
                theoryPrinciple = "Kr-Hook + Halved Tee-Kay",
                ruleNote = "Heavy Dee-Em joins Kr-hook and halved Tee followed by Kay on the 2nd-position line.",
                legalDefinition = "Constitutional governance anchored in elected assemblies and public accountability.",
                glyphKey = "dawn_parliament"
            )
        ),
        LexiconRule(
            aliases = listOf("reforming", "reforms", "reform"),
            spec = StenoStrokeSpec(
                term = "reform",
                strokeFormula = "R-F-R-M",
                position = StenoPosition.FIRST,
                category = StenoCategory.HALVING,
                theoryPrinciple = "1st-Pos Upward Ray + Ef-Ar-Em",
                ruleNote = "Upward Ray joins downward Ef, Ar, and horizontal Em in 1st position; final S-circle converts to 'reforms'.",
                legalDefinition = "Structural institutional or fiscal overhaul advocated in policy and editorial commentary.",
                glyphKey = "dawn_reform"
            )
        ),
        LexiconRule(
            aliases = listOf("foreign policy"),
            spec = StenoStrokeSpec(
                term = "foreign policy",
                strokeFormula = "F-R-N + P-L-S",
                position = StenoPosition.FIRST,
                category = StenoCategory.PHRASING,
                theoryPrinciple = "1st-Position F-R-N + P-L-S Phraseogram",
                ruleNote = "Ef-Ar-En in 1st position joins downward Pee-El-Es (policy) for diplomatic and editorial reporting.",
                legalDefinition = "State strategy governing external treaties, bilateral trade, and regional diplomacy.",
                glyphKey = "dawn_government"
            )
        )
    )

    private val grammalogues: Set<String> = setOf(
        "the", "of", "and", "to", "in", "is", "it", "that", "for", "be", "are", "by",
        "with", "under", "their", "shall", "which", "this", "on", "from", "not", "but",
        "have", "was", "were", "has", "had", "can", "could", "will", "would", "may",
        "might", "such", "all", "any", "more", "most", "other", "upon", "within",
        "without", "there", "where", "who", "whose", "an", "a", "as", "at", "or",
        "its", "them", "they", "been", "being", "if", "so", "no", "nor",
        "into", "over", "must", "should", "whether", "however", "therefore", "first"
    )

    private val extraLegalContractions: Set<String> = setOf(
        "petitioner", "petitioner's", "petitioners", "jurisdiction", "constitution",
        "constitutional", "judgment", "tribunals", "tribunal", "discretion",
        "adjudication", "appellate", "statutory", "fundamental", "legitimate",
        "expectation", "expeditious", "respondent", "appellant", "prosecution",
        "conviction", "acquittal", "arbitration", "enforceable", "doctrine",
        "precedent", "mandate", "arbitrary", "contends", "impugned", "violates",
        "guaranteed", "evidence", "corroboration", "limitation", "estoppel",
        "government", "governments", "parliament", "parliamentary", "economy",
        "economic", "macroeconomic", "inflation", "inflationary", "accountability",
        "legislation", "legislative", "infrastructure", "sustainable", "unsustainable",
        "democratic", "monetary", "fiscal", "regulatory", "transparency", "reform", "reforms"
    )

    private val halvedDoubledSuffixes = listOf(
        "ed", "nt", "nd", "ld", "rd", "pt", "kt", "ft", "ter", "der", "ther", "tor", "ture"
    )

    fun analyze(rawText: String): StenoAnalysis {
        val text = rawText.trim()
        if (text.isEmpty()) {
            return StenoAnalysis(
                wordCount = 0,
                charCount = 0,
                secondsAt80Wpm = 0,
                secondsAt100Wpm = 0,
                secondsAt120Wpm = 0,
                secondsAt140Wpm = 0,
                grammalogueCount = 0,
                grammalogueSamples = emptyList(),
                legalContractionCount = 0,
                contractionSamples = emptyList(),
                intersectionCount = 0,
                intersectionSamples = emptyList(),
                halvedDoubledCount = 0,
                complexityLevel = 1,
                complexityLabel = "Level 1: Foundation Drills",
                highlights = emptyList(),
                featuredStems = defaultFeaturedStems(),
                allDetectedOutlines = emptyList()
            )
        }

        val words = text.split(Regex("\\s+")).filter { it.isNotBlank() }
        val wordCount = words.size
        val charCount = text.length

        val sec80 = max(1, ceil(wordCount * 60.0 / 80.0).toInt())
        val sec100 = max(1, ceil(wordCount * 60.0 / 100.0).toInt())
        val sec120 = max(1, ceil(wordCount * 60.0 / 120.0).toInt())
        val sec140 = max(1, ceil(wordCount * 60.0 / 140.0).toInt())

        val normalizedWords = words.map {
            it.lowercase().trim(',', '.', ';', ':', '"', '\'', '(', ')', '“', '”', '—', '-')
        }.filter { it.isNotEmpty() }

        val matchedGrammalogues = normalizedWords.filter { it in grammalogues }
        val matchedExtraContractions = normalizedWords.filter { it in extraLegalContractions }

        val lowerText = text.lowercase()
        val candidateHighlights = mutableListOf<PassageHighlight>()

        val sortedRules = legalRules.flatMap { rule ->
            rule.aliases.map { alias -> alias to rule.spec }
        }.sortedByDescending { it.first.length }

        for ((alias, spec) in sortedRules) {
            var searchFrom = 0
            while (searchFrom < lowerText.length) {
                val idx = lowerText.indexOf(alias, startIndex = searchFrom)
                if (idx < 0) break
                val endIdx = idx + alias.length
                val validStart = idx == 0 || !lowerText[idx - 1].isLetterOrDigit()
                val validEnd = endIdx == lowerText.length || !lowerText[endIdx].isLetterOrDigit()
                if (validStart && validEnd) {
                    val overlaps = candidateHighlights.any { existing ->
                        idx < existing.endIndex && endIdx > existing.startIndex
                    }
                    if (!overlaps) {
                        candidateHighlights.add(
                            PassageHighlight(
                                startIndex = idx,
                                endIndex = endIdx,
                                matchedText = text.substring(idx, endIdx),
                                spec = spec
                            )
                        )
                    }
                }
                searchFrom = idx + alias.length
            }
        }

        val sortedHighlights = candidateHighlights.sortedBy { it.startIndex }

        val intersectionHighlights = sortedHighlights.filter {
            it.spec.category == StenoCategory.INTERSECTION || it.spec.category == StenoCategory.PHRASING
        }
        val contractionHighlights = sortedHighlights.filter {
            it.spec.category == StenoCategory.CONTRACTION
        }

        val intersectionCount = max(
            intersectionHighlights.size,
            if (wordCount > 20) (intersectionHighlights.size + (wordCount / 18)) else intersectionHighlights.size
        )
        val legalContractionCount = max(
            matchedExtraContractions.size,
            contractionHighlights.size
        )

        val halvedCount = normalizedWords.count { w ->
            w.length >= 4 && halvedDoubledSuffixes.any { suffix -> w.endsWith(suffix) }
        }

        val detectedSpecs = sortedHighlights.map { it.spec }.distinctBy { it.term }
        val featuredStems = if (detectedSpecs.isNotEmpty()) {
            val combined = detectedSpecs.toMutableList()
            for (fallback in defaultFeaturedStems()) {
                if (combined.size >= 4) break
                if (combined.none { it.glyphKey == fallback.glyphKey }) {
                    combined.add(fallback)
                }
            }
            combined.take(4)
        } else {
            buildCustomStems(normalizedWords)
        }

        val complexityScore = (legalContractionCount * 2 + intersectionHighlights.size * 3 + halvedCount)
        val (level, levelLabel) = when {
            complexityScore >= 24 -> 5 to "Level 5: Constitutional & Courtroom Phrasing"
            complexityScore >= 14 -> 4 to "Level 4: Appellate Advocacy & Intersections"
            complexityScore >= 7 -> 3 to "Level 3: Intermediate Halving & Contractions"
            complexityScore >= 3 -> 2 to "Level 2: Core Legal Grammalogues"
            else -> 1 to "Level 1: Plain Stenographic Cadence"
        }

        return StenoAnalysis(
            wordCount = wordCount,
            charCount = charCount,
            secondsAt80Wpm = sec80,
            secondsAt100Wpm = sec100,
            secondsAt120Wpm = sec120,
            secondsAt140Wpm = sec140,
            grammalogueCount = matchedGrammalogues.size,
            grammalogueSamples = matchedGrammalogues.distinct().take(6),
            legalContractionCount = legalContractionCount,
            contractionSamples = (matchedExtraContractions + contractionHighlights.map { it.spec.term }).distinct().take(5),
            intersectionCount = intersectionCount,
            intersectionSamples = intersectionHighlights.map { it.spec.term }.distinct().take(5),
            halvedDoubledCount = halvedCount,
            complexityLevel = level,
            complexityLabel = levelLabel,
            highlights = sortedHighlights,
            featuredStems = featuredStems,
            allDetectedOutlines = detectedSpecs.ifEmpty { featuredStems }
        )
    }

    private fun defaultFeaturedStems(): List<StenoStrokeSpec> =
        legalRules.take(4).map { it.spec }

    private fun buildCustomStems(words: List<String>): List<StenoStrokeSpec> {
        val candidates = words
            .filter { it.length >= 4 && it !in grammalogues }
            .distinct()
            .sortedByDescending { it.length }
            .take(4)

        if (candidates.isEmpty()) return defaultFeaturedStems()

        val glyphPool = listOf("learned_counsel", "jurisdiction", "petitioner", "constitution")
        return candidates.mapIndexed { idx, word ->
            val consonants = word.uppercase()
                .filter { it in "BCDFGHJKLMNPQRSTVWXYZ" }
                .take(6)
                .toCharArray()
                .joinToString("-")
                .ifEmpty { word.uppercase() }
            val position = when (idx % 3) {
                0 -> StenoPosition.FIRST
                1 -> StenoPosition.SECOND
                else -> StenoPosition.THIRD
            }
            StenoStrokeSpec(
                term = word,
                strokeFormula = consonants,
                position = position,
                category = StenoCategory.HALVING,
                theoryPrinciple = "Phonetic Consonant Skeleton (${position.badge})",
                ruleNote = "Consonant skeleton ($consonants) placed in ${position.badge} based on the primary stressed vowel sound.",
                legalDefinition = "Custom passage term analyzed dynamically for phonetic Pitman stroke construction.",
                glyphKey = glyphPool[idx % glyphPool.size]
            )
        }
    }
}
