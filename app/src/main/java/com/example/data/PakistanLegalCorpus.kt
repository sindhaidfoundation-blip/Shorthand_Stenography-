package com.example.data

import com.example.domain.model.LegalCasePassage

object PakistanLegalCorpus {

    val cases: List<LegalCasePassage> = listOf(
        LegalCasePassage(
            id = "pld_2023_sc_542",
            citation = "PLD 2023 SC 542",
            court = "Supreme Court of Pakistan",
            category = "Constitutional",
            subtitle = "Constitutional Petition • Fundamental Rights & Due Process (Art. 10-A)",
            isVerified = true,
            text = "\"The petitioner's learned counsel strenuously contends that the impugned judgment of the learned High Court violates fundamental rights guaranteed under the Constitution of the Islamic Republic of Pakistan. It is a well-settled principle of law that statutory tribunals must exercise their jurisdiction within the four corners of law without arbitrary exercise of discretion. The doctrine of legitimate expectation and principles of natural justice, audi alteram partem, are an integral part of due process under Article 10-A. The revision petition is accordingly disposed of with direct instructions to the appellate forum for expeditious adjudication on merits.\"",
            summary = "Reaffirms that Article 10-A of the Constitution embeds audi alteram partem and natural justice into every statutory adjudication. Quasi-judicial tribunals cannot act arbitrarily beyond their statutory jurisdiction, and appellate forums must decide revision petitions expeditiously on substantive merits.",
            keyPrinciples = listOf(
                "Article 10-A guarantees fair trial and due process across judicial and quasi-judicial tribunals.",
                "Statutory discretion must remain bounded within the four corners of enabling legislation.",
                "Doctrine of legitimate expectation protects procedural fairness where consistent practice exists."
            )
        ),
        LegalCasePassage(
            id = "2024_scmr_118",
            citation = "2024 SCMR 118",
            court = "Supreme Court of Pakistan",
            category = "Criminal",
            subtitle = "Criminal Appeal • Appraisal of Evidence & Benefit of Doubt",
            isVerified = true,
            text = "\"It is an axiomatic principle of criminal jurisprudence that the prosecution must establish its case beyond a shadow of reasonable doubt. Where a single circumstance creates a reasonable dent in the prosecution story, the accused is entitled to the benefit of doubt not as a matter of grace, but as a matter of right. The learned High Court failed to appreciate material contradictions in ocular testimony and medical evidence. Consequently, the impugned judgment of conviction is set aside and the appellant is acquitted of all charges forthwith.\"",
            summary = "Clarifies that reasonable doubt arises whenever a single material inconsistency undermines the prosecution narrative. Benefit of doubt is an enforceable legal right rather than judicial leniency, requiring acquittal where ocular and medical evidence diverge.",
            keyPrinciples = listOf(
                "Burden of proof rests squarely on the prosecution throughout criminal trials.",
                "A single material dent entitles the accused to benefit of doubt as of right.",
                "Appellate courts must independently re-appraise contradictions between ocular and medical evidence."
            )
        ),
        LegalCasePassage(
            id = "pld_2022_lhr_305",
            citation = "PLD 2022 Lahore 305",
            court = "Lahore High Court",
            category = "Civil",
            subtitle = "Civil Revision • Specific Performance & Discretionary Relief",
            isVerified = true,
            text = "\"The respondent instituted a suit for specific performance of an agreement to sell immovable property, yet failed to demonstrate continuous readiness and willingness to perform his part of the contract. Under Section 22 of the Specific Relief Act, jurisdiction to decree specific performance is discretionary and must be exercised on sound judicial principles rather than mechanical enforcement. Learned counsel for the petitioner rightly pointed out that unexplained delay defeats equitable relief. The revision petition is allowed and the suit stands dismissed.\"",
            summary = "Interprets Section 22 of the Specific Relief Act, emphasizing that specific performance is equitable and discretionary. A vendee seeking enforcement must prove continuous financial readiness and prompt action without unexplained laches.",
            keyPrinciples = listOf(
                "Continuous readiness and willingness is mandatory in suits for specific performance.",
                "Equitable jurisdiction under Section 22 is governed by settled judicial discretion.",
                "Unexplained delay by the plaintiff defeats claims for specific relief."
            )
        ),
        LegalCasePassage(
            id = "2023_plc_cs_410",
            citation = "2023 PLC (C.S.) 410",
            court = "Supreme Court of Pakistan",
            category = "Service",
            subtitle = "Service Appeal • Doctrine of Locus Poenitentiae & Regularization",
            isVerified = true,
            text = "\"Once an administrative order conferring valuable civil rights has taken legal effect and decisive steps have been acted upon, the competent authority cannot invoke locus poenitentiae to rescind the notification arbitrarily. The learned Service Tribunal overlooked binding precedent laid down by the Supreme Court regarding vested rights of regularised civil servants. Principles of natural justice demand a proper show-cause notice and personal hearing prior to any adverse order affecting tenure or seniority.\"",
            summary = "Restricts executive withdrawal of completed administrative orders once decisive steps create vested rights. Service Tribunals must enforce prior notice and hearing before seniority or tenure is curtailed.",
            keyPrinciples = listOf(
                "Locus poenitentiae ends once an order is acted upon and creates vested rights.",
                "Adverse service orders without show-cause notice violate natural justice.",
                "Service Tribunals are bound by Supreme Court precedents under Article 189."
            )
        ),
        LegalCasePassage(
            id = "pld_2021_sc_788",
            citation = "PLD 2021 SC 788",
            court = "Supreme Court of Pakistan",
            category = "Constitutional",
            subtitle = "Judicial Review • Article 199 Writ Jurisdiction & Proportionality",
            isVerified = true,
            text = "\"While exercising constitutional jurisdiction under Article 199, the learned High Court does not sit as a court of appeal over policy choices of the executive, yet it remains duty-bound to strike down subordinate legislation that suffers from patent illegality or disproportionate infringement of fundamental rights. Judicial review ensures accountability within constitutional boundaries so that public power is exercised fairly, transparently, and solely in the public interest.\"",
            summary = "Defines the balance between judicial restraint on executive policy and robust constitutional review under Article 199 when subordinate rules infringe fundamental rights disproportionately.",
            keyPrinciples = listOf(
                "Article 199 checks illegality, irrationality, and procedural impropriety.",
                "Proportionality test applies where fundamental rights face administrative restriction.",
                "Public power is a constitutional trust exercised transparently."
            )
        ),
        LegalCasePassage(
            id = "2024_cld_92",
            citation = "2024 CLD 92",
            court = "Sindh High Court",
            category = "Arbitration",
            subtitle = "Commercial Suit • Recognition & Enforcement of Foreign Arbitral Awards",
            isVerified = true,
            text = "\"Pakistan is a signatory to the New York Convention, and the Recognition and Enforcement Act mandates a pro-enforcement bias toward international arbitral awards. Refusal of enforcement under the public policy exception must be construed narrowly and cannot serve as a pretext to reopen factual findings on merits. Learned counsel for the respondent failed to establish lack of jurisdiction or breach of due process before the arbitral tribunal. The execution petition is accordingly decreed.\"",
            summary = "Affirms Pakistan's pro-enforcement judicial approach under the Recognition and Enforcement (Arbitration Agreements and Foreign Arbitral Awards) Act, construing the public policy defense strictly.",
            keyPrinciples = listOf(
                "Courts apply a strong pro-enforcement presumption for New York Convention awards.",
                "Public policy defense cannot be used to re-litigate factual merits.",
                "Jurisdictional objections must satisfy strict statutory grounds."
            )
        ),
        LegalCasePassage(
            id = "dawn_ed_fiscal_reform",
            citation = "DAWN Editorial • Fiscal Reform",
            court = "Dawn Newspaper — Editorial Desk",
            category = "Dawn Editorial",
            subtitle = "Macroeconomic Stabilization • Tax Base & Fiscal Deficit Control",
            isVerified = true,
            text = "\"Sustainable economic recovery demands structural fiscal reform rather than temporary external borrowing. For decades, successive governments have postponed broadening the tax base, placing an unsustainable burden on documented sectors and salaried citizens. Without curbing the fiscal deficit and reforming loss-making state enterprises, inflationary pressures will continue to erode household purchasing power. Parliamentary oversight over public expenditure must be strengthened so that development allocations translate into measurable infrastructure and human capital outcomes rather than recurrent administrative waste.\"",
            summary = "Dawn editorial examining Pakistan's structural fiscal deficit, over-reliance on indirect taxation, and the urgent need for parliament-backed reform of state enterprises and public spending.",
            keyPrinciples = listOf(
                "Structural fiscal reform requires documenting untaxed sectors rather than repeated indirect levies.",
                "Controlling the fiscal deficit is essential to easing chronic inflation.",
                "Parliamentary oversight ensures development budgets build real infrastructure."
            )
        ),
        LegalCasePassage(
            id = "dawn_ed_parliament_oversight",
            citation = "DAWN Editorial • Democratic Governance",
            court = "Dawn Newspaper — Opinion & Editorial",
            category = "Dawn Editorial",
            subtitle = "Parliamentary Supremacy • Transparency & Institutional Accountability",
            isVerified = true,
            text = "\"A resilient democratic order rests upon an assertive parliament capable of holding the executive branch to rigorous accountability. When substantive legislation is rushed through ordinances without committee scrutiny, both transparency and public interest suffer lasting damage. Standing committees must summon regulatory authorities, examine policy failures openly, and demand clear timelines for administrative reform. Strengthening local governments and empowering elected representatives remain indispensable steps toward durable constitutional stability and public trust.\"",
            summary = "Argues that meaningful democratic accountability depends on parliamentary committee scrutiny of legislation rather than governance by ordinance, alongside empowered local government institutions.",
            keyPrinciples = listOf(
                "Parliamentary committees safeguard legislative transparency and executive accountability.",
                "Rule by ordinance weakens institutional scrutiny and public trust.",
                "Local government empowerment anchors sustainable constitutional governance."
            )
        ),
        LegalCasePassage(
            id = "dawn_ed_climate_resilience",
            citation = "DAWN Editorial • Climate Resilience",
            court = "Dawn Newspaper — Editorial Desk",
            category = "Dawn Editorial",
            subtitle = "Water Governance • Urban Planning & Agricultural Adaptation",
            isVerified = true,
            text = "\"Extreme heatwaves and erratic monsoon floods underscore the urgency of integrating climate resilience into national infrastructure policy. Pakistan's agrarian economy cannot absorb recurring water scarcity without modernizing canal irrigation, lining watercourses, and enforcing strict urban zoning laws. Provincial governments must coordinate transparent disaster preparedness plans ahead of seasonal emergencies. Investing in early warning systems and sustainable watershed management is no longer discretionary; it is a fundamental imperative for food security and public safety.\"",
            summary = "Highlights Pakistan's acute climate vulnerability and calls for provincial coordination, irrigation modernization, urban zoning enforcement, and early warning infrastructure.",
            keyPrinciples = listOf(
                "Modernized irrigation and watershed protection are vital for agrarian economy resilience.",
                "Urban zoning laws reduce catastrophic flood exposure.",
                "Provincial coordination and early warning systems protect public safety."
            )
        ),
        LegalCasePassage(
            id = "dawn_news_monetary_policy",
            citation = "DAWN Business • Monetary Policy",
            court = "Dawn Newspaper — Business & Finance",
            category = "Dawn News",
            subtitle = "Central Bank Outlook • Policy Rate, Inflation & Export Growth",
            isVerified = true,
            text = "\"Reviewing recent macroeconomic indicators, the monetary policy committee noted that core inflation has moderated gradually amid tighter fiscal discipline and improved agricultural supplies. However, persistent energy tariff adjustments and global commodity volatility warrant a cautious stance on interest rates. Industrial chambers urged the government to ensure predictable power pricing and timely tax refunds so textile and technology exporters can sustain competitiveness across international markets while preserving foreign exchange reserves.\"",
            summary = "Reports on the central bank's cautious monetary policy stance balancing moderating inflation against energy tariff risks and exporter demands for competitive credit.",
            keyPrinciples = listOf(
                "Monetary policy balances disinflation momentum against energy tariff pressures.",
                "Predictable power tariffs and timely refunds support export competitiveness.",
                "Coordinated fiscal discipline underpins foreign exchange stability."
            )
        ),
        LegalCasePassage(
            id = "dawn_news_judicial_reforms",
            citation = "DAWN National • Digital Courts",
            court = "Dawn Newspaper — National Desk",
            category = "Dawn News",
            subtitle = "Case Management • E-Filing & Expeditious Disposal",
            isVerified = true,
            text = "\"Addressing a national judicial conference, senior policymakers emphasized that digital case management and automated court transcripts are essential to clearing millions of pending cases across district and appellate forums. Pilot e-filing desks have reduced procedural adjournments and improved record verification for learned counsel and litigants alike. Observers noted that training court stenographers in rapid phonetic reporting alongside modern digital archiving will significantly advance timely access to justice in the public interest.\"",
            summary = "Covers national judicial conference initiatives integrating e-filing, automated record verification, and stenographic speed training to reduce case backlogs.",
            keyPrinciples = listOf(
                "Digital case management curtails procedural delays in district and appellate courts.",
                "Trained court stenographers remain central to verbatim record accuracy.",
                "Expeditious case disposal strengthens public confidence in the justice system."
            )
        ),
        LegalCasePassage(
            id = "dawn_news_energy_grid",
            citation = "DAWN Economy • Energy Grid",
            court = "Dawn Newspaper — Economy Desk",
            category = "Dawn News",
            subtitle = "Power Sector • Transmission Losses & Renewable Transition",
            isVerified = true,
            text = "\"Energy economists cautioned yesterday that mounting circular debt cannot be resolved solely through consumer tariff hikes while transmission losses and distribution inefficiencies remain unaddressed. Independent regulatory oversight is needed to audit power purchase agreements and accelerate grid modernization. Expanding domestic solar and hydroelectric capacity offers a sustainable path to lowering generation costs, supporting industrial output, and shielding low-income households from recurring fuel price shocks.\"",
            summary = "Analyzes circular debt reform options, urging grid loss reduction, transparent regulatory audits, and renewable generation investment instead of perpetual tariff hikes.",
            keyPrinciples = listOf(
                "Grid modernization and loss reduction are prerequisites to curbing circular debt.",
                "Regulatory audits bring transparency to power purchase contracts.",
                "Renewable generation lowers long-term industrial and household energy costs."
            )
        )
    )

    val draftTemplates: List<Pair<String, String>> = listOf(
        "Constitutional Writ" to "The petitioner's learned counsel submits that the impugned notification issued by the respondent authority is ultra vires the Constitution and infringes fundamental rights guaranteed under Article 10-A. No statutory tribunal can exercise jurisdiction contrary to the principles of natural justice.",
        "Dawn Editorial" to "Parliamentary accountability and transparent governance are indispensable for sustainable economic reform. Unless the government curbs the fiscal deficit and checks inflation through disciplined public expenditure, citizens will continue to bear disproportionate hardship.",
        "Dawn Economy" to "The monetary policy committee observed that cautious interest rate calibration, stable foreign policy ties, and modern infrastructure investment remain essential to sustaining export growth and safeguarding public interest.",
        "Criminal Bail Grounds" to "Learned counsel for the petitioner contends that the prosecution case requires further inquiry under Section 497 Cr.P.C. There is no independent corroboration linking the accused with the alleged offence, and the benefit of doubt extends even at the bail stage.",
        "Civil Appellate Order" to "Having heard learned counsel for both parties and examined the impugned judgment of the learned High Court, we find that the appellate forum misread material documentary evidence on record. The appeal is allowed and the matter is remanded for expeditious adjudication."
    )
}
