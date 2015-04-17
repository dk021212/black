package com.stone.support.smileypicker;

import java.util.LinkedHashMap;
import java.util.Map;

public class SmileyMap {
	
	 public static final int GENERAL_EMOTION_POSITION = 0;

	    public static final int EMOJI_EMOTION_POSITION = 2;

	    public static final int HUAHUA_EMOTION_POSITION = 1;

	    private static SmileyMap instance = new SmileyMap();
	    private Map<String, String> general = new LinkedHashMap<String, String>();
	    private Map<String, String> huahua = new LinkedHashMap<String, String>();

	    private SmileyMap() {

	        /**
	         * general emotion
	         */
	        general.put("[ÍÚ±ÇÊº]", "kbsa_org.png");
	        general.put("[Àá]", "sada_org.png");
	        general.put("[Ç×Ç×]", "qq_org.png");
	        general.put("[ÔÎ]", "dizzya_org.png");
	        general.put("[¿É°®]", "tza_org.png");
	        general.put("[»¨ĞÄ]", "hsa_org.png");
	        general.put("[º¹]", "han.png");
	        general.put("[Ë¥]", "cry.png");
	        general.put("[ÍµĞ¦]", "heia_org.png");
	        general.put("[´ò¹şÇ·]", "k_org.png");
	        general.put("[Ë¯¾õ]", "sleepa_org.png");
	        general.put("[ºß]", "hatea_org.png");
	        general.put("[¿ÉÁ¯]", "kl_org.png");
	        general.put("[ÓÒºßºß]", "yhh_org.png");
	        general.put("[¿á]", "cool_org.png");
	        general.put("[Éú²¡]", "sb_org.png");
	        general.put("[²ö×ì]", "cza_org.png");
	        general.put("[º¦Ğß]", "shamea_org.png");
	        general.put("[Å­]", "angrya_org.png");
	        general.put("[±Õ×ì]", "bz_org.png");
	        general.put("[Ç®]", "money_org.png");
	        general.put("[ÎûÎû]", "tootha_org.png");
	        general.put("[×óºßºß]", "zhh_org.png");
	        general.put("[Î¯Çü]", "wq_org.png");
	        general.put("[±ÉÊÓ]", "bs2_org.png");
	        general.put("[³Ô¾ª]", "cj_org.png");
	        general.put("[ÍÂ]", "t_org.png");
	        general.put("[ÀÁµÃÀíÄã]", "ldln_org.png");
	        general.put("[Ë¼¿¼]", "sk_org.png");
	        general.put("[Å­Âî]", "nm_org.png");
	        general.put("[¹ş¹ş]", "laugh.png");
	        general.put("[×¥¿ñ]", "crazya_org.png");
	        general.put("[±§±§]", "bba_org.png");
	        general.put("[°®Äã]", "lovea_org.png");
	        general.put("[¹ÄÕÆ]", "gza_org.png");
	        general.put("[±¯ÉË]", "bs_org.png");
	        general.put("[Ğê]", "x_org.png");
	        general.put("[ºÇºÇ]", "smilea_org.png");
	        general.put("[¸ĞÃ°]", "gm.png");
	        general.put("[ºÚÏß]", "hx.png");
	        general.put("[·ßÅ­]", "face335.png");
	        general.put("[Ê§Íû]", "face032.png");
	        general.put("[×ö¹íÁ³]", "face290.png");
	        general.put("[ÒõÏÕ]", "face105.png");
	        general.put("[À§]", "face059.png");
	        general.put("[°İ°İ]", "face062.png");
	        general.put("[ÒÉÎÊ]", "face055.png");


	        general.put("[ÔŞ]", "face329.png");
	        general.put("[ĞÄ]", "hearta_org.png");
	        general.put("[ÉËĞÄ]", "unheart.png");
	        general.put("[‡å]", "j_org.png");
	        general.put("[°ÂÌØÂü]", "otm_org.png");
	        general.put("[À¯Öò]", "lazu_org.png");
	        general.put("[µ°¸â]", "cake.png");
	        general.put("[Èõ]", "sad_org.png");
	        general.put("[ok]", "ok_org.png");
	        general.put("[ÍşÎä]", "vw_org.png");
	        general.put("[ÖíÍ·]", "face281.png");
	        general.put("[ÔÂÁÁ]", "face18.png");
	        general.put("[¸¡ÔÆ]", "face229.png");
	        general.put("[¿§·È]", "face74.png");
	        general.put("[°®ĞÄ´«µİ]", "face221.png");
	        general.put("[À´]", "face277.png");


	        general.put("[ĞÜÃ¨]", "face002.png");
	        general.put("[Ë§]", "face94.png");
	        general.put("[²»Òª]", "face274.png");
	        general.put("[ĞÜÃ¨]", "face002.png");

	        /**
	         * huahua emotion
	         */
	        huahua.put("[Ğ¦¹ş¹ş]", "lxh_xiaohaha.png");
	        huahua.put("[ºÃ°®Å¶]", "lxh_haoaio.png");
	        huahua.put("[àŞÒ®]", "lxh_oye.png");
	        huahua.put("[ÍµÀÖ]", "lxh_toule.png");
	        huahua.put("[ÀáÁ÷ÂúÃæ]", "lxh_leiliumanmian.png");
	        huahua.put("[¾Şº¹]", "lxh_juhan.png");
	        huahua.put("[¿Ù±ÇÊº]", "lxh_koubishi.png");
	        huahua.put("[Çó¹Ø×¢]", "lxh_qiuguanzhu.png");
	        huahua.put("[ºÃÏ²»¶]", "lxh_haoxihuan.png");
	        huahua.put("[±ÀÀ£]", "lxh_bengkui.png");
	        huahua.put("[ºÃ‡å]", "lxh_haojiong.png");
	        huahua.put("[Õğ¾ª]", "lxh_zhenjing.png");
	        huahua.put("[±ğ·³ÎÒ]", "lxh_biefanwo.png");
	        huahua.put("[²»ºÃÒâË¼]", "lxh_buhaoyisi.png");
	        huahua.put("[Ğßàªàª]", "lxh_xiudada.png");
	        huahua.put("[µÃÒâµØĞ¦]", "lxh_deyidexiao.png");
	        huahua.put("[¾À½á]", "lxh_jiujie.png");
	        huahua.put("[¸ø¾¢]", "lxh_feijin.png");
	        huahua.put("[±¯´ß]", "lxh_beicui.png");
	        huahua.put("[Ë¦Ë¦ÊÖ]", "lxh_shuaishuaishou.png");
	        huahua.put("[ºÃ°ô]", "lxh_haobang.png");
	        huahua.put("[ÇÆÇÆ]", "lxh_qiaoqiao.png");
	        huahua.put("[²»ÏëÉÏ°à]", "lxh_buxiangshangban.png");
	        huahua.put("[À§ËÀÁË]", "lxh_kunsile.png");
	        huahua.put("[ĞíÔ¸]", "lxh_xuyuan.png");
	        huahua.put("[Çğ±ÈÌØ]", "lxh_qiubite.png");
	        huahua.put("[ÓĞÑ¼Àæ]", "lxh_youyali.png");
	        huahua.put("[ÏëÒ»Ïë]", "lxh_xiangyixiang.png");
	        huahua.put("[Ôê¿ñÖ¢]", "lxh_kuangzaozheng.png");
	        huahua.put("[×ª·¢]", "lxh_zhuanfa.png");
	        huahua.put("[»¥ÏàÄ¤°İ]", "lxh_xianghumobai.png");
	        huahua.put("[À×·æ]", "lxh_leifeng.png");
	        huahua.put("[½Ü¿ËÑ·]", "lxh_jiekexun.png");
	        huahua.put("[Ãµ¹å]", "lxh_meigui.png");
	        huahua.put("[hold×¡]", "lxh_holdzhu.png");
	        huahua.put("[ÈºÌåÎ§¹Û]", "lxh_quntiweiguan.png");
	        huahua.put("[ÍÆ¼ö]", "lxh_tuijian.png");
	        huahua.put("[ÔŞ°¡]", "lxh_zana.png");
	        huahua.put("[±»µç]", "lxh_beidian.png");
	        huahua.put("[Åùö¨]", "lxh_pili.png");


	    }

	    public static SmileyMap getInstance() {
	        return instance;
	    }

	    public Map<String, String> getGeneral() {
	        return general;
	    }

	    public Map<String, String> getHuahua() {
	        return huahua;
	    }

}
