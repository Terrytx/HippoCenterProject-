package tw.com.hippo_center_backend.h0bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "t22_categories")
public class T22CategoriesBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId; // 類別ID

    @Column(name = "name", length = 100, nullable = false)
    private String name; // 類別名稱

    @Column(name = "description", length = 255)
    private String description; // 類別描述

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<T21ProductBean> products;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<T21ProductBean> getProducts() {
        return products;
    }

    public void setProducts(List<T21ProductBean> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "T22CategoriesBean [categoryId=" + categoryId + ", name=" + name + "]";
    }

}
