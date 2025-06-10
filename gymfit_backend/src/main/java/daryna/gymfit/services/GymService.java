package daryna.gymfit.services;

import daryna.gymfit.dao.GymRepository;
import daryna.gymfit.entities.Gym;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class GymService {

    private final GymRepository gymRepository;

    public List<Gym> findAll() {
        return gymRepository.findAll();
    }
//
//    @Transactional
//    public void save(Gym gym) {
//        gymRepository.save(gym);
//    }
//
//    @Transactional
//    public void delete(Gym gym) {
//        gymRepository.delete(gym);
//    }
//
    public Gym getById(long id){
        return gymRepository.getReferenceById(id);
    }
}
