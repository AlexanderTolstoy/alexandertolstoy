package function;

import DataStructure.Item;
import DataStructure.ResourcePool;
import utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * CheckModule
 * Created by ccwei on 2018/9/20.
 */
public class Check {
    public static void checkNameAndCode(){
        HashMap<String, Item> map = ResourcePool.getItems();
        HashMap<String, Integer> res = new HashMap<String, Integer>();
        HashMap<String, String> mapping = new HashMap<String, String>();
        for(String key : map.keySet()){
            Item item = map.get(key);
            String name = item.getName();
            String nameChar = name;
            if(name==null || name.length() == 0 || name.trim().length() == 0){
                System.out.println("name=" + name + ";code=");
            }

            if(Utils.isContainChinese(name))
            {
                //System.out.println(name);
                nameChar = Utils.convertPinyin2Char(name.trim(),name.trim()).toUpperCase();

            }
            if(mapping.containsKey(nameChar)){
                mapping.put(nameChar,mapping.get(nameChar)+"|"+ name);

            }else{
                mapping.put(nameChar,name);

            }

            if(res.containsKey(nameChar)){
                res.put(nameChar,res.get(nameChar)+1);
            }else {
                res.put(nameChar,1);
            }
            item.setName(nameChar);
        }

        File file = new File("output.txt");

        for(String key : res.keySet()){
            //System.out.println(key + " --> " + mapping.get(key)+ " : " + res.get(key));

            FileWriter fw = null;
            BufferedWriter bw = null;
            try {
                if(!file.exists()){
                    file.createNewFile();
                }
                fw = new FileWriter(file,true);
                bw = new BufferedWriter(fw);
                bw.write(key + " --> " + mapping.get(key)+ " : " + res.get(key) + "\n");
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(fw != null){
                    try{
                        fw.close();
                    }catch (Exception e)
                    {

                    }
                }
                if(bw != null){
                    try{
                        bw.close();
                    }catch (Exception e)
                    {

                    }
                }
            }

        }
    }
}
