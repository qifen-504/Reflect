package cn.xdl.util;

import cn.xdl.bean.Bean;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BEncoderStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.Beans;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.rmi.MarshalledObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoUtil {
    //声明一个map集合来存储beans中的实例
    Map<String, Object> map = new HashMap<>();

    public List<Bean> DemoUtil() throws Exception {

        //创建一个List集合来存储所有的Bean
        List<Bean> data = new ArrayList<>();
        //获取文档输入流
        InputStream is = getClass().getClassLoader().getResourceAsStream("demo.xml");
        //创建一个xml读取工具对象
        SAXReader sr = new SAXReader();
        //通过读取工具读取xml输入流 并得到文档对象
        Document doc = sr.read(is);
        //找到根节点
        Element root = doc.getRootElement();
        //找到所有子节点
        List<Element> beans = root.elements();
        //循环遍历所有子节点
        for (Element bean :
                beans) {
            //找到所有id属性值
            String beanId = bean.attributeValue("id");
            //找到所有class值
            String className = bean.attributeValue("class");
            //找到所有property标签
            List<Element> properties = bean.elements();
            //创建一个map集合用来存放props
            Map<String, String> m = new HashMap<>();
            for (Element property : properties) {
                String name = property.attributeValue("name");
                String value = property.attributeValue("value");
                m.put(name, value);
            }
            Bean bean1 = new Bean(beanId, className, m);
            data.add(bean1);
        }
        return data;
    }

    public Map<String, Object> creatBeans() throws Exception {
        //调用xml解析方法
        List<Bean> list = DemoUtil();
        for (Bean bean : list
        ) {
            //取出List集合中的id值
            String beanId = bean.getId();
            //取出List集合中的calss
            String className = bean.getClazz();
            Class c1 = Class.forName(className);
            //创建bean类型的实例
            Object ins = c1.newInstance();
            //取出List集合中的property
            Map<String, String> props = bean.getProperties();
            for (String key : props.keySet()
            ) {
                String value = props.get(key);
//                System.out.println(name+"-"+value);value
                //获取map集合中key所表示的name值
                //获取名称为name的成员变量
                Field f1 = c1.getDeclaredField(key);
                //取消java语言访问检查(暴力反射)
                f1.setAccessible(true);
                //获取字段类型
                Type type = f1.getGenericType();
                //获取所有成员方法
                Method[] m1 = c1.getMethods();
                //拼接set方法名
                String methodName = "set" + key;
                for (Method method : m1) {
                    //判断获取到的方法名是不是与上述方法名一致
                    //comparetoignorecase比较字符串 若两个字符串比较相同则返回0
                    if (method.getName().compareToIgnoreCase(methodName) == 0) {
                        //判断字段类型是否与value的class相等
                        if (type.equals(value.getClass())) {
                            //调用Method表示的成员方法 传递实参创建实例
                            method.invoke(ins, value);
                        } else if (type == Integer.TYPE) {
                            int i = Integer.parseInt(value);
                            method.invoke(ins, i);
                        }
                    }
                }
                map.put(beanId, ins);
            }
        }
        return map;
    }

    public Object getBean(String beanId) {
        return map.get(beanId);
    }
}
