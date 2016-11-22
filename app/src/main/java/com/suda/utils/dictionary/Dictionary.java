package com.suda.utils.dictionary;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by YangJiali on 2016/11/16 0016.
 */
public class Dictionary {
    private int num;
    private String[] numdictionary;
    private Map<String,Integer> numdic;
    public Dictionary()
    {
        numdictionary = new String[]{"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八",
                "十九", "二十", "二十一", "二十二", "二十三", "二十四", "二十五", "二十六", "二十七", "二十八", "二十九", "三十", "三十一", "三十二", "三十三",
                "三十四", "三十五", "三十六", "三十七", "四十", "四十一", "四十二", "四十三", "四十四", "四十五", "四十六", "四十七", "四十八", "四十九", "五十"};
        numdic = new HashMap<>();
        numdic.put("一",1);
        numdic.put("二",2);
        numdic.put("三",3);
        numdic.put("四",4);
        numdic.put("五",5);
        numdic.put("六",6);
        numdic.put("七",7);
        numdic.put("八",8);
        numdic.put("九",9);
        numdic.put("十",10);
        numdic.put("十一",11);
        numdic.put("十二",12);
        numdic.put("十三",13);
        numdic.put("十四",14);
        numdic.put("十五",15);
        numdic.put("十六",16);
        numdic.put("十七",17);
        numdic.put("十八",18);
        numdic.put("十九",19);
        numdic.put("二十",20);
        numdic.put("二十一",21);
        numdic.put("二十二",22);
        numdic.put("二十三",23);
        numdic.put("二十四",24);
        numdic.put("二十五",25);
        numdic.put("二十六",26);
        numdic.put("二十七",27);
        numdic.put("二十八",28);
        numdic.put("二十九",29);
        numdic.put("三十",30);
        numdic.put("三十一",31);
        numdic.put("三十二",32);
        numdic.put("三十三",33);
        numdic.put("三十四",34);
        numdic.put("三十五",35);
        numdic.put("三十六",36);
        numdic.put("三十七",37);
        numdic.put("三十八",38);
        numdic.put("三十九",39);
        numdic.put("四十",40);
        numdic.put("四十一",41);
        numdic.put("四十二",42);
        numdic.put("四十三",43);
        numdic.put("四十四",44);
        numdic.put("四十五",45);
        numdic.put("四十六",46);
        numdic.put("四十七",47);
        numdic.put("四十八",48);
        numdic.put("四十九",49);
        numdic.put("五十",50);
        numdic.put("五十一",51);
        numdic.put("五十二",52);
        numdic.put("五十三",53);
        numdic.put("五十四",54);
        numdic.put("五十五",55);
        numdic.put("五十六",56);
        numdic.put("五十七",57);
        numdic.put("五十八",58);
        numdic.put("五十九",59);
        numdic.put("六十",60);
        numdic.put("六十一",61);
        numdic.put("六十二",62);
        numdic.put("六十三",63);
        numdic.put("六十四",64);
        numdic.put("六十五",65);
        numdic.put("六十六",66);
        numdic.put("六十七",67);
        numdic.put("六十八",68);
        numdic.put("六十九",69);
        numdic.put("七十",70);
        numdic.put("七十一",71);
        numdic.put("七十二",72);
        numdic.put("七十三",73);
        numdic.put("七十四",74);
        numdic.put("七十五",75);
        numdic.put("七十六",76);
        numdic.put("七十七",77);
        numdic.put("七十八",78);
        numdic.put("七十九",79);
        numdic.put("八十",80);
        numdic.put("八十一",81);
        numdic.put("八十二",82);
        numdic.put("八十三",83);
        numdic.put("八十四",84);
        numdic.put("八十五",85);
        numdic.put("八十六",86);
        numdic.put("八十七",87);
        numdic.put("八十八", 88);
        numdic.put("八十九", 89);
        numdic.put("九十",90);
        numdic.put("九十一",91);
        numdic.put("九十二",92);
        numdic.put("九十三",93);
        numdic.put("九十四",94);
        numdic.put("九十五",95);
        numdic.put("九十六",96);
        numdic.put("九十七",97);
        numdic.put("九十八",98);
        numdic.put("九十九",99);
        numdic.put("一百",100);
    }
    public int getnum(String num)
    {
        if (numdic.get(num) == null)
        {
            return 0;
        }
        return numdic.get(num);
    }

}
