package com.easy.marketgo.common.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.org.apache.xerces.internal.impl.xs.opti.DefaultText;
import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * @author : ssk 
 * @version : v1.0
 * @date :  2022-08-13 12:29:31
 * @description : XML转换工具类
 */
public class XmlUtils {
    public static Map<String, Object> xml2Map(String xmlString) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            SAXReader saxReader = new SAXReader();
            saxReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            saxReader.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            saxReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            saxReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            Document doc = saxReader.read(new StringReader(xmlString));
            Element root = doc.getRootElement();
            List<Element> elements = root.elements();
            for (Element element : elements) {
                map.put(element.getName(), element2MapOrString(element));
            }
        } catch (DocumentException | SAXException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    private static Object element2MapOrString(Element element) {

        final List<Node> content = element.content();
        final Set<String> names = names(content);

        // 判断节点下有无非文本节点(非Text和CDATA)，如无，直接取Text文本内容
        if (names.size() < 1) {
            return element.getText();
        }

        Map<String, Object> result = Maps.newHashMap();
        if (names.size() == 1) {
            // 说明是个列表，各个子对象是相同的name
            List<Object> list = Lists.newArrayList();
            for (Node node : content) {
                if (node instanceof DefaultText) {
                    continue;
                }

                if (node instanceof Element) {
                    list.add(element2MapOrString((Element) node));
                }
            }

            result.put(names.iterator().next(), list);
        } else {
            for (Node node : content) {
                if (node instanceof DefaultText) {
                    continue;
                }

                if (node instanceof Element) {
                    result.put(node.getName(), element2MapOrString((Element) node));
                }
            }
        }

        return result;
    }

    private static Set<String> names(List<Node> nodes) {
        Set<String> names = Sets.newHashSet();
        for (Node node : nodes) {
            // 如果节点类型是Text或CDATA跳过
            if (node instanceof DefaultText || node instanceof CDATA) {
                continue;
            }
            names.add(node.getName());
        }

        return names;
    }
}
