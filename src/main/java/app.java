import conf.DbCfg;
import util.YamlUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class app {
    public static void main(String[] args) {
        String file = System.getProperty("user.dir");
        DBExcutor dbExcutor = new DBExcutor();

        // 加载配置
        DbCfg dbCfg = YamlUtils.loadYaml("src/main/java/conf/DbCfg.yaml", DbCfg.class);
        // 装载
        dbExcutor.init(dbCfg);

        dbExcutor.getAllTablesName();

    }

}

class Solution {
    public int[][] reconstructQueue(int[][] people) {
        // 身高 ， 前面正好有大于或者等于的这个身高的

        // 排序 优先第一个数字大的排前面 第一个数字相同的时候 第二个数字小的排前面

        Arrays.sort(people, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                if (o1[0]!= o2[0]) {
                    return o1[0] - o2[0];
                }else {
                    return o1[1] - o2[1];
                }
            }
        });

        ArrayList<int[]> res = new ArrayList<>();

        for (int[] person : people) {
            int pos = person[1];
            res.add(pos, person);
        }
        return res.toArray(new int[people.length][]);
    }
}
