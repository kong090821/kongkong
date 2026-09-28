package com.kongkong.babybag.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class BenefitItem(
    val id: String,
    val title: String,
    val description: String,
    val eligibility: String,
    val region: String? = null, // null?´ë©´ ?„êµ­ ê³µí†µ / ê°œì¸ë³?
    val isNational: Boolean = (region == null),
    val badge: String? = null,
    val timing: String = "",
    val place: String = ""
)

object BenefitsRepository {

    // 1. ?„êµ­ ê³µí†µ ?œíƒ ëª©ë¡
    private val defaultNationalBenefits = listOf(
        BenefitItem(
            id = "nat_1",
            title = "ì²«ë§Œ?¨ì´?©ê¶Œ (200ë§???/ 300ë§???",
            description = "ì²«ì§¸??200ë§??? ?˜ì§¸???´ìƒ 300ë§???êµ???‰ë³µì¹´ë“œ ë°”ìš°ì²??¬ì¸??ì§€ê¸?,
            eligibility = "?€?œë?êµ?ëª¨ë“  ì¶œìƒ??(ì¶œìƒ ??1???´ë‚´ ? ì²­)",
            badge = "ë°”ìš°ì²?,
            timing = "ì¶œì‚° ??,
            place = "?‰ì •ë³µì??¼í„° / ë³µì?ë¡?
        ),
        BenefitItem(
            id = "nat_2",
            title = "ë¶€ëª¨ê¸‰??(ë§?0??100ë§???/ ë§?1??50ë§???",
            description = "ë§?0????100ë§??? ë§?1????50ë§???ë§¤ì›” 25???„ê¸ˆ ì§€ê¸?,
            eligibility = "ë§?0~1???ì•„ ?‘ìœ¡ ê°€??,
            badge = "?„ê¸ˆ ì§€??,
            timing = "ì¶œì‚° ??ë§¤ì›”",
            place = "?‰ì •ë³µì??¼í„° / ë³µì?ë¡?
        ),
        BenefitItem(
            id = "nat_3",
            title = "?„ë™?˜ë‹¹ (??10ë§???",
            description = "ë§?8??ë¯¸ë§Œ(0~95ê°œì›”) ëª¨ë“  ?„ë™ ë§¤ì›” 10ë§????„ê¸ˆ ì§€ê¸?(60???´ë‚´ ? ì²­ ???Œê¸‰)",
            eligibility = "ë§?8??ë¯¸ë§Œ ëª¨ë“  ?„ë™",
            badge = "?„ê¸ˆ ì§€??,
            timing = "60???´ë‚´ ? ì²­",
            place = "?‰ì •ë³µì??¼í„° / ë³µì?ë¡?
        ),
        BenefitItem(
            id = "nat_4",
            title = "ì¶œìƒ? ê³  ë°?ê±´ê°•ë³´í—˜ ?¼ë³´?˜ì ?±ë¡",
            description = "ë²•ì  ? ë¶„ ?±ë¡ ë°?ê±´ê°•ë³´í—˜ ?¼ë³´?˜ì/?¼ë??‘ì ?ë™ ?°ë™",
            eligibility = "ëª¨ë“  ì¶œìƒ??(ë¯¸ì‹ ê³???ê³¼íƒœë£?ë°œìƒ)",
            badge = "?‰ì • ?˜ì†",
            timing = "ì¶œìƒ ??30???´ë‚´",
            place = "?‰ì •ë³µì??¼í„° / ?€ë²•ì› ?„ì?˜ë¬´"
        ),
        BenefitItem(
            id = "nat_5",
            title = "ì¶œìƒì¦ëª…??ë°œê¸‰",
            description = "ì¶œìƒ? ê³ , ?œì•„ë³´í—˜ ì²?µ¬ ë°??Œì‚¬ ?œì¶œ??ì¶œìƒì¦ëª…??2~3ë¶€ ?˜ë ¹",
            eligibility = "ëª¨ë“  ì¶œì‚° ?°ëª¨",
            badge = "?œë¥˜ ?˜ì†",
            timing = "ì¶œìƒ ì¦‰ì‹œ",
            place = "ë¶„ë§Œ ë³‘ì›"
        ),
        BenefitItem(
            id = "nat_6",
            title = "?„ê¸°Â·ê°€?¤Â·ìˆ˜???”ê¸ˆ ê°ë©´ ? ì²­",
            description = "ì¶œì‚° ê°€êµ??„ê¸°?”ê¸ˆ 30% ê°ë©´ (??1.6ë§????œë„, 3?„ê°„), ê°€???˜ë„ ?”ê¸ˆ ê°ë©´",
            eligibility = "ì¶œì‚° ??3???´ë‚´ ê°€êµ?,
            badge = "?í™œë¹?ê°ë©´",
            timing = "ì¶œì‚° ??ì¦‰ì‹œ",
            place = "?¸í„°??(?œì „ 123/?•ë?24)"
        ),
        BenefitItem(
            id = "nat_7",
            title = "?´ë¦°?´ì§‘ ?…ì†Œ ?€ê¸?? ì²­",
            description = "ì¶œìƒ ì§í›„ ?„ì‹ ?¡ì•„ì¢…í•©?¬í„¸ ?„ì´?¬ë‘?ì„œ ?í•˜???´ë¦°?´ì§‘ ?…ì†Œ ?€ê¸??±ë¡",
            eligibility = "ëª¨ë“  ?ìœ ??,
            badge = "ë³´ìœ¡ ?˜ì†",
            timing = "ì¶œì‚° ??ì¦‰ì‹œ",
            place = "?„ì´?¬ë‘ ?¬í„¸ (?¸í„°??"
        ),
        BenefitItem(
            id = "nat_8",
            title = "ë§˜í¸??KTX & SRT ?„ì‚°ë¶€Â·?¤ì?€ ? ì¸",
            description = "ë§˜í¸??KTX(?¼ë°˜??ê°€ê²©ìœ¼ë¡??¹ì‹¤ ?´ìš©), SRT ë§˜í¸??30% ? ì¸",
            eligibility = "?„ì‚°ë¶€ ë°?ì¶œì‚° ??1???´ë‚´ ?°ëª¨",
            badge = "êµí†µ ? ì¸",
            timing = "?„ì‹  ì¤?~ ì¶œì‚° ??,
            place = "ì½”ë ˆ??/ SR (?•ë?24)"
        ),
        BenefitItem(
            id = "nat_9",
            title = "? ìƒ??ë¬´ë£Œ ?ˆë°©?‘ì¢… (BCG, B?•ê°„????",
            description = "BCG(?¼ë‚´??, B?•ê°„??1~3ì°????ìœ ??êµ?? ?„ìˆ˜ ?ˆë°©?‘ì¢… 18ì¢??„ì•¡ ë¬´ë£Œ",
            eligibility = "ëª¨ë“  ? ìƒ??ë°??ìœ ??,
            badge = "?˜ë£Œ ì§€??,
            timing = "ì¶œìƒ ì§í›„ ~ 4ì£??´ë‚´",
            place = "ë¶„ë§Œ ë³‘ì› / ë³´ê±´??
        ),
        BenefitItem(
            id = "nat_10",
            title = "?ìœ ??ê±´ê°•ê²€ì§?(ì´?8??ë¬´ë£Œ)",
            description = "?í›„ 14?¼ë???71ê°œì›”ê¹Œì? ?±ì¥Â·ë°œë‹¬ ?¨ê³„ë³?ë¬´ë£Œ ê²€ì§?ë°?êµ¬ê°•ê²€ì§?,
            eligibility = "ëª¨ë“  ?ìœ ??,
            badge = "?˜ë£Œ ê²€ì§?,
            timing = "?í›„ 14??71ê°œì›”",
            place = "ì§€???Œì•„ì²?†Œ?„ê³¼"
        )
    )

    // 2. ?€?œë?êµ?17ê°?ì§€?ì²´ ?„ì²´ ì§€??³„ ?œíƒ ëª©ë¡
    private val defaultRegionalBenefits = mapOf(
        "?¸ì¢…" to listOf(
            BenefitItem(
                id = "reg_sj_1",
                title = "?¸ì¢…??ì¶œìƒì¶•í•˜ê¸?(120ë§???",
                description = "?¸ì¢…?¹ë³„?ì¹˜??ì¶œìƒ??1?¸ë‹¹ 120ë§???ì§€??™”???„ê¸ˆ ì§€ê¸?,
                eligibility = "?¸ì¢…??ê±°ì£¼ ì¶œì‚° ê°€??(ì¶œìƒ ??6ê°œì›” ?´ë‚´ ? ì²­)",
                badge = "?¸ì¢…???¹í™”",
                timing = "6ê°œì›” ?´ë‚´",
                place = "?¸ì¢…???Â·ë©´Â·???‰ì •ë³µì??¼í„°"
            ),
            BenefitItem(
                id = "reg_sj_2",
                title = "?¸ì¢…???°ëª¨Â·? ìƒ??ê±´ê°•ê´€ë¦¬ì‚¬ ì§€?ê¸ˆ & ?ì‚°ë¬?,
                description = "?°í›„ì¡°ë„?°ë? ë³¸ì¸ë¶€?´ê¸ˆ ì¶”ê? ì§€??ë°?48ë§????ë‹¹ ì¹œí™˜ê²??ì‚°ë¬?ê¾¸ëŸ¬ë¯?ì§€??,
                eligibility = "?¸ì¢…??ê±°ì£¼ ?°ëª¨",
                badge = "?¸ì¢…??ì§€??,
                timing = "ì¶œì‚° ??40??~ ì¶œì‚° ??30??,
                place = "?¸ì¢…??ë³´ê±´??/ ë§˜í¸???„ì‹ "
            )
        ),
        "?œìš¸" to listOf(
            BenefitItem(
                id = "reg_seoul_1",
                title = "?œìš¸???°í›„ì¡°ë¦¬ê²½ë¹„ ì§€??(100ë§???",
                description = "ì¶œìƒ??1?¸ë‹¹ 100ë§???ë°”ìš°ì²?(?°í›„ì¡°ë¦¬?? ?˜ì•½?? ?´ë™ ???´ìš© ê°€??",
                eligibility = "?œìš¸??90???´ìƒ ê±°ì£¼ ì¶œì‚° ê°€??,
                badge = "?œìš¸???¹í™”",
                timing = "ì¶œì‚° ??60???´ë‚´",
                place = "?œìš¸?„ë§ˆ?„ë¹ ?ì‹œ / ëª½ë•…?•ë³´ë§ŒëŠ¥??
            ),
            BenefitItem(
                id = "reg_seoul_2",
                title = "?œìš¸ ?„ë§ˆ?„ë¹  ?ì‹œ & ?„ì‚°ë¶€ êµí†µë¹?(70ë§???",
                description = "?ì•„ 1?¸ë‹¹ 10ë§????ì‹œ ?¬ì¸??+ ?„ì‚°ë¶€ êµí†µë¹?70ë§???ì§€??,
                eligibility = "?œìš¸??ê±°ì£¼ ?„ì‚°ë¶€ ë°??ì•„ ê°€êµ?,
                badge = "êµí†µ ì§€??,
                timing = "?„ì‹  ì¤?~ ì¶œì‚° ??,
                place = "?•ë?24 / ëª½ë•…?•ë³´ë§ŒëŠ¥??
            )
        ),
        "ê²½ê¸°" to listOf(
            BenefitItem(
                id = "reg_gg_1",
                title = "ê²½ê¸°???°í›„ì¡°ë¦¬ë¹?ì§€??(50ë§???",
                description = "ì¶œìƒ??1?¸ë‹¹ 50ë§???ê²½ê¸°ì§€??™”??ì§€ê¸?,
                eligibility = "ê²½ê¸°??ê±°ì£¼ ì¶œì‚° ê°€??,
                badge = "ê²½ê¸°??ì§€??,
                timing = "ì¶œì‚° ??,
                place = "ê²½ê¸°???œÂ·êµ° ?‰ì •ë³µì??¼í„°"
            ),
            BenefitItem(
                id = "reg_gg_2",
                title = "ê²½ê¸°???„ì‚°ë¶€ ì¹œí™˜ê²??ì‚°ë¬?ê¾¸ëŸ¬ë¯?(48ë§???",
                description = "?°ê°„ 48ë§????ë‹¹ ì¹œí™˜ê²??ì‚°ë¬??ë???20% ì§€??,
                eligibility = "ê²½ê¸°??ê±°ì£¼ ?„ì‚°ë¶€/?°ëª¨",
                badge = "?ì‚°ë¬?ì§€??,
                timing = "?„ì‹  ì¤?~ ì¶œì‚° ??1??,
                place = "?„ì‚°ë¶€ ì¹œí™˜ê²½ë†?°ë¬¼ ?¼í•‘ëª?
            )
        ),
        "?¸ì²œ" to listOf(
            BenefitItem(
                id = "reg_ic_1",
                title = "?¸ì²œ 1???ŒëŸ¬???„ì´?œë¦¼ ì§€??,
                description = "ì²œì‚¬ì§€?ê¸ˆ(1~7????120ë§??? ë°??„ì´ ê¿??˜ë‹¹ ???¸ì²œ???‘ìœ¡ ?¨í‚¤ì§€ ì§€??,
                eligibility = "?¸ì²œ??ê±°ì£¼ ì¶œì‚° ê°€??,
                badge = "?¸ì²œ???¹í™”",
                timing = "ì¶œì‚° ??,
                place = "?¸ì²œ??/ ?‰ì •ë³µì??¼í„°"
            ),
            BenefitItem(
                id = "reg_ic_2",
                title = "?¸ì²œ???°í›„ì¡°ë¦¬ë¹?& ?„ì‚°ë¶€ êµí†µë¹?ì§€??(50ë§???",
                description = "?°í›„ì¡°ë¦¬ ë¹„ìš© ë°??´ë™ êµí†µë¹?50ë§???ë°”ìš°ì²?ì§€ê¸?,
                eligibility = "?¸ì²œ??ê±°ì£¼ ?„ì‚°ë¶€",
                badge = "?¸ì²œ??ì§€??,
                timing = "ì¶œì‚° ?„í›„",
                place = "?¸ì²œ??ë³´ê±´??
            )
        ),
        "ë¶€?? to listOf(
            BenefitItem(
                id = "reg_bs_1",
                title = "ë¶€?°ì‹œ ì¶œì‚°ì¶•í•˜ê¸?(ì²«ì§¸ 20ë§?/ ?˜ì§¸ ?´ìƒ 100ë§???",
                description = "ë¶€?°ê´‘??‹œ ì¶œì‚°ì¶•í•˜ê¸?+ êµ?·êµ°ë³?ì¶”ê? ì¶•í•˜ê¸?ì§€??,
                eligibility = "ë¶€?°ì‹œ ê±°ì£¼ ì¶œì‚° ê°€??,
                badge = "ë¶€?°ì‹œ ì§€??,
                timing = "ì¶œìƒ ??1???´ë‚´",
                place = "ë¶€?°ì‹œ ?Â·ë©´Â·???‰ì •ë³µì??¼í„°"
            ),
            BenefitItem(
                id = "reg_bs_2",
                title = "ë¶€?°ì‹œ ?°í›„ì¡°ë¦¬ë¹?ì§€??(50ë§???",
                description = "?™ë°±??ì§€??™”??50ë§????ë‹¹ ?°í›„ì¡°ë¦¬ë¹?ì§€??,
                eligibility = "ë¶€?°ì‹œ ê±°ì£¼ ?°ëª¨",
                badge = "ë¶€?°ì‹œ ?¹í™”",
                timing = "ì¶œì‚° ??,
                place = "ë¶€?°ì‹œ ë³´ê±´??/ ?ë©´??
            )
        ),
        "?€êµ? to listOf(
            BenefitItem(
                id = "reg_dg_1",
                title = "?€êµ¬ì‹œ ì¶œìƒì¶•í•˜ê¸?(?˜ì§¸ 100ë§?/ ?‹ì§¸ ?´ìƒ 200ë§???",
                description = "?¤ì?€ ì¶œìƒ???€???„ê¸ˆ ì¶œìƒì¶•í•˜ê¸?ì§€??,
                eligibility = "?€êµ¬ì‹œ ê±°ì£¼ ì¶œì‚° ê°€??,
                badge = "?€êµ¬ì‹œ ì§€??,
                timing = "ì¶œìƒ ??1???´ë‚´",
                place = "?€êµ¬ì‹œ ?‰ì •ë³µì??¼í„°"
            )
        ),
        "?€?? to listOf(
            BenefitItem(
                id = "reg_dj_1",
                title = "?€???€?„ë§˜ ì¶œì‚°?¥ë ¤ê¸?(30ë§?80ë§???",
                description = "?€?„ì‹œ ê¸°ë³¸ 30ë§???+ êµ¬ë³„ ì¶”ê? ì¶•í•˜ê¸?(30~50ë§???ì¶”ê?)",
                eligibility = "?€?„ì‹œ ê±°ì£¼ ì¶œì‚° ê°€??,
                badge = "?€?„ì‹œ ì§€??,
                timing = "ì¶œìƒ ??6ê°œì›” ?´ë‚´",
                place = "?€?„ì‹œ ?‰ì •ë³µì??¼í„°"
            )
        ),
        "ê´‘ì£¼" to listOf(
            BenefitItem(
                id = "reg_gj_1",
                title = "ê´‘ì£¼??ì¶œìƒì¶•í•˜ê¸?(100ë§???",
                description = "ê´‘ì£¼ê´‘ì—­??ì¶œìƒ??1?¸ë‹¹ 100ë§????„ê¸ˆ ì§€ê¸?,
                eligibility = "ê´‘ì£¼??ê±°ì£¼ ì¶œì‚° ê°€??,
                badge = "ê´‘ì£¼??ì§€??,
                timing = "ì¶œìƒ ??3ê°œì›” ?´ë‚´",
                place = "ê´‘ì£¼???‰ì •ë³µì??¼í„°"
            )
        ),
        "?¸ì‚°" to listOf(
            BenefitItem(
                id = "reg_us_1",
                title = "?¸ì‚°??ì¶œì‚°ì¶•í•˜ê¸?(ì²«ì§¸ 50ë§?/ ?˜ì§¸ 100ë§?/ ?‹ì§¸ 200ë§???",
                description = "?ë? ?œìœ„ë³?ì°¨ë“± ì¶œì‚°ì¶•í•˜ê¸??„ê¸ˆ ì§€ê¸?,
                eligibility = "?¸ì‚°??ê±°ì£¼ ì¶œì‚° ê°€??,
                badge = "?¸ì‚°??ì§€??,
                timing = "ì¶œìƒ ??1???´ë‚´",
                place = "?¸ì‚°???‰ì •ë³µì??¼í„°"
            )
        ),
        "ì¶©ë¶" to listOf(
            BenefitItem(
                id = "reg_cb_1",
                title = "ì¶©ë¶ ì¶œì‚°?¡ì•„?˜ë‹¹ (5?„ê°„ ì´?1,000ë§???",
                description = "1??300ë§? 2??200ë§? 3~5??ê°?170ë§???ì´?1ì²œë§Œ ??ë¶„í•  ì§€ê¸?,
                eligibility = "ì¶©ì²­ë¶ë„ ê±°ì£¼ ì¶œì‚° ê°€êµ?,
                badge = "ì¶©ë¶ ?¹í™”",
                timing = "ì¶œìƒ ??5?„ê°„",
                place = "ì¶©ë¶ ?œÂ·êµ° ?‰ì •ë³µì??¼í„°"
            )
        ),
        "ì¶©ë‚¨" to listOf(
            BenefitItem(
                id = "reg_cn_1",
                title = "ì¶©ë‚¨ ?‰ë³µ?¤ì??˜ë‹¹ (??10ë§???",
                description = "12~36ê°œì›” ?„ë™ ?€??ë§¤ì›” 10ë§????„ê¸ˆ ì¶”ê? ì§€??,
                eligibility = "ì¶©ì²­?¨ë„ ê±°ì£¼ ?ìœ ??,
                badge = "ì¶©ë‚¨ ì§€??,
                timing = "ë§¤ì›” 25??,
                place = "ì¶©ë‚¨ ?œÂ·êµ° ?‰ì •ë³µì??¼í„°"
            )
        ),
        "?„ë¶" to listOf(
            BenefitItem(
                id = "reg_jb_1",
                title = "?„ë¶ ì¶œì‚°ì¶•í•˜ê¸?(ì²«ì§¸ 30~100ë§?/ ?‹ì§¸ ?´ìƒ 500ë§???",
                description = "?œÂ·êµ°ë³?ì°¨ë“± ì§€ê¸‰ë˜???€??ì¶œìƒì¶•í•˜ê¸?ì§€??,
                eligibility = "?„ë¶ ê±°ì£¼ ì¶œì‚° ê°€??,
                badge = "?„ë¶ ì§€??,
                timing = "ì¶œìƒ ??1???´ë‚´",
                place = "?„ë¶ ?œÂ·êµ° ?‰ì •ë³µì??¼í„°"
            )
        ),
        "?„ë‚¨" to listOf(
            BenefitItem(
                id = "reg_jn_1",
                title = "?„ë‚¨ ì¶œìƒê¸°ë³¸?˜ë‹¹ (1~18??ë§¤ì›” 20ë§???",
                description = "18?¸ê¹Œì§€ ë§¤ì›” 20ë§???ì´?4,320ë§????¥ê¸° ?¡ì•„?˜ë‹¹ ì§€ê¸?,
                eligibility = "?„ë¼?¨ë„ ê±°ì£¼ ?„ë™",
                badge = "?„ë‚¨ ?Œê²©ì§€??,
                timing = "ë§¤ì›” ì§€ê¸?,
                place = "?„ë‚¨ ?œÂ·êµ° ?‰ì •ë³µì??¼í„°"
            )
        ),
        "ê²½ë¶" to listOf(
            BenefitItem(
                id = "reg_gb_1",
                title = "ê²½ë¶ ?€ì¶œìƒ ê·¹ë³µ ì¶œì‚°ì¶•í•˜ê¸?(100ë§?500ë§???",
                description = "?œÂ·êµ°ë³?ì²«ì§¸ 100ë§??ë????‹ì§¸ ?´ìƒ 500ë§????´ìƒ ì§€??,
                eligibility = "ê²½ìƒë¶ë„ ê±°ì£¼ ì¶œì‚° ê°€??,
                badge = "ê²½ë¶ ì§€??,
                timing = "ì¶œìƒ ??1???´ë‚´",
                place = "ê²½ë¶ ?œÂ·êµ° ?‰ì •ë³µì??¼í„°"
            )
        ),
        "ê²½ë‚¨" to listOf(
            BenefitItem(
                id = "reg_gn_1",
                title = "ê²½ë‚¨ ì¶œì‚°ì¶•í•˜ê¸?& ?°í›„ì¡°ë¦¬ë¹?(50ë§?300ë§???",
                description = "?œÂ·êµ°ë³?ì¶œì‚°ì¶•í•˜ê¸?ë°??°í›„ì¡°ë¦¬ë¹?ì§€?ê¸ˆ ?Œê²© ?œíƒ",
                eligibility = "ê²½ìƒ?¨ë„ ê±°ì£¼ ì¶œì‚° ê°€??,
                badge = "ê²½ë‚¨ ì§€??,
                timing = "ì¶œì‚° ??,
                place = "ê²½ë‚¨ ?œÂ·êµ° ?‰ì •ë³µì??¼í„°"
            )
        ),
        "ê°•ì›" to listOf(
            BenefitItem(
                id = "reg_gw_1",
                title = "ê°•ì› ?¡ì•„ê¸°ë³¸?˜ë‹¹ (ë§?0~5????30ë§?50ë§???",
                description = "?•ë? ë¶€ëª¨ê¸‰?¬ì? ?°ê³„?˜ì—¬ ë§?5?¸ê¹Œì§€ ë§¤ì›” ?¡ì•„?˜ë‹¹ ?„ê¸ˆ ì§€ê¸?,
                eligibility = "ê°•ì›?¹ë³„?ì¹˜??ê±°ì£¼ ê°€êµ?,
                badge = "ê°•ì› ?¹í™”",
                timing = "ë§¤ì›” 25??,
                place = "ê°•ì› ?œÂ·êµ° ?‰ì •ë³µì??¼í„°"
            )
        ),
        "?œì£¼" to listOf(
            BenefitItem(
                id = "reg_jj_1",
                title = "?œì£¼ ì¶œì‚°ë³µì??˜ë‹¹ (ì²«ì§¸ 50ë§?/ ?˜ì§¸ ?´ìƒ 200ë§???",
                description = "?˜ì§¸ ?´ìƒ ??50ë§??ì”© 4?„ê°„ ì´?200ë§???ë¶„í•  ì§€ê¸?,
                eligibility = "?œì£¼?¹ë³„?ì¹˜??ê±°ì£¼ ê°€??,
                badge = "?œì£¼ ?¹í™”",
                timing = "ì¶œìƒ ??4?„ê°„",
                place = "?œì£¼ ?Â·ë©´Â·??ì£¼ë??¼í„°"
            ),
            BenefitItem(
                id = "reg_jj_2",
                title = "?œì£¼ ?„ì‚°ë¶€ ?‰ë³µ?ì‹œ ì§€??(??12ë§???",
                description = "?„ì‚°ë¶€ ë³‘ì› ì§„ë£Œ ë°??´ë™???‰ë³µ?ì‹œ 12ë§????´ìš©ê¶?ì§€??,
                eligibility = "?œì£¼ ê±°ì£¼ ?„ì‚°ë¶€",
                badge = "êµí†µ ì§€??,
                timing = "?„ì‹  ì¤?,
                place = "?œì£¼??ë³´ê±´??
            )
        )
    )

    // 3. ê°œì¸ë³??œíƒ ëª©ë¡ (ì§ì¥, ê³µë¬´?? êµì§?? ë³´í—˜, ?°ë§?•ì‚° ??
    private val defaultPersonalBenefits = listOf(
        BenefitItem(
            id = "per_1",
            title = "ê³µë¬´?ì—°ê¸ˆê³µ??ì¶œì‚°ì¶•í•˜ê¸?(100ë§???",
            description = "ê³µë¬´??ë§ì¶¤??ë³µì? / ê³µë¬´?ì—°ê¸ˆê³µ??ì¶œì‚°ì¶•í•˜ê¸?ì²«ì§¸ 100ë§???ì§€ê¸?,
            eligibility = "ê³µë¬´??ë°?êµì§??,
            badge = "ê³µë¬´???œíƒ",
            timing = "ì¶œì‚° ??,
            place = "ê³µë¬´?ì—°ê¸ˆê³µ??/ ê¸°ê? ë³µì??€"
        ),
        BenefitItem(
            id = "per_2",
            title = "êµì§?ê³µ?œíšŒ ì¶œì‚°ì¶•í•˜ê¸?(10ë§???+ ì¶•í•˜? ë¬¼)",
            description = "?œêµ­êµì§?ê³µ?œíšŒ ?Œì› ì¶œì‚° ì¶•í•˜ê¸?10ë§???ë°?? ìƒ??ì¶•í•˜?©í’ˆ ? ë¬¼ ì§€ê¸?,
            eligibility = "?œêµ­êµì§?ê³µ?œíšŒ ?Œì›",
            badge = "êµì§???œíƒ",
            timing = "ì¶œì‚° ??,
            place = "êµì§?ê³µ?œíšŒ ?ˆí˜?´ì?"
        ),
        BenefitItem(
            id = "per_3",
            title = "ê³µë¬´??ë³µì??¬í„¸ ì¶œì‚° ê°€ì¡±ì ??(?ë? ??10ë§Œx2 ??",
            description = "?ë? ì¶œì‚° ??ë§ì¶¤??ë³µì??¬ì¸??ì¶œì‚° ì¶•í•˜ ?ìˆ˜ ë°?ê°€ì¡??ìˆ˜ ì¶”ê? ë¶€??,
            eligibility = "ê³µë¬´??ë°?ë§ì¶¤??ë³µì? ?ìš© ?€?ì",
            badge = "ë³µì??¬ì¸??,
            timing = "ì¶œì‚° ??,
            place = "ê³µë¬´??ë§ì¶¤??ë³µì??¬í„¸"
        ),
        BenefitItem(
            id = "per_4",
            title = "ê³µë¬´??ì§ì¥ ê°€ì¡±ìˆ˜??? ì²­ (??3ë§?11ë§???",
            description = "ê°€ì¡±ìˆ˜??ì§€ê¸?(ì²«ì§¸ ?ë? ??3ë§? ?˜ì§¸ ??7ë§? ?‹ì§¸ ?´ìƒ ??11ë§???",
            eligibility = "ê³µë¬´?? êµì§??ë°??´ë‹¹ ì§ì¥??,
            badge = "ê°€ì¡±ìˆ˜??,
            timing = "ì¶œì‚° ??ê¸‰ì—¬ë°˜ì˜",
            place = "?™êµ/ê¸°ê? ?‰ì •??/ ?¸ì‚¬ê³?
        ),
        BenefitItem(
            id = "per_5",
            title = "ë°°ìš°??ì¶œì‚°?´ê? (10??? ê¸‰?´ê?) & ?¡ì•„?´ì§ ê¸‰ì—¬",
            description = "?¨í¸ ë°°ìš°??ì¶œì‚°?´ê? 10??? ê¸‰) ë°?6+6 ë¶€ëª¨ìœ¡?„íœ´ì§ì œ (ìµœë? ??450ë§???",
            eligibility = "ê·¼ë¡œ??ë°?ê³µë¬´??êµì§??,
            badge = "?´ê?Â·?´ì§",
            timing = "ì¶œì‚° ?œê¸° ~ ?´ì§ ??,
            place = "ê³ ìš©24 / ?Œì† ê¸°ê? ?¸ì‚¬ê³?
        ),
        BenefitItem(
            id = "per_6",
            title = "?œì•„ë³´í—˜ ?˜ê¸‰ ë°?ê³„ì•½ ì¡°ì • (?…ì›Â·?˜ìˆ ë¹?ì²?µ¬)",
            description = "?œì™•?ˆê°œ ?˜ìˆ ë¹? ? ìƒ???…ì› ?¼ë‹¹ ì²?µ¬ + ì¶œìƒ ???¼ë³´?˜ì ëª…ì˜ ë³€ê²?? ë‚©ë£??˜ê¸‰",
            eligibility = "?œì•„ë³´í—˜ ê°€?…ì",
            badge = "ë³´í—˜ ?œíƒ",
            timing = "?˜ìˆ /ì¶œìƒ ??,
            place = "ê°€??ë³´í—˜??(??ì½œì„¼??"
        ),
        BenefitItem(
            id = "per_7",
            title = "?ë™ì°¨ë³´???œì•„/?ë? ? ì¸ ?˜ê¸‰",
            description = "?œì•„ ?•ì¸???ëŠ” ì¶œìƒì¦ëª…???œì¶œ ???ë™ì°¨ë³´?˜ë£Œ 3~15% ?˜ê¸‰ (?œì•„ ? ì¸ ?¹ì•½)",
            eligibility = "?ë™ì°¨ë³´??ê°€?…ì",
            badge = "ë³´í—˜ ?˜ê¸‰",
            timing = "?œì•„?•ì¸ ??/ ì¶œìƒ ??,
            place = "ê°€???ë™ì°¨ë³´?˜ì‚¬"
        ),
        BenefitItem(
            id = "per_8",
            title = "ê³µë¬´???¨ì²´ë³´í—˜ / ?¤ì†ë³´í—˜ ë³‘ì› ì§„ë£Œë¹?? ì²­",
            description = "?„ì‹ Â·ì¶œì‚° ?˜ìˆ ë¹? ?„ì  ë³‘ì› ì§„ë£Œë¹??¤ì†ë³´í—˜ ë°??¨ì²´ë³´í—˜ ì²?µ¬",
            eligibility = "ê³µë¬´???¨ì²´ë³´í—˜ / ê°œì¸ ?¤ì† ê°€?…ì",
            badge = "ë³´í—˜ ì²?µ¬",
            timing = "?˜ìˆ /ì¶œì‚° ??,
            place = "ê³µë¬´?ì—°ê¸ˆê³µ??/ ë³´í—˜??
        ),
        BenefitItem(
            id = "per_9",
            title = "?°ë§?•ì‚° - ì¶œì‚°Â·?…ì–‘ ?¸ì•¡ê³µì œ (30ë§?70ë§???",
            description = "?´ë‹¹ ?°ë„ ì¶œìƒ??ì²«ì§¸ 30ë§??? ?˜ì§¸ 50ë§??? ?‹ì§¸ ?´ìƒ 70ë§????¸ì•¡ê³µì œ",
            eligibility = "ê·¼ë¡œ?Œë“??ë°?ì¢…í•©?Œë“??,
            badge = "?¸ì•¡ê³µì œ",
            timing = "?°ë§?•ì‚° ?œê¸°",
            place = "êµ?„¸ì²??ˆíƒ??
        ),
        BenefitItem(
            id = "per_10",
            title = "?°ë§?•ì‚° - ?°í›„ì¡°ë¦¬?ë¹„ ?˜ë£Œë¹??¸ì•¡ê³µì œ (ìµœë? 200ë§???",
            description = "?°í›„ì¡°ë¦¬??ê²°ì œê¸ˆì•¡ ì¶œìƒ?„ë‹¹ 200ë§????œë„ ?˜ë£Œë¹??¸ì•¡ê³µì œ (ì´ê¸‰??7ì²œë§Œ ???´í•˜)",
            eligibility = "ì´ê¸‰??7,000ë§????´í•˜ ê·¼ë¡œ??,
            badge = "?˜ë£Œë¹?ê³µì œ",
            timing = "ì¡°ë¦¬?ë¹„ ê²°ì œ ??,
            place = "êµ?„¸ì²??ˆíƒ??/ ì¡°ë¦¬???ìˆ˜ì¦?
        ),
        BenefitItem(
            id = "per_11",
            title = "?°ë§?•ì‚° - ? ìƒ???¸ì ê³µì œ (ë¶€?‘ê?ì¡?150ë§???",
            description = "ì¶œìƒ?„ë? ë¶€?‘ê?ì¡?ê¸°ë³¸ê³µì œ ?€?ìë¡??±ë¡?˜ì—¬ ?Œë“ê³µì œ ?ìš©",
            eligibility = "ê·¼ë¡œ?Œë“??,
            badge = "?¸ì ê³µì œ",
            timing = "?°ë§?•ì‚° ?œê¸°",
            place = "êµ?„¸ì²??ˆíƒ??
        ),
        BenefitItem(
            id = "per_12",
            title = "?¸ë¦¬?ˆì? / ?´ìš© ë³‘ì› ?„ê¸° ?‘ì„± ?œíƒ",
            description = "?¸ë¦¬?ˆì? ?¬ì„±ë³‘ì› ???´ìš© ?°ë??¸ê³¼/ì¡°ë¦¬???„ê¸° ?‘ì„± ???í’ˆê¶?ë°?ê¸°í”„???œê³µ",
            eligibility = "?´ë‹¹ ë³‘ì›/ì¡°ë¦¬???´ìš©??,
            badge = "ë³‘ì› ?œíƒ",
            timing = "?´ì›/?´ì†Œ ??,
            place = "ë³‘ì›/ì¡°ë¦¬??ê³µì‹ ì¹´í˜Â·ë¸”ë¡œê·?
        )
    )

    private val _nationalBenefits = MutableStateFlow(defaultNationalBenefits)
    val nationalBenefits: StateFlow<List<BenefitItem>> = _nationalBenefits.asStateFlow()

    private val _regionalBenefits = MutableStateFlow(defaultRegionalBenefits)
    val regionalBenefits: StateFlow<Map<String, List<BenefitItem>>> = _regionalBenefits.asStateFlow()

    private val _personalBenefits = MutableStateFlow(defaultPersonalBenefits)
    val personalBenefits: StateFlow<List<BenefitItem>> = _personalBenefits.asStateFlow()

    private val _savedBenefitIds = MutableStateFlow<Set<String>>(emptySet())
    val savedBenefitIds: StateFlow<Set<String>> = _savedBenefitIds.asStateFlow()

    private val _completedBenefitIds = MutableStateFlow<Set<String>>(emptySet())
    val completedBenefitIds: StateFlow<Set<String>> = _completedBenefitIds.asStateFlow()

    fun setSavedBenefitIds(ids: Set<String>) {
        _savedBenefitIds.value = ids
    }

    fun toggleBenefitSaved(id: String) {
        _savedBenefitIds.update { set ->
            if (set.contains(id)) set - id else set + id
        }
    }

    fun setCompletedBenefitIds(ids: Set<String>) {
        _completedBenefitIds.value = ids
    }

    fun toggleBenefitCompleted(id: String) {
        _completedBenefitIds.update { set ->
            if (set.contains(id)) set - id else set + id
        }
    }

    fun isBenefitCompleted(id: String): Boolean {
        return _completedBenefitIds.value.contains(id)
    }

    fun deleteBenefitItem(id: String) {
        _nationalBenefits.update { list -> list.filterNot { it.id == id } }
        _personalBenefits.update { list -> list.filterNot { it.id == id } }
        _regionalBenefits.update { map ->
            map.mapValues { entry -> entry.value.filterNot { it.id == id } }
        }
        _savedBenefitIds.update { set -> set - id }
        _completedBenefitIds.update { set -> set - id }
    }

    fun addBenefitItem(
        type: String, // "national", "regional", "personal"
        title: String,
        description: String,
        eligibility: String,
        badge: String = "? ê·œ ?œíƒ",
        region: String? = null,
        timing: String = "",
        place: String = ""
    ) {
        val newId = "custom_benefit_${System.currentTimeMillis()}"
        val newItem = BenefitItem(
            id = newId,
            title = title,
            description = description,
            eligibility = eligibility,
            region = region,
            badge = badge,
            timing = timing,
            place = place
        )
        when (type) {
            "national" -> _nationalBenefits.update { it + newItem }
            "personal" -> _personalBenefits.update { it + newItem }
            "regional" -> {
                val r = region ?: "?¸ì¢…"
                _regionalBenefits.update { map ->
                    val list = map[r] ?: emptyList()
                    map + (r to (list + newItem))
                }
            }
        }
        _savedBenefitIds.update { it + newId }
    }
}
