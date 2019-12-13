package it.lei.demojdk8;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * @author huangl
 * @date 2019/12/13 15:29
 * @desciption TODO
 */
public class StreamTest extends DemoJdk8ApplicationTests {
    @Test
    public void filter(){
        ArrayList<String> strings = new ArrayList<>();
        strings.add("张三");
        strings.add("李四");
        strings.add("王五");
        strings.add("马六");

        strings.stream()
                .filter(item->item.equals("张三"))
         //       .filter(item->item.length()==4)
                .forEach(item-> System.out.println(item));

    }

    /**
     * 获取stream的两种方式
     */
    @Test
    public void getStream(){
        ArrayList<String> strings = new ArrayList<>();
        Stream<String> stream = strings.stream();

        Stream<String> stream2 = Stream.of(new String[]{"张三", "李四"});

    }

    /**
     * 转换接口,将a类型的数据转换为b类型 感觉可以用于dto与dao之间的转换
     */
    @Test
    public void streamMapTest(){
        Stream.of("1","2","3","4").map(item-> Integer.parseInt(item)).forEach(item-> System.out.println(item+1));
    }

    /**
     * 排序和跳过
     */
    @Test
    public void sortTest(){
        Stream.of("3","2","1","4")
                .sorted((a,b)->Integer.parseInt(a)>Integer.parseInt(b)?1:Integer.parseInt(a)==Integer.parseInt(b)?0:-1)
                .skip(2)
                .limit(2)
                .forEach(item-> System.out.println(item));

    }

    /**
     * 两个流组合为一个流
     */
    @Test
    public void linkTest(){
        Stream<String> stream = Stream.of("3", "2", "1", "4");
        Stream<String> stream2 = Stream.of("3", "2", "1", "4");
        Stream<String> all = Stream.concat(stream, stream2);
    }
}
