package cn.xdl.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlUtil {
    //创建一个Map集合用来存储bean对象
    Map<String, Object> map = new HashMap<>();

    public XmlUtil(String Xmlname) throws Exception {
        //加载输入流
        InputStream is = getClass().getClassLoader().getResourceAsStream(Xmlname);
        //创建XML读取工具对象
        SAXReader sax = new SAXReader();
        //通过读取工具 读取XML文档的输入流,并得到文档对象
        Document doc = sax.read(is);
        //将文档对象传入init
        init(doc);
    }

    public void init(Document doc) throws Exception {
        //得到根标签 得到了beans标签
        Element root = doc.getRootElement();
        //得到根节点的所有子节点
        List<Element> beans = root.elements();
        for (Element bean : beans) {
            //获取bean标签里的Class值
            String className = bean.attributeValue("class");
            //获取bean标签里的id
            String beanId = bean.attributeValue("id");
            if (beanId == null) {
                //跳过本次循环
                continue;
            }
            //根据bean标签里的class值创建对象
            Class c = Class.forName(className);
            //根据c记录的类来创建实例
            Object ins = c.newInstance();
            //获取bean标签下的property标签
            List<Element> properties = bean.elements();
            //取出property标签
            for (Element property : properties) {
                String name = property.attributeValue("name");
                //获取名称为name的单个成员变量信息
                Field field = c.getDeclaredField(name);
                //取消java语言访问检查
                field.setAccessible(true);
                //获取字段类型
                Type type = field.getGenericType();
                //拼接set方法
                String methodName = "set" + name;
                //获取所有方法
                Method[] methods = c.getMethods();
                //获取property中的value值
                String value = property.attributeValue("value");
                for (Method method : methods) {
                    //判断获取到的方法名是不是与上述方法名一致
                    //comparetoignorecase比较字符串 若两个字符串比较相同则返回0
                    if (method.getName().equalsIgnoreCase(methodName)) {
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
                //将准备好的beanId作为Key以及实例作为value放入map集合
                map.put(beanId, ins);
            }
        }
    }

    public Object getBean(String beanId) {
        return map.get(beanId);
    }
}
