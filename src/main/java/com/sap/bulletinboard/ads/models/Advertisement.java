package com.sap.bulletinboard.ads.models;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Component
@Entity
@Table(name = "advertisements")
public class Advertisement {



    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Integer id;

    @Column(name = "mytitle")
    private String title;

    @Column(name = "created")
    private Timestamp created;

    @Column(name = "lastUpdate")
    private Timestamp updated;

    @Version
    private long version;

    @PrePersist
    public void prePersist(){
        this.created = now();
    }

    @PreUpdate
    public void preUpdate(){
        this.updated = now();
    }

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

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }


    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Advertisement{" +
                "title='" + title + '\'' +
                ", id=" + id +
                '}';
    }


    protected Timestamp now() {
        return new Timestamp(new Date().getTime());
    }
}
