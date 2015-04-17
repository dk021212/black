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
	        general.put("[�ڱ�ʺ]", "kbsa_org.png");
	        general.put("[��]", "sada_org.png");
	        general.put("[����]", "qq_org.png");
	        general.put("[��]", "dizzya_org.png");
	        general.put("[�ɰ�]", "tza_org.png");
	        general.put("[����]", "hsa_org.png");
	        general.put("[��]", "han.png");
	        general.put("[˥]", "cry.png");
	        general.put("[͵Ц]", "heia_org.png");
	        general.put("[���Ƿ]", "k_org.png");
	        general.put("[˯��]", "sleepa_org.png");
	        general.put("[��]", "hatea_org.png");
	        general.put("[����]", "kl_org.png");
	        general.put("[�Һߺ�]", "yhh_org.png");
	        general.put("[��]", "cool_org.png");
	        general.put("[����]", "sb_org.png");
	        general.put("[����]", "cza_org.png");
	        general.put("[����]", "shamea_org.png");
	        general.put("[ŭ]", "angrya_org.png");
	        general.put("[����]", "bz_org.png");
	        general.put("[Ǯ]", "money_org.png");
	        general.put("[����]", "tootha_org.png");
	        general.put("[��ߺ�]", "zhh_org.png");
	        general.put("[ί��]", "wq_org.png");
	        general.put("[����]", "bs2_org.png");
	        general.put("[�Ծ�]", "cj_org.png");
	        general.put("[��]", "t_org.png");
	        general.put("[��������]", "ldln_org.png");
	        general.put("[˼��]", "sk_org.png");
	        general.put("[ŭ��]", "nm_org.png");
	        general.put("[����]", "laugh.png");
	        general.put("[ץ��]", "crazya_org.png");
	        general.put("[����]", "bba_org.png");
	        general.put("[����]", "lovea_org.png");
	        general.put("[����]", "gza_org.png");
	        general.put("[����]", "bs_org.png");
	        general.put("[��]", "x_org.png");
	        general.put("[�Ǻ�]", "smilea_org.png");
	        general.put("[��ð]", "gm.png");
	        general.put("[����]", "hx.png");
	        general.put("[��ŭ]", "face335.png");
	        general.put("[ʧ��]", "face032.png");
	        general.put("[������]", "face290.png");
	        general.put("[����]", "face105.png");
	        general.put("[��]", "face059.png");
	        general.put("[�ݰ�]", "face062.png");
	        general.put("[����]", "face055.png");


	        general.put("[��]", "face329.png");
	        general.put("[��]", "hearta_org.png");
	        general.put("[����]", "unheart.png");
	        general.put("[��]", "j_org.png");
	        general.put("[������]", "otm_org.png");
	        general.put("[����]", "lazu_org.png");
	        general.put("[����]", "cake.png");
	        general.put("[��]", "sad_org.png");
	        general.put("[ok]", "ok_org.png");
	        general.put("[����]", "vw_org.png");
	        general.put("[��ͷ]", "face281.png");
	        general.put("[����]", "face18.png");
	        general.put("[����]", "face229.png");
	        general.put("[����]", "face74.png");
	        general.put("[���Ĵ���]", "face221.png");
	        general.put("[��]", "face277.png");


	        general.put("[��è]", "face002.png");
	        general.put("[˧]", "face94.png");
	        general.put("[��Ҫ]", "face274.png");
	        general.put("[��è]", "face002.png");

	        /**
	         * huahua emotion
	         */
	        huahua.put("[Ц����]", "lxh_xiaohaha.png");
	        huahua.put("[�ð�Ŷ]", "lxh_haoaio.png");
	        huahua.put("[��Ү]", "lxh_oye.png");
	        huahua.put("[͵��]", "lxh_toule.png");
	        huahua.put("[��������]", "lxh_leiliumanmian.png");
	        huahua.put("[�޺�]", "lxh_juhan.png");
	        huahua.put("[�ٱ�ʺ]", "lxh_koubishi.png");
	        huahua.put("[���ע]", "lxh_qiuguanzhu.png");
	        huahua.put("[��ϲ��]", "lxh_haoxihuan.png");
	        huahua.put("[����]", "lxh_bengkui.png");
	        huahua.put("[�Ç�]", "lxh_haojiong.png");
	        huahua.put("[��]", "lxh_zhenjing.png");
	        huahua.put("[����]", "lxh_biefanwo.png");
	        huahua.put("[������˼]", "lxh_buhaoyisi.png");
	        huahua.put("[����]", "lxh_xiudada.png");
	        huahua.put("[�����Ц]", "lxh_deyidexiao.png");
	        huahua.put("[����]", "lxh_jiujie.png");
	        huahua.put("[����]", "lxh_feijin.png");
	        huahua.put("[����]", "lxh_beicui.png");
	        huahua.put("[˦˦��]", "lxh_shuaishuaishou.png");
	        huahua.put("[�ð�]", "lxh_haobang.png");
	        huahua.put("[����]", "lxh_qiaoqiao.png");
	        huahua.put("[�����ϰ�]", "lxh_buxiangshangban.png");
	        huahua.put("[������]", "lxh_kunsile.png");
	        huahua.put("[��Ը]", "lxh_xuyuan.png");
	        huahua.put("[�����]", "lxh_qiubite.png");
	        huahua.put("[��Ѽ��]", "lxh_youyali.png");
	        huahua.put("[��һ��]", "lxh_xiangyixiang.png");
	        huahua.put("[���֢]", "lxh_kuangzaozheng.png");
	        huahua.put("[ת��]", "lxh_zhuanfa.png");
	        huahua.put("[����Ĥ��]", "lxh_xianghumobai.png");
	        huahua.put("[�׷�]", "lxh_leifeng.png");
	        huahua.put("[�ܿ�ѷ]", "lxh_jiekexun.png");
	        huahua.put("[õ��]", "lxh_meigui.png");
	        huahua.put("[holdס]", "lxh_holdzhu.png");
	        huahua.put("[Ⱥ��Χ��]", "lxh_quntiweiguan.png");
	        huahua.put("[�Ƽ�]", "lxh_tuijian.png");
	        huahua.put("[�ް�]", "lxh_zana.png");
	        huahua.put("[����]", "lxh_beidian.png");
	        huahua.put("[����]", "lxh_pili.png");


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
