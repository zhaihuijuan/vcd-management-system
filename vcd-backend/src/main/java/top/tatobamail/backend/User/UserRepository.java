package top.tatobamail.backend.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// 继承Jpa大类，所以大部分方法不用实现
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 包装后的用户对象（可能为空）
     */
    Optional<User> findByUsername(String username);

    /**
     * 校验用户名是否已存在
     * @param username 用户名
     * @return 存在返回true，不存在返回false
     */
    boolean existsByUsername(String username);
}
