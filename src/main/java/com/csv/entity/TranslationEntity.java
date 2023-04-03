package com.csv.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "translation", schema = "spring_sentence", catalog = "")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class TranslationEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "audio_url", nullable = false)
    private String audioUrl;

    @Column(name = "translate_id", nullable = false)
    private String translateId;

    @Column(name = "translate_text", nullable = false)
    private String translateText;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TranslationEntity that = (TranslationEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
