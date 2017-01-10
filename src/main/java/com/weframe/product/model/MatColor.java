package com.weframe.product.model;

import javax.persistence.*;

@Entity
@Table(name = "MAT_COLORS")
public class MatColor {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "NAME", nullable = false)
    private String name;

    public MatColor() {
    }

    public MatColor(final Long id,
                    final String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatColor matColor = (MatColor) o;

        if (!id.equals(matColor.id)) return false;
        return name.equals(matColor.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MatColor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
