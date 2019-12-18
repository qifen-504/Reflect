package cn.xdl.util;

import cn.xdl.bean.Bean;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoUtil1 {
    //声明一个map集合来存储Bean
    Map<String ,Object> map = new HashMap<>();
    public List<Bean> XmlUtil() throws Exception {
        List<Bean> list  =new ArrayList<>();
        InputStream is = getClass().getClassLoader().getResourceAsStream("demo.xml");
        SAXReader sr = new SAXReader();
        Document doc = sr.read(is);
        Element root = doc.getRootElement();
        List <Element> beans = root.elements();
        for (Element bean: beans) {
            String beanId = bean.attributeValue("id");
            String ClassName = bean.attributeValue("class" );
            List<Element> props = bean.elements();
            Map<String,String> data= new HashMap<>();
            for (Element prop: props) {
                String name = prop.attributeValue("name");
                String value = prop.attributeValue("value");
                data.put(name,value);
            }
            Bean bean1 = new Bean(beanId,ClassName,data);
            list.add(bean1);

        }
        return list;
    }
    public Map<String,Object> creatBean() throws Exception {
        List<Bean> list = XmlUtil();
        for (Bean bean:
             list) {
            String beanId = bean.getId();
            String ClassName = bean.getClazz();
            Class c = Class.forName(ClassName);
            Object ins = c.newInstance();
            Map<String, String> props = bean.getProperties();
            for (String name:props.keySet()
                 ) {
                String value = props.get(name);
                Field f = c.getDeclaredField(value);
                f.setAccessible(true);
                Type type = f.getGenericType();
                Method[] methods = c.getMethods();
                String methodName ="set"+name;
                for (Method method:
                     methods) {
                    if (method.getName().compareToIgnoreCase(methodName)==0){
                        if (type.equals(value.getClass())){
                            method.invoke(ins,value);
                        }else if(type==Integer.TYPE){
                            int i = Integer.parseInt(value);
                            method.invoke(ins,i);
                        }
                    }
                }
                map.put(beanId,ins);
            }

        }
        return map;
    }
    public Object getBean(String beanId){
        return map.get(beanId);
    }
}
