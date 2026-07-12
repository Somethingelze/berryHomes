package net.berryhomes.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "settings", schema = "berryhomes")
public class Setting {
    @Id
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "setting_key", nullable = false, length = 100)
    private String settingKey;

    @Column(name = "setting_value", length = Integer.MAX_VALUE)
    private String settingValue;

}