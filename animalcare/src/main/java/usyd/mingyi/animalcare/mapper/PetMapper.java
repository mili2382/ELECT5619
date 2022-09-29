package usyd.mingyi.animalcare.mapper;

import org.apache.ibatis.annotations.Mapper;
import usyd.mingyi.animalcare.pojo.Pet;

import java.util.List;
import java.util.Map;

@Mapper
public interface PetMapper {
    int addPet(Pet pet);
    List<Pet> getPetList(int userId);
    Pet getPet(int petId,int useId);
    int deletePet(int petId,int useId);
    int addImage(int imagePetId,String imageUrl);
    Map<String,Object> myTest (int petId,int useId);

}
