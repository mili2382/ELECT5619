package usyd.mingyi.animalcare.utils;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.*;
public class ImageUtil {

    // 对字节数组字符串进行Base64解码并生成图片
    //imgFilePath 待保存的本地路径
    public static void generateImage(String base64Str, String imgFilePath) throws IOException {
        if (base64Str == null) // 图像数据为空
            return;

            // Base64解码
            byte[] bytes = Base64.decodeBase64(base64Str);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            // 生成jpeg图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(bytes);
            out.flush();
            out.close();

    }

    public static boolean checkImage(String base64Data){
        String dataPrix = ""; //base64格式前头
        String data = "";//实体部分数据
        if(base64Data==null||"".equals(base64Data)){
            return false;
        }else {
            String [] d = base64Data.split("base64,");//将字符串分成数组
            if(d != null && d.length == 2){
                dataPrix = d[0];
                data = d[1];
            }else {
                return false;

            }
        }
        String suffix = "";//图片后缀，用以识别哪种格式数据
        //data:image/jpeg;base64,base64编码的jpeg图片数据
        if("data:image/jpeg;".equalsIgnoreCase(dataPrix)){
            suffix = ".jpg";
        }else if("data:image/x-icon;".equalsIgnoreCase(dataPrix)){
            //data:image/x-icon;base64,base64编码的icon图片数据
            suffix = ".ico";
        }else if("data:image/gif;".equalsIgnoreCase(dataPrix)){
            //data:image/gif;base64,base64编码的gif图片数据
            suffix = ".gif";
        }else if("data:image/png;".equalsIgnoreCase(dataPrix)){
            //data:image/png;base64,base64编码的png图片数据
            suffix = ".png";
        }else {
            return false;
        }
        return true;
    }

    public static String getData(String base64Data){
        String dataPrix = ""; //base64格式前头
        String data = "";//实体部分数据
        String [] d = base64Data.split("base64,");//将字符串分成数组
        if(d != null && d.length == 2){
            dataPrix = d[0];
            data = d[1];
        }
        return data;
    }


    public static String getSuffix(String base64Data){
        String dataPrix = ""; //base64格式前头
        String data = "";//实体部分数据
            String [] d = base64Data.split("base64,");//将字符串分成数组
            if(d != null && d.length == 2){
                dataPrix = d[0];
                data = d[1];
            }

        String suffix = "";//图片后缀，用以识别哪种格式数据
        //data:image/jpeg;base64,base64编码的jpeg图片数据
        if("data:image/jpeg;".equalsIgnoreCase(dataPrix)){
            suffix = ".jpg";
        }else if("data:image/x-icon;".equalsIgnoreCase(dataPrix)){
            //data:image/x-icon;base64,base64编码的icon图片数据
            suffix = ".ico";
        }else if("data:image/gif;".equalsIgnoreCase(dataPrix)){
            //data:image/gif;base64,base64编码的gif图片数据
            suffix = ".gif";
        }else if("data:image/png;".equalsIgnoreCase(dataPrix)){
            //data:image/png;base64,base64编码的png图片数据
            suffix = ".png";
        }
        return suffix;
    }



}
