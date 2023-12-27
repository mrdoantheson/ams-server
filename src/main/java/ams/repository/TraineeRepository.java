package ams.repository;

import ams.model.entity.Trainee;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author <a href="mailto:danghuyxd.3092@gmail.com"> HuyDD12
 * <p>
 * This interface represents the repository for Trainee entity.
 */

@Repository
public interface TraineeRepository
        extends BaseRepository<Trainee, Long> {


    boolean existsByPhoneAndDeletedFalse(@Param("phone") String phone);

    boolean existsByEmailAndDeletedFalse(String email);

    boolean existsByAccountAndDeletedFalse(String account);



    boolean existsByPhoneAndDeletedFalseAndIdNot(@Param("phone") String phone, @Param("id") Long id);

    boolean existsByEmailAndDeletedFalseAndIdNot(String email, @Param("id") Long id);

    boolean existsByAccountAndDeletedFalseAndIdNot(String account, @Param("id") Long id);

    Trainee findByAccountAndDeletedFalse(String account);

}
