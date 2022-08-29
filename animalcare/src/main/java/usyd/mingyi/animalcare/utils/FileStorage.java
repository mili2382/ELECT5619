package usyd.mingyi.animalcare.utils;

import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileStorage {

    public static String SaveFile(MultipartFile file,String location) throws IOException {

        String fileName = file.getOriginalFilename();//获取文件名称
        String suffixName=fileName.substring(fileName.lastIndexOf("."));//获取文件后缀
        fileName= UUID.randomUUID()+suffixName;//重新生成文件名
        File targetFile = new File(location);
        if (!targetFile.exists()) {
            // 判断文件夹是否未空，空则创建
            targetFile.mkdirs();
        }
        File saveFile = new File(targetFile, fileName);
        file.transferTo(saveFile);

        return fileName; //返回文件名

    }

}
