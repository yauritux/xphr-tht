package link.yauritux.xphrtht.adapter.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Yauri Attamimi
 * @version 0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole(T(link.yauritux.xphrtht.core.domain.vo.UserRole).ADMIN)" +
    "|| hasRole(T(link.yauritux.xphrtht.core.domain.vo.UserRole).EMPLOYEE)")
public @interface IsAuthenticatedUser {
}
