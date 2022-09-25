package usyd.mingyi.animalcare.mapper;

import org.apache.ibatis.annotations.Mapper;
import usyd.mingyi.animalcare.pojo.Pet;

import java.util.List;

@Mapper
public interface PetMapper {
    int addPet(Pet pet);
    List<Pet> getPetList(int userId);
    Pet getPet(int petId,int useId);
    int deletePet(int petId,int useId);

}
