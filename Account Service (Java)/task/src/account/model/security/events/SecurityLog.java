package account.model.security.events;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "logs")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SecurityLog {
    @Id
    @Column(name = "log_id")
    private long id;
    private long date;
    @Enumerated(EnumType.STRING)
    private SecurityEventType action;
    private String subject;
    private String object;
    private String path;

    public void setSubject(String subject) {
        this.subject = subject == null || subject.isBlank() ? "Anonymous" : subject;
    }
}
