package usyd.mingyi.animalcare.utils;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.*;
public class ImageUtil {

    // 对字节数组字符串进行Base64解码并生成图片
    //imgFilePath 待保存的本地路径
    /*** 将base64字符串，生成文件 */
    public static void convertBase64ToFile(String fileBase64String, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            //判断文件目录是否存在
            if (!dir.exists()) {
                System.out.println("创建成功");
                dir.mkdirs();
            }
            byte[] bfile = Base64.decodeBase64(fileBase64String);
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return;
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



    public static byte[]  getBytesByStream(InputStream inputStream){
        byte[] bytes = new byte[1024];

        int b;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            while((b = inputStream.read(bytes)) != -1){

                byteArrayOutputStream.write(bytes,0,b);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }




}
