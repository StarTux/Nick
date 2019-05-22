package com.cavetale.nick;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Table(name = "nicks")
public final class SQLNick {
    @Id
    private Integer id;
    @Column(nullable = false, unique = true)
    private UUID uuid;
    @Column(nullable = false, length = 255)
    private String nickname;

    public SQLNick() { }

    SQLNick(final UUID uuid,
            final String nickname) {
        this.uuid = uuid;
        this.nickname = nickname;
    }
}
