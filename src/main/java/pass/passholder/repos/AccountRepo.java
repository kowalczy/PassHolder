package pass.passholder.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pass.passholder.entity.Account;
import pass.passholder.entity.AppUser;

@Repository
public interface AccountRepo extends CrudRepository<Account, Integer> {
}
