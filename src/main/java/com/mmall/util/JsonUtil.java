package com.mmall.util;
import com.mmall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //对象所有的字段全部录入
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);

        //取消默认转换timestamp的类型
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);

        //忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);

        //日期格式统一
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //反序列化的操作
        //忽略在json字符串中存在，但是在java对象中不存在对应的属性的情况，防止出错
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }
    //普通对象转化为Json字符串
    public static <T> String obj2String(T obj){

        if (obj==null){
            return null;
        }

        try {
            return obj instanceof String ? (String) obj:objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("Parse Object to json error");
            e.printStackTrace();
        }

        return null;
    }
    //普通对象转化为Json字符串,并且美化
    public static <T> String obj2StringPretty(T obj){

        if (obj==null){
            return null;
        }

        try {
            return obj instanceof String ? (String) obj:objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("Parse Object to json error");
            e.printStackTrace();
        }

        return null;
    }

    //传入json字符串，转化为指定的类型
    public static <T> T string2Obj(String str,Class<T> clazz){

        if (StringUtils.isEmpty(str)){
            return null;
        }

        try {
            return clazz.equals(String.class)? (T) str:objectMapper.readValue(str,clazz);
        } catch (IOException e) {
            log.warn("Parse json to class error");
            e.printStackTrace();
        }

        return null;
    }
    //传入json字符串，转化为指定的类型（复杂情况）
    public static <T> T string2Obj(String str, TypeReference<T> typeReference){
        if (StringUtils.isEmpty(str)||typeReference==null){
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class) ? str:objectMapper.readValue(str,typeReference));
        } catch (IOException e) {
            log.warn("Parse json to class error");
            e.printStackTrace();
        }
        return null;
    }
    //传入json字符串，转化为指定的类型（复杂情况）
    public static <T> T string2Obj(String str,Class<?> collectionClass,Class<?>... elementClasses){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);

        try {
            return objectMapper.readValue(str,javaType);
        } catch (IOException e) {
            log.warn("Parse json to class error");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        User user = new User();
        user.setId(1);
        user.setUsername("张三");

        User u = new User();
        u.setId(2);
        u.setUsername("李四");

        Map<String,User> map = new HashMap<>();

        map.put("1",user);
        map.put("2",u);

        String s = JsonUtil.obj2StringPretty(map);
        System.out.println(s);

        Map<String, User> obj = JsonUtil.string2Obj(s, new TypeReference<Map<String, User>>() {
        });

        for (String s1 : obj.keySet()) {
            System.out.println(s1);
            System.out.println(obj.get(s1));
        }
        System.out.println("...............");
        Map<String,User> map1 = JsonUtil.string2Obj(s, Map.class, String.class,User.class);

        for (String s1 : map1.keySet()) {
            System.out.println(s1);
            System.out.println(map1.get(s1));
        }
    }
    @Test
    public void t(){
        String s = " ";
        System.out.println(s.length());
        System.out.println(StringUtils.isNotEmpty(s));
        System.out.println(StringUtils.isNotBlank(s));
    }
}
