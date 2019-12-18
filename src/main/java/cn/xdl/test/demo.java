package cn.xdl.test;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class demo {
    public static void main(String[] args) throws FileNotFoundException, DocumentException {
        InputStream is = new FileInputStream("D:\\idea\\IdeaCode\\Dom4J\\src\\main\\resources\\demo.xml");
        SAXReader sr = new SAXReader();
        Document doc = sr.read(is);
        Element root =doc.getRootElement();
        List<Element> es = root.elements();
        for (Element bean:es
             ) {
            String idValue = bean.attributeValue("id");
            Element name = bean.element("name");
            String nameValue =name.getText();
            Element info =bean.element("info");
            String  infoValue = info.getText();
            System.out.println(idValue+nameValue+infoValue);
        }
    }

}
