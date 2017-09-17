package com.paul.learn.wq.netty.primary.http.xml.j2butil;

import com.paul.learn.wq.netty.primary.http.xml.bean.Address;
import com.paul.learn.wq.netty.primary.http.xml.bean.Customer;
import com.paul.learn.wq.netty.primary.http.xml.bean.Order;
import com.paul.learn.wq.netty.primary.http.xml.bean.Shipping;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/9/9
 */
public class XmlBeanUtil {
    /**
     * JAXBContext类，是应用的入口，用于管理XML/Java绑定信息。
     * Marshaller接口，将Java对象序列化为XML数据。
     * Unmarshaller接口，将XML数据反序列化为Java对象。
     */
    private static Logger logger = Logger.getLogger(XmlBeanUtil.class);


    public static <T> T convertXml2Bean(String xml,Class<T> clazz){
        T t = null;
        try {
            logger.info("开始构造反序列化解码器");
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            logger.info("反序列化编码器构造成功，开始将xml转换为bean");
            StringReader reader = new StringReader(xml);
            t = (T) unmarshaller.unmarshal(reader);
            logger.info("bean构建成功-->" + t);
            reader.close();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static String convertBean2Xml(Object object){
        String xml = null;
        try {
            logger.info("开始构造序列化编码器");
            JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            logger.info("设置xml输出格式为不换行");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
            logger.info("设置默认编码为UTF-8");
            marshaller.setProperty(Marshaller.JAXB_ENCODING,"UTF-8");
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(object,stringWriter);
            xml = stringWriter.toString();
            logger.info("序列化成功，输出xml【"+xml+"】");
            stringWriter.close();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xml;
    }


    public static Order instanceOrder(){
        Order object = new Order();

        Customer customer = new Customer();
        customer.setCustomerNumber(1);
        customer.setFirstName("Paul");
        customer.setLastName("George");
        List<String> list = new ArrayList<>();
        list.add("Michael");
        list.add("Jony");
        customer.setMiddleName(list);

        Address address = new Address();
        address.setCity("杭州");
        address.setCountry("China");
        address.setPostCode("310018");
        address.setState("浙江");
        address.setStreet1("滨江");
        address.setStreet2("西兴");

        Address address1 = new Address();
        address1.setCity("金华");
        address1.setCountry("China");
        address1.setPostCode("321100");
        address1.setState("浙江");
        address1.setStreet1("兰溪");
        address1.setStreet2("灵洞");

        object.setCustomer(customer);
        object.setBillTo(address);
        object.setOrderNumber(1);
        object.setShipping(Shipping.DOMESTIC_EXPRESS);
        object.setShipTo(address1);
        object.setTotal(Float.valueOf("12.0"));

        return object;
    }
}
