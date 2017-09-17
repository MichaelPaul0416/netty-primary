package com.paul.learn.wq.netty.primary.decoder.normal.util;

import org.msgpack.type.ArrayValue;
import org.msgpack.type.Value;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author:wangqiang20995
 * @description:
 * @Date:2017/8/27
 */
public class ReflectionUtil {
    public static  <T> T builderInstance(ArrayValue arrayValue, Class<T> clazz) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Field[] fields = clazz.getDeclaredFields();
        T t = clazz.newInstance();
        int index = 0;
        for(Field field : fields){
            String name = field.getName();

            String methodName = "set" + name.substring(0,1).toUpperCase()+name.substring(1);

            Method method = clazz.getDeclaredMethod(methodName,new Class[]{field.getType()});

            Value value = arrayValue.get(index);

            Class type = field.getType();

            Object o = dealWithBaseType(value, type);

            if(o == null) {
                o = dealWithOther(value, type);
            }

            method.invoke(t,castType(field.getType(),o));

            index ++;
        }
        return t;
    }

    /**
     * @Author:wangqiang20995
     * @Description:处理非八种基本类型的情况，这里暂时先当作String处理
     * @Date:2017/8/27 23:31
     * @param:[value, type]
     **/
    public static  <T> T dealWithOther(Value value, Class type) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        Constructor constructor = type.getDeclaredConstructor(String.class);

        Object obj = constructor.newInstance(new Object[]{String.valueOf(value)});

        return (T) obj;
    }

    public static  <T> T dealWithBaseType(Value value, Class type) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        /**
         * @Author:wangqiang20995
         * @Description:处理基本数据类型
         * @Date:2017/8/27 23:32
         * @param:[value, type]
         **/
        Set<String> set = constructSet();
        if(set.contains(type.getName())){
            String typeName = type.getTypeName();
            typeName = "java.lang." + ("int".equals(typeName) ? "Integer" : typeName.substring(0,1).toUpperCase() + typeName.substring(1));
            try {
                Constructor constructor = Class.forName(typeName).getConstructor(String.class);
                return (T) constructor.newInstance(new Object[]{String.valueOf(value)});
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Set<String> constructSet() {
        Set<String> set = new HashSet<>();
        set.add("int");
        set.add("long");
        set.add("short");
        set.add("boolean");
        set.add("char");
        set.add("float");
        set.add("double");
        set.add("byte");
        return set;
    }

    /**
     * @Author:wangqiang20995
     * @Description:强转
     * @Date:2017/8/27 23:33
     * @param:[clazz, instance]
     **/
    public static <T> T castType(Class<T> clazz,Object instance){

        return (T) instance;
    }
}
