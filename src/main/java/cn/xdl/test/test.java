package cn.xdl.test;

import cn.xdl.bean.Person;
import cn.xdl.util.DemoUtil;
import cn.xdl.util.XmlUtil;

import java.util.List;
import java.util.Map;

public class test {
    public static void main(String[] args) throws Exception {
        DemoUtil demo = new DemoUtil();
        Map<String, Object> map = demo.creatBeans();
        Object o = demo.getBean("101");
        System.out.println(o);
    }
}
