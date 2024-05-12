package account.model;

import account.service.roles.UserRole;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonIncludeProperties({ "id", "name", "lastname", "email", "roles" })
@JsonPropertyOrder({ "id", "name", "lastname", "email", "roles" })
public class User implements UserDetails {
    @Id
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "user_seq")
    @Column(name = "user_id")
    private long id;
    private String name;
    private String lastname;

    private String email;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<UserRole> roles;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Payroll> payrolls;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
