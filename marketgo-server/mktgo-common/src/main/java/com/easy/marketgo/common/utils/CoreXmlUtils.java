package com.easy.marketgo.common.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;

/**
 * @author : ssk
 * @version : v1.0
 * @date :  2022-08-01 20:19:46
 * @description : xml 转换工具类
 */
public class CoreXmlUtils {

    private static DocumentBuilderFactory documentBuilderFactory = null;

    /**
     * 将 xml 文件解析为指定类型的实体对象。此方法只能解析简单的只有一层的xml@param xml
     *
     * @param tclass
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T parseXml2Obj(String xml, Class<T> tclass) throws Exception {

        if (isEmpty(xml)) {
            throw new NullPointerException("要解析的xml字符串不能为空。");
        }
        // 文档解析器工厂初始
        if (documentBuilderFactory == null) {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
        }
        // 拿到一个文档解析器。
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        // 准备数据并解析。
        byte[] bytes = xml.getBytes("UTF-8");
        Document parsed = documentBuilder.parse(new ByteArrayInputStream(bytes));
        // 获取数据
        T obj = tclass.newInstance();
        Element documentElement = parsed.getDocumentElement();
        NodeList childNodes = documentElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            // 节点类型是 ELEMENT 才读取值
            // 进行此判断是因为如果xml不是一行，而是多行且有很好的格式的，就会产生一些文本的node，这些node内容只有换行符或空格
            // 所以排除这些换行符和空格。
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                String key = item.getNodeName();
                String field = key.substring(0, 1).toUpperCase().concat(key.substring(1));
                String value = item.getTextContent();
                // 拿到设置值的set方法。
                Class tmpClass = tclass;
                while (tmpClass != null && !tmpClass.getName().toLowerCase().equals("java.lang.object")) {
                    Method declaredMethod = null;
                    try {
                        declaredMethod = tmpClass.getDeclaredMethod("set" + field, String.class);
                    } catch (NoSuchMethodException e) {
                    }
                    if (declaredMethod != null) {
                        declaredMethod.setAccessible(true);
                        // 设置值
                        declaredMethod.invoke(obj, value);
                        break;
                    }
                    tmpClass = tmpClass.getSuperclass();
                }
            }
        }
        return obj;
    }


    public static boolean isEmpty(String value) {

        return value == null || value.length() == 0;
    }

}
