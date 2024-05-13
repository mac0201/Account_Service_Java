package account.model.security.events;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Entity
@Table(name = "logs")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SecurityLog {
    @Id
    @Column(name = "log_id")
    private long id;
    private long date;
    @Enumerated(EnumType.STRING)
    private SecurityEventType action;
    @Builder.Default
    private String subject = "Anonymous";
    private String object;
    private String path;

    public void setSubject(String subject) {
        this.subject = subject == null || subject.isBlank() ? "Anonymous" : subject;
    }
}
