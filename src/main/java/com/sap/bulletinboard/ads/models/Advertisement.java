package com.sap.bulletinboard.ads.models;

import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Table(name = "advertisements")
public class Advertisement {

    @Column(name = "mytitle")
    private String title;

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Integer id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Advertisement{" +
                "title='" + title + '\'' +
                ", id=" + id +
                '}';
    }
}
