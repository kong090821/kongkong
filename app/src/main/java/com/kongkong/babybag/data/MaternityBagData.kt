package com.kongkong.babybag.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MaternityBagItem(
    val id: String,
    val title: String,
    val recommendedQty: String = "",
    val tabCategory: String, // "?°ëª¨ ?©í’ˆ", "? ìƒ???©í’ˆ", "ë³´í˜¸??& ê³µí†µ"
    val section: String = "", // e.g. "?˜ë¥˜ & ì°©ìš©??, "?„ìƒ & ?°í›„ ?Œë³µ ?©í’ˆ", etc.
    val locationTags: List<String>, // e.g. listOf("#ë³‘ì›", "#ì¡°ë¦¬??), listOf("#?´ì›?´ì†Œ")
    val priorityTag: String = "", // "#?„ìˆ˜", "#? íƒ" (?˜ìœ„?¸í™˜)
    val note: String = "", // e.g. "(?„ì‚°ë¶€ ?„ìš©/?œì™•?ˆê°œ??"
    val isCustom: Boolean = false
)

object MaternityBagRepository {

    // 1ë²??? ?°ëª¨ ?©í’ˆ (ì´?30ê°???ª© ?„ìˆ˜ ?±ë¡)
    private val defaultMaternityItems = listOf(
        // [?˜ë¥˜ & ì°©ìš©??
        MaternityBagItem("m_cloth_1", "?˜ìœ ë¸Œë¼ / ?˜ìœ ?˜ì‹œ (3~4ê°?", "", "?°ëª¨ ?©í’ˆ", "?˜ë¥˜ & ì°©ìš©??, listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_cloth_2", "?°ëª¨ ?¬í‹° (3~4ê°?", "", "?°ëª¨ ?©í’ˆ", "?˜ë¥˜ & ì°©ìš©??, listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(?„ì‚°ë¶€ ?„ìš©/?œì™•?ˆê°œ??"),
        MaternityBagItem("m_cloth_3", "?¸í•œ ?°ëª¨ ë°”ì? (3~4ë²?", "", "?°ëª¨ ?©í’ˆ", "?˜ë¥˜ & ì°©ìš©??, listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_cloth_4", "ë¬´ì••ë°??‘ë§ (4~6ì¼¤ë ˆ)", "", "?°ëª¨ ?©í’ˆ", "?˜ë¥˜ & ì°©ìš©??, listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_cloth_5", "?•ë°•?¤í???(1~2ê°?", "", "?°ëª¨ ?©í’ˆ", "?˜ë¥˜ & ì°©ìš©??, listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_cloth_6", "?ëª©ë³´í˜¸?€ (1~2ê°?", "", "?°ëª¨ ?©í’ˆ", "?˜ë¥˜ & ì°©ìš©??, listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_cloth_7", "?°í›„ë³µë? (1ê°?", "", "?°ëª¨ ?©í’ˆ", "?˜ë¥˜ & ì°©ìš©??, listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(?œì™•?ˆê°œ ??ë³‘ì› êµ¬ë§¤ ê°€??"),
        MaternityBagItem("m_cloth_8", "?°ëª¨???¬ë¦¬??(1ì¼¤ë ˆ)", "", "?°ëª¨ ?©í’ˆ", "?˜ë¥˜ & ì°©ìš©??, listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(ë¯¸ë„??ë°©ì? ??‹ ??ê²?"),
        MaternityBagItem("m_cloth_9", "?¸ì¶œë³?/ ê°€?”ê±´ (1ë²?", "", "?°ëª¨ ?©í’ˆ", "?˜ë¥˜ & ì°©ìš©??, listOf("#?´ì›?´ì†Œ"), note = "(?´ì› ë°??´ë™ ??ì°©ìš©)"),

        // [?„ìƒ & ?°í›„ ?Œë³µ ?©í’ˆ]
        MaternityBagItem("m_hyg_1", "?…ëŠ” ?ˆì‹¬?¬í‹° (2~3??", "", "?°ëª¨ ?©í’ˆ", "?„ìƒ & ?°í›„ ?Œë³µ ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_hyg_2", "?°ëª¨ ?¨ë“œÂ·?ë¦¬?€ (1?¸íŠ¸)", "", "?°ëª¨ ?©í’ˆ", "?„ìƒ & ?°í›„ ?Œë³µ ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_hyg_3", "ë§ˆì´ë¹„ë° / ë¹„ë°ë¬¼í‹°??(3ê°?", "", "?°ëª¨ ?©í’ˆ", "?„ìƒ & ?°í›„ ?Œë³µ ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_hyg_4", "ë°”ë””?¤ì›Œ ?°ìŠˆ (1~3ê°?", "", "?°ëª¨ ?©í’ˆ", "?„ìƒ & ?°í›„ ?Œë³µ ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_hyg_5", "?„ìƒ ?°ìŠˆÂ·ë¬¼í‹°??(1~2ê°?", "", "?°ëª¨ ?©í’ˆ", "?„ìƒ & ?°í›„ ?Œë³µ ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_hyg_6", "ê°€??ì¿¨ë§?©Â·ì¹´ë³´í¬ë¦?(?„ìš”??", "", "?°ëª¨ ?©í’ˆ", "?„ìƒ & ?°í›„ ?Œë³µ ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(?¸í˜ˆÂ·?¸ìœ  ?€ë¹?"),
        MaternityBagItem("m_hyg_7", "?‰í„°ê´€ë¦?ê²”Â·ì‹œ??(?„ìš”??", "", "?°ëª¨ ?©í’ˆ", "?„ìƒ & ?°í›„ ?Œë³µ ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(?œì™•?ˆê°œ??"),
        MaternityBagItem("m_hyg_8", "?ŒìŒë¶€ ë°©ì„ / ì¢Œìš•??(1ê°?", "", "?°ëª¨ ?©í’ˆ", "?„ìƒ & ?°í›„ ?Œë³µ ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(?ì—°ë¶„ë§Œ??/ ë³‘ì› êµ¬ë¹„ ?•ì¸)"),
        MaternityBagItem("m_hyg_9", "ì¹˜ì§ˆ?°ê³  / ë¹„íŒ??(?„ìš”??", "", "?°ëª¨ ?©í’ˆ", "?„ìƒ & ?°í›„ ?Œë³µ ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_hyg_10", "?¤ë¦¬ ê³µê¸°??ë§ˆì‚¬ì§€ê¸?(1ê°?", "", "?°ëª¨ ?©í’ˆ", "?„ìƒ & ?°í›„ ?Œë³µ ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(ë¶“ê¸° ê´€ë¦¬ìš©)"),

        // [?˜ìœ  ?©í’ˆ]
        MaternityBagItem("m_feed_1", "?˜ìœ ?¨ë“œ (1~2??", "", "?°ëª¨ ?©í’ˆ", "?˜ìœ  ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_feed_2", "ëª¨ìœ ?€?¥íŒ© (1~2??", "", "?°ëª¨ ?©í’ˆ", "?˜ìœ  ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_feed_3", "?˜ìœ  ?°ìŠˆ (?„ìš”??", "", "?°ëª¨ ?©í’ˆ", "?˜ìœ  ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_feed_4", "? ë‘ë³´í˜¸?¬ë¦¼ (1~2ê°?", "", "?°ëª¨ ?©í’ˆ", "?˜ìœ  ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_feed_5", "? ë‘ë³´í˜¸ê¸?(1~2ê°?", "", "?°ëª¨ ?©í’ˆ", "?˜ìœ  ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_feed_6", "? ì¶• ê¹”ë•Œê¸?& ?´ë¦½ (1~2ê°?", "", "?°ëª¨ ?©í’ˆ", "?˜ìœ  ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("m_feed_7", "?–ë³‘ ?¸ì œ & ?–ë³‘ ??(1?¸íŠ¸)", "", "?°ëª¨ ?©í’ˆ", "?˜ìœ  ?©í’ˆ", listOf("#ì¡°ë¦¬??)),

        // [ê°œì¸ ?„ìƒ & ?¤í‚¨ì¼€??
        MaternityBagItem("m_sk_1", "?°ëª¨ ?¸ë©´?„êµ¬ ?¸íŠ¸ (1?¸íŠ¸)", "", "?°ëª¨ ?©í’ˆ", "ê°œì¸ ?„ìƒ & ?¤í‚¨ì¼€??, listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(?´í‘¸Â·ë°”ë””?Œì‹œÂ·ì¹«ì†”ì¹˜ì•½ ??"),
        MaternityBagItem("m_sk_2", "ê¸°ì´ˆ?”ì¥Â·ë³´ìŠµ ?¸íŠ¸ (1?¸íŠ¸)", "", "?°ëª¨ ?©í’ˆ", "ê°œì¸ ?„ìƒ & ?¤í‚¨ì¼€??, listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(?¤í‚¨Â·ë¡œì…˜Â·ë¦½ë°¤Â·?¸ë“œ?¬ë¦¼)"),
        MaternityBagItem("m_sk_3", "?¤ì–´ ì¼€???„êµ¬ ?¸íŠ¸ (1?¸íŠ¸)", "", "?°ëª¨ ?©í’ˆ", "ê°œì¸ ?„ìƒ & ?¤í‚¨ì¼€??, listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(?œë¼?´ê¸°Â·ë¨¸ë¦¬ë¹—Â·ë¨¸ë¦¬ëˆ ??"),
        MaternityBagItem("m_sk_4", "?œë¼?´ìƒ´??(1ê°?", "", "?°ëª¨ ?©í’ˆ", "ê°œì¸ ?„ìƒ & ?¤í‚¨ì¼€??, listOf("#ë³‘ì›"), note = "(?…ì› ì¤??¤ì›Œ ë¶ˆê? ??ì¶”ì²œ)")
    )

    // 2ë²??? ? ìƒ???©í’ˆ (ì´?11ê°???ª© ?„ìˆ˜ ?±ë¡)
    private val defaultBabyItems = listOf(
        // [?˜ë¥˜ & ì°©ìš©??(?´ì› ë°?ëª¨ì?™ì‹¤??]
        MaternityBagItem("b_cloth_1", "ë°°ëƒ‡?€ê³ ë¦¬ / ë°°ëƒ‡?˜íŠ¸ (1~2ê°?", "", "? ìƒ???©í’ˆ", "?˜ë¥˜ & ì°©ìš©??(?´ì› ë°?ëª¨ì?™ì‹¤??", listOf("#?´ì›?´ì†Œ"), note = "(?¸íƒ ?„ë£Œ ?„ìˆ˜)"),
        MaternityBagItem("b_cloth_2", "?ì‹¸ê°?(1~2ê°?", "", "? ìƒ???©í’ˆ", "?˜ë¥˜ & ì°©ìš©??(?´ì› ë°?ëª¨ì?™ì‹¤??", listOf("#?´ì›?´ì†Œ")),
        MaternityBagItem("b_cloth_3", "ê²‰ì‹¸ê°?(1ê°?", "", "? ìƒ???©í’ˆ", "?˜ë¥˜ & ì°©ìš©??(?´ì› ë°?ëª¨ì?™ì‹¤??", listOf("#?´ì›?´ì†Œ")),
        MaternityBagItem("b_cloth_4", "? ìƒ??ëª¨ìÂ·?¸ê°œë¥?(ê°?1ê°?", "", "? ìƒ???©í’ˆ", "?˜ë¥˜ & ì°©ìš©??(?´ì› ë°?ëª¨ì?™ì‹¤??", listOf("#?´ì›?´ì†Œ"), note = "(?ë°œ?¸ê°œÂ·ëª¨ìÂ·?‘ë§)"),

        // [?„ìƒ & ì¼€???©í’ˆ]
        MaternityBagItem("b_care_1", "ê°€???ìˆ˜ê±?(10???´ìƒ)", "", "? ìƒ???©í’ˆ", "?„ìƒ & ì¼€???©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(ê±°ì¦ˆÂ·? ë³´ ?¸íƒ ?„ë£Œ)"),
        MaternityBagItem("b_care_2", "?„ê¸° ë¬¼í‹°??(1~2??", "", "? ìƒ???©í’ˆ", "?„ìƒ & ì¼€???©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("b_care_3", "?„ê¸° ë¡œì…˜ / ?„ê¸° ?¬ë¦¼ (1ê°?", "", "? ìƒ???©í’ˆ", "?„ìƒ & ì¼€???©í’ˆ", listOf("#ì¡°ë¦¬??)),
        MaternityBagItem("b_care_4", "?„ê¸° ?ì–‘??(1ê°?", "", "? ìƒ???©í’ˆ", "?„ìƒ & ì¼€???©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(ë¹„í?ë¯¼DÂ·? ì‚°ê·?"),
        MaternityBagItem("b_care_5", "? ìƒ???í†±ê¹ì´ ?¸íŠ¸ (1?¸íŠ¸)", "", "? ìƒ???©í’ˆ", "?„ìƒ & ì¼€???©í’ˆ", listOf("#ì¡°ë¦¬??)),
        MaternityBagItem("b_care_6", "ëª¨ì?™ì‹¤ ì´¬ì˜ ?Œí’ˆ (?„ìš”??", "", "? ìƒ???©í’ˆ", "?„ìƒ & ì¼€???©í’ˆ", listOf("#ì¡°ë¦¬??), note = "(?”ë°?´ë‹¬?¥Â·ì´ˆ?ì±…Â·?¸í˜•)"),

        // [?´ë™ ë°??ˆì „ ?©í’ˆ]
        MaternityBagItem("b_safe_1", "? ìƒ??ì¹´ì‹œ??(1ê°?", "", "? ìƒ???©í’ˆ", "?´ë™ ë°??ˆì „ ?©í’ˆ", listOf("#?´ì›?´ì†Œ"), note = "(?ì°¨ ?´ë™ ??ë²•ì  ?„ìˆ˜ ?ˆëª©)")
    )

    // 3ë²??? ë³´í˜¸??& ê³µí†µ (ì´?15ê°???ª© ?„ìˆ˜ ?±ë¡)
    private val defaultGuardianItems = listOf(
        // [?„ìˆ˜ ?œë¥˜ & ?ê¸ˆ]
        MaternityBagItem("g_doc_1", "?°ëª¨?˜ì²© & ? ë¶„ì¦?(1?¸íŠ¸)", "", "ë³´í˜¸??& ê³µí†µ", "?„ìˆ˜ ?œë¥˜ & ?ê¸ˆ", listOf("#ë³‘ì›")),
        MaternityBagItem("g_doc_2", "ê²°ì œ ?˜ë‹¨ (ì¹´ë“œ/?„ê¸ˆ)", "", "ë³´í˜¸??& ê³µí†µ", "?„ìˆ˜ ?œë¥˜ & ?ê¸ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),

        // [ë³´í˜¸???¨í¸) ?„ìš© ?©í’ˆ]
        MaternityBagItem("g_dad_1", "?¨í¸ ?¸ë©´?„êµ¬ & ?¬ë²Œ??(1?¸íŠ¸)", "", "ë³´í˜¸??& ê³µí†µ", "ë³´í˜¸???¨í¸) ?„ìš© ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("g_dad_2", "?¨í¸??ì¹¨êµ¬ë¥?(1?¸íŠ¸)", "", "ë³´í˜¸??& ê³µí†µ", "ë³´í˜¸???¨í¸) ?„ìš© ?©í’ˆ", listOf("#ë³‘ì›"), note = "(ë³‘ì› ?œê³µ ?¬ë? ?•ì¸ ?„ìš”)"),
        MaternityBagItem("g_dad_3", "?¨í¸???¬ë¦¬??(1ì¼¤ë ˆ)", "", "ë³´í˜¸??& ê³µí†µ", "ë³´í˜¸???¨í¸) ?„ìš© ?©í’ˆ", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),

        // [ê³µí†µ ?í™œ?©í’ˆ & ?„ìê¸°ê¸°]
        MaternityBagItem("g_life_1", "?€ë¸”ëŸ¬ & ë¹¨ë? ?¸íŠ¸ (1?¸íŠ¸)", "", "ë³´í˜¸??& ê³µí†µ", "ê³µí†µ ?í™œ?©í’ˆ & ?„ìê¸°ê¸°", listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(?„ì›Œ??ë§ˆì‹œ??êº¾ì¸ ë¹¨ë?)"),
        MaternityBagItem("g_life_2", "ì¢…ì´ì»?& ë³´ë¦¬ì°?ë¶“ê¸°ì°?(1?¸íŠ¸)", "", "ë³´í˜¸??& ê³µí†µ", "ê³µí†µ ?í™œ?©í’ˆ & ?„ìê¸°ê¸°", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("g_life_3", "?¼íšŒ???˜ì„¸ë¯?& ?˜ì? (1?¸íŠ¸)", "", "ë³´í˜¸??& ê³µí†µ", "ê³µí†µ ?í™œ?©í’ˆ & ?„ìê¸°ê¸°", listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(?€ë¸”ëŸ¬ ?¸ì²™ ë°??ì‚¬ ë³´ì¡°??"),
        MaternityBagItem("g_life_4", "ì¶©ì „ê¸?& ë©€?°íƒ­ (1?¸íŠ¸)", "", "ë³´í˜¸??& ê³µí†µ", "ê³µí†µ ?í™œ?©í’ˆ & ?„ìê¸°ê¸°", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("g_life_5", "ê±°ì¹˜?€ & ë¯¸ë‹ˆ? í’ê¸?(1?¸íŠ¸)", "", "ë³´í˜¸??& ê³µí†µ", "ê³µí†µ ?í™œ?©í’ˆ & ?„ìê¸°ê¸°", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("g_life_6", "ë¯¸ë‹ˆ ê°€?µê¸° (1ê°?", "", "ë³´í˜¸??& ê³µí†µ", "ê³µí†µ ?í™œ?©í’ˆ & ?„ìê¸°ê¸°", listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(ê±´ì¡°??ë³‘ì‹¤ ?€ë¹?"),
        MaternityBagItem("g_life_7", "?„ê¸°?„êµ¬ (1?¸íŠ¸)", "", "ë³´í˜¸??& ê³µí†µ", "ê³µí†µ ?í™œ?©í’ˆ & ?„ìê¸°ê¸°", listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(ê°€?„Â·ë„¤?„íœÂ·?¼ë°˜??/ ëª¨ìœ ê¸°ë¡??"),
        MaternityBagItem("g_life_8", "?°ëª¨ ?ì–‘??(1?¸íŠ¸)", "", "ë³´í˜¸??& ê³µí†µ", "ê³µí†µ ?í™œ?©í’ˆ & ?„ìê¸°ê¸°", listOf("#ë³‘ì›", "#ì¡°ë¦¬??), note = "(ì² ë¶„Â·ì¹¼ìŠ˜Â·ë§ˆê·¸?¤ìŠ˜ ??"),
        MaternityBagItem("g_life_9", "ë³´í˜¸??ë§ˆìŠ¤??(1??", "", "ë³´í˜¸??& ê³µí†µ", "ê³µí†µ ?í™œ?©í’ˆ & ?„ìê¸°ê¸°", listOf("#ë³‘ì›", "#ì¡°ë¦¬??)),
        MaternityBagItem("g_life_10", "?‘ì´??ë³´ì¡°ê°€ë°?/ ?ì½”ë°?(1~2ê°?", "", "ë³´í˜¸??& ê³µí†µ", "ê³µí†µ ?í™œ?©í’ˆ & ?„ìê¸°ê¸°", listOf("#?´ì›?´ì†Œ"), note = "(?´ì› ? ë¬¼ ë°??˜ì–´??ì§??˜ê±°??")
    )

    private val _maternityItems = MutableStateFlow(defaultMaternityItems)
    val maternityItems: StateFlow<List<MaternityBagItem>> = _maternityItems.asStateFlow()

    private val _babyItems = MutableStateFlow(defaultBabyItems)
    val babyItems: StateFlow<List<MaternityBagItem>> = _babyItems.asStateFlow()

    private val _guardianItems = MutableStateFlow(defaultGuardianItems)
    val guardianItems: StateFlow<List<MaternityBagItem>> = _guardianItems.asStateFlow()

    // ì´ˆê¸° ?œê°??ë§Œì¡±ê°ì„ ?„í•œ ?´ê? ?„ì´???¤ì • (?°ëª¨ ?©í’ˆ 15ê°??´ê? ì¤?12ê°??„ë£Œ -> 80%)
    private val initialSavedIds = setOf(
        "m_cloth_1", "m_cloth_2", "m_cloth_3", "m_cloth_4", "m_cloth_5",
        "m_cloth_6", "m_cloth_7", "m_cloth_8", "m_hyg_1", "m_hyg_2",
        "m_hyg_3", "m_hyg_5", "m_feed_1", "m_feed_2", "m_sk_1"
    )

    private val initialCompletedIds = setOf(
        "m_cloth_1", "m_cloth_2", "m_cloth_3", "m_cloth_4", "m_cloth_5",
        "m_cloth_6", "m_cloth_7", "m_cloth_8", "m_hyg_1", "m_hyg_2",
        "m_hyg_3", "m_hyg_5"
    )

    private val _savedItemIds = MutableStateFlow<Set<String>>(initialSavedIds)
    val savedItemIds: StateFlow<Set<String>> = _savedItemIds.asStateFlow()

    private val _completedItemIds = MutableStateFlow<Set<String>>(initialCompletedIds)
    val completedItemIds: StateFlow<Set<String>> = _completedItemIds.asStateFlow()

    // ì¤€ë¹„ë¬¼ ì¶”ê? (?í•˜??ì¹´í…Œê³ ë¦¬ ??— ì¶”ê?)
    fun addItemToTab(
        tabCategory: String,
        title: String,
        recommendedQty: String = "",
        locationTags: List<String> = listOf("#ë³‘ì›", "#ì¡°ë¦¬??),
        priorityTag: String = "",
        note: String = ""
    ) {
        val newId = "custom_${System.currentTimeMillis()}"
        val fullTitle = if (recommendedQty.isNotBlank() && !title.contains("(")) "$title ($recommendedQty)" else title
        val newItem = MaternityBagItem(
            id = newId,
            title = fullTitle,
            recommendedQty = recommendedQty,
            tabCategory = tabCategory,
            section = "ì¶”ê???ì¤€ë¹„ë¬¼",
            locationTags = locationTags.ifEmpty { listOf("#ë³‘ì›") },
            priorityTag = priorityTag,
            note = note,
            isCustom = true
        )
        when (tabCategory) {
            "?°ëª¨ ?©í’ˆ" -> _maternityItems.update { it + newItem }
            "? ìƒ???©í’ˆ" -> _babyItems.update { it + newItem }
            "ë³´í˜¸??& ê³µí†µ" -> _guardianItems.update { it + newItem }
            else -> _maternityItems.update { it + newItem }
        }
        // ?ë™ ??ê°€ë°??´ê¸°
        toggleItemSaved(newId)
    }

    // ì¤€ë¹„ë¬¼ ?? œ
    fun deleteItem(itemId: String) {
        _maternityItems.update { list -> list.filterNot { it.id == itemId } }
        _babyItems.update { list -> list.filterNot { it.id == itemId } }
        _guardianItems.update { list -> list.filterNot { it.id == itemId } }
        _savedItemIds.update { it - itemId }
        _completedItemIds.update { it - itemId }
    }

    fun setSavedItems(itemIds: Set<String>) {
        _savedItemIds.value = itemIds
        _completedItemIds.value = emptySet()
    }

    fun toggleItemSaved(itemId: String) {
        _savedItemIds.update { current ->
            if (current.contains(itemId)) {
                _completedItemIds.update { it - itemId }
                current - itemId
            } else {
                current + itemId
            }
        }
    }

    fun isItemSaved(itemId: String): Boolean {
        return _savedItemIds.value.contains(itemId)
    }

    fun toggleItemCompleted(itemId: String) {
        _completedItemIds.update { current ->
            if (current.contains(itemId)) {
                current - itemId
            } else {
                current + itemId
            }
        }
    }

    fun isItemCompleted(itemId: String): Boolean {
        return _completedItemIds.value.contains(itemId)
    }

    fun getAllItems(): List<MaternityBagItem> {
        return _maternityItems.value + _babyItems.value + _guardianItems.value
    }

    fun getItemsForTab(tabIndex: Int): List<MaternityBagItem> {
        return when (tabIndex) {
            0 -> _maternityItems.value
            1 -> _babyItems.value
            else -> _guardianItems.value
        }
    }

    fun getSavedItems(tabCategoryFilter: String? = null): List<MaternityBagItem> {
        val ids = _savedItemIds.value
        val all = getAllItems().filter { ids.contains(it.id) }
        return if (tabCategoryFilter == null || tabCategoryFilter == "?„ì²´") {
            all
        } else {
            all.filter { it.tabCategory == tabCategoryFilter }
        }
    }

    fun getSavedUncompletedItems(tabCategoryFilter: String? = null): List<MaternityBagItem> {
        val saved = getSavedItems(tabCategoryFilter)
        val completed = _completedItemIds.value
        return saved.filter { !completed.contains(it.id) }
    }

    fun getSavedCompletedItems(tabCategoryFilter: String? = null): List<MaternityBagItem> {
        val saved = getSavedItems(tabCategoryFilter)
        val completed = _completedItemIds.value
        return saved.filter { completed.contains(it.id) }
    }

    fun getCategoryProgress(tabCategoryName: String): Pair<Int, Int> {
        val savedInCat = getSavedItems(tabCategoryName)
        val completedInCat = getSavedCompletedItems(tabCategoryName)
        return Pair(completedInCat.size, savedInCat.size)
    }
}
